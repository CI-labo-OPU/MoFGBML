package cilabo.fuzzy.rule.antecedent.factory;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.rule.antecedent.Antecedent;

public class AllCombinationAntecedentFactoryTest {
	@Test
	public void testAllCombination() {
		int dimension = 2;
		float[][] params = new float[][]
		{	//3分割
			new float[] {0f, 0f, 0.5f},
			new float[] {0f, 0.5f, 1f},
			new float[] {0.5f, 1f, 1f}
		};
		HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();

		AllCombinationAntecedentFactory factory = AllCombinationAntecedentFactory.builder()
													.knowledge(Knowledge.getInstance())
													.build();

		String[] actual = new String[]
		{	" 0,  0", " 0,  1", " 0,  2", " 0,  3",
			" 1,  0", " 1,  1", " 1,  2", " 1,  3",
			" 2,  0", " 2,  1", " 2,  2", " 2,  3",
			" 3,  0", " 3,  1", " 3,  2", " 3,  3"
		};

		assertEquals(factory.getRuleNum(), actual.length);

		for(int i = 0; i < factory.getRuleNum(); i++) {
			Antecedent antecedent = factory.create();
			assertEquals(antecedent.toString(), actual[i]);
		}

		assertEquals(factory.create(), null);
		assertEquals(factory.create(), null);
	}
}
