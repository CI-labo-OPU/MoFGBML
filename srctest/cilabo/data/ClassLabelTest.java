package cilabo.data;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.data.ClassLabel;

public class ClassLabelTest {

	@Test
	public void testSingleLabel() {
		Integer C = 7;
		String actual = "7";

		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabel(C);

		String expected = classLabel.toString();

		assertEquals(expected, actual);
	}

	@Test
	public void testMultiLabel() {
		Integer[] cVec = new Integer[] {1, 0, 1};
		String actual = "1, 0, 1";

		ClassLabel classLabel = new ClassLabel();
		for(int i = 0; i < cVec.length; i++) {
			classLabel.addClassLabel(cVec[i]);
		}

		String expected = classLabel.toString();

		assertEquals(expected, actual);
	}
}
