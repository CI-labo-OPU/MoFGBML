package cilabo.labo.developing.twostage;

import java.util.List;

import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.solution.Solution;

/**
 * 1stステージ終了時の個体群を
 * 2ndステージの初期個体群とすることで，
 * 疑似的にスムースな探索の流れを実現させる．
 *
 *
 * インスタンス生成時に必ず個体群のListを受け取る
 * create()は，その時受け取った個体群Listをそのまま返す．
 */
public class PopulationCopyCreation<S extends Solution<?>> implements InitialSolutionsCreation<S> {
	  List<S> population;

	  public PopulationCopyCreation(List<S> population) {
		  this.population = population;
	  }

	  @Override
	  public List<S> create() {
		  return this.population;
	  }

}
