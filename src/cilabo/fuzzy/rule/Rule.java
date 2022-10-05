package cilabo.fuzzy.rule;

import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;

public class Rule implements InterfaceRule {
	// ************************************************************
	// Fields

	/** */
	Antecedent antecedent;
	/** */
	Consequent consequent;

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

	public static RuleBuilder builder() {
		return new RuleBuilder();
	}

	public static class RuleBuilder {
		private Antecedent antecedent;
		private Consequent consequent;

		RuleBuilder() {}

		public Rule.RuleBuilder antecedent(Antecedent antecedent) {
			this.antecedent = antecedent;
			return this;
		}

		public Rule.RuleBuilder consequent(Consequent consequent) {
			this.consequent = consequent;
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
