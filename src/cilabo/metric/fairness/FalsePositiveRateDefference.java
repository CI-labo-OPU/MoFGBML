package cilabo.metric.fairness;

import cilabo.data.ClassLabel;
import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.labo.developing.fairness.FairnessPattern;
import cilabo.metric.Metric;

public class FalsePositiveRateDefference implements Metric {

	/**
	 * @param classifier : FuzzyClassifier
	 * @param dataset : DataSet
	 * @return Double
	 */
	@Override
	public Double metric(Object... objects) {
		RuleBasedClassifier classifier = null;
		DataSet dataset = null;
		for(Object object : objects) {
			if(object.getClass() == RuleBasedClassifier.class) {
				classifier = (RuleBasedClassifier)object;
			}
			else if(object.getClass() == DataSet.class) {
				dataset = (DataSet)object;
			}
			else {
				(new IllegalArgumentException()).printStackTrace();
				return null;
			}
		}

		if(classifier != null && dataset != null) {
			return metric(classifier, dataset);
		}
		else {
			return null;
		}
	}

	public Double metric(RuleBasedClassifier classifier, DataSet dataset) {
		double size = dataset.getDataSize();
		double[] sizeForSensitive = new double[2];
		double[] countForSensitive = new double[2];

		for(int p = 0; p < size; p++) {
			FairnessPattern pattern = (FairnessPattern)dataset.getPattern(p);
			ClassLabel trueClass = pattern.getTrueClass();

			// "y = 0"でなければ次のパターン
			if(trueClass.getClassLabel() != 0) {
				continue;
			}

			// Sensitive attribute value
			int a = pattern.getA();
			sizeForSensitive[a]++;

			// Classification
			ClassLabel classifiedClass = classifier.classify(pattern.getInputVector()).getConsequent().getClassLabel();

			// "y^ = 1"を判定
			if(classifiedClass.getClassLabel() == 1) {
				countForSensitive[a]++;
			}
		}

		double[] P_a = new double[2];
		for(int i = 0; i < P_a.length; i++) {
			P_a[i] = countForSensitive[i] / sizeForSensitive[i];
		}

		double FPR_diff = Math.abs(P_a[0] - P_a[1]);
		return FPR_diff;
	}

}
