package cilabo.data;

import java.util.Arrays;

public class InputVector {
	// ************************************************************
	// Fields

	// Input vector
	double[] inputVector;

	// ************************************************************
	// Constructor
	public InputVector(int dimension) {
		this.inputVector = new double[dimension];
	}

	public InputVector(double[] inputVector) {
		this.inputVector = Arrays.copyOf(inputVector, inputVector.length);
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	public double getDimValue(int index) {
		return this.inputVector[index];
	}

	/**
	 *
	 */
	public double[] getVector() {
		return this.inputVector;
	}

	/**
	 *
	 */
	public void setVector(double[] inputVector) {
		this.inputVector = Arrays.copyOf(inputVector, inputVector.length);
	}

	@Override
	public String toString() {
		if(this.inputVector == null) {
			return null;
		}
		String str = String.valueOf(this.inputVector[0]);
		for(int i = 1; i < this.inputVector.length; i++) {
			str += ", " + this.inputVector[i];
		}
		return str;
	}

}
