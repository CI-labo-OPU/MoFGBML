package cilabo.gbml.operator.crossover;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.gbml.problem.impl.michigan.ProblemMichiganFGBML;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.gbml.solution.util.attribute.ErroredPatternsAttribute;
import cilabo.main.Consts;
import cilabo.utility.Input;

public class HybridGBMLcrossoverTest {
	@Test
	public void testMichiganX() {
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
		IntegerSolution parent = problem.createSolution();
		List<IntegerSolution> solutions = new ArrayList<>();
		solutions.add(parent);

		// Michigan Evaluation
		ProblemMichiganFGBML<IntegerSolution> michiganProblem = new ProblemMichiganFGBML<>(Consts.RAND_SEED, train);
		michiganProblem.michiganEvaluate(((PittsburghSolution)parent).getMichiganPopulation());
		// Add errored patterns to parents
		List<Integer> erroredPatternsId = new ArrayList<>();
		erroredPatternsId.add(0);
		erroredPatternsId.add(2);
		parent.setAttribute((new ErroredPatternsAttribute<>()).getAttributeId(), erroredPatternsId);

		/* Michigan operation*/
		MichiganOperation michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT,
															Knowledge.getInstace(),
				 											problem.getConsequentFactory());
		String doCrossover = michiganX.doCrossover(1.0, parent).get(0).toString();

//		System.out.println("doCrossover");
//		System.out.println(doCrossover);
	}

	@Test
	public void testExecute() {
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
		IntegerSolution parent1 = problem.createSolution();
		IntegerSolution parent2 = problem.createSolution();
		List<IntegerSolution> solutions = new ArrayList<>();
		solutions.add(parent1);
		solutions.add(parent2);

		// Michigan Evaluation
		ProblemMichiganFGBML<IntegerSolution> michiganProblem = new ProblemMichiganFGBML<>(Consts.RAND_SEED, train);
		michiganProblem.michiganEvaluate(((PittsburghSolution)parent1).getMichiganPopulation());
		michiganProblem.michiganEvaluate(((PittsburghSolution)parent2).getMichiganPopulation());

		// Add errored patterns to parents
		List<Integer> erroredPatternsId = new ArrayList<>();
		erroredPatternsId.add(0);
		erroredPatternsId.add(2);
		parent1.setAttribute((new ErroredPatternsAttribute<>()).getAttributeId(), erroredPatternsId);
		parent2.setAttribute((new ErroredPatternsAttribute<>()).getAttributeId(), erroredPatternsId);

		// Crossover
		double probability = 1.0;
		double michiganOperationProbability = Consts.MICHIGAN_OPE_RT;
//		double michiganOperationProbability = 1.0;	// Only Michigan operation: OK
//		double michiganOperationProbability = 0.0;	// Only Pittsburgh operation: OK

		/* Michigan operation*/
		CrossoverOperator<IntegerSolution> michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT,
																			Knowledge.getInstace(),
				 															problem.getConsequentFactory());
		/* Pittsburgh operation */
		CrossoverOperator<IntegerSolution> pittsburghX = new PittsburghCrossover(Consts.PITTSBURGH_CROSS_RT);
		/* Hybrid-style crossover */
		HybridGBMLcrossover crossover = new HybridGBMLcrossover(probability, michiganOperationProbability,
																michiganX, pittsburghX);

		List<IntegerSolution> offspring = crossover.execute(solutions);
//		System.out.println(offspring);

		michiganProblem.michiganEvaluate(((PittsburghSolution)offspring.get(0)).getMichiganPopulation());
//		System.out.println();
	}
}
