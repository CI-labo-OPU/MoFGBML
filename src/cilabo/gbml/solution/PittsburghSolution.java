package cilabo.gbml.solution;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;

import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.gbml.solution.util.SortMichiganPopulation;

public class PittsburghSolution extends DefaultIntegerSolution implements IntegerSolution {
	// ************************************************************
	// Fields
	/**   */
	List<IntegerSolution> michiganPopulation;

	/**  */
	Classifier classifier;

	// ************************************************************
	// Constructor

	/** Constructor */
	public PittsburghSolution(List<Pair<Integer, Integer>> bounds,
								int numberOfObjectives,
								List<IntegerSolution> michiganPopulation,
								Classification classification) {
		this(bounds, numberOfObjectives, 0, michiganPopulation, classification);
	}

	/** Constructor */
	public PittsburghSolution(List<Pair<Integer, Integer>> bounds,
								int numberOfObjectives,
								int numberOfConstraints,
								List<IntegerSolution> michiganPopulation,
								Classification classification)
	{
		/* ruleNum*Ndim: The number of variables, the length of that is variable. */
		super(bounds, numberOfObjectives, numberOfConstraints);

		// Radix sort Michigan solution list
		SortMichiganPopulation.radixSort(michiganPopulation);

		this.michiganPopulation = michiganPopulation;

		// Build classifier from michigan population.
		classifier = new RuleBasedClassifier();
		((RuleBasedClassifier)classifier).setClassification(classification);
		for(int i = 0; i < michiganPopulation.size(); i++) {
			IntegerSolution michigan = michiganPopulation.get(i);
			((RuleBasedClassifier)classifier).addRule(((MichiganSolution)michigan).getRule());
			// Set variable from michigan-type solution.
			for(int j = 0; j < michigan.getNumberOfVariables(); j++) {
				setVariable(i*michigan.getNumberOfVariables() + j, michigan.getVariable(j));
			}
		}
	}

	/** Copy constructor */
	public PittsburghSolution(PittsburghSolution solution) {
		super(solution);
		michiganPopulation = new ArrayList<>(solution.getMichiganPopulation().size());
		for(int i = 0; i < michiganPopulation.size(); i++) {
			michiganPopulation.add(i, (IntegerSolution)solution.getMichiganPopulation().get(i).copy());
		}
		classifier = solution.getClassifier().copy();
	}

	// ************************************************************
	// Methods

	@Override
	public PittsburghSolution copy() {
		return new PittsburghSolution(this);
	}

	/* Setters */
	public void setMichiganPopulation(List<IntegerSolution> solutions) {
		this.michiganPopulation = solutions;
	}

	/* Getters */
	public List<IntegerSolution> getMichiganPopulation() {
		return this.michiganPopulation;
	}

	public List<Pair<Integer, Integer>> getBounds() {
		return this.bounds;
	}

	public Classifier getClassifier() {
		return this.classifier;
	}

}
