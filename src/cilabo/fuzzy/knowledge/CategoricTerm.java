package cilabo.fuzzy.knowledge;

import jfml.term.FuzzyTermType;

public class CategoricTerm extends FuzzyTermType {
	protected int index;

	public CategoricTerm(String name, int index) {
		this.setName(name);
		this.index = index;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	// データのinformationとかを読ませるようにすれば、index以外で管理することができるかも
	@Override
	public String toString() {
		String b = name;

		b += " -  " + String.valueOf(index);
		return b;
	}

}
