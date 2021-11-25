package cilabo.labo.developing.twostage;

/**
 * cilabo.gbml.operator.crossover.PittsburghCrossover を参考に作成
 * CrossoverOperatorをimplements
 *
 * 生成される2つの子個体の内、複雑な方を必ず選択
 *
 * cilabo.gbml.operator.crossover.PittsburghCrossoverでは子個体は1つしか作られていないことに注意
 *
 * 親1: {ルールA, ルールB, ルールC, ルールD}
 * 親2: {ルールa, ルールb, ルールc}
 * から子個体作る．
 * -> PittsburghCrossoverでは...
 *     親1から{ルールA, ルールC}を受け継ぐ（ランダムに選択）
 *     親2から{ルールc}を受け継ぐ（ランダムに選択）
 *     -> 子個体Z:{ルールA, ルールC, ルールc}を生成
 *     ※※
 *     ※実際には，受け継いでないルールを集めた
 *     ※子個体Z':{ルールB, ルールD, ルールa, ルールb}も生成されているのと同義
 *     ※-> さらに，子個体Zはルール数3，子個体Z'はルール数4
 *     ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *     ※-> 本研究では，ZとZ'のうちルール数が多い方を子個体として採用する交叉操作を実装したい※
 *     ※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※※
 *
 */
public class PittsburghCrossoverComplexOriented {

}
