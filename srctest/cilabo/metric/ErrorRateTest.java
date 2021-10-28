package cilabo.metric;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.StaticFuzzyClassifierForTest;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.utility.Input;

public class ErrorRateTest {
	@Test
	public void testMetric1() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern1.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeSingleLabelClassifier(train);

		Metric errorRate = new ErrorRate();

		double expected = (double)errorRate.metric(classifier, train);

		double diff = 0.006;
		assertEquals(expected, 100 - 91.67, diff);
	}

	@Test
	public void testMetric2() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern2.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeSingleLabelClassifier(train);

		Metric errorRate = new ErrorRate();

		double expected = (double)errorRate.metric(classifier, train);

		double diff = 0.006;
		assertEquals(expected, 100 - 90.00, diff);
	}
}
