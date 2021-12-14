package cilabo.labo.developing.fairness;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.gbml.algorithm.HybridMoFGBMLwithNSGAII;
import cilabo.gbml.operator.crossover.HybridGBMLcrossover;
import cilabo.gbml.operator.crossover.MichiganOperation;
import cilabo.gbml.operator.crossover.PittsburghCrossover;
import cilabo.gbml.operator.mutation.PittsburghMutation;
import cilabo.gbml.problem.AbstractPitssburghGBML_Problem;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.main.Consts;
import cilabo.metric.Gmean;
import cilabo.metric.Metric;
import cilabo.metric.RuleNum;
import cilabo.metric.fairness.FalsePositiveRateDifference;
import cilabo.metric.fairness.PositivePredictiveValuesDifference;
import cilabo.utility.Input;
import cilabo.utility.Output;
import cilabo.utility.Parallel;
import cilabo.utility.Random;

/**
 * @version 1.0
 *
 * 2021, November
 *
 */
public class Fairness_Main {
	public static void main(String[] args) throws JMetalException, FileNotFoundException {
		String sep = File.separator;

		/* ********************************************************* */
		System.out.println();
		System.out.println("==== INFORMATION ====");
		String version = "1.0";
		System.out.println("main: " + Fairness_Main.class.getCanonicalName());
		System.out.println("version: " + version);
		System.out.println();
		System.out.println("Algorithm: Hybrid-style Multiobjective Fuzzy Genetics-Based Machine Learning for Fairness Datasets");
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
		Random.getInstance().initRandom(Consts.RAND_SEED);
		JMetalRandom.getInstance().setSeed(Consts.RAND_SEED);

		/* Load Dataset ======================== */
		TrainTestDatasetManager datasetManager = new TrainTestDatasetManager();
		DataSet train = new DataSet();
		DataSet test = new DataSet();
		Input.inputFairnessDataSet(train, CommandLineArgs.trainFile);
		Input.inputFairnessDataSet(test, CommandLineArgs.testFile);
		datasetManager.addTrains(train);
		datasetManager.addTests(test);

		/* Run MoFGBML algorithm =============== */
		fairnessMoFGBML(train, test);
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
	public static void fairnessMoFGBML(DataSet train, DataSet test) {
		String sep = File.separator;

		/** Fuzzy Initialization **/
		/* 2-5分割 等分割三角型メンバシップ関数 */
		float[][] params = HomoTriangle_2_3_4_5.getParams();
		Knowledge knowledge = HomoTriangleKnowledgeFactory.builder()
								.dimension(train.getNdim())
								.params(params)
								.build()
								.create();
		Classification classification = new SingleWinnerRuleSelection();

		/* MOP: Multi-objective Optimization Problem */
		AbstractPitssburghGBML_Problem<IntegerSolution> problem = getMOP(train);

		/* Crossover: Hybrid-style GBML specific crossover operator. */
		double crossoverProbability = 1.0;
		/* Michigan operation */
		CrossoverOperator<IntegerSolution> michiganX = new MichiganOperation(Consts.MICHIGAN_CROSS_RT,
																			 knowledge,
																			 problem.getConsequentFactory());
		/* Pittsburgh operation */
		CrossoverOperator<IntegerSolution> pittsburghX = new PittsburghCrossover(Consts.PITTSBURGH_CROSS_RT);
		/* Hybrid-style crossover */
		CrossoverOperator<IntegerSolution> crossover = new HybridGBMLcrossover(crossoverProbability, Consts.MICHIGAN_OPE_RT,
																				michiganX, pittsburghX);
		/* Mutation: Pittsburgh-style GBML specific mutation operator. */
		MutationOperator<IntegerSolution> mutation = new PittsburghMutation(knowledge, train);

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
        	.setVarFileOutputContext(new DefaultFileOutputContext(Consts.EXPERIMENT_ID_DIR+sep+"VAR.csv", ","))
        	.setFunFileOutputContext(new DefaultFileOutputContext(Consts.EXPERIMENT_ID_DIR+sep+"FUN.csv", ","))
        	.print();

	    /* Output results from non-dominated solutions in final generation */
	    outputResults(nonDominatedSolutions, train, test);

		return;
	}

	public static AbstractPitssburghGBML_Problem<IntegerSolution> getMOP(DataSet train) {
		AbstractPitssburghGBML_Problem<IntegerSolution> mop = null;
		switch(CommandLineArgs.mopIndex) {
		case 1:
			mop = new MOP1_fairness<>(train);
			break;

		case 2:
			mop = new MOP2_fairness<>(train);
			break;

		case 3:
			mop = new MOP3_fairness<>(train);
			break;

		case 4:
			mop = new MOP4_fairness<>(train);
			break;
		}
		return mop;
	}

	public static void outputResults(List<IntegerSolution> nonDominatedSolutions, DataSet train, DataSet test) {
		String sep = File.separator;
	    ArrayList<String> strs = new ArrayList<>();
	    String str = "";

	    /* Functions */
	    Metric gmean = new Gmean();
	    Metric FPR = new FalsePositiveRateDifference();
	    Metric PPV = new PositivePredictiveValuesDifference();
	    Metric ruleNum = new RuleNum();

	    /* Header */
	    str = "pop";
	    str += "," + "Gmean_Dtra" + "," + "Gmean_Dtst";
	    str += "," + "FPR_Dtra" + "," + "FPR_Dtst";
	    str += "," + "PPV_Dtra" + "," + "PPV_Dtst";
	    str += "," + "ruleNum";
	    strs.add(str);

	    for(int i = 0; i < nonDominatedSolutions.size(); i++) {
	    	IntegerSolution solution = nonDominatedSolutions.get(i);
	    	Classifier classifier = ((PittsburghSolution)solution).getClassifier();

	    	// pop
	    	str = String.valueOf(i);
	    	// Gmean
	    	str += "," + gmean.metric(classifier, train);
	    	str += "," + gmean.metric(classifier, test);
	    	// FPR
	    	str += "," + FPR.metric(classifier, train);
	    	str += "," + FPR.metric(classifier, test);
	    	// PPV
	    	str += "," + PPV.metric(classifier, train);
	    	str += "," + PPV.metric(classifier, test);
	    	// ruleNum
	    	str += "," + ruleNum.metric(classifier);

	    	strs.add(str);
	    }
	    String fileName = Consts.EXPERIMENT_ID_DIR + sep + "results.csv";
	    Output.writeln(fileName, strs, false);
	}


}
