package cilabo.fuzzy.rule.antecedent.factory;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;

public class AllCombinationAntecedentFactory implements AntecedentFactory {
	// ************************************************************
	// Fields
	/**  */
	Knowledge knowledge;

	/** Internal parameter */
	int[][] antecedents;
	private int head = 0;

	// ************************************************************
	// Constructor
	public AllCombinationAntecedentFactory(Knowledge knowledge) {
		this.knowledge = knowledge;
		init();
	}

	// ************************************************************
	// Methods

	public void init() {
		int dimension = knowledge.getDimension();
		int[] fuzzySetNum = new int[dimension];
		int ruleNum = 1;

		for(int i = 0; i < dimension; i++) {
			fuzzySetNum[i] = knowledge.getFuzzySetNum(i);
			ruleNum *= fuzzySetNum[i];
		}

		// Antecedent Part
		antecedents = new int[ruleNum][dimension];
		for(int i = 0; i < dimension; i++) {
			int rule_i = 0;
			int repeatNum = 1;
			int interval = 1;
			int count = 0;
			for(int j = 0; j < i; j++) {
				repeatNum *= fuzzySetNum[j];
			}
			for(int j = i+1; j< dimension; j++) {
				interval *= fuzzySetNum[j];
			}
			for(int j = 0; j < repeatNum; j++) {
				count = 0;
				for(int k = 0; k < fuzzySetNum[i]; k++) {
					for(int l = 0; l < interval; l++) {
						antecedents[rule_i][i] = count;
						rule_i++;
					}
					count++;
				}
			}
		}
	}

	/**
	 *
	 */
	@Override
	public Antecedent create() {
		if(head >= antecedents.length) return null;

		int[] antecedentIndex = this.antecedents[head];
		head++;

		return Antecedent.builder()
						.knowledge(knowledge)
						.antecedentIndex(antecedentIndex)
						.build();
	}

	public void setKnowledge(Knowledge knowledge) {
		this.knowledge = knowledge;
	}

	public Knowledge getKnowledge() {
		return this.knowledge;
	}

	public int getRuleNum() {
		return this.antecedents.length;
	}

	public static AllCombinationAntecedentFactory.AllCombinationAntecedentFactoryBuilder builder() {
		return new AllCombinationAntecedentFactoryBuilder();
	}

	public static class AllCombinationAntecedentFactoryBuilder {
		private Knowledge knowledge;

		AllCombinationAntecedentFactoryBuilder() {}

		public AllCombinationAntecedentFactory.AllCombinationAntecedentFactoryBuilder knowledge(Knowledge knowledge) {
			this.knowledge = knowledge;
			return this;
		}

		/**
		 * @param knowledge : Knowledge
		 */
		public AllCombinationAntecedentFactory build() {
			return new AllCombinationAntecedentFactory(knowledge);
		}
	}

}
