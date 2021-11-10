package cilabo.gbml.objectivefunction.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.data.Pattern;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.rule.RejectedRule;
import cilabo.fuzzy.rule.Rule;
import cilabo.gbml.objectivefunction.ObjectiveFunction;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.gbml.solution.util.attribute.ErroredPatternsAttribute;
import cilabo.gbml.solution.util.attribute.NumberOfWinner;

public class ErrorRateForPittsburgh implements ObjectiveFunction<PittsburghSolution, Double> {

	private DataSet data;

	public ErrorRateForPittsburgh(DataSet data) {
		this.data = data;
	}

	public void setData(DataSet data) {
		this.data = data;
	}

	/**
	 * ピッツバーグ個体を受け取って，
	 *  + michiganPopulationの評価（勝利回数・NCP）
	 *  + 誤識別パターンのAttribute付与
	 *  + ピッツバーグ個体の誤識別率計算
	 * を行うメソッド.
	 * @param solution PittsburghSolution
	 * @return Double : error rate
	 */
	@Override
	public Double function(PittsburghSolution solution) {
		List<IntegerSolution> michiganPopulation = solution.getMichiganPopulation();

		/* Clear fitness of michigan population. */
		michiganPopulation.stream().forEach(s -> s.setObjective(0, 0.0));
		/* Clear Attribute of NumberOfWinner for michigan population. */
		michiganPopulation.stream().forEach(s -> s.setAttribute((new NumberOfWinner<>()).getAttributeId(), 0));
		/* Clear Attribute of ErroredPatternsAttribute for pittsburgh population. */
		solution.setAttribute((new ErroredPatternsAttribute<>()).getAttributeId(), new ArrayList<Integer>());

		// for Evaluation without Duplicates
		Map<String, IntegerSolution> map = new HashMap<>();
		for(int i = 0; i < michiganPopulation.size(); i++) {
			MichiganSolution michiganSolution = (MichiganSolution)michiganPopulation.get(i);
			Rule rule = michiganSolution.getRule();
			if(!map.containsKey(rule.toString())) {
				map.put(rule.toString(), michiganSolution);
			}
		}

		// Classification
		RuleBasedClassifier classifier = (RuleBasedClassifier)solution.getClassifier();
		double numberOfErrorPatterns = 0.0;
		for(int i = 0; i < data.getDataSize(); i++) {
			Pattern pattern = data.getPattern(i);
			Rule winnerRule = classifier.classify(pattern.getInputVector());

			// If output is rejected then continue next pattern.
			if(winnerRule.getClass() == RejectedRule.class) {
				/* Add errored pattern Attribute */
				addErroredPattern(solution, pattern.getID());
				continue;
			}

			if( winnerRule != null) {
				/* Add Attribute of NumberOfWinner */
				String attributeId = (new NumberOfWinner<>()).getAttributeId();
				Integer Nwin = (Integer)map.get(winnerRule.toString()).getAttribute(attributeId);
				map.get(winnerRule.toString()).setAttribute(attributeId, Nwin+1);

				/* If a winner rule correctly classify a pattern,
				 * then the winner rule's fitness will be incremented. */
				if(pattern.getTrueClass().toString()
						.equals(winnerRule.getConsequent().getClassLabel().toString()))
				{
					IntegerSolution winnerMichigan = map.get(winnerRule.toString());
					winnerMichigan.setObjective(0, 1+winnerMichigan.getObjective(0));
				}
				/* If a winner rule errored a pattern,
				 * then add the classified pattern to erroredPatterns. */
				else {
					addErroredPattern(solution, pattern.getID());
					numberOfErrorPatterns += 1.0;
				}
			}
		}

		double errorRate = numberOfErrorPatterns / (double)data.getDataSize();
		return errorRate;
	}

	@SuppressWarnings("unchecked")
	private void addErroredPattern(PittsburghSolution solution, int patternID) {
		ArrayList<Integer> erroredList = (ArrayList<Integer>)solution.getAttribute((new ErroredPatternsAttribute<>()).getAttributeId());
		erroredList.add(patternID);
		return;
	}
}
















