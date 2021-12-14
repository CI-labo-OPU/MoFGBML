package cilabo.labo.developing.twostage;

import java.io.File;
import java.util.List;

import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.gbml.algorithm.HybridMoFGBMLwithNSGAII;
import cilabo.util.fileoutput.PittsburghSolutionListOutput;

/**
 *
 * cilabo.gbml.algorithm.HybridMoFGBMLwithNSGAII を参考に作成
 * 主にrun()の中身を書き換える
 *
 * 2ndステージの初期個体群は1stの結果から始めたい
 * -> createInitialPopulation()の中身を書き換えるのはどうか？
 */
	//TODO 今までのMoFGBMLをそのまま使うので，このクラスは必要ない可能性大．
	//TODO
	/* 評価個体数を1stの終了時から始めたい -> run()をOverrideする必要があるため、
	 * 継承して作成．
	 */
public class SecondStageAlgorithm<S extends Solution<?>> extends HybridMoFGBMLwithNSGAII<S> {

	/** Constructor */
	public SecondStageAlgorithm(
			/* Arguments */
			Problem<S> problem,
			int populationSize,
			int offspringPopulationSize,
			int frequency,
			String outputRootDir,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator,
			Termination termination,
			ConsequentFactory consequentFactory,
			/* Requirments for 2nd algorithm */
			List<S> population, int nowEvaluations
			)
	{
		super(problem, populationSize, offspringPopulationSize, frequency, outputRootDir,
				crossoverOperator, mutationOperator, termination, consequentFactory);
		setPopulation(population);
		setEvaluations(nowEvaluations);
	}

	@Override
	protected void initProgress() {
		getAlgorithmStatusData().put("EVALUATIONS", getEvaluations());
		getAlgorithmStatusData().put("POPULATION", getPopulation());
		getAlgorithmStatusData().put("COMPUTING_TIME", System.currentTimeMillis() - getStartTime());

		getObservable().setChanged();
		getObservable().notifyObservers(getAlgorithmStatusData());
	}

	@Override
	public void run() {

		/* === START === */
		List<S> offspringPopulation;
		List<S> matingPopulation;

		initProgress();
/**************************************************************************/
//		/* Step 1. 初期個体群生成 - Initialization Population */
//		population = createInitialPopulation();
//		/* Step 2. 初期個体群評価 - Initial Population Evaluation */
//		population = evaluatePopulation(population);
//		/* JMetal progress initialization */
//		initProgress();
/**************************************************************************/

		/* GA loop */
		while(!isStoppingConditionReached()) {
			/* 親個体選択 - Mating Selection */
			matingPopulation = selection(population);
			/* 子個体群生成 - Offspring Generation */
			offspringPopulation = reproduction(matingPopulation);
			/* 子個体群評価 - Offsprign Evaluation */
			offspringPopulation = evaluatePopulation(offspringPopulation);
			/* 個体群更新・環境選択 - Environmental Selection */
			population = replacement(population, offspringPopulation);

			/* JMetal progress update */
			updateProgress();
		}

		/* ===  END  === */
		setTotalComputingTime(System.currentTimeMillis() - getStartTime());
	}

	@Override
	protected void updateProgress() {
		setEvaluations(getEvaluations() + getOffspringPopulationSize());
		getAlgorithmStatusData().put("EVALUATIONS", getEvaluations());
		getAlgorithmStatusData().put("POPULATION", getPopulation());
		getAlgorithmStatusData().put("COMPUTING_TIME", System.currentTimeMillis() - getStartTime());

		getObservable().setChanged();
		getObservable().notifyObservers(getAlgorithmStatusData());

		//Output for each generation;
		String sep = File.separator;
		Integer evaluations = (Integer)getAlgorithmStatusData().get("EVALUATIONS");
		if(evaluations != null) {
			if(evaluations % getFrequency() == 0) {
				//TODO
				String path = getOutputRootDir()+sep + "solution-"+evaluations+".txt";
			    new PittsburghSolutionListOutput(getPopulation())
	        	.printSolutionsToFile(new DefaultFileOutputContext(path), getPopulation());
			    path = getOutputRootDir()+sep + "FUN-"+evaluations+".csv";
			    new PittsburghSolutionListOutput(getPopulation())
	        	.printObjectivesToFile(new DefaultFileOutputContext(path), getPopulation());

			}
		}
		else {
			JMetalLogger.logger.warning(getClass().getName()
			+ ": The algorithm has not registered yet any info related to the EVALUATIONS key");
		}
	}

}

