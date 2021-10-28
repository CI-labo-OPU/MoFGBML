package cilabo.fuzzy.knowledge;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_3_4_5;

public class KnowledgeTest {

	@Test
	public void testHomoTriangle_3_4_5() {
		int dimension = 3;
		float[][] params = HomoTriangle_3_4_5.getParams();
		Knowledge knowledge = HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();

		double diff = 0.00000001;

		// Don't care
		assertEquals(1, knowledge.getMembershipValue(0, 0, 0), diff);
		assertEquals(1, knowledge.getMembershipValue(0.5, 0, 0), diff);
		assertEquals(1, knowledge.getMembershipValue(1, 0, 0), diff);

		// Medium
		int H = 2;
		assertEquals(0.0, knowledge.getMembershipValue(0.0, 0, H), diff);
		assertEquals(1.0, knowledge.getMembershipValue(0.5, 0, H), diff);
		assertEquals(0.0, knowledge.getMembershipValue(1.0, 0, H), diff);
	}
}
