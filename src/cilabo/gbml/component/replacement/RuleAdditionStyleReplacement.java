package cilabo.gbml.component.replacement;

import java.util.Collections;
import java.util.List;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import cilabo.main.Consts;

/**
 * FAN2021の面﨑論文で発表されているルール追加型ミシガン操作.
 *
 */
public class RuleAdditionStyleReplacement
	implements Replacement<IntegerSolution> {
	public List<IntegerSolution> replace(List<IntegerSolution> currentList, List<IntegerSolution> offspringList) {

		// 親個体をfitness順にソートする
		Collections.sort(currentList,
						 new ObjectiveComparator<IntegerSolution>(0, ObjectiveComparator.Ordering.DESCENDING));

		// 最大ルール数を超えるかどうかを判定
		int NumberOfReplacement = 0;
		if( Consts.MAX_RULE_NUM < (currentList.size() + offspringList.size()) ) {
			NumberOfReplacement = (currentList.size() + offspringList.size()) - Consts.MAX_RULE_NUM;
		}

		// Replace rules from bottom of list.
		for(int i = 0; i < NumberOfReplacement; i++) {
			currentList.set( (currentList.size()-1) - i , offspringList.get(i));
		}
		// Add rules
		for(int i = NumberOfReplacement; i < offspringList.size(); i++) {
			currentList.add(offspringList.get(i));
		}

		return currentList;
	}

}
