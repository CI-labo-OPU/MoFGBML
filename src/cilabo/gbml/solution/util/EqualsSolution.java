package cilabo.gbml.solution.util;

import org.uma.jmetal.solution.Solution;

public class EqualsSolution {

	public static boolean equals(Solution<?> solution1, Solution<?> solution2) {
		boolean flg = false;
		if(solution1.getVariables().toString().equals(solution2.getVariables().toString())) flg = true;
		return flg;
	}
}
