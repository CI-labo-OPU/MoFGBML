package cilabo.gbml.objectivefunction;

import org.uma.jmetal.solution.Solution;

public interface ObjectiveFunction<Argument extends Solution<?>, Result> {

	public Result function(Argument solution);
}
