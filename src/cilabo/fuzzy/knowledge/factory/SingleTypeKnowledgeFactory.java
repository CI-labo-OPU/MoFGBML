package cilabo.fuzzy.knowledge.factory;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.KnowledgeFactory;
import jfml.term.FuzzyTermType;

/**
 * Create single Shape Knowledge.
 *
 * @author hirot
 *
 */
public class SingleTypeKnowledgeFactory implements KnowledgeFactory {

	/** Number of features */
	int dimension;

	/** Parameters of membership functions */
	float[][] params;

	/** name of fuzzyTerm's Shape */
	String fuzzyTermShapeName;

	public SingleTypeKnowledgeFactory(int dimension, float[][] params, String fuzzyTermShapeName) {
		super();
		this.dimension = dimension;
		this.params = params;
		this.fuzzyTermShapeName = fuzzyTermShapeName;
	}

	@Override
	public Knowledge create() {
		int fuzzySetNum = params.length;
		int fuzzyTermTypeID;
		String name;

		switch(this.fuzzyTermShapeName) {
			case "gaussian":
				fuzzyTermTypeID = FuzzyTermType.TYPE_gaussianShape;
				name = "HomoGaussian";
				break;
			case "trapezoid":
				fuzzyTermTypeID = FuzzyTermType.TYPE_trapezoidShape;
				name = "HomoTrapezoid";
				break;
			case "interval":
				fuzzyTermTypeID = FuzzyTermType.TYPE_rectangularShape;
				name = "HomoInterval";
				break;
			case "triangle":
			default:
				fuzzyTermTypeID = FuzzyTermType.TYPE_triangularShape;
				name = "HomoFuzzy";
				break;
		}

		// make fuzzy sets
		final FuzzyTermType[][] fuzzySets = new FuzzyTermType[dimension][fuzzySetNum+1];
		for(int i = 0; i < dimension; i++) {
			//Don't care
			fuzzySets[i][0] = new FuzzyTermType(" 0",
												FuzzyTermType.TYPE_rectangularShape,
												new float[] {-10000f, 1f});
			for(int j = 1; j < fuzzySetNum+1; j++) {
				name += String.format("%2s", String.valueOf(j));
				float[] param = params[j-1];
				fuzzySets[i][j] = new FuzzyTermType(name, fuzzyTermTypeID, param);
			}
		}

		// Create
		Knowledge knowledge = new Knowledge();
		knowledge.setFuzzySets(fuzzySets);

		return knowledge;
	}

	public static SingleTypeKnowledgeFactory.KnowledgeBuilder builder() {
		return new KnowledgeBuilder();
	}

	public static class KnowledgeBuilder {
		private int dimension = -1;
		private float[][] params;
		private String fuzzyTermShapeName = "triangle";

		KnowledgeBuilder() {}

		public SingleTypeKnowledgeFactory.KnowledgeBuilder dimension(int dimension) {
			this.dimension = dimension;
			return this;
		}

		public SingleTypeKnowledgeFactory.KnowledgeBuilder params(float[][] params) {
			this.params = params;
			return this;
		}

		public SingleTypeKnowledgeFactory.KnowledgeBuilder fuzzyTermShapeName(String fuzzyTermShapeName) {
			this.fuzzyTermShapeName = fuzzyTermShapeName;
			return this;
		}

		/**
		 * @param dimension : int
		 * @param params : float[][]
		 * @param fuzzyTermShape : String
		 */
		public SingleTypeKnowledgeFactory build() {
			return new SingleTypeKnowledgeFactory(dimension, params, fuzzyTermShapeName);
		}

	}

}
