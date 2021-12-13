package cilabo.labo.developing.twostage;

import java.util.ArrayList;
import java.util.Arrays;
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

/**
 * cilabo.gbml.operator.crossover.PittsburghCrossover を参考に作成
 * CrossoverOperatorをimplements
 *
 * 生成される2つの子個体の内、複雑な方を必ず選択
 *
 * cilabo.gbml.operator.crossover.PittsburghCrossoverでは子個体は1つしか作られていないことに注意
 *
 * 親1: {ルールA, ルールB, ルールC, ルールD}
 * 親2: {ルールa, ルールb, ルールc}
 * から子個体作る．
 * -> PittsburghCrossoverでは...
 *     親1から{ルールA, ルールC}を受け継ぐ（ランダムに選択）
 *     親2から{ルールc}を受け継ぐ（ランダムに選択）
 *     -> 子個体Z:{ルールA, ルールC, ルールc}を生成
 *     ※※
 *     ※実際には，受け継いでないルールを集めた
 *     ※子個体Z':{ルールB, ルールD, ルールa, ルールb}も生成されているのと同義
 *     ※-> さらに，子個体Zはルール数3，子個体Z'はルール数4
 *     ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *     ※-> 本研究では，ZとZ'のうちルール数が多い方を子個体として採用する交叉操作を実装したい※
 *     ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *
 */
public class PittsburghCrossoverComplexOriented implements CrossoverOperator<IntegerSolution> {
	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	/** Constructor */
	public PittsburghCrossoverComplexOriented(double crossoverProbability) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghCrossoverComplexOriented(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghCrossoverComplexOriented(
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
					if( N1 > 0 && N2 > 0){
						if(selectRandomGenerator.getRandomValue(0, 1) == 0) {
							N1--;
						}
						else {
							N2--;
						}
					}
					else if(N1 == 0 && N2 > 0) {
						N2--;
					}
					else if(N1 > 0 && N2 == 0) {
						N1--;
					}
					else {
						break;
					}
				}
			}
			// Replenishing lack of number of rules
			if((N1+N2) < Consts.MIN_RULE_NUM) {
				int lackNum = Consts.MIN_RULE_NUM - (N1+N2);
				for(int i = 0; i < lackNum; i++) {
					if( N1 < parent1.getMichiganPopulation().size() &&
						N2 < parent2.getMichiganPopulation().size())
					{
						if(selectRandomGenerator.getRandomValue(0, 1) == 0) {
							N1++;
						}
						else {
							N2++;
						}
					}
					else if(N1 >= parent1.getMichiganPopulation().size() &&
							N2 < parent2.getMichiganPopulation().size()) {
						N2++;
					}
					else if(N1 < parent1.getMichiganPopulation().size() &&
							N2 >= parent2.getMichiganPopulation().size()) {
						N1++;
					}
					else {
						break;
					}
				}
			}

			// Crossover
			List<IntegerSolution> michiganPopulation1 = new ArrayList<>();	// offspring 1
			List<IntegerSolution> michiganPopulation2 = new ArrayList<>();	// offspring 2
			// Select inherited rules for offspring 1
			int ruleNum = ((RuleBasedClassifier)parent1.getClassifier()).getRuleNum();
			Integer[] index1 = GeneralFunctions.samplingWithout(ruleNum, N1, Random.getInstance().getGEN());
			ruleNum = ((RuleBasedClassifier)parent2.getClassifier()).getRuleNum();
			Integer[] index2 = GeneralFunctions.samplingWithout(ruleNum, N2, Random.getInstance().getGEN());

			// Inheriting
			// from parint1
			for(int i = 0; i < parent1.getMichiganPopulation().size(); i++) {
				if(Arrays.asList(index1).contains(i)) {
					michiganPopulation1.add((IntegerSolution)parent1.getMichiganPopulation().get(i).copy());
				}
				else {
					michiganPopulation2.add((IntegerSolution)parent1.getMichiganPopulation().get(i).copy());
				}
			}
			// from parint2
			for(int i = 0; i < parent2.getMichiganPopulation().size(); i++) {
				if(Arrays.asList(index2).contains(i)) {
					michiganPopulation1.add((IntegerSolution)parent2.getMichiganPopulation().get(i).copy());
				}
				else {
					michiganPopulation2.add((IntegerSolution)parent2.getMichiganPopulation().get(i).copy());
				}
			}

			List<IntegerSolution> manyRules;
			if(michiganPopulation1.size() >= michiganPopulation2.size()) {
				manyRules = michiganPopulation1;
			}
			else {
				manyRules = michiganPopulation2;
			}

			PittsburghSolution child = new PittsburghSolution(
										parent1.getBounds(),
										parent1.getNumberOfObjectives(),
										manyRules,
										((RuleBasedClassifier)parent1.getClassifier()).getClassification());
			offspring.clear();
			offspring.add(child);
		}
		/* Don't crossover */
		else {
			offspring.add(parent1.copy());
			offspring.add(parent2.copy());
			int index = selectRandomGenerator.getRandomValue(0,  1);
			offspring.remove(index);
		}

		return offspring;
	}
}
