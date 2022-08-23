package cilabo.fuzzy.knowledge.factory;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.KnowledgeFactory;
import jfml.term.FuzzyTermType;

public class HomoTriangleKnowledgeFactory implements KnowledgeFactory {
	// ************************************************************
	// Fields

	/** Number of features */
	int dimension;

	/** Parameters of membership functions */
	float[][] params;

	// ************************************************************
	// Constructor
	public HomoTriangleKnowledgeFactory(int dimension, float[][] params) {
		this.dimension = dimension;
		this.params = params;
	}

	// ************************************************************
	// Methods

	@Override
	public void create() {
		int fuzzySetNum = params.length;

		// make fuzzy sets
		final FuzzyTermType[][] fuzzySets = new FuzzyTermType[dimension][fuzzySetNum+1];
		for(int i = 0; i < dimension; i++) {
			//Don't care
			fuzzySets[i][0] = new FuzzyTermType(" 0",
												FuzzyTermType.TYPE_rectangularShape,
												new float[] {-10000f, 1f});
			for(int j = 1; j < fuzzySetNum+1; j++) {
				String name = String.format("%2s", String.valueOf(j));
				int shapeType = FuzzyTermType.TYPE_triangularShape;
				float[] param = params[j-1];
				fuzzySets[i][j] = new FuzzyTermType(name, shapeType, param);
			}
		}

		// Create
		Knowledge knowledge = Knowledge.getInstace();
		knowledge.setFuzzySets(fuzzySets);

		return;
	}

	public static HomoTriangleKnowledgeFactory.KnowledgeBuilder builder() {
		return new KnowledgeBuilder();
	}

	public static class KnowledgeBuilder {
		private int dimension = -1;
		private float[][] params;

		KnowledgeBuilder() {}

		public HomoTriangleKnowledgeFactory.KnowledgeBuilder dimension(int dimension) {
			this.dimension = dimension;
			return this;
		}

		public HomoTriangleKnowledgeFactory.KnowledgeBuilder params(float[][] params) {
			this.params = params;
			return this;
		}

		/**
		 * @param dimension : int
		 * @param params : float[][]
		 */
		public HomoTriangleKnowledgeFactory build() {
			return new HomoTriangleKnowledgeFactory(dimension, params);
		}

	}

}
