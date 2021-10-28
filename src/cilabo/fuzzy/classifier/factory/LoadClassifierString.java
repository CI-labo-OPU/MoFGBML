package cilabo.fuzzy.classifier.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import cilabo.data.ClassLabel;
import cilabo.fuzzy.classifier.ClassifierFactory;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.Rule;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.RuleWeight;
import cilabo.utility.GeneralFunctions;

public class LoadClassifierString implements ClassifierFactory {
	// ************************************************************
	// Fields
	String classifierString;

	Knowledge knowledge;

	// ************************************************************
	// Constructor
	public LoadClassifierString(String classifierString, Knowledge knowledge) {
		this.classifierString = classifierString;
		this.knowledge = knowledge;
	}

	// ************************************************************
	// Methods

	private String[] split(String original) {
		// Uniform line separator to this system's line separator
		original = GeneralFunctions.uniformLineSeparator(original);

		// Split by line separator
		String ln = System.lineSeparator();
		return original.split(ln);
	}

	private Classification getClassification(String classificationClass) {
		Classification classification = null;
		try {
			classification = (Classification)Class.forName(classificationClass).getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return classification;
	}

	private Antecedent getAntecedent(String string) {
		String[] array = string.split(",");
		int[] antecedentIndex = new int[array.length];
		for(int i = 0; i < antecedentIndex.length; i++) {
			antecedentIndex[i] = Integer.parseInt(array[i]);
		}
		return Antecedent.builder()
						.knowledge(knowledge)
						.antecedentIndex(antecedentIndex)
						.build();
	}

	private ClassLabel getClassLabel(String string) {
		String[] array = string.split(",");
		ClassLabel classLabel = new ClassLabel();
		for(int i = 0; i < array.length; i++) {
			classLabel.addClassLabel(Integer.parseInt(array[i]));
		}
		return classLabel;
	}

	private RuleWeight getRuleWeight(String string) {
		String[] array = string.split(",");
		RuleWeight ruleWeight = new RuleWeight();
		for(int i = 0; i < array.length; i++) {
			ruleWeight.addRuleWeight(Double.parseDouble(array[i]));
		}
		return ruleWeight;
	}

	@Override
	public RuleBasedClassifier create() {
		RuleBasedClassifier classifier = new RuleBasedClassifier();

		String[] array = split(classifierString);
		ArrayList<String> lines = new ArrayList<>();
		lines.addAll(Arrays.asList(array));

		// 1st row : Classification class
		String header = lines.remove(0);
		String classificationClass = header.split(" ")[2];
		Classification classification = getClassification(classificationClass);
		classifier.setClassification(classification);

		// Remind rows : Fuzzy Rules
		int ruleNum = lines.size();
		for(int i = 0; i < ruleNum; i++) {
			String line = lines.get(i);
			line = line .replaceAll(" *", "")
					.replace("If", "")
					.replace("Then", "")
					.replace("class:", "")
					.replace("weight:", "")
					.replaceAll("^\\[", "")
					.replaceAll("\\]$", "");
			String[] params = line.split("\\]\\[");

			Antecedent antecedent = getAntecedent(params[0]);
			ClassLabel classLabel = getClassLabel(params[1]);
			RuleWeight ruleWeight = getRuleWeight(params[2]);
			Consequent consequent = Consequent.builder()
					.consequentClass(classLabel)
					.ruleWeight(ruleWeight)
					.build();
			Rule fuzzyRule = Rule.builder()
					.antecedent(antecedent)
					.consequent(consequent)
					.build();

			classifier.addRule(fuzzyRule);
		}
		return classifier;
	}

	public static LoadClassifierStringBuilder builder() {
		return new LoadClassifierStringBuilder();
	}

	public static class LoadClassifierStringBuilder {
		private String classifierString;
		private Knowledge knowledge;

		LoadClassifierStringBuilder() {}

		public LoadClassifierString.LoadClassifierStringBuilder classifierString(String classifierString) {
			this.classifierString = classifierString;
			return this;
		}

		public LoadClassifierString.LoadClassifierStringBuilder knowledge(Knowledge knowledge) {
			this.knowledge = knowledge;
			return this;
		}

		/**
		 * @param classifierString : String
		 * @param knowledge : Knowledge
		 */
		public LoadClassifierString build() {
			return new LoadClassifierString(classifierString, knowledge);
		}
	}

}
