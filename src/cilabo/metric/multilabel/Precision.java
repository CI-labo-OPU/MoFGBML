package cilabo.metric.multilabel;

import cilabo.data.DataSet;
import cilabo.data.InputVector;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.metric.Metric;

public class Precision implements Metric {
	// ************************************************************
	// Fields

	// ************************************************************
	// Constructor

	// ************************************************************
	// Methods

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

		double precision = 0;
		for(int p = 0; p < size; p++) {
			InputVector vector = dataset.getPattern(p).getInputVector();
			Integer[] trueClass = dataset.getPattern(p).getTrueClass().getClassVector();

			Integer[] classifiedClass = classifier.classify(vector)
					.getConsequent().getClassLabel()
					.getClassVector();

			precision += PrecisionMetric(classifiedClass, trueClass);
		}
		return 100.0 * precision/size;
	}


	/**
	 * <h1>Calculate Precision for Multi-Label Classification</h1>
	 * @param classified : Integer[]
	 * @param trueClass : Integer[]
	 * @return double : Precision
	 */
	public static double PrecisionMetric(Integer[] classified, Integer[] trueClass) {
		double correctAssociate = 0.0;		//Number of "1"s in both trueClass and classifiedClass;
		double classifiedAssociate = 0.0;	//Number of "1"s in classifiedClass;
		for(int i = 0; i < classified.length; i++) {
			if(classified[i] == 1 && trueClass[i] == 1) {
				correctAssociate++;
			}
			if(classified[i] == 1) {
				classifiedAssociate++;
			}
		}
		if(classifiedAssociate == 0) {
			return 0;
		}
		return correctAssociate / classifiedAssociate;
	}

}
