package cilabo.metric.multilabel;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.StaticFuzzyClassifierForTest;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.metric.Metric;
import cilabo.utility.Input;

public class HammingLossTest {
	@Test
	public void testMetric1() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "richromatic" + sep + "a0_0_richromatic-10tra.dat";
		DataSet train = new DataSet();
		Input.inputMultiLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeMultiLabelClassifier(train);

		Metric errorRate = new HammingLoss();

		double expected = (double)errorRate.metric(classifier, train);
		double diff = 0.006;
		assertEquals(expected, 3.950617283950618, diff);
	}
}
