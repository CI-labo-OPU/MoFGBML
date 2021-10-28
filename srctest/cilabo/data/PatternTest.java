package cilabo.data;

import static org.junit.Assert.*;

import org.junit.Test;

public class PatternTest {
	@Test
	public void testGetDimValue() {
		int id = 0;
		double[] vector = new double[] {0, 1};
		int dimension = vector.length;

		InputVector inputVector = new InputVector(vector);
		ClassLabel classLabel = new ClassLabel();

		Pattern pattern = Pattern.builder()
								.id(id)
								.inputVector(inputVector)
								.trueClass(classLabel)
								.build();

		for(int i = 0; i < dimension; i++) {
			Double actual = vector[i];
			Double expected = pattern.getDimValue(i);
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testGetTrueClass() {
		int id = 0;
		double[] vector = new double[] {0, 1};
		InputVector inputVector = new InputVector(vector);

		Pattern pattern;

		//Single Label
		Integer C = 7;
		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabel(C);
		pattern = Pattern.builder()
						.id(id)
						.inputVector(inputVector)
						.trueClass(classLabel)
						.build();
		Integer actualC = C;
		Integer expectedC = pattern.getTrueClass().getClassLabel();
		assertEquals(expectedC, actualC);

		//Multi Label
		Integer[] cVec = new Integer[] {1, 0, 1};
		classLabel = new ClassLabel();
		classLabel.addClassLabels(cVec);
		pattern = Pattern.builder()
						.id(id)
						.inputVector(inputVector)
						.trueClass(classLabel)
						.build();
		Integer[] actualVector = cVec;
		Integer[] expectedVector = pattern.getTrueClass().getClassVector();
		assertArrayEquals(expectedVector, actualVector);

	}
}
