package cilabo.fuzzy.rule.consequent;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.data.ClassLabel;

public class ConsequentTest {
	@Test
	public void testSingleLabel() {
		Integer C = 7;
		Double w = 0.5;
		String actual = "class:[7] weight:[0.5]";

		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabel(C);
		RuleWeight ruleWeight = new RuleWeight();
		ruleWeight.addRuleWeight(w);

		Consequent consequent = Consequent.builder()
									.consequentClass(classLabel)
									.ruleWeight(ruleWeight)
									.build();

		String expected = consequent.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testMultiLabel() {
		Integer[] cVec = new Integer[] {1, 0, 1};
		Double[] wVec = new Double[] {0.5, 0.8, 0.9};
		String actual = "class:[1, 0, 1] weight:[0.5, 0.8, 0.9]";

		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabels(cVec);
		RuleWeight ruleWeight = new RuleWeight();
		ruleWeight.addRuleWeightVector(wVec);

		Consequent consequent = Consequent.builder()
									.consequentClass(classLabel)
									.ruleWeight(ruleWeight)
									.build();

		String expected = consequent.toString();

		assertEquals(expected, actual);
	}

}
