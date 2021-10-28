package org.uma.jmetal.auto.component.initialsolutionscreation.impl;

import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.AlgorithmDefaultOutputData;
import org.uma.jmetal.util.NormalizeUtils;

import java.util.ArrayList;
import java.util.List;

public class LatinHypercubeSamplingSolutionsCreation
    implements InitialSolutionsCreation<DoubleSolution> {
  private final int numberOfSolutionsToCreate;
  private final DoubleProblem problem;

  public LatinHypercubeSamplingSolutionsCreation(
      DoubleProblem problem, int numberOfSolutionsToCreate) {
    this.problem = problem;
    this.numberOfSolutionsToCreate = numberOfSolutionsToCreate;
  }

  public List<DoubleSolution> create() {
    int[][] latinHypercube = new int[numberOfSolutionsToCreate][problem.getNumberOfVariables()];
    for (int dim = 0; dim < problem.getNumberOfVariables(); dim++) {
      List<Integer> permutation = getPermutation(numberOfSolutionsToCreate);
      for (int v = 0; v < numberOfSolutionsToCreate; v++) {
        latinHypercube[v][dim] = permutation.get(v);
      }
    }

    List<DoubleSolution> solutionList = new ArrayList<>(numberOfSolutionsToCreate);
    for (int i = 0; i < numberOfSolutionsToCreate; i++) {
      DoubleSolution newSolution =
          new DefaultDoubleSolution(problem.getBounds(), problem.getNumberOfObjectives());
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        newSolution.setVariable(
            j,
            NormalizeUtils.normalize(
                latinHypercube[i][j],
                problem.getLowerBound(j),
                problem.getUpperBound(j),
                0,
                numberOfSolutionsToCreate));
      }

      solutionList.add(newSolution);
    }

    return solutionList;
  }

  private List<Integer> getPermutation(int permutationLength) {
    List<Integer> randomSequence = new ArrayList<>(permutationLength);

    for (int j = 0; j < permutationLength; j++) {
      randomSequence.add(j);
    }

    java.util.Collections.shuffle(randomSequence);

    return randomSequence;
  }

  public static void main(String[] args) {
    DoubleProblem problem = new ZDT4(2);
    int numberOfSolutionsToCreate = 100;
    List<DoubleSolution> solutions =
        new LatinHypercubeSamplingSolutionsCreation(problem, numberOfSolutionsToCreate).create();
    AlgorithmDefaultOutputData.generateMultiObjectiveAlgorithmOutputData(solutions, 0);
  }
}
