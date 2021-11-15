package cilabo.gbml.operator.mutation;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.knowledge.Knowledge;

public class MichiganMutation implements MutationOperator<IntegerSolution> {
	private double mutationProbability;
	private RandomGenerator<Double> randomGenerator;
	private BoundedRandomGenerator<Integer> intRandomGenerator;
	private Knowledge knowledge;
	private DataSet data;

	  /** Constructor */
	  public MichiganMutation(double mutationProbability, Knowledge knowledge, DataSet data) {
	    this(mutationProbability, knowledge, data,
	    	 () -> JMetalRandom.getInstance().nextDouble(),
	    	 (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	  }

	  /** Constructor */
	  public MichiganMutation(double mutationProbability, Knowledge knowledge, DataSet data, RandomGenerator<Double> randomGenerator) {
		  this(
			mutationProbability, knowledge, data,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	  }

	  /** Constructor */
	  public MichiganMutation(double mutationProbability, Knowledge knowledge, DataSet data, RandomGenerator<Double> randomGenerator, BoundedRandomGenerator<Integer> intRandomGenerator) {
	    if (mutationProbability < 0) {
	      throw new JMetalException("Mutation probability is negative: " + mutationProbability);
	    }
	    this.mutationProbability = mutationProbability;
	    this.knowledge = knowledge;
	    this.data = data;
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

	  public void setData(DataSet data) {
		  this.data = data;
	  }

	  /** Execute() method */
	  @Override
	  public IntegerSolution execute(IntegerSolution solution) {
		  Check.isNotNull(solution);

		  doMutation(mutationProbability, solution);
		  return solution;
	  }

	  /**
	   * Perform the mutation operation
	   *
	   * @param probability Mutation setProbability
	   * @param solution The solution to mutate
	   */
	  public void doMutation(double probability, IntegerSolution solution) {
		  for(int i = 0; i < solution.getNumberOfVariables(); i++) {
			// To judge which attribute i is categorical or numerical.
			Pattern randPattern = data.getPattern(intRandomGenerator.getRandomValue(0, data.getDataSize()-1));

			// Decide new variable
			int fuzzySetNum = knowledge.getFuzzySetNum(i);
			int newFuzzySet = intRandomGenerator.getRandomValue(0, fuzzySetNum-2);

			if(randomGenerator.getRandomValue() < probability) {
				if(randPattern.getDimValue(i) >= 0) {
					// Numerical attribute:
					if(newFuzzySet < solution.getVariable(i)) {
						solution.setVariable(i, newFuzzySet);
					}
					else {
						solution.setVariable(i, newFuzzySet+1);
					}
				}
				else {
					// Categorical attribute:
					solution.setVariable(i, (int)randPattern.getDimValue(i));
				}
			}
		  }
	  }
}
