package cilabo.metric;

import cilabo.data.ClassLabel;
import cilabo.data.DataSet;
import cilabo.data.InputVector;
import cilabo.fuzzy.classifier.RuleBasedClassifier;

public class ErrorRate implements Metric {
	// ************************************************************
	// Fields

	/**  */

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

		double error = 0;
		for(int p = 0; p < size; p++) {
			InputVector vector = dataset.getPattern(p).getInputVector();
			ClassLabel trueClass = dataset.getPattern(p).getTrueClass();

			ClassLabel classifiedClass = classifier.classify(vector).getConsequent().getClassLabel();

			if( !trueClass.toString().equals( classifiedClass.toString() ) ) {
				error += 1;
			}
		}
		return 100.0 * error/size;
	}


}
