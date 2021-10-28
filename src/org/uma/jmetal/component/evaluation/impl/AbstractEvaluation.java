package org.uma.jmetal.component.evaluation.impl;

import java.util.List;

import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public abstract  class AbstractEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private SolutionListEvaluator<S> evaluator ;
  private int numberOfComputedEvaluations ;

  public AbstractEvaluation(SolutionListEvaluator<S> evaluator) {
    this.numberOfComputedEvaluations = 0 ;
    this.evaluator = evaluator ;
  }

  @Override
  public List<S> evaluate(List<S> solutionList, Problem<S> problem) {
    evaluator.evaluate(solutionList, problem) ;

    numberOfComputedEvaluations += solutionList.size() ;

    return solutionList;
  }

  public int getComputedEvaluations() {
    return numberOfComputedEvaluations ;
  }
}
