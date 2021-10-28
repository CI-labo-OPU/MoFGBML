package cilabo.fuzzy.classifier.factory;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.StaticFuzzyClassifierForTest;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.knowledge.Knowledge;

public class LoadClassifierStringTest {
	@Test
	public void testLoadCreate() {
		DataSet train = FuzzyClassifierFactoryTest.makeTestTrain();
		Knowledge knowledge = FuzzyClassifierFactoryTest.makeTestKnowledge();
		RuleBasedClassifier classifier = StaticFuzzyClassifierForTest.makeSingleLabelClassifier(train);

		RuleBasedClassifier newClassifier = LoadClassifierString.builder()
											.classifierString(classifier.toString())
											.knowledge(knowledge)
											.build()
											.create();

		assertEquals(newClassifier.toString(), classifier.toString());
	}

}
