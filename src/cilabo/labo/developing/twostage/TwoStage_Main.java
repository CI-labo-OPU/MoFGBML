package cilabo.labo.developing.twostage;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.data.impl.TrainTestDatasetManager;
import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.gbml.operator.crossover.HybridGBMLcrossover;
import cilabo.gbml.operator.crossover.MichiganOperation;
import cilabo.gbml.operator.crossover.PittsburghCrossover;
import cilabo.gbml.operator.mutation.PittsburghMutation;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.main.Consts;
import cilabo.metric.ErrorRate;
import cilabo.metric.Metric;
import cilabo.metric.RuleLength;
import cilabo.metric.RuleNum;
import cilabo.utility.Output;
import cilabo.utility.Parallel;
import cilabo.utility.Random;

/**
 * @version 1.0
 *
 * 2021, November
 */
public class TwoStage_Main {
	public static void main(String[] args) throws JMetalException, FileNotFoundException {
		String sep = File.separator;

		/* ********************************************************* */
		System.out.println();
		System.out.println("==== INFORMATION ====");
		String version = "1.0";
		System.out.println("main: " + TwoStage_Main.class.getCanonicalName());
		System.out.println("version: " + version);
		System.out.println();
		System.out.println("Algorithm: Two-stage for accuracy-oriented FGBML");
		System.out.println("EMOA: NSGA-II");
		System.out.println();
		/* ********************************************************* */
		// Load consts.properties
		Consts.set("consts");
		// make result directory
		Output.mkdirs(Consts.ROOTFOLDER);

		// set command arguments to static variables
		CommandLineArgs.loadArgs(CommandLineArgs.class.getCanonicalName(), args);
		// set max number of rules to large value
		Consts.MAX_RULE_NUM = 5000;
		// Output constant parameters
		String fileName = Consts.EXPERIMENT_ID_DIR + sep + "Consts.txt";
		Output.writeln(fileName, Consts.getString(), true);
		Output.writeln(fileName, CommandLineArgs.getParamsString(), true);

		// Initialize ForkJoinPool
		Parallel.getInstance().initLearningForkJoinPool(CommandLineArgs.parallelCores);

		System.out.println("Processors: " + Runtime.getRuntime().availableProcessors() + " ");
		System.out.print("args: ");
		for(int i = 0; i < args.length; i++) {
			System.out.print(args[i] + " ");
		}
		System.out.println();
		System.out.println("=====================");
		System.out.println();

		/* ********************************************************* */
		System.out.println("==== EXPERIMENT =====");
		Date start = new Date();
		System.out.println("START: " + start);

		/* Random Number ======================= */
		Random.getInstance().initRandom(Consts.RAND_SEED);
		JMetalRandom.getInstance().setSeed(Consts.RAND_SEED);

		/* Load Dataset ======================== */
		TrainTestDatasetManager datasetManager = new TrainTestDatasetManager();
		datasetManager.loadTrainTestFiles(CommandLineArgs.trainFile, CommandLineArgs.testFile);

		/* Run MoFGBML algorithm =============== */
		DataSet train = datasetManager.getTrains().get(0);
		DataSet test = datasetManager.getTests().get(0);
		twoStageMoFGBML(train, test);
		/* ===================================== */

		Date end = new Date();
		System.out.println("END: " + end);
		System.out.println("=====================");
		/* ********************************************************* */

		System.exit(0);
	}

	/**
	 *
	 */
	/* このメソッド全体 -> ************************************************* */
	/* TODO
	 * ステージを切り替えるように変更
	 * 1stStage用のalgorithmと2ndStage用のalgorithmを作成する方針はどうか？
	 *   案)
	 *   population = firstAlgorithm.run();
	 *   secondAlgorithm.setPopulation(population)
	 *   population = secondAlgorithm.run();
	 *   ...
	 *  */
	public static void twoStageMoFGBML(DataSet train, DataSet test) {
		String sep = File.separator;

		/* MOP: Multi-objective Optimization Problem */
		MOP1<IntegerSolution> problem = new MOP1<>(train);
		problem.setClassification(new SingleWinnerRuleSelection());
		Consts.knowledge = problem.getKnowledge();

		//TODO 仮置き
		List<IntegerSolution> population = null;
		int nowEvaluations = 0;

		//TODO 1stステージの実装
		/* ======================================= */
		/* == 1st Stage                         == */
		/* ======================================= */
		FirstStageAlgorithm<IntegerSolution> firstAlgorithm = make1stStageAlgorithm(train, test, problem);
		/* === GA RUN === */
		firstAlgorithm.run();
		/* ============== */
		population = firstAlgorithm.getPopulation();
		nowEvaluations = firstAlgorithm.getEvaluations();
		/* ======================================= */

		System.out.println("== Changed stage: EVALUATIONS="+nowEvaluations+" ==");

		/* ======================================= */
		/* == 2nd Stage                         == */
		/* ======================================= */
		SecondStageAlgorithm<IntegerSolution> secondAlgorithm
		= make2ndStageAlgorithm(train, test, problem,
								/* Set current population as the initial population for 2nd stage */
								population,
								/* Set current number of evaluations */
								nowEvaluations);
		/* === GA RUN === */
		secondAlgorithm.run();
		/* ============== */
		/* ======================================= */

		/* Non-dominated solutions in final generation */
		List<IntegerSolution> nonDominatedSolutions = secondAlgorithm.getResult();
	    outputResults(nonDominatedSolutions, train,test);

		return;
	}

	public static FirstStageAlgorithm<IntegerSolution> make1stStageAlgorithm(DataSet train, DataSet test, MOP1<IntegerSolution> problem) {
		/* Crossover: Hybrid-style GBML specific crossover operator. */
		double crossoverProbability = 1.0;

		/* Michigan operation */
		CrossoverOperator<IntegerSolution> michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT,
																			 problem.getKnowledge(),
																			 problem.getConsequentFactory());
		/* Pittsburgh operation: Complex-oriented Pittsburgh-style crossover */
		CrossoverOperator<IntegerSolution> pittsburghX = new PittsburghCrossoverComplexOriented(Consts.PITTSBURGH_CROSS_RT);

		/* Hybrid-style crossover */
		CrossoverOperator<IntegerSolution> crossover = new HybridGBMLcrossover(crossoverProbability, Consts.MICHIGAN_OPE_RT,
																				michiganX, pittsburghX);
		/* Mutation: Pittsburgh-style GBML specific mutation operator. */
		MutationOperator<IntegerSolution> mutation = new PittsburghMutation(problem.getKnowledge(), train);

		/* Termination: TODO TODO TODO */
		Termination termination = new ChangeStageTermination(CommandLineArgs.changeStageSetting);

		/* Algorithm: Hybrid-style MoFGBML with NSGA-II */
		FirstStageAlgorithm<IntegerSolution> algorithm
			= new FirstStageAlgorithm<>(problem,
										Consts.populationSize,
										Consts.offspringPopulationSize,
										Consts.outputFrequency,
										Consts.EXPERIMENT_ID_DIR,
										crossover,
										mutation,
										termination,
										problem.getConsequentFactory()
										);
		/* Running observation */
		EvaluationObserver evaluationObserver = new EvaluationObserver(Consts.outputFrequency);
		algorithm.getObservable().register(evaluationObserver);

		/* Replacement: Accuracy-oriented replacement */
		Replacement<IntegerSolution> replacement = new AccuracyOrientedReplacement<>();
		algorithm.setReplacement(replacement);

		return algorithm;
	}

	public static SecondStageAlgorithm<IntegerSolution> make2ndStageAlgorithm(DataSet train, DataSet test, MOP1<IntegerSolution> problem,
																				List<IntegerSolution> population, int nowEvaluations) {


		/* Crossover: Hybrid-style GBML specific crossover operator. */
		double crossoverProbability = 1.0;

		/* Michigan operation */
		CrossoverOperator<IntegerSolution> michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT,
																			 problem.getKnowledge(),
																			 problem.getConsequentFactory());
		/* Pittsburgh operation */
		CrossoverOperator<IntegerSolution> pittsburghX = new PittsburghCrossover(Consts.PITTSBURGH_CROSS_RT);

		/* Hybrid-style crossover */
		CrossoverOperator<IntegerSolution> crossover = new HybridGBMLcrossover(crossoverProbability, Consts.MICHIGAN_OPE_RT,
																				michiganX, pittsburghX);
		/* Mutation: Pittsburgh-style GBML specific mutation operator. */
		MutationOperator<IntegerSolution> mutation = new PittsburghMutation(problem.getKnowledge(), train);

		/* Termination: Number of total evaluations */
		Termination termination = new TerminationByEvaluations(Consts.terminateEvaluation);

		/* Algorithm: Hybrid-style MoFGBML with NSGA-II */
		SecondStageAlgorithm<IntegerSolution> algorithm
			= new SecondStageAlgorithm<>(problem,
											Consts.populationSize,
											Consts.offspringPopulationSize,
											Consts.outputFrequency,
											Consts.EXPERIMENT_ID_DIR,
											crossover,
											mutation,
											termination,
											problem.getConsequentFactory(),
											population, nowEvaluations
											);

		/* Running observation */
		EvaluationObserver evaluationObserver = new EvaluationObserver(Consts.outputFrequency);
		algorithm.getObservable().register(evaluationObserver);

		return algorithm;
	}

	// TODO TODO TODO
	public static void outputResults(List<IntegerSolution> nonDominatedSolutions, DataSet train, DataSet test) {
		String sep = File.separator;

		ArrayList<String> strs = new ArrayList<>();
		String str = "";

		/* Functions */
		Metric errorRate = new ErrorRate();
		Metric ruleNum = new RuleNum();
		Metric ruleLength = new RuleLength();

		/* Header */
		str = "pop";
		str += "," + "errorRate_Dtra" + "," + "errorRate_Dtst";
		str += "," + "ruleNum";
		str += "," + "ruleLength";
		strs.add(str);

		for(int i = 0; i < nonDominatedSolutions.size(); i++) {
			IntegerSolution solution = nonDominatedSolutions.get(i);
			Classifier classifier = ((PittsburghSolution)solution).getClassifier();

			// pop
			str = String.valueOf(i);
			// Error Rate
			str += "," + errorRate.metric(classifier, train);
			str += "," + errorRate.metric(classifier, test);
			// Number of rules
			str += "," + ruleNum.metric(classifier);
			// Rule Length
			str += "," + ruleLength.metric(classifier);

			strs.add(str);
		}
		String fileName = Consts.EXPERIMENT_ID_DIR + sep + "results.csv";
		Output.writeln(fileName, strs, false);
	}



}
