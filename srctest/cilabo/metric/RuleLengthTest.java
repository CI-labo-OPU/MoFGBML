package cilabo.metric;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.StaticFuzzyClassifierForTest;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.utility.Input;

public class RuleLengthTest {
	@Test
	public void testMetric() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern1.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeSingleLabelClassifier(train);

		Metric ruleLength = new RuleLength();

		int expected = (int)ruleLength.metric(classifier);

		assertEquals(expected, 22);
	}
}
