package cilabo.labo.developing.fan2021;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.data.impl.TrainTestDatasetManager;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.gbml.algorithm.HybridMoFGBMLwithNSGAII;
import cilabo.gbml.operator.crossover.HybridGBMLcrossover;
import cilabo.gbml.operator.crossover.MichiganOperation;
import cilabo.gbml.operator.crossover.PittsburghCrossover;
import cilabo.gbml.operator.mutation.PittsburghMutation;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.main.Consts;
import cilabo.utility.Input;
import cilabo.utility.Output;
import cilabo.utility.Parallel;
import cilabo.utility.Random;

/**
 * @version 1.0
 *
 * FAN2021時点
 */
public class FAN2021_Main {
	public static void main(String[] args) throws JMetalException, FileNotFoundException {
		String sep = File.separator;

		/* ********************************************************* */
		System.out.println();
		System.out.println("==== INFORMATION ====");
		String version = "1.0";
		System.out.println("main: " + FAN2021_Main.class.getCanonicalName());
		System.out.println("version: " + version);
		System.out.println();
		System.out.println("Algorithm: Hybrid-style Multiobjective Fuzzy Genetics-Based Machine Learning");
		System.out.println("EMOA: NSGA-II");
		System.out.println();
		/* ********************************************************* */
		// Load consts.properties
		Consts.set("consts");
		// make result directory
		Output.mkdirs(Consts.ROOTFOLDER);

		// set command arguments to static variables
		CommandLineArgs.loadArgs(CommandLineArgs.class.getCanonicalName(), args);
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
		Consts.RAND_SEED = 2020;
		Random.getInstance().initRandom(Consts.RAND_SEED);
		JMetalRandom.getInstance().setSeed(Consts.RAND_SEED);

		/* Load Dataset ======================== */
		TrainTestDatasetManager datasetManager = loadIrisTrial00();
//		TrainTestDatasetManager datasetManager = loadTrainTestFiles(CommandLineArgs.trainFile, CommandLineArgs.testFile);

		/* Run MoFGBML algorithm =============== */
		DataSet train = datasetManager.getTrains().get(0);
		DataSet test = datasetManager.getTests().get(0);
		HybridStyleMoFGBML(train, test);
		/* ===================================== */

		Date end = new Date();
		System.out.println("END: " + end);
		System.out.println("=====================");
		/* ********************************************************* */

		System.exit(0);
	}

	/**
	 * ファイル名を指定してデータセットをロードする関数
	 * @param trainFile String
	 * @param testFile String
	 * @return DatasetManager
	 */
	public static TrainTestDatasetManager loadTrainTestFiles(String trainFile, String testFile) {
		TrainTestDatasetManager manager = new TrainTestDatasetManager();

		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, trainFile);
		manager.addTrains(train);

		DataSet test = new DataSet();
		Input.inputSingleLabelDataSet(test, testFile);
		manager.addTests(test);

		return manager;
	}

	/**
	 * irisのtrial00をロードする関数.
	 * @return DatasetManager
	 */
	public static TrainTestDatasetManager loadIrisTrial00() {
		TrainTestDatasetManager manager = new TrainTestDatasetManager();
		String sep = File.separator;
		String fileName;

		// Training dataset
		fileName = Consts.DATASET;
		fileName += sep + CommandLineArgs.dataName;
		fileName += sep + "a0_0_" + CommandLineArgs.dataName + "-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, fileName);
		manager.addTrains(train);

		// Test dataset
		fileName = Consts.DATASET;
		fileName += sep + CommandLineArgs.dataName;
		fileName += sep + "a0_0_" + CommandLineArgs.dataName + "-10tst.dat";
		DataSet test = new DataSet();
		Input.inputSingleLabelDataSet(test, fileName);
		manager.addTests(test);

		return manager;
	}

	/**
	 *
	 */
	public static void HybridStyleMoFGBML(DataSet train, DataSet test) {
		/* MOP: Multi-objective Optimization Problem */
		MOP1<IntegerSolution> problem = new MOP1<>(train);
		problem.setClassification(new SingleWinnerRuleSelection());

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
		HybridMoFGBMLwithNSGAII<IntegerSolution> algorithm
			= new HybridMoFGBMLwithNSGAII<>(problem,
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

		/* === GA RUN === */
		algorithm.run();
		/* ============== */

		/* Non-dominated solutions in final generation */
		List<IntegerSolution> nonDominatedSolutions = algorithm.getResult();
	    new SolutionListOutput(nonDominatedSolutions)
        	.setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        	.setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        	.print();

		return;
	}



}
