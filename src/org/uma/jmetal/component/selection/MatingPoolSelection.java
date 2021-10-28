package org.uma.jmetal.component.selection;

import java.util.List;

import org.uma.jmetal.solution.Solution;

@FunctionalInterface
public interface MatingPoolSelection<S extends Solution<?>> {
  List<S> select(List<S> solutionList) ;
}
