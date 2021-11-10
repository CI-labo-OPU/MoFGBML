package cilabo.gbml.operator.crossover;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.selection.impl.NaryTournamentMatingPoolSelection;
import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.gbml.component.replacement.RuleAdditionStyleReplacement;
import cilabo.gbml.component.variation.MichiganSolutionVariation;
import cilabo.gbml.operator.heuristic.HeuristicRuleGeneration;
import cilabo.gbml.operator.mutation.MichiganMutation;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.gbml.solution.util.SortMichiganPopulation;
import cilabo.gbml.solution.util.attribute.ErroredPatternsAttribute;
import cilabo.main.Consts;
import cilabo.utility.GeneralFunctions;
import cilabo.utility.Random;

public class MichiganOperation implements CrossoverOperator<IntegerSolution> {
	private double crossoverProbability;
	private RandomGenerator<Double> crossoverRandomGenerator;
	BoundedRandomGenerator<Integer> selectRandomGenerator;

	HeuristicRuleGeneration heuristicRuleGeneration;

	Knowledge knowledge;
	ConsequentFactory consequentFactory;

	/** Constructor */
	public MichiganOperation(double crossoverProbability,
							 Knowledge knowledge, ConsequentFactory consequentFactory) {
		this(
			crossoverProbability,
			() -> JMetalRandom.getInstance().nextDouble(),
			(a, b) -> JMetalRandom.getInstance().nextInt(a, b));
		this.heuristicRuleGeneration = new HeuristicRuleGeneration(knowledge);
		this.knowledge = knowledge;
		this.consequentFactory = consequentFactory;
	}

	/** Constructor */
	public MichiganOperation(
			double crossoverProbability, RandomGenerator<Double> randomGenerator) {
		this(
			crossoverProbability,
			randomGenerator,
			BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
	}

	/** Constructor */
	public MichiganOperation(
			double crossoverProbability,
			RandomGenerator<Double> crossoverRandomGenerator,
			BoundedRandomGenerator<Integer> selectRandomGenerator) {
		if(crossoverProbability < 0) {
			throw new JMetalException("Crossover probability is negative: " + crossoverProbability);
		}
		this.crossoverProbability = crossoverProbability;
		this.crossoverRandomGenerator = crossoverRandomGenerator;
		this.selectRandomGenerator = selectRandomGenerator;
	}

	/* Getter */
	@Override
	public double getCrossoverProbability() {
		return this.crossoverProbability;
	}

	/* Setter */
	public void setCrossoverProbability(double crossoverProbability) {
		this.crossoverProbability = crossoverProbability;
	}

	@Override
	public int getNumberOfRequiredParents() {
		return 1;
	}

	@Override
	public int getNumberOfGeneratedChildren() {
		return 1;
	}

	@Override
	public List<IntegerSolution> execute(List<IntegerSolution> solutions) {
		Check.isNotNull(solutions);
		Check.that(solutions.size() == 1, "There must be single parent instead of " + solutions.size());
		return doCrossover(crossoverProbability, solutions.get(0));
	}

	/**
	 * 後件部の学習はここではしない
	 * @param probability
	 * @param _parent
	 * @return
	 */
	public List<IntegerSolution> doCrossover(double probability, IntegerSolution _parent) {
		// Cast IntegerSolution to PittsburghSolution
		PittsburghSolution parent = (PittsburghSolution)_parent;

		List<IntegerSolution> generatedMichiganSolution = new ArrayList<>();

		/* Step 1. Calculate number of all of generating rules. */
		int numberOfRulesOnParent = ((RuleBasedClassifier)parent.getClassifier()).getRuleNum();
		int numberOfGeneratingRules = (int)((numberOfRulesOnParent - 0.00001)*Consts.RULE_CHANGE_RT) + 1;

		/* Step 2. Calculate numbers of rules generated by GA and Heuristic rule generation method. */
		int numberOfHeuristic;
		if(numberOfGeneratingRules % 2 == 0) {
			numberOfHeuristic = numberOfGeneratingRules/2;
		}
		else {
			int plus = selectRandomGenerator.getRandomValue(0, 1);
			numberOfHeuristic = (numberOfGeneratingRules-1)/2 + plus;
		}

		/* Step 3. Heuristic Rule Generation */
		@SuppressWarnings("unchecked")
		List<Integer> erroredPatterns = (List<Integer>)parent.getAttribute((new ErroredPatternsAttribute<IntegerSolution>()).getAttributeId());
		DataSet train = consequentFactory.getTrain();
		//誤識別パターンが足りないor無い場合は，ランダムなパターンをリストに追加
		int NumberOfLack = numberOfHeuristic - erroredPatterns.size();
		for(int i = 0; i < NumberOfLack; i++) {
			int id = train.getPattern(Random.getInstance().getGEN()
						.nextInt(train.getDataSize()))
					.getID();
			erroredPatterns.add(id);
		}
		//Sampling patterns without replacement from erroredPatterns.
		Integer[] erroredPatternsIdx = GeneralFunctions.samplingWithout(erroredPatterns.size(),
																		numberOfHeuristic,
																		Random.getInstance().getGEN());
		for(int i = 0; i < erroredPatternsIdx.length; i++) {
			Pattern pattern = train.getPatternWithID(erroredPatterns.get(erroredPatternsIdx[i]));
			Antecedent generatedAntecedent = heuristicRuleGeneration.heuristicRuleGeneration(pattern);
			MichiganSolution michiganSolution = (MichiganSolution)parent.getMichiganPopulation().get(0).copy();

			for(int n = 0; n < generatedAntecedent.getDimension(); n++) {
				michiganSolution.setVariable(n, generatedAntecedent.getAntecedentIndexAt(n));
			}

			generatedMichiganSolution.add(michiganSolution);
		}

		/* Step 4. Rule Generation by Genetic Algorithm - Michigan-style GA */
		int NumberOfGA = numberOfGeneratingRules - numberOfHeuristic;
		/* Crossover: Uniform crossover */
		CrossoverOperator<IntegerSolution> crossover = new UniformCrossover(Consts.MICHIGAN_CROSS_RT);
		/* Mutation: Michigan-style specific mutation operation */
		double mutationProbability = 1.0 / (double)train.getNdim();
		MutationOperator<IntegerSolution> mutation = new MichiganMutation(mutationProbability,
																		  heuristicRuleGeneration.getKnowledge(),
																		  train);
		/* Mating Selection: Binray tournament */
		int tournamentSize = 2;
		int matingPoolSize = NumberOfGA *
							crossover.getNumberOfRequiredParents() / crossover.getNumberOfGeneratedChildren();
		MatingPoolSelection<IntegerSolution> selection = new NaryTournamentMatingPoolSelection<>(
														tournamentSize,
														matingPoolSize,
														new ObjectiveComparator<>(0, ObjectiveComparator.Ordering.DESCENDING));
		/* == GA START == */
		/* Mating Selection */
		List<IntegerSolution> matingPopulation = selection.select(parent.getMichiganPopulation());
		/* Offspring Generation */
		List<IntegerSolution> generatedSolutionByGA = new ArrayList<>();
		int numberOfParents = crossover.getNumberOfRequiredParents();
		for(int i = 0; i < matingPoolSize; i+= numberOfParents) {
			List<IntegerSolution> parents = new ArrayList<>();
			for(int j = 0; j < numberOfParents; j++) {
				parents.add(matingPopulation.get(i + j));
			}
			/* Crossover: Uniform crossover */
			List<IntegerSolution> offspring = crossover.execute(parents);
			/* Mutation */
			for(IntegerSolution s : offspring) {
				mutation.execute(s);
				generatedSolutionByGA.add(s);
				if(generatedSolutionByGA.size() == NumberOfGA) {
					break;
				}
			}
		}
		/* == GA END == */

		/* Replacement: Single objective maximization repelacement */
		Replacement<IntegerSolution> replacement = new RuleAdditionStyleReplacement();
		// Merge rules (generated by Heuristic) and rules (generated by GA)
		generatedMichiganSolution.addAll(generatedSolutionByGA);
		List<IntegerSolution> currentList = new ArrayList<>();
		//Deep copy
		for(int i = 0; i < parent.getMichiganPopulation().size(); i++) {
			currentList.add((IntegerSolution)parent.getMichiganPopulation().get(i).copy());
		}
		currentList = replacement.replace(currentList, generatedMichiganSolution);

		/* Pittsburgh solution */
		IntegerSolution child = parent.copy();
		// Radix sort Michigan solution list
		SortMichiganPopulation.radixSort(currentList);
		/* Set variables */
		((PittsburghSolution)child).setMichiganPopulation(currentList);

		List<IntegerSolution> offspring = new ArrayList<>();
		offspring.add(child);
		return offspring;
	}


	@Deprecated
	/**
	 * 交叉操作内で後件部の学習をしてしまうため，
	 * 突然変異操作を行うと再学習が必要となり，学習回数（計算コスト）が無駄になる．
	 * 使用予定が無ければ，廃止推奨．
	 * @param probability
	 * @param parent ピッツバーグ型の個体1体
	 * @return
	 */
	public List<IntegerSolution> doCrossover2(double probability, IntegerSolution parent){
		// Cast IntegerSolution to PittsburghSolution
		PittsburghSolution pittsburghParent = (PittsburghSolution)parent;
		List<Pair<Integer, Integer>> boundsMichigan = ((MichiganSolution)pittsburghParent.getMichiganPopulation().get(0)).getBounds();
		int numberOfObjectivesMichigan = ((MichiganSolution)pittsburghParent.getMichiganPopulation().get(0)).getNumberOfObjectives();
		int numberOfConstraintsMichigan = ((MichiganSolution)pittsburghParent.getMichiganPopulation().get(0)).getNumberOfConstraints();

		List<IntegerSolution> michiganPopulation = new ArrayList<>();

		/* Step 1. Calculate number of all of generating rules. */
		int numberOfRulesOnParent = ((RuleBasedClassifier)pittsburghParent.getClassifier()).getRuleNum();
		int numberOfGeneratingRules = (int)((numberOfRulesOnParent - 0.00001)*Consts.RULE_CHANGE_RT) + 1;

		/* Step 2. Calculate numbers of rules generated by GA and Heuristic rule generation method. */
		int numberOfHeuristic;
		if(numberOfGeneratingRules % 2 == 0) {
			numberOfHeuristic = numberOfGeneratingRules/2;
		}
		else {
			int plus = selectRandomGenerator.getRandomValue(0, 1);
			numberOfHeuristic = (numberOfGeneratingRules-1)/2 + plus;
		}

		/* Step 3. Heuristic Rule Generation */
		@SuppressWarnings("unchecked")
		List<Integer> erroredPatterns = (List<Integer>)parent.getAttribute((new ErroredPatternsAttribute<IntegerSolution>()).getAttributeId());
		DataSet train = consequentFactory.getTrain();
		//誤識別パターンが足りないor無い場合は，ランダムなパターンをリストに追加
		int NumberOfLack = numberOfHeuristic - erroredPatterns.size();
		for(int i = 0; i < NumberOfLack; i++) {
			int id = train.getPattern(Random.getInstance().getGEN()
						.nextInt(train.getDataSize()))
					.getID();
			erroredPatterns.add(id);
		}
		//Sampling patterns without replacement from erroredPatterns.
		Integer[] erroredPatternsIdx = GeneralFunctions.samplingWithout(erroredPatterns.size(),
																		numberOfHeuristic,
																		Random.getInstance().getGEN());
		for(int i = 0; i < erroredPatternsIdx.length; i++) {
			Pattern pattern = train.getPatternWithID(erroredPatterns.get(erroredPatternsIdx[i]));
			Antecedent generatedAntecedent = heuristicRuleGeneration.heuristicRuleGeneration(pattern);
			Consequent consequent = consequentFactory.learning(generatedAntecedent);
			MichiganSolution michiganSolution = new MichiganSolution(boundsMichigan,
																	 numberOfObjectivesMichigan,
																	 numberOfConstraintsMichigan,
																	 generatedAntecedent, consequent);
			michiganPopulation.add(michiganSolution);
		}

		/* Step 4. Rule Generation by Genetic Algorithm - Michigan-style GA */
		int NumberOfGA = numberOfGeneratingRules - numberOfHeuristic;
		/* Crossover: Uniform crossover */
		CrossoverOperator<IntegerSolution> crossover = new UniformCrossover(Consts.MICHIGAN_CROSS_RT);
		/* Mutation: Michigan-style specific mutation operation */
		double mutationProbability = 1.0 / (double)train.getNdim();
		MutationOperator<IntegerSolution> mutation = new MichiganMutation(mutationProbability,
																		  heuristicRuleGeneration.getKnowledge(),
																		  train);
		/* Variation: */
		Variation<IntegerSolution> variation = new MichiganSolutionVariation<>(
													NumberOfGA,	// offspringSize
													crossover, mutation,
													heuristicRuleGeneration.getKnowledge(),
													consequentFactory);
		/* Mating Selection: Binray tournament */
		int tournamentSize = 2;
		MatingPoolSelection<IntegerSolution> selection = new NaryTournamentMatingPoolSelection<>(
														tournamentSize,
														variation.getMatingPoolSize(),
														new ObjectiveComparator<>(0, ObjectiveComparator.Ordering.DESCENDING));
		/* == GA START == */
		// Mating Selection
		List<IntegerSolution> matingPopulation = selection.select(pittsburghParent.getMichiganPopulation());
		// Offspring Generation
		List<IntegerSolution> generatedSolutionByGA = variation.variate(pittsburghParent.getMichiganPopulation(),
																		matingPopulation);

		/* Replacement: Single objective maximization repelacement */
		Replacement<IntegerSolution> replacement = new RuleAdditionStyleReplacement();
		// Merge rules (generated by Heuristic) and rules (generated by GA)
		michiganPopulation.addAll(generatedSolutionByGA);

		List<IntegerSolution> currentList = new ArrayList<>();
		//Deep copy
		for(int i = 0; i < pittsburghParent.getMichiganPopulation().size(); i++) {
			currentList.add((IntegerSolution)pittsburghParent.getMichiganPopulation().get(i).copy());
		}
		currentList = replacement.replace(currentList, michiganPopulation);


		// Pittsburgh solution
		IntegerSolution child = new PittsburghSolution(
									pittsburghParent.getBounds(),
									pittsburghParent.getNumberOfObjectives(),
									currentList,
									((RuleBasedClassifier)pittsburghParent.getClassifier()).getClassification() );

		List<IntegerSolution> offspring = new ArrayList<>();
		offspring.add(child);

		return offspring;
	}


}




























