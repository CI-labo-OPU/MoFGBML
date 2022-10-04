package cilabo.fuzzy.rule;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;

public class Rule implements InterfaceRule {
	// ************************************************************
	// Fields

	/** */
	protected Antecedent antecedent;
	/** */
	protected Consequent consequent;

	// ************************************************************
	// Constructor

	/**
	 *
	 * @param antecedent : Shallow copy
	 * @param consequent : Shallow copy
	 */
	public Rule(Antecedent antecedent, Consequent consequent) {
		this.antecedent = antecedent;
		this.consequent = consequent;
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	@Override
	public Rule deepcopy() {
		return new Rule(this.antecedent.deepcopy(), this.consequent.deepcopy());
	}

	@Override
	public void setAntecedent(Antecedent antecedent) {
		this.antecedent = antecedent;
	}

	@Override
	public Antecedent getAntecedent() {
		return this.antecedent;
	}

	@Override
	public void setConsequent(Consequent consequent) {
		this.consequent = consequent;
	}

	@Override
	public Consequent getConsequent() {
		return this.consequent;
	}

	@Override
	public String toString() {
		String str = "";
		str += "If [" + this.antecedent.toString() + "] ";
		str += "Then " + this.consequent.toString();
		return str;
	}

	/**
	 * antecedent内のFuzzySetsをindexを基に更新し直します．
	 * 計算量が無駄に増えるのであんま使わない方が良い
	 * @param knowledge
	 */
	public void refreshFuzzySets(Knowledge knowledge) {
		int[] antecedentIndex = this.getAntecedent().getAntecedentIndex();
		for(int i=0; i<antecedentIndex.length; i++) {
			this.antecedent.setAntecedentFuzzySets(i, antecedentIndex[i], knowledge);
		}
	}

	public static RuleBuilder builder() {
		return new RuleBuilder();
	}

	public static class RuleBuilder {
		private Antecedent antecedent;
		private Consequent consequent;

		RuleBuilder() {}

		public Rule.RuleBuilder antecedent(Antecedent antecedent) {
			this.antecedent = antecedent.deepcopy();
			return this;
		}

		public Rule.RuleBuilder consequent(Consequent consequent) {
			this.consequent = consequent.deepcopy();
			return this;
		}

		/**
		 * @param antecedent : Antecedent
		 * @param consequent : Consequent
		 */
		public Rule build() {
			return new Rule(antecedent, consequent);
		}
	}
}
