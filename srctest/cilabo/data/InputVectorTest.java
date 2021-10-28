package cilabo.data;

import static org.junit.Assert.*;

import org.junit.Test;

import cilabo.data.InputVector;

public class InputVectorTest {

	@Test
	public void testGetDimValue() {
		double[] vector = new double[] {0, 1};
		InputVector inputVector = new InputVector(vector);

		Double[] actual = new Double[vector.length];
		for(int i = 0; i < actual.length; i++) {
			actual[i] = vector[i];
		}

		Double[] expected = new Double[inputVector.getVector().length];
		for(int i = 0; i < expected.length; i++) {
			expected[i] = inputVector.getDimValue(i);
		}

		assertArrayEquals(expected, actual);
	}
}
