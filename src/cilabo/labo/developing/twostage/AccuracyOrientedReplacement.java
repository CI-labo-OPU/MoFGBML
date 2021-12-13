package cilabo.labo.developing.twostage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.solution.Solution;

/**
 * org.uma.jmetal.component.replacement.Replacementをimplements
 *
 * org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement
 * を参考に作成
 * しかし，replace()の中身のみ実装すればOK
 *
 * currentListとoffspringListの合併個体群を
 *  1. 識別精度の良い順（f0の良い順）
 *  2. 識別精度が同じなら，複雑性の小さい順（f1の小さい順）
 * にソートして、上からpopulationSizeだけ返す
 *
 * これは1stステージのReplacementとして使用される．
 */
public class AccuracyOrientedReplacement<S extends Solution<?>> implements Replacement<S> {

	//TODO 未実装
	@Override
	public List<S> replace(List<S> currentList, List<S> offspringList) {
		List<S> jointPopulation = new ArrayList<>();
		jointPopulation.addAll(currentList);
		jointPopulation.addAll(offspringList);


		List<S> resultList = new ArrayList<>();;

		/** 辞書式ソート
		 * 第一に識別性能(第一目的)が優れた個体順にソートする．
		 * 識別性能が等しい場合、ルール数(第二目的)が少ない個体を優れた個体とする．
		 */
		jointPopulation
		= jointPopulation.stream()
			.sorted(new Comparator<S>() {
				@Override
				public int compare(S p1, S p2) {
					double error_p1 = p1.getObjective(0);
					double error_p2 = p2.getObjective(0);
					double complex_p1 = p1.getObjective(1);
					double complex_p2 = p2.getObjective(1);

					if(error_p1 > error_p2) {
						return 1;
					}
					else if(error_p1 < error_p2) {
						return -1;
					}
					else {
						if(complex_p1 > complex_p2) {
							return 1;
						}
						else if(complex_p1 < complex_p2) {
							return -1;
						}
						else {
							return 0;
						}
					}
				}
			}
			).collect(Collectors.toList());

		for(int i = 0; i < currentList.size(); i++) {
			resultList.add(jointPopulation.get(i));
		}

		return resultList;
	}

}
