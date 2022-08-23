package cilabo.fuzzy.knowledge;

import jfml.term.FuzzyTermType;


/**
 * singletoneに変更，アプリケーション内で唯一のインスタンスを持ちます．≒グローバル変数
 * @author hirot
 *
 */
public class Knowledge {
	// ************************************************************
	// Fields
	private static Knowledge instace = new Knowledge();

	/** */
	private FuzzyTermType[][] fuzzySets;

	// ************************************************************
	// Constructor
	private Knowledge() {}


	// ************************************************************
	// Methods

	public static Knowledge getInstace() {
		return instace;
	}

	public FuzzyTermType getFuzzySet(int dimension, int H) {
		return fuzzySets[dimension][H];
	}

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


	/**
	 *
	 */
	public int getDimension() {
		return fuzzySets.length;
	}

	/**
	 *
	 */
	public int getFuzzySetNum(int dimension) {
		return fuzzySets[dimension].length;
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
