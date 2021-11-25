package cilabo.labo.developing.twostage;

/**
 * org.uma.jmetal.component.replacement.Replacementをimplements
 *
 * org.uma.jmetal.component.replacement.impl.RankingAndDensityEstimatorReplacement
 * を参考に作成
 * しかし，replace()の中身のみ実装すればOK
 *
 * currentListとoffspringListの合併個体群を
 *  1. 識別精度の良い順（f0の良い順）
 *  2. 識別精度が同じなら，複雑性の小さい順（f1の小さい順）
 * にソートして、上からpopulationSizeだけ返す
 *
 * これは1stステージのReplacementとして使用される．
 */
public class AccuracyOrientedReplacement {

}
