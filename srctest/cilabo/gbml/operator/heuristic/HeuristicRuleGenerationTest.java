package cilabo.gbml.operator.heuristic;

import java.io.File;

import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.fuzzy.knowledge.Knowledge;
import cilabo.fuzzy.knowledge.factory.HomoTriangleKnowledgeFactory;
import cilabo.fuzzy.knowledge.membershipParams.HomoTriangle_2_3_4_5;
import cilabo.fuzzy.rule.Rule;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.factory.MoFGBML_Learning;
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
		Knowledge knowledge = HomoTriangleKnowledgeFactory.builder()
								.dimension(train.getNdim())
								.params(params)
								.build()
								.create();

		ConsequentFactory consequentFactory = MoFGBML_Learning.builder()
													.train(train)
													.build();

		HeuristicRuleGeneration generator = new HeuristicRuleGeneration(knowledge, consequentFactory);


		// TEST
		Random.getInstance().getGEN().setSeed(0);
		int num = 5;
		for(int i = 0; i < num; i++) {

			Rule rule = generator.heuristicRuleGeneration(train.getPattern(i));
			System.out.println(rule);
		}





	}

}
