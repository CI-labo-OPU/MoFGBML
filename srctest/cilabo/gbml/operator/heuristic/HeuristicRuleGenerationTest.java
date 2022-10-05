package cilabo.gbml.operator.heuristic;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.utility.Input;
import cilabo.utility.Random;

public class HeuristicRuleGenerationTest {
	@Test
	public void testHeuristicRuleGeneration() {
		String sep = File.separator;
		String traFile = "dataset" + sep + "iris" + sep + "a0_0_iris-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, traFile);

		float[][] params = HomoTriangle_2_3_4_5.getParams();
		HomoTriangleKnowledgeFactory.builder()
								.dimension(train.getNdim())
								.params(params)
								.build()
								.create();


		HeuristicRuleGeneration generator = new HeuristicRuleGeneration(Knowledge.getInstance());

		// TEST
		Random.getInstance().getGEN().setSeed(0);
		int num = 5;
		for(int i = 0; i < num; i++) {

			Antecedent antecedent = generator.heuristicRuleGeneration(train.getPattern(i));
//			System.out.println(antecedent);
		}





	}

}
