package cilabo.fuzzy.rule;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.fuzzy.rule.consequent.RuleWeight;

public class RuleWeightTest {

	@Test
	public void testRuleWeight() {
		Double w = 0.5;
		String actual = "0.5";

		RuleWeight ruleWeight = new RuleWeight();
		ruleWeight.addRuleWeight(w);

		String expected = ruleWeight.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testRuleWeightVector() {
		Double[] wVec = new Double[] {0.5, 0.8, 0.9};
		String actual = "0.5, 0.8, 0.9";

		RuleWeight ruleWeight = new RuleWeight();
		ruleWeight.addRuleWeightVector(wVec);

		String expected = ruleWeight.toString();

		assertEquals(expected, actual);
	}
}
