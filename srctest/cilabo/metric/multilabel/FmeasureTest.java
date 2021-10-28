package cilabo.metric.multilabel;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.StaticFuzzyClassifierForTest;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.metric.Metric;
import cilabo.utility.Input;

public class FmeasureTest {
	@Test
	public void testMetric() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "richromatic" + sep + "a0_0_richromatic-10tra.dat";
		DataSet train = new DataSet();
		Input.inputMultiLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeMultiLabelClassifier(train);

		// Fmeasure
		Metric metric = new Fmeasure();
		double expected = (double)metric.metric(classifier, train);
		double diff = 0.006;
		assertEquals(expected, 97.09401709401708, diff);

		// Precision
		metric = new Precision();
		expected = (double)metric.metric(classifier, train);
		assertEquals(expected, 97.77777777777777, diff);

		// Recall
		metric = new Recall();
		expected = (double)metric.metric(classifier, train);
		assertEquals(expected, 96.41975308641972, diff);
	}

}
