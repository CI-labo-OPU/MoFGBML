package cilabo.fuzzy.classifier.operator.postProcessing.factory;

import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.operator.postProcessing.PostProcessing;

public class NopPostProcessing implements PostProcessing {
	@Override
	public Classifier postProcess(Classifier classifier) {
		// No operate
		return classifier;
	}
}
