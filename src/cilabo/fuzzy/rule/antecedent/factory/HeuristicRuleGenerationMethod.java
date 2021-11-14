package cilabo.fuzzy.rule.antecedent.factory;

import java.util.Arrays;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;
import cilabo.main.Consts;
import cilabo.utility.Random;

public class HeuristicRuleGenerationMethod implements AntecedentFactory {
	// ************************************************************
	// Fields

	/**  */
	Knowledge knowledge;

	/**  */
	DataSet train;

	/** */
	Integer[] samplingIndex;

	/** Internal parameter */
	int head = 0;

	// ************************************************************
	// Constructor
	public HeuristicRuleGenerationMethod(Knowledge knowledge, DataSet train, Integer[] samplingIndex) {
		this.knowledge = knowledge;
		this.train = train;
		this.samplingIndex = samplingIndex;
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	@Override
	public Antecedent create() {
		if(head >= samplingIndex.length) return null;

		int dimension = train.getNdim();
		Pattern pattern = train.getPattern(samplingIndex[head]);
		head++;
		double[] vector = pattern.getInputVector().getVector();

		double dcRate;
		if(Consts.IS_PROBABILITY_DONT_CARE) {
			dcRate = Consts.DONT_CARE_RT;
		}
		else {
			// (Ndim - const) / Ndim
			dcRate = (double)(((double)dimension - (double)Consts.ANTECEDENT_LEN)/(double)dimension);
		}

		int[] antecedentIndex = new int[dimension];
		for(int n = 0; n < dimension; n++) {
			// don't care
			if(Random.getInstance().getGEN().nextDouble() < dcRate) {
				antecedentIndex[n] = 0;
				continue;
			}

			// Categorical Judge
			if(vector[n] < 0) {
				antecedentIndex[n] = (int)vector[n];
				continue;
			}

			// Numerical
			int fuzzySetNum = knowledge.getFuzzySetNum(n)-1;
			double[] membershipValueRoulette = new double[fuzzySetNum];
			double sumMembershipValue = 0;
			membershipValueRoulette[0] = 0;
			for(int h = 0; h < fuzzySetNum; h++) {
				sumMembershipValue += knowledge.getMembershipValue(vector[n], n, h+1);
				membershipValueRoulette[h] = sumMembershipValue;
			}

			double arrow = Random.getInstance().getGEN().nextDouble() * sumMembershipValue;
			for(int h = 0; h < fuzzySetNum; h++) {
				if(arrow < membershipValueRoulette[h]) {
					antecedentIndex[n] = h+1;
					break;
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

	public void setSamplingIndex(Integer[] samplingIndex) {
		this.samplingIndex = Arrays.copyOf(samplingIndex, samplingIndex.length);
		this.head = 0;
	}


	public static HeuristicRuleGenerationMethod.HeuristicRuleGenerationMethodBuilder builder() {
		return new HeuristicRuleGenerationMethodBuilder();
	}

	public static class HeuristicRuleGenerationMethodBuilder {
		private Knowledge knowledge;
		private DataSet train;
		private Integer[] samplingIndex;

		HeuristicRuleGenerationMethodBuilder() {}

		public HeuristicRuleGenerationMethod.HeuristicRuleGenerationMethodBuilder knowledge(Knowledge knowledge){
			this.knowledge = knowledge;
			return this;
		}

		public HeuristicRuleGenerationMethod.HeuristicRuleGenerationMethodBuilder samplingIndex(Integer[] samplingIndex) {
			this.samplingIndex = samplingIndex;
			return this;
		}

		public HeuristicRuleGenerationMethod.HeuristicRuleGenerationMethodBuilder train(DataSet train) {
			this.train = train;
			return this;
		}

		/**
		 * @param seed : int
		 * @param knowledge : Knowledge
		 * @param train : DataSet
		 * @param samplingIndex : Integer[]
		 */
		public HeuristicRuleGenerationMethod build() {
			return new HeuristicRuleGenerationMethod(knowledge, train, samplingIndex);
		}
	}
}
