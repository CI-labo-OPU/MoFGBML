package cilabo.labo.developing.twostage;

import java.util.Map;

import org.uma.jmetal.component.termination.Termination;

/**
 *
 * org.uma.jmetal.component.termination.impl.TerminationByEvaluations を参考に作成
 *
 * 主にisMet()の中身で，ステージ切り替えの条件を実装
 *
 */
public class ChangeStageTermination implements Termination {
	//Testとして，一定世代数で切り替わるようにする
	int changeNumberOfEvaluations;

	public ChangeStageTermination(int changeNumberOfEvaluations) {
		this.changeNumberOfEvaluations = changeNumberOfEvaluations;
	}

	@Override
	public boolean isMet(Map<String, Object> algorithmStatusData) {
		int currentNumberOfEvaluations = (int) algorithmStatusData.get("EVALUATIONS");

		return (currentNumberOfEvaluations >= changeNumberOfEvaluations);
	}
}
