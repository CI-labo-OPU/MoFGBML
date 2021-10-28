package cilabo.fuzzy.classifier.operator.preProcessing.factory;

import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.operator.preProcessing.PreProcessing;

public class NopPreProcessing implements PreProcessing {

	@Override
	public Classifier preProcess(Classifier classifier) {
		// No operate
		return classifier;
	}

}
