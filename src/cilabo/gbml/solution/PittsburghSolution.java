package cilabo.gbml.solution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.solution.integersolution.impl.DefaultIntegerSolution;

import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.rule.Rule;
import cilabo.gbml.solution.util.SortMichiganPopulation;

public class PittsburghSolution extends DefaultIntegerSolution implements IntegerSolution {
	// ************************************************************
	// Fields
	/**   */
	List<IntegerSolution> michiganPopulation;

	/**  */
	Classifier classifier;

	/** */
	Classification classification;

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

		this.classification = classification;
		this.setMichiganPopulation(michiganPopulation);

	}

	/** Copy constructor */
	public PittsburghSolution(PittsburghSolution solution) {
		super(solution);
		michiganPopulation = new ArrayList<>(solution.getMichiganPopulation().size());
		for(int i = 0; i < solution.getMichiganPopulation().size(); i++) {
			michiganPopulation.add(i, (IntegerSolution)solution.getMichiganPopulation().get(i).copy());
		}
		classifier = solution.getClassifier().copy();
		classification = solution.getClassification();
	}

	// ************************************************************
	// Methods

	@Override
	public PittsburghSolution copy() {
		return new PittsburghSolution(this);
	}

	/* Setters */
	public void setMichiganPopulation(List<IntegerSolution> solutions) {
		// Clear this.variables
		for(int i = 0; i < getNumberOfVariables(); i++) {
			setVariable(i, null);
		}

		// Eliminate duplicate solutions
		this.michiganPopulation = new ArrayList<>();
		Map<String, IntegerSolution> map = new HashMap<>();
		for(int i = 0; i < solutions.size(); i++) {
			MichiganSolution michigan = (MichiganSolution)solutions.get(i);
			Rule rule = michigan.getRule();
			if(!map.containsKey(rule.toString())) {
				map.put(rule.toString(), michigan);
				this.michiganPopulation.add(michigan);
			}
		}

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

	public Classification getClassification() {
		return this.classification;
	}

	@Override
	public String toString() {
		String result = "Variables: ";
		for (Integer var : getVariables()) {
			if(var != null) {
				result += "" + var + " ";
			}
		}
		result += "Objectives: ";
		for (Double obj : getObjectives()) {
			result += "" + obj + " ";
		}
		result += "Constraints: ";
		for (Double obj : getConstraints()) {
			result += "" + obj + " ";
		}
		result += "\t";
		result += "AlgorithmAttributes: " + attributes + "\n";

		return result;
	}

}
