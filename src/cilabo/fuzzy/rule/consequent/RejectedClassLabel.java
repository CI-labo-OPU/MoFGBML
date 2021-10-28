package cilabo.fuzzy.rule.consequent;

import cilabo.data.ClassLabel;

public class RejectedClassLabel extends ClassLabel {
	private static RejectedClassLabel instance;

	public static RejectedClassLabel getInstance() {
		if(instance == null) {
			instance = new RejectedClassLabel();
		}
		return instance;
	}

	@Override
	public String toString() {
		return "rejected";
	}

}
