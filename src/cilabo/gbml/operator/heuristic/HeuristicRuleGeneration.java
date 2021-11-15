package cilabo.gbml.operator.heuristic;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.Operator;

import cilabo.data.Pattern;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.main.Consts;
import cilabo.utility.Random;

/**
 * 誤識別パターン集合を受けとり，ヒューリスティクルール生成法によって生成された前件部を返す.
 *
 */
public class HeuristicRuleGeneration implements Operator<List<Pattern>, List<Antecedent>>{
	Knowledge knowledge;

	public HeuristicRuleGeneration(Knowledge knowledge) {
		this.knowledge = knowledge;
	}


	@Override
	public List<Antecedent> execute(List<Pattern> erroredPatterns) {
		List<Antecedent> generatedRules = new ArrayList<>();
		for(int i = 0; i < erroredPatterns.size(); i++) {
			generatedRules.add(heuristicRuleGeneration(erroredPatterns.get(i)));
		}
		return generatedRules;
	}

	public Antecedent heuristicRuleGeneration(Pattern pattern) {
		/** Number of attribute. */
		int dimension = pattern.getInputVector().getVector().length;
		/** Ratio of don't care
		 *  (dimension - const) / dimension */
		double RatioOfDontCare;
		/**TODO 定数(ANTECEDENT_LEN)がdimensionより大きい場合，ドントケア確率が実質0となることをケアする必要がある．**/
//		if(Consts.ANTECEDENT_LEN > dimension) {
//			RatioOfDontCare = Consts.DONT_CARE_RT;
//		}
//		else {
//			/*  (dimension - const) / dimension */
//			RatioOfDontCare = (double)(((double)dimension - (double)Consts.ANTECEDENT_LEN)/(double)dimension);
//		}
		RatioOfDontCare = (double)(((double)dimension - (double)Consts.ANTECEDENT_LEN)/(double)dimension);

		/* Select fuzzy sets */
		int[] antecedentIndex = new int[dimension];
		for(int n = 0; n < dimension; n++) {
			if(Random.getInstance().getGEN().nextDouble() < RatioOfDontCare) {
				antecedentIndex[n] = 0;
			}
			else {
				//Categorical Dimension
				if(pattern.getDimValue(n) < 0) {
					antecedentIndex[n] = (int)pattern.getDimValue(n);
				}
				//Numerical Dimension
				else {
					double[] membershipValueRoulette = new double[knowledge.getFuzzySetNum(n)];
					double sumMembershipValue = 0.0;

					// Make roulette
					membershipValueRoulette[0] = 0.0;
					for(int f = 1; f < knowledge.getFuzzySetNum(n); f++) {
						sumMembershipValue += knowledge.getMembershipValue(pattern.getDimValue(n), n, f);
						membershipValueRoulette[f] = sumMembershipValue;
					}

					// Select fuzzy set
					double arrow = Random.getInstance().getGEN().nextDouble() * sumMembershipValue;
					for(int f = 0; f < knowledge.getFuzzySetNum(n); f++) {
						if(arrow < membershipValueRoulette[f]) {
							antecedentIndex[n] = f;
							break;
						}
					}

				}
			}
		}

		return Antecedent.builder()
							.knowledge(knowledge)
							.antecedentIndex(antecedentIndex)
							.build();
	}

	public Knowledge getKnowledge() {
		return this.knowledge;
	}

}
