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

import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.main.Consts;
import cilabo.utility.GeneralFunctions;
import cilabo.utility.Random;

public class PittsburghCrossover implements CrossoverOperator<IntegerSolution> {
	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public PittsburghCrossover(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghCrossover(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghCrossover(
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

	/**
	 * 後件部の学習はここでは行わない
	 * @param probability
	 * @param _parent1
	 * @param _parent2
	 * @return
	 */
	public List<IntegerSolution> doCrossover(double probability, IntegerSolution _parent1, IntegerSolution _parent2) {
		// Cast IntegerSolution to PittsburghSolution
		PittsburghSolution parent1 = (PittsburghSolution)_parent1;
		PittsburghSolution parent2 = (PittsburghSolution)_parent2;

		List<IntegerSolution> offspring = new ArrayList<>();

		/* Do crossover */
		if(crossoverRandomGenerator.getRandomValue() < probability) {
			/** Number of rules inherited from parent1.  */
			int N1 = selectRandomGenerator.getRandomValue(0, parent1.getMichiganPopulation().size());
			/** Number of rules inherited from parent2.  */
			int N2 = selectRandomGenerator.getRandomValue(0, parent2.getMichiganPopulation().size());

			// Reduciong excess of rules
			if((N1+N2) > Consts.MAX_RULE_NUM) {
				int delNum = (N1+N2) - Consts.MAX_RULE_NUM;
				for(int i = 0; i < delNum; i++) {
					if(selectRandomGenerator.getRandomValue(0, 1) == 0) {
						N1--;
					}
					else {
						N2--;
					}
				}
			}

			List<IntegerSolution> michiganPopulation = new ArrayList<>();

			// Select inherited rules
			int ruleNum = ((RuleBasedClassifier)parent1.getClassifier()).getRuleNum();
			Integer[] index1 = GeneralFunctions.samplingWithout(ruleNum, N1, Random.getInstance().getGEN());
			ruleNum = ((RuleBasedClassifier)parent2.getClassifier()).getRuleNum();
			Integer[] index2 = GeneralFunctions.samplingWithout(ruleNum, N2, Random.getInstance().getGEN());

			// Inheriting
			for(int i = 0; i < index1.length; i++) {
				int index = index1[i];
				michiganPopulation.add((IntegerSolution)parent1.getMichiganPopulation().get(index).copy());
			}
			for(int i = 0; i < index2.length; i++) {
				int index = index2[i];
				michiganPopulation.add((IntegerSolution)parent2.getMichiganPopulation().get(index).copy());
			}

			PittsburghSolution child = new PittsburghSolution(
					parent1.getBounds(),
					parent1.getNumberOfObjectives(),
					michiganPopulation,
					((RuleBasedClassifier)parent1.getClassifier()).getClassification());
			offspring.clear();
			offspring.add(child);
		}
		/* Don't crossover */
		else {
			offspring.add((IntegerSolution)parent1.copy());
			offspring.add((IntegerSolution)parent2.copy());
			int index = selectRandomGenerator.getRandomValue(0,  1);
			offspring.remove(index);
		}

		return offspring;
	}
}
