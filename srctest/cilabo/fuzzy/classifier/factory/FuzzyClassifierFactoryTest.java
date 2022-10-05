package cilabo.fuzzy.classifier.factory;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.StaticFuzzyClassifierForTest;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_3;
import cilabo.fuzzy.rule.Rule;
import cilabo.utility.Input;

public class FuzzyClassifierFactoryTest {

	public static DataSet makeTestTrain() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern1.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		return train;
	}

	public static Knowledge makeTestKnowledge() {
		DataSet train = makeTestTrain();

		int dimension = train.getNdim();
		float[][] params = HomoTriangle_3.getParams();
		HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();
		return Knowledge.getInstance();
	}



	public double evaluateCorrectRate(DataSet dataset, RuleBasedClassifier classifier) {
		double correct = 0;
		for(int i = 0; i < dataset.getDataSize(); i++) {
			Pattern pattern = dataset.getPattern(i);
			Rule winnerRule = classifier.classify(pattern.getInputVector());
			if(winnerRule == null) continue;
			if(pattern.getTrueClass().toString().equals(winnerRule.getConsequent().getClassLabel().toString())) {
				correct += 1;
			}
		}
		double rate = 100 * (correct / (double)dataset.getDataSize());
		return rate;
	}

	@Test
	public void testCreateWithKadai5_1() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern1.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeSingleLabelClassifier(train);

		// RuleNum
		assertEquals(classifier.getRuleNum(), 13);

		//RuleLength
		assertEquals(classifier.getRuleLength(), 22);

		// Classification rate
		double diff = 0.006;
		assertEquals(evaluateCorrectRate(train, classifier), 91.67, diff);
	}

	@Test
	public void testCreateWithKadai5_2() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern2.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeSingleLabelClassifier(train);

		// RuleNum
		assertEquals(classifier.getRuleNum(), 12);

		//RuleLength
		assertEquals(classifier.getRuleLength(), 20);

		// Classification rate
		double diff = 0.006;
		assertEquals(evaluateCorrectRate(train, classifier), 90.00, diff);
	}
}
