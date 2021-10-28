package cilabo.gbml.solution.util.attribute;

import java.util.Comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.Attribute;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;

public class NumberOfWinner<S extends Solution<?>> implements Attribute<S> {

	private String attributeId = getClass().getName();
	private Comparator<S> solutionComparator;

	public NumberOfWinner() {
		// The higher value is better.
		solutionComparator = new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING);
	}

	@Override
	public String getAttributeId() {
		return attributeId;
	}

	@Override
	public Comparator<S> getSolutionComparator() {
		return solutionComparator;
	}


}
