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

import cilabo.main.Consts;

public class HybridGBMLcrossover implements CrossoverOperator<IntegerSolution> {

	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	private double michiganOperationProbability;


	/** Constructor */
	public HybridGBMLcrossover(double crossoverProbability, double michiganOperationProbability) {
		this(crossoverProbability,
			 () -> JMetalRandom.getInstance().nextDouble(),
			 (a, b) -> JMetalRandom.getInstance().nextInt(a, b));

		this.michiganOperationProbability = michiganOperationProbability;
	}

	/** Constructor */
	public HybridGBMLcrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(crossoverProbability,
			 randomGenerator,
			 BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public HybridGBMLcrossover(double crossoverProbability,
			RandomGenerator<Double> crossoverRandomGenerator,
			BoundedRandomGenerator<Integer> selectRandomGenerator)
	{
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
		return 2;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}

	@Override
	public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
		Check.isNotNull(solutions);
		Check.that(solutions.size() == 2, "There must be two parents instead of " + solutions.size());
		return doCrossover(crossoverProbability, solutions.get(0), solutions.get(1));
	}

	public List<IntegerSolution> doCrossover(
			double probability, IntegerSolution parent1, IntegerSolution parent2)
	{
		PittsburghCrossover pittsburghX = new PittsburghCrossover(Consts.PITTSBURGH_CROSS_RT);
		MichiganOperation michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT);

		List<IntegerSolution> offspring = new ArrayList<>();

		if(crossoverRandomGenerator.getRandomValue() < probability) {/* Do crossover */
			/* Judge if two parents are same. */
			double p = michiganOperationProbability;
			if(parent1.toString().equals(parent2.toString())) p = 1.0;
			else p = michiganOperationProbability;

			if(crossoverRandomGenerator.getRandomValue() < p) {
				/* Michigan operation */
				offspring = michiganX.doCrossover(michiganX.getCrossoverProbability(), parent1);
			}
			else {
				/* Pittsburgh operation */
				offspring = pittsburghX.doCrossover(pittsburghX.getCrossoverProbability(), parent1, parent2);
			}
		}
		else {/* Don't crossover */
			offspring.add((IntegerSolution)parent1.copy());
			offspring.add((IntegerSolution)parent2.copy());
			int index = selectRandomGenerator.getRandomValue(0,  1);
			offspring.remove(index);
		}

		return offspring;
	}

}
