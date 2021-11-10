package cilabo.labo.main.b3;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.postProcessing.PostProcessing;
import cilabo.fuzzy.classifier.operator.postProcessing.factory.RemoveNotBeWinnerProcessing;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3;
import cilabo.fuzzy.rule.Rule;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
import cilabo.gbml.algorithm.MichiganFGBML;
import cilabo.gbml.component.evaluation.MichiganEvaluation;
import cilabo.gbml.component.replacement.SingleObjectiveMaximizeReplacementWithoutOffspringFitness;
import cilabo.gbml.component.variation.MichiganSolutionVariation;
import cilabo.gbml.operator.crossover.UniformCrossover;
import cilabo.gbml.operator.mutation.MichiganMutation;
import cilabo.gbml.problem.impl.ProblemMichiganFGBML;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.main.Consts;
import cilabo.metric.ErrorRate;
import cilabo.metric.Metric;
import cilabo.utility.Input;
import cilabo.utility.Output;
import cilabo.utility.Parallel;

/**
 * For student belonging in CILAB,
 * this example is implementation of the programming exercise 6.
 */
public class ProgrammingExercise_MichiganFGBML {

	public static void main(String[] args) throws JMetalException, FileNotFoundException {
		String sep = File.separator;
		Parallel.getInstance().initLearningForkJoinPool(1);
		Output.mkdirs(Consts.ROOTFOLDER);
		Consts.ALGORITHM_ID_DIR = Consts.ROOTFOLDER + sep + "ProgrammingExercise_MichiganFGBML";
		Output.mkdirs(Consts.ALGORITHM_ID_DIR);

		// 3.2. 数値実験準備
		deterministicTest();

		// 2.9 Michigan-type Fuzzy GBML
		String header = "trial,Dtra, Dtst";
		Output.writeln(Consts.ALGORITHM_ID_DIR + sep + "errorRate.csv", header, true);

		int crossValidation = 10;
		int repeat = 3;
		for(int rr = 0; rr < repeat; rr++) {
			for(int cc = 0; cc < crossValidation; cc++) {
				System.out.println("---");
				System.out.println("trial"+ String.valueOf(rr)+String.valueOf(cc));
				MichiganStyleFGBML(rr, cc);
				System.out.println("---");
			}
		}

		System.exit(0);
	}

	// 2.9 Michigan-type Fuzzy GBML
	public static void MichiganStyleFGBML(int rr, int cc) {
		String sep = File.separator;
		String trialRootDir = Consts.ALGORITHM_ID_DIR + sep + "trial" + String.valueOf(rr) + String.valueOf(cc);
		Output.mkdirs(trialRootDir);

		// Load "Pima" dataset
		String dataName = "dataset" + sep + "pima" + sep + "a"+rr+"_"+cc+"_pima-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		// Parameters
		int populationSize = 30;
		int offspringPopulationSize = 10;
		// Problem
		int seed = 0;
		JMetalRandom.getInstance().setSeed(seed);
		ProblemMichiganFGBML<IntegerSolution> problem = new ProblemMichiganFGBML<>(seed, train);
		// Crossover
		double crossoverProbability = 0.9;
		CrossoverOperator<IntegerSolution> crossover = new UniformCrossover(crossoverProbability);
		// Mutation
		double mutationProbability = 1.0 / (double)train.getNdim();
		MutationOperator<IntegerSolution> mutation = new MichiganMutation(mutationProbability,
																	  problem.getKnowledge(),
																	  train);
		// Termination
		int generation = 1000;
		int evaluations = populationSize + generation*offspringPopulationSize;
		int outputFrequency = 2000;
		Termination termination = new TerminationByEvaluations(evaluations);
		// Variation
		Variation<IntegerSolution> variation = new MichiganSolutionVariation<>(
													offspringPopulationSize, crossover, mutation,
													problem.getKnowledge(),
													problem.getConsequentFactory());
		// Replacement
		Replacement<IntegerSolution> replacement = new SingleObjectiveMaximizeReplacementWithoutOffspringFitness<>();

		// Algorithm
		MichiganFGBML<IntegerSolution> algorithm
				= new MichiganFGBML<>(problem, populationSize, offspringPopulationSize,
									outputFrequency, trialRootDir,
									crossover, mutation, termination, variation, replacement);

		int observeFrequency = 1000;
		EvaluationObserver evaluationObserver = new EvaluationObserver(observeFrequency);
		algorithm.getObservable().register(evaluationObserver);

		algorithm.run();

		// Result
		List<RuleBasedClassifier> totalClassifiers = algorithm.getTotalClassifier();
		Metric metric = new ErrorRate();
		RuleBasedClassifier bestClassifier = null;
		double minValue = Double.MAX_VALUE;
		for(int i = 0; i < totalClassifiers.size(); i++) {
			double errorRate = (double)metric.metric(totalClassifiers.get(i), train);
			if(errorRate < minValue) {
				minValue = errorRate;
				bestClassifier = totalClassifiers.get(i);
			}
		}
		PostProcessing postProcessing = new RemoveNotBeWinnerProcessing(train);
		postProcessing.postProcess(bestClassifier);

		// Test data
		String testDataName = "dataset" + sep + "pima" + sep + "a"+rr+"_"+cc+"_pima-10tst.dat";
		DataSet test = new DataSet();
		Input.inputSingleLabelDataSet(test, testDataName);
		double errorRate = (double)metric.metric(bestClassifier, test);

		System.out.println();
		System.out.println("Error Rate(Train): " + minValue);
		System.out.println("Error Rate(Test) : " + errorRate);
		String fileName = trialRootDir + sep + "BestClassifier.txt";
		Output.writeln(fileName, bestClassifier.toString(), false);

		String str = String.valueOf(rr)+String.valueOf(cc);
		str += "," + minValue;
		str += "," + errorRate;
		Output.writeln(Consts.ALGORITHM_ID_DIR + sep + "errorRate.csv", str, true);
	}



	// 3.2. 数値実験準備
	public static void deterministicTest() {
		String sep = File.separator;

		// Load "Pima" dataset
		String dataName = "dataset" + sep + "pima" + sep + "a0_0_pima-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, dataName);

		// Initialization Knowledge base
		Knowledge knowledge = HomoTriangleKnowledgeFactory.builder()
								.dimension(train.getNdim())
								.params(HomoTriangle_2_3.getParams())
								.build()
								.create();

		// Test Rule: 0 4 0 3 3 0 0 0
		int[] antecedentIndex = new int[] {0, 4, 0, 3, 3, 0, 0, 0};
		Antecedent antecedent = Antecedent.builder()
								.knowledge(knowledge)
								.antecedentIndex(antecedentIndex)
								.build();
		ConsequentFactory consequentFactory = new MoFGBML_Learning(train);
		Consequent consequent = consequentFactory.learning(antecedent);
		Rule rule = Rule.builder()
						.antecedent(antecedent)
						.consequent(consequent)
						.build();
		// Check: class=0, weight=0.504
		System.out.println("[Rule]");
		System.out.println(rule.toString());
		System.out.println();

		/* Test Fitnesses
		 * Rule: 0 0 0 2 0 0 0 0, Fitness: 179
		 * Rule: 0 4 0 0 3 0 0 4, Fitness: 136
		 * Rule: 0 1 0 3 1 2 0 0, Fitness: 134
		 */
		Antecedent[] antecedents = new Antecedent[3];
		antecedents[0] = Antecedent.builder()
				.knowledge(knowledge)
				.antecedentIndex(new int[] {0, 0, 0, 2, 0, 0, 0, 0})
				.build();
		antecedents[1] = Antecedent.builder()
				.knowledge(knowledge)
				.antecedentIndex(new int[] {0, 4, 0, 0, 3, 0, 0, 4})
				.build();
		antecedents[2] = Antecedent.builder()
				.knowledge(knowledge)
				.antecedentIndex(new int[] {0, 1, 0, 3, 1, 2, 0, 0})
				.build();

		int seed = 0;
		Problem<IntegerSolution> problem = new ProblemMichiganFGBML<MichiganSolution>(seed, train);
		List<IntegerSolution> population = new ArrayList<>();
		for(int i = 0; i < 3; i++) {
			antecedent = antecedents[i];
			consequent = consequentFactory.learning(antecedent);
			MichiganSolution solution = new MichiganSolution(((ProblemMichiganFGBML<MichiganSolution>)problem).getBounds(),
													 problem.getNumberOfObjectives(),
													 problem.getNumberOfConstraints(),
													 antecedent,
													 consequent);
			population.add(solution);
		}

		// Evaluate
		(new MichiganEvaluation<IntegerSolution>()).evaluate(population, problem);

		System.out.println("[population] ");
		for(IntegerSolution solution : population) {
			System.out.print(solution);
		}
		System.out.println();
	}
}



















