package cilabo.gbml.component.variation;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.gbml.solution.MichiganSolution;

public class MichiganSolutionVariation<S extends Solution<?>> implements Variation<S> {
	private CrossoverOperator<S> crossover;
	private MutationOperator<S> mutation;
	private int matingPoolSize;
	private int offspringPopulationSize;
	private Knowledge knowledge;
	private ConsequentFactory consequentFactory;

	public MichiganSolutionVariation(
			int offspringPopulationSize,
			CrossoverOperator<S> crossover,
			MutationOperator<S> mutation,
			Knowledge knowledge,
			ConsequentFactory consequentFactory)
	{
		this.crossover = crossover;
		this.mutation = mutation;
		this.offspringPopulationSize = offspringPopulationSize;

		this.matingPoolSize = offspringPopulationSize *
				crossover.getNumberOfRequiredParents() / crossover.getNumberOfGeneratedChildren();
		int remainder = matingPoolSize % crossover.getNumberOfRequiredParents();
		if (remainder != 0) {
			matingPoolSize += remainder;
		}
		this.knowledge = knowledge;
		this.consequentFactory = consequentFactory;
	}

	@Override
	public List<S> variate(List<S> population, List<S> matingPopulation){
		int numberOfParents = crossover.getNumberOfRequiredParents();

		checkNumberOfParents(matingPopulation, numberOfParents);
		List<S> offspringPopulation = new ArrayList<>(offspringPopulationSize);
		for(int i = 0; i < matingPoolSize; i+= numberOfParents) {
			List<S> parents = new ArrayList<>(numberOfParents);
			for(int j = 0; j < numberOfParents; j++) {
				parents.add(matingPopulation.get(i + j));
			}
			// Crossover
			List<S> offspring = crossover.execute(parents);

			for(S s : offspring) {
				// Mutation
				mutation.execute(s);
				// Learning
				if(s.getClass() == MichiganSolution.class) {
					((MichiganSolution)s).learning(knowledge, consequentFactory);
				}
				offspringPopulation.add(s);
				if(offspringPopulation.size() == offspringPopulationSize) {
					break;
				}
			}
		}
		return offspringPopulation;
	}

	/**
	 * A crossover operator is applied to a number of parents, and it assumed that the population contains
	 * a valid number of population. This method checks that.
	 *
	 * @param population
	 * @param numberOfParentsForCrossover
	 * */
	private void checkNumberOfParents(List<S> population, int numberOfParentsForCrossover) {
		if ((population.size() % numberOfParentsForCrossover) != 0) {
			throw new JMetalException("Wrong number of parents: the remainder if the " +
					"population size (" + population.size() + ") is not divisible by " +
					numberOfParentsForCrossover);
		}
	}

	@Override
	public int getMatingPoolSize() {
		return matingPoolSize ;
		}

	@Override
	public int getOffspringPopulationSize() {
		return offspringPopulationSize ;
	}

}
