package cilabo.labo.developing.fairness;

import cilabo.data.ClassLabel;
import cilabo.data.InputVector;
import cilabo.data.Pattern;

/**
 *
 */

public class FairnessPattern extends Pattern {
	// ************************************************************
	/**
	 * sensitive attribute
	 */
	int a;

	// ************************************************************
	public FairnessPattern(int id, InputVector inputVector, ClassLabel trueClass, int a) {
		super(id, inputVector, trueClass);
		this.a = a;
	}



	// ************************************************************


	public int getA() {
		return this.a;
	}


	@Override
	public String toString() {
		if(getInputVector() == null || getTrueClass() == null) {
			return null;
		}

		String str = "id:" + String.valueOf(getID());
		str += "," + "input:[" + getInputVector() + "]";
		str += "," + "class:[" + getTrueClass() + "]";
		str += "," + "sensitiveAttribute:" + getA();
		return str;
	}


}
