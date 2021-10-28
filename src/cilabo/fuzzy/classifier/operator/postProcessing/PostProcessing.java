package cilabo.fuzzy.classifier.operator.postProcessing;

import cilabo.fuzzy.classifier.Classifier;

public interface PostProcessing {
	public Classifier postProcess(Classifier classifier);
}
