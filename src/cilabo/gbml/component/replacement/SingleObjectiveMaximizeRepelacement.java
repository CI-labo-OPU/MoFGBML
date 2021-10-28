package cilabo.gbml.component.replacement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.uma.jmetal.component.replacement.impl.SingleObjectiveGenerationalReplacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

public class SingleObjectiveMaximizeRepelacement<S extends Solution<?>>
	extends SingleObjectiveGenerationalReplacement<S>
{
	@Override
	public List<S> replace(List<S> currentList, List<S> offspringList){
		List<S> jointPopulation = new ArrayList<>();
		jointPopulation.addAll(currentList);
		jointPopulation.addAll(offspringList);

		Collections.sort(jointPopulation,
				new ObjectiveComparator<S>(0, ObjectiveComparator.Ordering.DESCENDING) );

		while(jointPopulation.size() > currentList.size()) {
			jointPopulation.remove(jointPopulation.size() - 1);
		}
		return jointPopulation;
	}

}
