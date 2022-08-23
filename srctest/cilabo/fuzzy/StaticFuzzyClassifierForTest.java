package cilabo.fuzzy;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.ClassifierFactory;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.factory.FuzzyClassifierFactory;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.CFmeanClassification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.classifier.operator.postProcessing.PostProcessing;
import cilabo.fuzzy.classifier.operator.postProcessing.factory.NopPostProcessing;
import cilabo.fuzzy.classifier.operator.postProcessing.factory.SimplePostProcessing;
import cilabo.fuzzy.classifier.operator.preProcessing.PreProcessing;
import cilabo.fuzzy.classifier.operator.preProcessing.factory.NopPreProcessing;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_3;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;
import cilabo.fuzzy.rule.antecedent.factory.AllCombinationAntecedentFactory;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
import cilabo.fuzzy.rule.consequent.factory.MultiLabel_MoFGBML_Learning;

public class StaticFuzzyClassifierForTest {

	public static RuleBasedClassifier makeSingleLabelClassifier(DataSet train) {
		int dimension = train.getNdim();
		float[][] params = HomoTriangle_3.getParams();
		HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();

		PreProcessing preProcessing = new NopPreProcessing();

		AntecedentFactory antecedentFactory = AllCombinationAntecedentFactory.builder()
												.knowledge(Knowledge.getInstace())
												.build();
		int ruleNum = ((AllCombinationAntecedentFactory)antecedentFactory).getRuleNum();

		ConsequentFactory consequentFactory = MoFGBML_Learning.builder()
												.train(train)
												.build();

		PostProcessing postProcessing = new SimplePostProcessing();

		Classification classification = new SingleWinnerRuleSelection();

		ClassifierFactory factory = FuzzyClassifierFactory.builder()
										.preProcessing(preProcessing)
										.antecedentFactory(antecedentFactory)
										.consequentFactory(consequentFactory)
										.postProcessing(postProcessing)
										.classification(classification)
										.train(train)
										.ruleNum(ruleNum)
										.build();

		RuleBasedClassifier classifier = (RuleBasedClassifier)factory.create();
		return classifier;
	}

	public static RuleBasedClassifier makeMultiLabelClassifier(DataSet train) {
		int dimension = train.getNdim();
		float[][] params = HomoTriangle_2_3_4_5.getParams();
		HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();

		PreProcessing preProcessing = new NopPreProcessing();

		AntecedentFactory antecedentFactory = AllCombinationAntecedentFactory.builder()
												.knowledge(Knowledge.getInstace())
												.build();
		int ruleNum = ((AllCombinationAntecedentFactory)antecedentFactory).getRuleNum();

		ConsequentFactory consequentFactory = MultiLabel_MoFGBML_Learning.builder()
												.train(train)
												.build();

		PostProcessing postProcessing = new NopPostProcessing();

		Classification classification = new CFmeanClassification();

		ClassifierFactory factory = FuzzyClassifierFactory.builder()
										.preProcessing(preProcessing)
										.antecedentFactory(antecedentFactory)
										.consequentFactory(consequentFactory)
										.postProcessing(postProcessing)
										.classification(classification)
										.train(train)
										.ruleNum(ruleNum)
										.build();

		RuleBasedClassifier classifier = (RuleBasedClassifier)factory.create();
		return classifier;
	}
}
