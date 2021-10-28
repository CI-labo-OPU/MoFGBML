package cilabo.data;

import java.util.ArrayList;

public class ClassLabel {
	// ************************************************************
	// Fields
	ArrayList<Integer> classLabel = new ArrayList<>();

	// ************************************************************
	// Constructor
	public ClassLabel() {}

	private ClassLabel(ArrayList<Integer> classLabel) {
		this.classLabel.addAll(classLabel);
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	public ClassLabel deepcopy() {
		return new ClassLabel(this.classLabel);
	}

	/**
	 *
	 */
	public Integer getClassLabel() {
		return this.classLabel.get(0);
	}

	public Integer[] getClassVector() {
		return this.classLabel.toArray(new Integer[0]);
	}

	/**
	 *
	 */
	public void addClassLabels(Integer[] classLabel) {
		for(int i = 0; i < classLabel.length; i++) {
			this.classLabel.add(classLabel[i]);
		}
	}

	/**
	 *
	 */
	public void addClassLabel(Integer classLabel) {
		this.classLabel.add(classLabel);
	}

	@Override
	public String toString() {
		if(this.classLabel.size() == 0) {
			return null;
		}

		String str = String.valueOf(classLabel.get(0));
		if(this.classLabel.size() > 1) {
			for(int i = 1; i < this.classLabel.size(); i++) {
				str += ", " + this.classLabel.get(i);
			}
		}
		return str;
	}

}
