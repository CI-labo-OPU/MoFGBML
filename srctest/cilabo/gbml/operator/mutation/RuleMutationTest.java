package cilabo.gbml.operator.mutation;

import java.io.File;

import org.junit.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.gbml.problem.impl.michigan.ProblemMichiganFGBML;
import cilabo.utility.Input;

public class RuleMutationTest {

	@Test
	public void testExecute() {
		String sep = File.separator;
		// Load "Pima" dataset
		String dataName = "dataset" + sep + "pima" + sep + "a0_0_pima-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		// Problem
		int seed = 0;
		Problem<IntegerSolution> problem = new ProblemMichiganFGBML<>(seed, train);
		Knowledge knowledge = ((ProblemMichiganFGBML<?>)problem).getKnowledge();

		// Solution
		IntegerSolution solution = problem.createSolution();
		System.out.println("[original]");
		System.out.println(solution.toString());

		// Operator
		double mutationProbability = 1.0 / (double)train.getDataSize();
		mutationProbability = 1.0;
		MichiganMutation mutation = new MichiganMutation(mutationProbability, knowledge, train);
		mutation.execute(solution);
		System.out.println("[new solution]");
		System.out.println(solution.toString());
	}
}
