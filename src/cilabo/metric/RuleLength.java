package cilabo.metric;

import cilabo.fuzzy.classifier.RuleBasedClassifier;

public class RuleLength implements Metric {
	// ************************************************************
	// Fields

	// ************************************************************
	// Constructor

	// ************************************************************
	// Methods

	/**
	 * @param classifier : FuzzyClassifier
	 * @return Integer
	 */
	@Override
	public Integer metric(Object...objects) {
		if(objects[0].getClass() == RuleBasedClassifier.class) {
			RuleBasedClassifier classifier = (RuleBasedClassifier)objects[0];
			return metric(classifier);
		}
		else {
			(new IllegalArgumentException()).printStackTrace();
			return null;
		}
	}

	public Integer metric(RuleBasedClassifier classifier) {
		return classifier.getRuleLength();
	}
}
