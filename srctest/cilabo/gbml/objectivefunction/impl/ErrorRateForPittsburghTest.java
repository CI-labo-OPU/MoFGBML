package cilabo.gbml.objectivefunction.impl;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.rule.Rule;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.utility.Input;

public class ErrorRateForPittsburghTest {
	@Test
	public void functionTest() {
		JMetalRandom.getInstance().setSeed(0);

		String sep = File.separator;
		// Load "Iris" dataset
		String dataName = "dataset" + sep + "iris" + sep + "a0_0_iris-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		//Problem
		MOP1<IntegerSolution> problem = new MOP1<>(train);
		Classification classification = new SingleWinnerRuleSelection();
		problem.setClassification(classification);

		// Parents
		PittsburghSolution solution = problem.createSolution();

		// Classifier
		RuleBasedClassifier classifier = (RuleBasedClassifier)solution.getClassifier();

		double rate = evaluateErrorRate(train, classifier);

		ErrorRateForPittsburgh function = new ErrorRateForPittsburgh(train);
		double test = function.function(solution);

		double delta = 0.0001;
		assertEquals(rate, test, delta);
	}


	public static double evaluateErrorRate(DataSet dataset, RuleBasedClassifier classifier) {
		double correct = 0;
		for(int i = 0; i < dataset.getDataSize(); i++) {
			Pattern pattern = dataset.getPattern(i);
			Rule winnerRule = classifier.classify(pattern.getInputVector());
			if(winnerRule == null) continue;
			if(pattern.getTrueClass().toString().equals(winnerRule.getConsequent().getClassLabel().toString())) {
				correct += 1;
			}
		}
		double rate = 1 - (correct / (double)dataset.getDataSize());
		return rate;
	}
}
