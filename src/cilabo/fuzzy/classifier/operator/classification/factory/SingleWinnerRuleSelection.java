package cilabo.fuzzy.classifier.operator.classification.factory;

import java.util.List;

import cilabo.data.InputVector;
import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.rule.RejectedRule;
import cilabo.fuzzy.rule.Rule;

public class SingleWinnerRuleSelection implements Classification {
	// ************************************************************
	// Fields

	// ************************************************************
	// Constructor

	// ************************************************************
	// Methods

	@Override
	public Rule classify(Classifier classifier, InputVector vector) {
		if(classifier.getClass() != RuleBasedClassifier.class) return null;

		List<Rule> ruleSet = ((RuleBasedClassifier)classifier).getRuleSet();

		boolean canClassify = true;
		double max = -Double.MAX_VALUE;
		int winner = 0;
		for(int q = 0; q < ruleSet.size(); q++) {
			Rule rule = ruleSet.get(q);
			double membership = rule.getAntecedent().getCompatibleGrade(vector.getVector());
			double CF = rule.getConsequent().getRuleWeight().getRuleWeight();

			double value = membership * CF;
			if(value > max) {
				max = value;
				winner = q;
				canClassify = true;
			}
			else if(value == max) {
				Rule winnerRule = ruleSet.get(winner);
				// "membership*CF"が同値 かつ 結論部クラスが異なる
				if(!rule.getConsequent().getClassLabel().toString().equals(winnerRule.getConsequent().getClassLabel().toString())) {
					canClassify = false;
				}
			}
		}

		if(canClassify && max > 0) {
			return ruleSet.get(winner);
		}
		else {
			return RejectedRule.getInstance();
		}
	}

	@Override
	public String toString() {
		return this.getClass().toString();
	}

}
