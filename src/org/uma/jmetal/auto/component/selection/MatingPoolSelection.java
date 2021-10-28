package org.uma.jmetal.auto.component.selection;

import org.uma.jmetal.solution.Solution;

import java.util.List;

@FunctionalInterface
public interface MatingPoolSelection<S extends Solution<?>> {
  List<S> select(List<S> solutionList) ;
}
