package org.uma.jmetal.component.evaluation;

import java.util.List;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

public interface Evaluation<S extends Solution<?>> {
  List<S> evaluate(List<S> solutionList, Problem<S> problem) ;
  int getComputedEvaluations() ;
}
