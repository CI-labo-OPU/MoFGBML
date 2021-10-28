package cilabo.labo.develop;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;

import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import cilabo.data.DataSet;
import cilabo.data.impl.SimpleDatasetManager;
import cilabo.gbml.problem.impl.pittsburgh.MOP1;
import cilabo.main.Consts;
import cilabo.utility.Input;
import cilabo.utility.Output;
import cilabo.utility.Parallel;
import cilabo.utility.Random;

/**
 * @version 1.0
 *
 * WCCI2020
 */
public class HybridMoFGBMLwithNSGAII {
	public static void main(String[] args) throws JMetalException, FileNotFoundException {
		String sep = File.separator;
		String ln = System.lineSeparator();

		/* ********************************************************* */
		System.out.println();
		System.out.println("==== INFORMATION ====");
		String version = "1.0";
		System.out.println("main: " + HybridMoFGBMLwithNSGAII.class.getCanonicalName());
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
		System.out.println("START: "+ln + start);

		/* Random Number ======================= */
		Consts.RAND_SEED = 2020;
		Random.getInstance().initRandom(Consts.RAND_SEED);
		JMetalRandom.getInstance().setSeed(Consts.RAND_SEED);

		/* Load Dataset ======================== */
		SimpleDatasetManager datasetManager = loadIrisTrial00();

		/* Run MoFGBML algorithm =============== */
		DataSet train = datasetManager.getTrains().get(0);
		DataSet test = datasetManager.getTests().get(0);
		HybridStyleMoFGBML(train, test);
		/* ===================================== */

		Date end = new Date();
		System.out.println("END: "+ln + end);
		System.out.println("=====================");
		/* ********************************************************* */
	}

	/**
	 * irisのtrial00をロードする関数.
	 * @return DatasetManager
	 */
	public static SimpleDatasetManager loadIrisTrial00() {
		SimpleDatasetManager manager = new SimpleDatasetManager();
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
		MOP1<IntegerSolution> problem = new MOP1<>(Consts.RAND_SEED, train);




	}




}
