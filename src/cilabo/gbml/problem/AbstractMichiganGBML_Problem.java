package cilabo.gbml.problem;

import java.util.List;

import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.Solution;

import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.classifier.operator.classification.factory.SingleWinnerRuleSelection;
import cilabo.fuzzy.rule.Rule;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.gbml.solution.MichiganSolution;

public abstract class AbstractMichiganGBML_Problem<S extends Solution<?>> extends AbstractIntegerProblem {
	// ************************************************************
	// Fields
	/**  */
	protected AntecedentFactory antecedentFactory;

	/**  */
	protected ConsequentFactory consequentFactory;

	// ************************************************************
	// Constructor

	// ************************************************************
	// Methods


	/* Getters */
	public AntecedentFactory getAntecedentFactory() {
		return this.antecedentFactory;
	}

	public ConsequentFactory getConsequentFactory() {
		return this.consequentFactory;
	}

	/* Setters */
	public void setAntecedentFactory(AntecedentFactory antecedentFactory) {
		this.antecedentFactory = antecedentFactory;
	}

	public void setConsequentFactory(ConsequentFactory consequentFactory) {
		this.consequentFactory = consequentFactory;
	}

	public RuleBasedClassifier population2classifier(List<S> population) {
		if( population.size() == 0 ||
				population.get(0).getClass() != MichiganSolution.class)
				return null;

		// Make classifier
		RuleBasedClassifier classifier = new RuleBasedClassifier();
		Classification classification = new SingleWinnerRuleSelection();
		classifier.setClassification(classification);

		for(int i = 0; i < population.size(); i++) {
			Rule rule = ((MichiganSolution)population.get(i)).getRule();
			classifier.addRule(rule);
		}
		return classifier;
	}

	@Override
	public String toString() {
		String ln = System.lineSeparator();
		String str = "";
		str += "AntecedentFactory: " + antecedentFactory.getClass().getCanonicalName() + ln;
		str += "ConsequentFactory: " + consequentFactory.getClass().getCanonicalName() + ln;
		return str;
	}

}
