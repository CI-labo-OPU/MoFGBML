package cilabo.gbml.operator.mutation;

import java.util.ArrayList;

import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.utility.Random;

public class PittsburghMutation implements MutationOperator<IntegerSolution> {
	private double mutationProbability;
	private RandomGenerator<Double> randomGenerator;
	private BoundedRandomGenerator<Integer> intRandomGenerator;
	private Knowledge knowledge;
	private ConsequentFactory consequentFactory;

	/** Constructor */
	public PittsburghMutation(double mutationProbability, Knowledge knowledge, ConsequentFactory consequentFactory) {
		this(mutationProbability, knowledge, consequentFactory,
			 () -> JMetalRandom.getInstance().nextDouble(),
			 (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
	}

	/** Constructor */
	public PittsburghMutation(double mutationProbability, Knowledge knowledge, ConsequentFactory consequentFactory, RandomGenerator<Double> randomGenerator) {
		this(
			mutationProbability, knowledge, consequentFactory,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public PittsburghMutation(double mutationProbability, Knowledge knowledge, ConsequentFactory consequentFactory, RandomGenerator<Double> randomGenerator, BoundedRandomGenerator<Integer> intRandomGenerator) {
		if (mutationProbability < 0) {
			throw new JMetalException("Mutation probability is negative: " + mutationProbability);
		}
		this.mutationProbability = mutationProbability;
		this.knowledge = knowledge;
		this.consequentFactory = consequentFactory;
		this.randomGenerator = randomGenerator;
		this.intRandomGenerator = intRandomGenerator;
	}

	/* Getter */
	@Override
	public double getMutationProbability() {
		return mutationProbability;
	}

	/* Setters */
	public void setMutationProbability(double mutationProbability) {
		this.mutationProbability = mutationProbability;
	}

	public void setKnowledge(Knowledge knowledge) {
		this.knowledge = knowledge;
	}

	public void setConsequentFactory(ConsequentFactory consequentFactory) {
		this.consequentFactory = consequentFactory;
	}

	/** Execute() method
	 * @param solution IntegerSolution : PittsburghSolutionを前提
	 */
	@Override
	public IntegerSolution execute(IntegerSolution solution) {
		Check.isNotNull(solution);
		Check.that(solution.getClass() == PittsburghSolution.class, "The argument must be class: " + PittsburghSolution.class.getCanonicalName());

		doMutation(mutationProbability, (PittsburghSolution)solution);
		return solution;
	}

	/**
	 * @param probability
	 * @param solution PittsburghSolution
	 */
	public void doMutation(double probability, PittsburghSolution solution) {

		int numberOfRules = solution.getMichiganPopulation().size();
		int dimension = consequentFactory.getTrain().getNdim();

		ArrayList<IntegerSolution> newMichiganPopulation = new ArrayList<>();
		for(int rule_i = 0; rule_i < numberOfRules; rule_i++) {
			MichiganSolution michiganSolution = (MichiganSolution)solution.getMichiganPopulation().get(rule_i);
			Antecedent antecedent = michiganSolution.getRule().getAntecedent().deepcopy();
			Consequent consequent = michiganSolution.getRule().getConsequent().deepcopy();

			/* Probability for each rule := 1/NumberOfRules */
			if(Random.getInstance().getGEN().nextInt(numberOfRules) == 0) {
				/* Perform muation for rule_i */
				int[] antecedentIndex = michiganSolution.getRule().getAntecedent().getAntecedentIndex();

				/* Decide which demension is performed mutation. */
				int mutatedDimension = Random.getInstance().getGEN().nextInt(dimension);

				/* To judge which mutatedDimension is categorical or numerical  */
				double variableOfRandomPattern = consequentFactory.getTrain().getPattern(Random.getInstance().getGEN().nextInt(consequentFactory.getTrain().getDataSize()))
					  							.getDimValue(mutatedDimension);

				/* Attribute is Numeric */
				if(variableOfRandomPattern >= 0.0) {
					int numberOfCandidates = knowledge.getFuzzySetNum(mutatedDimension);
					ArrayList<Integer> list = new ArrayList<>();
					for(int i = 0; i < numberOfCandidates; i++) {
						if(i != solution.getMichiganPopulation().get(rule_i).getVariable(mutatedDimension)) {
							list.add(i);
						}
					}
					/* Perform mutation */
					int newFuzzySet = list.get( Random.getInstance().getGEN().nextInt(list.size()) );
					antecedentIndex[mutatedDimension] = newFuzzySet;
				}
				/* Attribute is categorical */
				else {
					antecedentIndex[mutatedDimension] = (int)variableOfRandomPattern;
				}

				antecedent = Antecedent.builder()
						.antecedentIndex(antecedentIndex)
						.knowledge(knowledge)
						.build();
				consequent = consequentFactory.learning(antecedent);
			}
			/* Don't perform mutation for rule_i -> Don't learning */
			newMichiganPopulation.add(new MichiganSolution(michiganSolution.getBounds(),
					michiganSolution.getNumberOfObjectives(),
					michiganSolution.getNumberOfConstraints(),
					antecedent, consequent) );
		}

		solution.setMichiganPopulation(newMichiganPopulation);
	}


}
