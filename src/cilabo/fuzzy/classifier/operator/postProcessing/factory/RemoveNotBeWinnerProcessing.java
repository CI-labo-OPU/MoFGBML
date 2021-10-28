package cilabo.fuzzy.classifier.operator.postProcessing.factory;

import java.util.HashMap;
import java.util.Map;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.rule.Rule;

public class RemoveNotBeWinnerProcessing extends SimplePostProcessing {

	private DataSet data;

	public RemoveNotBeWinnerProcessing(DataSet data) {
		this.data = data;
	}

	public Classifier postProcess(Classifier classifier) {
		super.postProcess(classifier);

		RuleBasedClassifier ruleset = (RuleBasedClassifier)classifier;
		int ruleNum = ruleset.getRuleNum();
		Map<String, Boolean> map = new HashMap<>();
		for(int i = 0; i < ruleNum; i++) {
			map.put(ruleset.getRule(i).toString(), false);
		}

		// Classification
		for(int i = 0; i < data.getDataSize(); i++) {
			Pattern pattern = data.getPattern(i);
			Rule winnerRule = ruleset.classify(pattern.getInputVector());
			map.put(winnerRule.toString(), true);
		}

		// Remove
		for(int i = ruleNum-1; i >= 0; i--) {
			Rule rule = ruleset.getRule(i);
			if(!map.get(rule.toString())) {
				ruleset.popRule(i);
			}
		}

		return ruleset;
	}


}
