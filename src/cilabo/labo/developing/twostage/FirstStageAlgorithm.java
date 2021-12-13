package cilabo.labo.developing.twostage;

import java.io.File;

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
 * Terminationを使って、ステージの切り替えをすれば良さそう
 */
public class FirstStageAlgorithm<S extends Solution<?>> extends HybridMoFGBMLwithNSGAII<S> {

	/** Constructor */
	public FirstStageAlgorithm(
			/* Arguments */
			Problem<S> problem,
			int populationSize,
			int offspringPopulationSize,
			int frequency,
			String outputRootDir,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator,
			Termination termination,
			ConsequentFactory consequentFactory)
	{
		super(problem, populationSize, offspringPopulationSize, frequency, outputRootDir,
				crossoverOperator, mutationOperator, termination, consequentFactory);
	}

	@Override
	public void updateProgress() {
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
