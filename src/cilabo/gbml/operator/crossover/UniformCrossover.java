package cilabo.gbml.operator.crossover;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class UniformCrossover implements CrossoverOperator<IntegerSolution> {
	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public UniformCrossover(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public UniformCrossover(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public UniformCrossover(
			double crossoverProbability,
			RandomGenerator<Double> crossoverRandomGenerator,
			BoundedRandomGenerator<Integer> selectRandomGenerator) {
		if(crossoverProbability < 0) {
			throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
		}
		this.crossoverProbability = crossoverProbability;
		this.crossoverRandomGenerator = crossoverRandomGenerator;
		this.selectRandomGenerator = selectRandomGenerator;
	}

	/* Getter */
	@Override
	public double getCrossoverProbability() {
		return this.crossoverProbability;
	}

	/* Setter */
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}

	@Override
	public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
		Check.isNotNull(solutions);
		Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());
		return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
	}

	/**
	 * Perform the crossover operation.
	 *
	 * @param probability Crossover setProbability
	 * @param parent1 The first parent
	 * @param parent2 The second parent
	 * @return An array containing the one offspring
	 */
	public List<IntegerSolution> doCrossover(
			double probability, IntegerSolution parent1, IntegerSolution parent2){

		List<IntegerSolution> offspring = new ArrayList<>(1);
		offspring.add((IntegerSolution)parent1.copy());
		offspring.add((IntegerSolution)parent2.copy());

		if(crossoverRandomGenerator.getRandomValue() < probability) {
			// 1. Get the number of variables;
			int numberOfVariables = parent1.getNumberOfVariables();

			// 2. Set the probability for uniform;
			double uniformProbability = 0.5;

			// 2. Apply the crossover to the variable;
			for(int i = 0; i < numberOfVariables; i++) {
				if(crossoverRandomGenerator.getRandomValue() < uniformProbability) {
					// Swap i-th variable between parent1 and parent2.
					Integer tmp = parent1.getVariable(i);
					offspring.get(0).setVariable(i, parent2.getVariable(i));
					offspring.get(1).setVariable(i, tmp);
				}
			}
		}

		// Select one offspring by random.
		int remove = selectRandomGenerator.getRandomValue(0, 1);
		offspring.remove(remove);

		return offspring;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}
}
