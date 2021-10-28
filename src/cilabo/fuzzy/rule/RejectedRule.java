package cilabo.fuzzy.rule;

import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.RejectedClassLabel;
import cilabo.fuzzy.rule.consequent.RuleWeight;

public class RejectedRule extends Rule {
	private static RejectedRule instance;

	public RejectedRule(Antecedent antecedent, Consequent consequent) {
		super(antecedent, consequent);
	}

	public static RejectedRule getInstance() {
		if(instance == null) {
			int[] nullIndex = new int[] {};
			Antecedent nullAntecedent = new Antecedent(null, nullIndex);
			RuleWeight nullWeight = new RuleWeight();
			Consequent consequent = new Consequent(RejectedClassLabel.getInstance(), nullWeight);
			instance = new RejectedRule(nullAntecedent, consequent);
		}
		return instance;
	}

	@Override
	public String toString() {
		return "Rejected";
	}
}
