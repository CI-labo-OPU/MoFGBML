package cilabo.fuzzy.classifier.operator.classification;

import cilabo.data.InputVector;
import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.rule.InterfaceRule;

public interface Classification {

	public InterfaceRule classify(Classifier classifier, InputVector vector);
}
