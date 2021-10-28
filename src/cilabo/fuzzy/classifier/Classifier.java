package cilabo.fuzzy.classifier;

import cilabo.data.InputVector;
import cilabo.fuzzy.rule.InterfaceRule;

public interface Classifier {

	public InterfaceRule classify(InputVector vector);

	public Classifier copy();
}
