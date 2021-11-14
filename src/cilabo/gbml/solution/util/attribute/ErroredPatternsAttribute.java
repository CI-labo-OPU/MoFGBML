package cilabo.gbml.solution.util.attribute;

import java.util.Comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.Attribute;

/**
 * {@literal List<Integer>}として，誤識別パターンのIDのリストを保持するAttribute.
 *
 */
public class ErroredPatternsAttribute<S extends Solution<?>> implements Attribute<S> {

	private String attributeId = getClass().getName();
	private Comparator<S> solutionComparator;

	public ErroredPatternsAttribute() {
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
