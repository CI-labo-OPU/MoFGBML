package cilabo.metric;

import cilabo.data.ClassLabel;
import cilabo.data.DataSet;
import cilabo.data.InputVector;
import cilabo.fuzzy.classifier.RuleBasedClassifier;

public class Gmean implements Metric {


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

	/**
	 * 2クラス問題を前提
	 * @param classifier
	 * @param dataset
	 * @return
	 */
	public Double metric(RuleBasedClassifier classifier, DataSet dataset) {
		double size = dataset.getDataSize();
		double[] sizeForClass = new double[2];

		double[] correctForClass = new double[2];

		for(int p = 0; p < size; p++) {
			InputVector vector = dataset.getPattern(p).getInputVector();
			ClassLabel trueClass = dataset.getPattern(p).getTrueClass();

			sizeForClass[trueClass.getClassLabel()]++;

			ClassLabel classifiedClass = classifier.classify(vector).getConsequent().getClassLabel();

			if(trueClass.toString().equals(classifiedClass.toString())) {
				correctForClass[classifiedClass.getClassLabel()]++;
			}
		}

		// 各クラスの正解率を計算
		double[] P = new double[2];
		for(int i = 0; i < P.length; i++) {
			P[i] = correctForClass[i] / sizeForClass[i];
		}

		double gmean = Math.sqrt(P[0] * P[1]);
		return gmean;
	}



}
