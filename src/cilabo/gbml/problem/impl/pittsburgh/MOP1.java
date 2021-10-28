package cilabo.gbml.problem.impl.pittsburgh;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.antecedent.AntecedentFactory;
import cilabo.fuzzy.rule.antecedent.factory.RandomInitialization;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
import cilabo.gbml.problem.AbstractPitssburghGBML_Problem;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.main.Consts;

/**
 * The first multi-objective optimization problem definition on MoFGBML.
 * The first objective is minimizing classification error for the training dataset.
 * The second objective is minimizing number of rules.
 *
 */
public class MOP1<S extends Solution<?>> extends AbstractPitssburghGBML_Problem<S> {
	// ************************************
	private Knowledge knowledge;
	private Classification classification;
	private DataSet evaluationDataset;

	// ************************************
	public MOP1(int seed, DataSet train) {
		this.evaluationDataset = train;
		setNumberOfVariables(train.getNdim()*Consts.MAX_RULE_NUM);	// 可変だが、最大値で設定
		setNumberOfObjectives(2);
		setNumberOfConstraints(0);
		setName("MOP1_minError_and_minNrule");

		// Initialization
		this.knowledge = HomoTriangleKnowledgeFactory.builder()
				.dimension(train.getNdim())
				.params(HomoTriangle_2_3_4_5.getParams())
				.build()
				.create();
		//TODO Heuristic rule generation methodのAntecedentFactoryに変更
		AntecedentFactory antecedentFactory = RandomInitialization.builder()
				.seed(seed)
				.knowledge(knowledge)
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
		for(int i = 0; i < getNumberOfVariables(); i++) {
			lowerLimit.add(0);
			upperLimit.add(knowledge.getFuzzySetNum(i));
		}
		setVariableBounds(lowerLimit, upperLimit);
	}

	// ************************************

	/* Getter */
	public Knowledge getKnowledge() {
		return this.knowledge;
	}

	/* Setter */
	public void setEvaluationDataset(DataSet evaluationDataset) {
		this.evaluationDataset = evaluationDataset;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	@Override
	public PittsburghSolution createSolution() {
		List<IntegerSolution> michiganPopulation = new ArrayList<>();
		for(int i = 0; i < Consts.INITIATION_RULE_NUM; i++) {
			Antecedent antecedent = antecedentFactory.create();
			Consequent consequent = consequentFactory.learning(antecedent);

			MichiganSolution solution = new MichiganSolution(this.getBounds(),
															 this.getNumberOfObjectives(),
															 this.getNumberOfConstraints(),
															 antecedent,
															 consequent);
			michiganPopulation.add(solution);
		}

		PittsburghSolution solution = new PittsburghSolution(this.getBounds(),
								 							this.getNumberOfObjectives(),
								 							michiganPopulation,
								 							classification);
		return solution;
	}

	//TODO
	@Override
	public void evaluate(IntegerSolution solution) {

	}
}
