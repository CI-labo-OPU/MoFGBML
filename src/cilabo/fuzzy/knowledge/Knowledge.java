package cilabo.fuzzy.knowledge;

import jfml.term.FuzzyTermType;

public class Knowledge {
	// ************************************************************
	// Fields

	/** */
	FuzzyTermType[][] fuzzySets;

	// ************************************************************
	// Constructor
	public Knowledge() {}

	// ************************************************************
	// Methods

	/**
	 *
	 * @param x
	 * @param dimension
	 * @param H
	 * @return
	 */
	public double getMembershipValue(double x, int dimension, int H) {
		return (double)fuzzySets[dimension][H].getMembershipValue((float)x);
	}

	public FuzzyTermType getFuzzySet(int dimension, int H) {
		return this.fuzzySets[dimension][H];
	}

	/**
	 *
	 */
	public int getDimension() {
		return this.fuzzySets.length;
	}

	/**
	 *
	 */
	public int getFuzzySetNum(int dimension) {
		return this.fuzzySets[dimension].length;
	}

	/**
	 * Shallow copy
	 */
	public void setFuzzySets(FuzzyTermType[][] fuzzySets) {
		this.fuzzySets = fuzzySets;
	}

	@Override
	public String toString() {
		String ln = System.lineSeparator();
		String str = "";

		for(int i = 0; i < fuzzySets.length; i++) {
			for(int j = 0; j < fuzzySets[i].length; j++) {
				str += fuzzySets[i][j].toString() + ln;
			}
		}

		return str;
	}
}
