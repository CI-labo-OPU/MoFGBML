package test;

import java.io.File;
import java.util.ArrayList;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.ClassifierFactory;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.factory.FuzzyClassifierFactory;
import cilabo.fuzzy.classifier.factory.LoadClassifierString;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.classifier.operator.postProcessing.PostProcessing;
import cilabo.fuzzy.classifier.operator.postProcessing.factory.SimplePostProcessing;
import cilabo.fuzzy.classifier.operator.preProcessing.PreProcessing;
import cilabo.fuzzy.classifier.operator.preProcessing.factory.NopPreProcessing;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_3;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;
import cilabo.fuzzy.rule.antecedent.factory.AllCombinationAntecedentFactory;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
import cilabo.utility.GeneralFunctions;
import cilabo.utility.Input;

public class Test {
	public static void main(String[] args) {
//		checkLineSeparator();

//		checkClassifierToString();

		checkAddAll();
	}

	public static void checkAddAll() {
		ArrayList<Double> origin = new ArrayList<>();
		origin.add(1.0);
		origin.add(2.0);


		ArrayList<Double> newInstance = new ArrayList<>();
		newInstance.addAll(origin);

		origin.set(0, 2.0);
	}

	public static void checkClassifierToString() {
		Knowledge knowledge = makeTestKnowledge();
		RuleBasedClassifier classifier = makeTestClassifier();

		String classifierString = classifier.toString();
		classifierString = GeneralFunctions.uniformLineSeparator(classifierString);
		String[] l = classifierString.split(System.lineSeparator());

//		ArrayList<String> lines = new ArrayList<>();
//		lines.addAll(Arrays.asList(l));
//
//		for(int i = 0; i < l.length; i++) {
//			if(!lines.get(i).equals(l[i])) {
//				System.out.println("order is destroyed");
//			}
//		}

//		System.out.println(classifierString);

		ClassifierFactory factory = LoadClassifierString.builder()
									.classifierString(classifierString)
									.knowledge(knowledge)
									.build();
		RuleBasedClassifier newClassifier = (RuleBasedClassifier)factory.create();


	}

	public static void checkLineSeparator() {
		String windows = "\r\n";
		String mac = "\n\r";
		String unix = "\n";

		String ln = windows;

		String a = "Hello" + unix + "world!";
		if(a.contains(windows)) {
			a = a.replace(windows, ln);
		}
		else if(a.contains(mac)) {
			a = a.replace(mac, ln);
		}
		else {
			a = a.replace(unix, ln);
		}

		System.out.print(a);

	}

	private static Knowledge makeTestKnowledge() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern1.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		int dimension = train.getNdim();
		float[][] params = HomoTriangle_3.getParams();
		HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();
		return Knowledge.getInstace();
	}

	private static RuleBasedClassifier makeTestClassifier() {
		String sep = File.separator;
		String dataName = "dataset" + sep + "cilabo" + sep + "kadai5_pattern1.txt";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

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
}












