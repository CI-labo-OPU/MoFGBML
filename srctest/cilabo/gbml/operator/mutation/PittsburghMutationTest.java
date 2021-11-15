package cilabo.gbml.operator.mutation;

import java.io.File;

import org.junit.Test;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.utility.Input;

public class PittsburghMutationTest {
	@Test
	public void testExecute() {
		String sep = File.separator;
		// Load "Iris" dataset
		String dataName = "dataset" + sep + "iris" + sep + "a0_0_iris-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		JMetalRandom.getInstance().setSeed(0);

		//Problem
		MOP1<IntegerSolution> problem = new MOP1<>(train);
		Classification classification = new SingleWinnerRuleSelection();
		problem.setClassification(classification);

		// Solution
		IntegerSolution solution = problem.createSolution();
		String beforeString = solution.toString();
//		System.out.println(beforeString);

		MutationOperator<IntegerSolution> mutation = new PittsburghMutation(problem.getKnowledge(), train);
		mutation.execute(solution);

		String afterString = solution.toString();
//		System.out.println(afterString);
	}

}
