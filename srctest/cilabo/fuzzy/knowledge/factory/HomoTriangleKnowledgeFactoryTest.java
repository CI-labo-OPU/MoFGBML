package cilabo.fuzzy.knowledge.factory;

import org.junit.Test;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_3_4_5;
import jfml.term.FuzzyTermType;

public class HomoTriangleKnowledgeFactoryTest {

	@Test
	public void testCreate() {
		int dimension = 3;
		float[][] params = HomoTriangle_3_4_5.getParams();

		//actual
		FuzzyTermType[][] fuzzySets = new FuzzyTermType[dimension][params.length+1];
		for(int i = 0; i < dimension; i++) {
			//Don't care
			fuzzySets[i][0] = new FuzzyTermType(" 0", FuzzyTermType.TYPE_rectangularShape, new float[] {-10000f, 1f});
			for(int j = 0; j < params.length; j++) {
				fuzzySets[i][j+1] = new FuzzyTermType(String.format("%2s", String.valueOf(j+1)), FuzzyTermType.TYPE_triangularShape, params[j]);
			}
		}
		Knowledge.getInstace().setFuzzySets(fuzzySets);

		HomoTriangleKnowledgeFactory.builder()
							.dimension(dimension)
							.params(params)
							.build()
							.create();

//		assertEquals(expected.toString(), Knowledge.getInstace());
	}
}
