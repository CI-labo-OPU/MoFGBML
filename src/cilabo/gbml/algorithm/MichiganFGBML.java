package cilabo.gbml.algorithm;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator.Ordering;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.gbml.component.evaluation.MichiganEvaluation;
import cilabo.gbml.problem.AbstractMichiganGBML_Problem;
import cilabo.util.fileoutput.MichiganSolutionListOutput;

public class MichiganFGBML<S extends Solution<?>> extends AbstractEvolutionaryAlgorithm<S, List<S>>
							implements ObservableEntity
{
	private int evaluations;
	private int populationSize;
	private int offspringPopulationSize;
	private int frequency;
	private String outputRootDir;

	protected SelectionOperator<List<S>, S> selectionOperator;
	protected CrossoverOperator<S> crossoverOperator;
	protected MutationOperator<S> mutationOperator;

	private Map<String, Object> algorithmStatusData;

	private InitialSolutionsCreation<S> initialSolutionsCreation;
	private Termination termination;
	private Evaluation<S> evaluation ;
	private Replacement<S> replacement;
	private Variation<S> variation;
	private MatingPoolSelection<S> selection;

	private long startTime;
	private long totalComputingTime;

	private Observable<Map<String, Object>> observable;

	private List<RuleBasedClassifier> totalClassifiers = new ArrayList<>();

	/** Constructor */
	public MichiganFGBML(
			Problem<S> problem,
			int populationSize,
			int offspringPopulationSize,
			int frequency,
			String outputRootDir,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator,
			Termination termination,
			Variation<S> variation,
			Replacement<S> replacement)
	{
		this.populationSize = populationSize;
		this.offspringPopulationSize = offspringPopulationSize;
		this.frequency = frequency;
		this.outputRootDir = outputRootDir;

		this.problem = problem;

		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.termination = termination;
		this.variation = variation;
		this.replacement = replacement;

		this.initialSolutionsCreation = new RandomSolutionsCreation<>(problem, populationSize);
		this.evaluation = new MichiganEvaluation<S>();

		this.selection =
				new NaryTournamentMatingPoolSelection<>(
						2,	//Tournament Size
						variation.getMatingPoolSize(),
						new ObjectiveComparator<>(0, Ordering.DESCENDING));	//Single Objective, Maximize

		this.algorithmStatusData = new HashMap<>();
		this.observable = new DefaultObservable<>("Michigan-type FGBML with Single-objective");
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();

		/*** START ***/
	    List<S> offspringPopulation;
	    List<S> matingPopulation;

	    population = createInitialPopulation();
	    population = evaluatePopulation(population);
	    initProgress();
	    while (!isStoppingConditionReached()) {
	      matingPopulation = selection(population);
	      offspringPopulation = reproduction(matingPopulation);
	      population = replacement(population, offspringPopulation);
	      population = evaluatePopulation(population);
	      updateProgress();
	    }
		/***  END ***/
		totalComputingTime = System.currentTimeMillis() - startTime;
	}

	@Override
	protected void initProgress() {
		evaluations = populationSize;

	    algorithmStatusData.put("EVALUATIONS", evaluations);
	    algorithmStatusData.put("POPULATION", population);
	    algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

	    observable.setChanged();
	    observable.notifyObservers(algorithmStatusData);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void updateProgress() {
		this.totalClassifiers.add(((AbstractMichiganGBML_Problem<S>)problem).population2classifier(population));
		evaluations += offspringPopulationSize;
		algorithmStatusData.put("EVALUATIONS", evaluations);
		algorithmStatusData.put("POPULATION", population);
		algorithmStatusData.put("COMPUTING_TIME", System.currentTimeMillis() - startTime);

		observable.setChanged();
		observable.notifyObservers(algorithmStatusData);

		String sep = File.separator;
		Integer evaluations = (Integer)algorithmStatusData.get("EVALUATIONS");
		if (evaluations!=null) {
			if (evaluations % frequency == 0) {
				new MichiganSolutionListOutput(getPopulation())
					.printSolutionsToFile(new DefaultFileOutputContext(outputRootDir+sep+"solutions-"+evaluations+".txt"), getPopulation());
			}
		}
		else {
			JMetalLogger.logger.warning(getClass().getName()
			+ ": The algorithm has not registered yet any info related to the EVALUATIONS key");
		}

	}

	@Override
	protected boolean isStoppingConditionReached() {
		return termination.isMet(algorithmStatusData);
	}

	@Override
	protected List<S> createInitialPopulation() {
		return initialSolutionsCreation.create();
	}

	@Override
	protected List<S> evaluatePopulation(List<S> population) {
		return  evaluation.evaluate(population, getProblem());
	}

	@Override
	protected List<S> selection(List<S> population) {
		return this.selection.select(population);
	}

	@Override
	protected List<S> reproduction(List<S> matingPool){
		return variation.variate(population, matingPool);
	}

	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
		return replacement.replace(population, offspringPopulation);
	}

	@Override
	public List<S> getResult(){
		return SolutionListUtils.getNonDominatedSolutions(getPopulation());
	}

	@Override
	public String getName() {
		return "Michigan FGBML with Single-objective";
	}

	@Override
	public String getDescription() {
		return "Single-objective Michigan-type Fuzzy Genetics-Based Machine Learning";
	}

	public Map<String, Object> getAlgorithmStatusData() {
		return algorithmStatusData;
	}

	@Override
	public Observable<Map<String, Object>> getObservable() {
		return observable;
	}

	public long getTotalComputingTime() {
		return totalComputingTime;
	}

	public long getEvaluations() {
		return evaluations;
	}

	public List<RuleBasedClassifier> getTotalClassifier() {
		return this.totalClassifiers;
	}

	/* Setter */

	public MichiganFGBML<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
		this.selectionOperator = selectionOperator;
		return this;
	}

	public MichiganFGBML<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
		this.crossoverOperator = crossoverOperator;
		return this;
	}

	public MichiganFGBML<S> setMutationOperator(MutationOperator<S> mutationOperator) {
		this.mutationOperator = mutationOperator;
		return this;
	}

	public MichiganFGBML<S> setInitialSolutionsCreation(InitialSolutionsCreation<S> initialSolutionsCreation) {
		this.initialSolutionsCreation = initialSolutionsCreation;
		return this;
	}

	public MichiganFGBML<S> setTermination(Termination termination) {
		this.termination = termination;
		return this;
	}

	public MichiganFGBML<S> setEvaluation(Evaluation<S> evaluation) {
		this.evaluation = evaluation;
		return this;
	}

	public MichiganFGBML<S> setReplacement(Replacement<S> replacement) {
		this.replacement = replacement;
		return this;
	}

	public MichiganFGBML<S> setVariation(Variation<S> variation) {
		this.variation = variation;
		return this;
	}

	public MichiganFGBML<S> setSelection(MatingPoolSelection<S> selection) {
		this.selection = selection;
		return this;
	}
}
