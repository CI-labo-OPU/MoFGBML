package cilabo.gbml.operator.mutation;

import java.util.ArrayList;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import cilabo.data.DataSet;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.utility.Random;

public class PittsburghMutation implements MutationOperator<IntegerSolution> {
	private double mutationProbability;
	private RandomGenerator<Double> randomGenerator;
	private BoundedRandomGenerator<Integer> intRandomGenerator;
	private Knowledge knowledge;
	private DataSet train;

	/** Constructor */
	public PittsburghMutation(Knowledge knowledge, DataSet train) {
		this(1.0, knowledge, train);
	}

	/** Constructor */
	public PittsburghMutation(double mutationProbability, Knowledge knowledge, DataSet train) {
		this(mutationProbability, knowledge, train,
			 () -> JMetalRandom.getInstance().nextDouble(),
			 (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghMutation(double mutationProbability, Knowledge knowledge, DataSet train, RandomGenerator<Double> randomGenerator) {
		this(
			mutationProbability, knowledge, train,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghMutation(double mutationProbability, Knowledge knowledge, DataSet train, RandomGenerator<Double> randomGenerator, BoundedRandomGenerator<Integer> intRandomGenerator) {
		if (mutationProbability < 0) {
			throw new JMetalException("Mutation probability is negative: " + mutationProbability);
		}
		this.mutationProbability = mutationProbability;
		this.knowledge = knowledge;
		this.train = train;
		this.randomGenerator = randomGenerator;
		this.intRandomGenerator = intRandomGenerator;
	}

	/* Getter */
	@Override
	public double getMutationProbability() {
		return mutationProbability;
	}

	/* Setters */
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	public void setKnowledge(Knowledge knowledge) {
		this.knowledge = knowledge;
	}

	/** Execute() method
	 * @param solution IntegerSolution : PittsburghSolutionを前提
	 */
	@Override
	public IntegerSolution execute(IntegerSolution solution) {
		Check.isNotNull(solution);
		Check.that(solution.getClass() == PittsburghSolution.class, "The argument must be class: " + PittsburghSolution.class.getCanonicalName());

		doMutation(mutationProbability, (PittsburghSolution)solution);
		return solution;
	}

	/**
	 * 後件部の学習をここでは行わない.
	 * @param probability
	 * @param solution PittsburghSolution
	 */
	public void doMutation(double probability, PittsburghSolution solution) {
		int numberOfRules = solution.getMichiganPopulation().size();
		int dimension = train.getNdim();

		for(int rule_i = 0; rule_i < numberOfRules; rule_i++) {
			/* Perform muation for rule_i */
			if(Random.getInstance().getGEN().nextInt(numberOfRules) == 0) {/* Probability for each rule := 1/NumberOfRules */
				/* Decide which demension is performed mutation. */
				int mutatedDimension = Random.getInstance().getGEN().nextInt(dimension);
				/* To judge which mutatedDimension is categorical or numerical  */
				double variableOfRandomPattern = train
												.getPattern(Random.getInstance().getGEN().nextInt(train.getDataSize()))
					  							.getDimValue(mutatedDimension);
				/* Attribute is Numeric */
				if(variableOfRandomPattern >= 0.0) {
					int numberOfCandidates = knowledge.getFuzzySetNum(mutatedDimension);
					ArrayList<Integer> list = new ArrayList<>();
					for(int i = 0; i < numberOfCandidates; i++) {
						if(i != solution.getMichiganPopulation().get(rule_i).getVariable(mutatedDimension)) {
							list.add(i);
						}
					}
					/* Perform mutation */
					int newFuzzySet = list.get( Random.getInstance().getGEN().nextInt(list.size()) );
					/* Set variable */
					solution.setVariable(rule_i*dimension + mutatedDimension, newFuzzySet);
				}
				/* Attribute is categorical */
				else {
					solution.setVariable(rule_i*dimension + mutatedDimension, (int)variableOfRandomPattern);
				}
			}
		}

	}

}
