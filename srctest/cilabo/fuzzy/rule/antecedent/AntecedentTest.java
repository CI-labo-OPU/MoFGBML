package cilabo.fuzzy.rule.antecedent;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_3_4_5;

public class AntecedentTest {
	@Test
	public void testAntecedent() {
		int[] antecedentIndex = new int[] {0, 2, 1};
		int dimension = 3;
		float[][] params = HomoTriangle_3_4_5.getParams();

		Knowledge knowledge = HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(params)
								.build()
								.create();

		Antecedent antecedent = Antecedent.builder()
								.knowledge(knowledge)
								.antecedentIndex(antecedentIndex)
								.build();

		String expected = antecedent.toString();

		String actual = " 0,  2,  1";

		assertEquals(expected, actual);
	}

	@Test
	public void testCategorical() {
		int dimension = 3;
		Knowledge knowledge = HomoTriangleKnowledgeFactory.builder()
								.dimension(dimension)
								.params(HomoTriangle_2_3_4_5.getParams())
								.build()
								.create();

		int[] antecedentIndex = new int[] {-1, 0, -2};
		Antecedent antecedent = Antecedent.builder()
									.knowledge(knowledge)
									.antecedentIndex(antecedentIndex)
									.build();

		double[] x1 = new double[] {-1, 0.5, -2};
		double[] x2 = new double[] {-3, 0.5, -2};
		double[] x3 = new double[] {-1, -1, -2};

		double diff = 0.00001;
		assertEquals(antecedent.getCompatibleGrade(x1), 1.0, diff);
		assertEquals(antecedent.getCompatibleGrade(x2), 0.0, diff);
		assertEquals(antecedent.getCompatibleGrade(x3), 1.0, diff);
	}

}
