package cilabo.gbml.solution.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
import cilabo.gbml.problem.impl.michigan.ProblemMichiganFGBML;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.utility.Input;

public class SortMichiganPopulationTest {
	@Test
	public void testRadixSortMichiganSolutionList() {
		String sep = File.separator;

		// Dataset
		String dataName = "dataset" + sep + "iris" + sep + "a0_0_iris-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		// Initialization Knowledge base
		HomoTriangleKnowledgeFactory.builder()
								.dimension(train.getNdim())
								.params(HomoTriangle_2_3.getParams())
								.build()
								.create();
		ConsequentFactory consequentFactory = new MoFGBML_Learning(train);

		// Problem
		int seed = 0;
		Problem<IntegerSolution> problem = new ProblemMichiganFGBML<IntegerSolution>(seed, train);

		// Make Population
		Antecedent[] antecedents = new Antecedent[5];
		antecedents[0] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 0, 0, 5})
				.build();
		antecedents[1] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 0, 5, 4})
				.build();
		antecedents[2] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 0, 5, 1})
				.build();
		antecedents[3] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {5, 0, 0, 0})
				.build();
		antecedents[4] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 5, 0, 0})
				.build();

		List<IntegerSolution> actual = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			Antecedent antecedent = antecedents[i];
			Consequent consequent = consequentFactory.learning(antecedent);
			MichiganSolution solution = new MichiganSolution(((ProblemMichiganFGBML<IntegerSolution>)problem).getBounds(),
													 problem.getNumberOfObjectives(),
													 problem.getNumberOfConstraints(),
													 antecedent,
													 consequent);
			actual.add(solution);
		}

		/* ========================================================== */
		SortMichiganPopulation.radixSort(actual);
		/* ========================================================== */

		// Make Answer sorted Population
		antecedents = new Antecedent[5];
		antecedents[0] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 0, 0, 5})
				.build();
		antecedents[1] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 0, 5, 1})
				.build();
		antecedents[2] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 0, 5, 4})
				.build();
		antecedents[3] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {0, 5, 0, 0})
				.build();
		antecedents[4] = Antecedent.builder()
				.knowledge(Knowledge.getInstance())
				.antecedentIndex(new int[] {5, 0, 0, 0})
				.build();


		List<IntegerSolution> expected = new ArrayList<>();
		for(int i = 0; i < 5; i++) {
			Antecedent antecedent = antecedents[i];
			Consequent consequent = consequentFactory.learning(antecedent);
			MichiganSolution solution = new MichiganSolution(((ProblemMichiganFGBML<IntegerSolution>)problem).getBounds(),
													 problem.getNumberOfObjectives(),
													 problem.getNumberOfConstraints(),
													 antecedent,
													 consequent);
			expected.add(solution);
		}

		assertEquals(expected.toString(), actual.toString());

	}
}
