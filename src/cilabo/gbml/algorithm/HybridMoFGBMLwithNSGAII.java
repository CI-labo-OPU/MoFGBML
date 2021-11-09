package cilabo.gbml.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observable.ObservableEntity;
import org.uma.jmetal.util.observable.impl.DefaultObservable;

import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.gbml.component.variation.CrossoverAndMutationAndPittsburghLearningVariation;

public class HybridMoFGBMLwithNSGAII<S extends Solution<?>> extends AbstractEvolutionaryAlgorithm<S, List<S>>
										implements ObservableEntity {
	private int evaluations;
	private int populationSize;
	private int offspringPopulationSize;
	private int frequency;
	private String outputRootDir;

	protected SelectionOperator<List<S>, S> selectionOperator;
	protected CrossoverOperator<S> crossoverOperator;
	protected MutationOperator<S> mutationOperator;
	private Termination termination;
	private Variation<S> variation;
	private InitialSolutionsCreation<S> initialSolutionsCreation;

	private Map<String, Object> algorithmStatusData;

	private Evaluation<S> evaluation;
	private Replacement<S> replacement;
	private MatingPoolSelection<S> selection;

	private long startTime;
	private long totalComputingTime;

	private Observable<Map<String, Object>> observable;

	/** Constructor */
	public HybridMoFGBMLwithNSGAII(
			/* Arguments */
			Problem<S> problem,
			int populationSize,
			int offspringPopulationSize,
			int frequency,
			String outputRootDir,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator,
			Termination termination,
			ConsequentFactory consequentFactory) {
		/* Constructor Body */
		this.problem = problem;

		this.populationSize = populationSize;
		this.offspringPopulationSize = offspringPopulationSize;
		this.frequency = frequency;
		this.outputRootDir = outputRootDir;

		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.termination = termination;

		/* NSGA-II */
		DensityEstimator<S> densityEstimator = new CrowdingDistanceDensityEstimator<>();
		Ranking<S> ranking = new FastNonDominatedSortRanking<>();

		this.replacement =
				new RankingAndDensityEstimatorReplacement<>(
						ranking, densityEstimator, Replacement.RemovalPolicy.oneShot);

		this.variation =
				new CrossoverAndMutationAndPittsburghLearningVariation<>(
						offspringPopulationSize, crossoverOperator, mutationOperator, consequentFactory);

		this.selection =
				new NaryTournamentMatingPoolSelection<>(
						2,
						variation.getMatingPoolSize(),
						new MultiComparator<>(
								Arrays.asList(
										ranking.getSolutionComparator(), densityEstimator.getSolutionComparator())));

		this.initialSolutionsCreation = new RandomSolutionsCreation<>(problem, populationSize);
		this.evaluation = new SequentialEvaluation<>();

		this.algorithmStatusData = new HashMap<>();
		this.observable = new DefaultObservable<>("Hybrid MoFGBML with NSGA-II algorithm");
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		/* === START === */
		//TODO
		/* ===  END  === */
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


	@Override
	protected void updateProgress() {
		//TODO
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
		return "Hybrid-style Multi-objective FGBML with NSGA-II";
	}

	@Override
	public String getDescription() {
		return "Hybrid-style Multi-objective Fuzzy Genetics-Based Machine Learning with NSGA-II";
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

	/* Setter */
	public HybridMoFGBMLwithNSGAII<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
		this.selectionOperator = selectionOperator;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setCrossoverOperator(CrossoverOperator<S> crossoverOperator) {
		this.crossoverOperator = crossoverOperator;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setMutationOperator(MutationOperator<S> mutationOperator) {
		this.mutationOperator = mutationOperator;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setInitialSolutionsCreation(InitialSolutionsCreation<S> initialSolutionsCreation) {
		this.initialSolutionsCreation = initialSolutionsCreation;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setTermination(Termination termination) {
		this.termination = termination;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setEvaluation(Evaluation<S> evaluation) {
		this.evaluation = evaluation;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setReplacement(Replacement<S> replacement) {
		this.replacement = replacement;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setVariation(Variation<S> variation) {
		this.variation = variation;
		return this;
	}

	public HybridMoFGBMLwithNSGAII<S> setSelection(MatingPoolSelection<S> selection) {
		this.selection = selection;
		return this;
	}

}
