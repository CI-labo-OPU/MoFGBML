package cilabo.gbml.objectivefunction.impl;

import java.util.List;

import org.uma.jmetal.solution.integersolution.IntegerSolution;

import cilabo.data.DataSet;
import cilabo.gbml.objectivefunction.ObjectiveFunction;
import cilabo.gbml.solution.PittsburghSolution;

public class GmeanForPittsburgh implements ObjectiveFunction<PittsburghSolution, Double> {

	private DataSet data;

	public GmeanForPittsburgh(DataSet data) {
		this.data = data;
	}

	public void setData(DataSet data) {
		this.data = data;
	}

	/**
	 * ピッツバーグ個体を受け取って，
	 *  + michiganPopulationの評価（勝利回数・NCP）
	 *  + 誤識別パターンのAttribute付与
	 *  + ピッツバーグ個体のGmean計算
	 * を行うメソッド.
	 * @param solution PittsburghSolution
	 * @return Double : Gmean
	 */
	@Override
	public Double function(PittsburghSolution solution) {
		List<IntegerSolution> michiganPopulation = solution.getMichiganPopulation();
	}

}
