package cilabo.gbml.component.replacement;

import java.util.Collections;
import java.util.List;

import org.uma.jmetal.component.replacement.impl.SingleObjectiveGenerationalReplacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

public class SingleObjectiveMaximizeReplacementWithoutOffspringFitness<S extends Solution<?>>
	extends SingleObjectiveGenerationalReplacement<S>
{
	@Override
	public List<S> replace(List<S> currentList, List<S> offspringList) {
		// Sort current population by fitness value
		Collections.sort(currentList,
					new ObjectiveComparator<S>(0, ObjectiveComparator.Ordering.DESCENDING));

		for(int i = 0; i < offspringList.size(); i++) {
			currentList.remove(currentList.size() - (1+i));
			currentList.add(offspringList.get(i));
		}
		return currentList;
	}

}
