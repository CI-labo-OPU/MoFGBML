package cilabo.gbml.solution.util;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.utility.Input;

public class EqualsSolutionTest {
	@Test
	public void testEquals() {
		String sep = File.separator;
		// Load "Iris" dataset
		String dataName = "dataset" + sep + "iris" + sep + "a0_0_iris-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		JMetalRandom.getInstance().setSeed(20);

		//Problem
		MOP1<IntegerSolution> problem = new MOP1<>(train);
		Classification classification = new SingleWinnerRuleSelection();
		problem.setClassification(classification);

		// Parents
		IntegerSolution solution = problem.createSolution();
		Solution<Integer> copy = solution.copy();
		copy.setObjective(0, 1);
		Solution<Integer> change = solution.copy();
		change.setVariable(0, 1+change.getVariable(0));

		assertTrue(EqualsSolution.equals(solution, copy));
		assertTrue(!EqualsSolution.equals(solution, change));

	}
}
