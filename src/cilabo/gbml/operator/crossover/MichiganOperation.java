package cilabo.gbml.operator.crossover;

import java.util.List;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

public class MichiganOperation implements CrossoverOperator<IntegerSolution> {
	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public MichiganOperation(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public MichiganOperation(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public MichiganOperation(
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
	public int getNumberOfRequiredParents() {
		return 1;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}

	@Override
	public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
		Check.isNotNull(solutions);
		Check.that(solutions.size() == 1, "There must be single parent instead of " + solutions.size());
		return doCrossover(crossoverProbability, solutions.get(0));
	}

	public List<IntegerSolution> doCrossover(double probability, IntegerSolution parent){

		//TODO
		//TODO
		//TODO

		return null;
	}
}
