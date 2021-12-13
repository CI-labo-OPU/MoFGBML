package cilabo.gbml.objectivefunction.impl;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.gbml.objectivefunction.ObjectiveFunction;
import cilabo.gbml.solution.MichiganSolution;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.gbml.solution.util.attribute.NumberOfWinner;

public class NumberOfRules implements ObjectiveFunction<PittsburghSolution, Double> {

	/**
	 * ピッツバーグ個体を受け取り，
	 * michiganPopulationの勝利回数Attribute(cilabo.gbml.solution.util.attribute.NumberOfWinner)
	 * が0の個体を削除した後のルール数を計算する.
	 */
	@Override
	public Double function(PittsburghSolution solution) {
		List<IntegerSolution> michiganPopulation = solution.getMichiganPopulation();
		List<IntegerSolution> newMichiganPopulation = new ArrayList<>();
		for(int i = 0; i < michiganPopulation.size(); i++) {
			MichiganSolution michiganSolution = (MichiganSolution)michiganPopulation.get(i);
			Integer numberOfWin = (Integer)michiganSolution.getAttribute((new NumberOfWinner<>()).getAttributeId());
			if(numberOfWin > 0) {
				newMichiganPopulation.add(michiganSolution);
			}
		}

		double numberOfRules;
		// もし勝者ルールが1つも含まれない場合、ルール数を大きくして個体の評価を悪くする．
		// そして、念のため削除前のルール集合を残しておく
		if(newMichiganPopulation.size() == 0) {
			numberOfRules = Double.MAX_VALUE;
			solution.setMichiganPopulation(michiganPopulation);
		}
		else {
			solution.setMichiganPopulation(newMichiganPopulation);
			numberOfRules = solution.getMichiganPopulation().size();
		}

		return numberOfRules;
	}
}
