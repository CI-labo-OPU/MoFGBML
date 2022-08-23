package cilabo.gbml.problem.impl.michigan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.fuzzy.rule.RejectedRule;
import cilabo.fuzzy.rule.Rule;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;
import cilabo.fuzzy.rule.antecedent.factory.RandomInitialization;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
import cilabo.gbml.problem.AbstractMichiganGBML_Problem;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.gbml.solution.util.attribute.NumberOfWinner;

public class ProblemMichiganFGBML<S extends Solution<?>> extends AbstractMichiganGBML_Problem<S> {
	// ************************************************************
	// Fields
	private DataSet evaluationDataset;

	// ************************************************************
	// Constructor
	public ProblemMichiganFGBML(int seed, DataSet train) {
		this.evaluationDataset = train;
		setNumberOfVariables(train.getNdim());
		setNumberOfObjectives(1);
		setNumberOfConstraints(0);
		setName("MichiganFGBML");

		// Initialization
		HomoTriangleKnowledgeFactory.builder()
				.dimension(train.getNdim())
				.params(HomoTriangle_2_3_4_5.getParams())
				.build()
				.create();
		AntecedentFactory antecedentFactory = RandomInitialization.builder()
				.seed(seed)
				.knowledge(Knowledge.getInstace())
				.train(train)
				.build();
		ConsequentFactory consequentFactory = MoFGBML_Learning.builder()
				.train(train)
				.build();
		setAntecedentFactory(antecedentFactory);
		setConsequentFactory(consequentFactory);

		// Boundary
	    List<Integer> lowerLimit = new ArrayList<>(getNumberOfVariables());
	    List<Integer> upperLimit = new ArrayList<>(getNumberOfVariables());
	    for (int i = 0; i < getNumberOfVariables(); i++) {
	      lowerLimit.add(0);
	      upperLimit.add(Knowledge.getInstace().getFuzzySetNum(i));
	    }
	    setVariableBounds(lowerLimit, upperLimit);
	}

	// ************************************************************
	// Methods

	/* Setter */
	public void setEvaluationDataset(DataSet evaluationDataset) {
		this.evaluationDataset = evaluationDataset;
	}

	public List<S> michiganEvaluate(List<S> population) {
		if( population.size() == 0 ||
			population.get(0).getClass() != MichiganSolution.class)
			return null;

		// Clear fitness
		population.stream().forEach(s -> s.setObjective(0, 0.0));

		// for Evaluation without Duplicates
		Map<String, S> map = new HashMap<>();

		// Make classifier
		RuleBasedClassifier classifier = new RuleBasedClassifier();

		Classification classification = new SingleWinnerRuleSelection();
		classifier.setClassification(classification);

		for(int i = 0; i < population.size(); i++) {
			MichiganSolution solution = (MichiganSolution)population.get(i);
			solution.setAttribute((new NumberOfWinner<S>()).getAttributeId(), 0);
			Rule rule = solution.getRule();
			if(!map.containsKey(rule.toString())) {
				map.put(rule.toString(), population.get(i));
				classifier.addRule(rule);
			}
		}

		// Evaluation
		for(int i = 0; i < evaluationDataset.getDataSize(); i++) {
			Pattern pattern = evaluationDataset.getPattern(i);
			Rule winnerRule = classifier.classify(pattern.getInputVector());
			// If output is rejected then continue next pattern.
			if(winnerRule.getClass() == RejectedRule.class) continue;

			/* Add Attribute NumberOfWinner for winner rule. */
			String attributeId = (new NumberOfWinner<S>()).getAttributeId();
			Integer Nwin = (Integer)map.get(winnerRule.toString()).getAttribute(attributeId);
			map.get(winnerRule.toString()).setAttribute((new NumberOfWinner<S>()).getAttributeId(), Nwin+1);

			/* If a winner rule correctly classify a pattern,
			 * then the winner rule's fitness will be incremented. */
			if( winnerRule != null &&
				pattern.getTrueClass().toString()
					.equals(winnerRule.getConsequent().getClassLabel().toString())) {
				S winnerMichigan = map.get(winnerRule.toString());
				((IntegerSolution)winnerMichigan).setObjective(0, ((IntegerSolution)winnerMichigan).getObjective(0)+1);
			}
		}

		return population;
	}

	@Override
	public MichiganSolution createSolution() {
		Antecedent antecedent = antecedentFactory.create();
		Consequent consequent = consequentFactory.learning(antecedent);

		MichiganSolution solution = new MichiganSolution(this.getBounds(),
														 this.getNumberOfObjectives(),
														 this.getNumberOfConstraints(),
														 antecedent,
														 consequent);
		return solution;
	}

	@Override
	public void evaluate(IntegerSolution solution) {
//		solution.setObjective(0, solution.getObjective(0)+1);
		return;
	}


}
