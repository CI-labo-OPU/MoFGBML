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
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.main.Consts;
import cilabo.utility.Input;

public class HybridGBMLcrossoverTest {
	@Test
	public void testExecute() {
		String sep = File.separator;
		// Load "Pima" dataset
		String dataName = "dataset" + sep + "pima" + sep + "a0_0_pima-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		JMetalRandom.getInstance().setSeed(20);

		//Problem
		MOP1<IntegerSolution> problem = new MOP1<>(Consts.RAND_SEED, train);
		Classification classification = new SingleWinnerRuleSelection();
		problem.setClassification(classification);

		// Parents
		IntegerSolution parent1 = problem.createSolution();
		IntegerSolution parent2 = problem.createSolution();
		List<IntegerSolution> solutions = new ArrayList<>();
		solutions.add(parent1);
		solutions.add(parent2);

		// Crossover
		double probability = 1.0;
//		double michiganOperationProbability = Consts.MICHIGAN_OPE_RT;
		double michiganOperationProbability = 1.0;	// Only Michigan operation
//		double michiganOperationProbability = 0.0;	// Only Pittsburgh operation

		/* Michigan operation*/
		CrossoverOperator<IntegerSolution> michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT,
				 															 problem.getKnowledge(),
				 															 problem.getConsequentFactory());
		/* Pittsburgh operation */
		CrossoverOperator<IntegerSolution> pittsburghX = new PittsburghCrossover(Consts.PITTSBURGH_CROSS_RT);
		/* Hybrid-style crossover */
		HybridGBMLcrossover crossover = new HybridGBMLcrossover(probability, michiganOperationProbability,
																michiganX, pittsburghX);

		List<IntegerSolution> offspring = crossover.execute(solutions);
		System.out.println(offspring);
	}
}
