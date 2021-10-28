package org.uma.jmetal.component.densityestimator;

import java.util.List;

import org.uma.jmetal.solution.util.attribute.Attribute;

/**
 * Interface representing implementations to compute the crowding distance
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface DensityEstimator<S> extends Attribute<S> {
  void computeDensityEstimator(List<S> solutionSet) ;

  List<S> sort(List<S> solutionList) ;
}
