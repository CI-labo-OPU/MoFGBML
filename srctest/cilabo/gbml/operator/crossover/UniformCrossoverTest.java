package cilabo.gbml.operator.crossover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.gbml.problem.impl.michigan.ProblemMichiganFGBML;
import cilabo.utility.Input;

// don't using JUnit
public class UniformCrossoverTest {
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

		// Parents
		IntegerSolution parent1 = problem.createSolution();
		IntegerSolution parent2 = problem.createSolution();
		String p1 = parent1.toString();
		String p2 = parent2.toString();
		System.out.println("[original]");
		System.out.print(p1);
		System.out.println(p2);

		List<IntegerSolution> solutions = new ArrayList<>();
		solutions.add(parent1);
		solutions.add(parent2);

		// Case 1: don't crossover
		System.out.println("[Case 1: don't crossover]");
		double probability = 0.0;
		UniformCrossover crossover = new UniformCrossover(probability);
		List<IntegerSolution> offspring = crossover.execute(solutions);
		System.out.print(p1);
		System.out.print(p2);
		System.out.println(offspring.get(0));
//		assertEquals(p1, offspring.get(0).toString());

		// Case 2: do fully crossover
		System.out.println("[Case 2: do fully crossover]");
		probability = 1.0;
		crossover = new UniformCrossover(probability);
		offspring = crossover.execute(solutions);
		System.out.print(p1);
		System.out.print(p2);
		System.out.println(offspring.get(0));
	}
}
