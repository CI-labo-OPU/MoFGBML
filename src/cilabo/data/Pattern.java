package cilabo.data;


public class Pattern {
	// ************************************************************
	// Fields

	/**
	 *  Identification number
	 */
	int id;

	/**
	 * Input vector
	 */
	InputVector inputVector;

	/**
	 * Class label
	 */
	ClassLabel trueClass;

	// ************************************************************
	// Constructor
	public Pattern(int id, InputVector inputVector, ClassLabel trueClass) {
		this.id = id;
		this.inputVector = inputVector;
		this.trueClass = trueClass;
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	public double getDimValue(int index) {
		return this.inputVector.getDimValue(index);
	}

	/**
	 *
	 */
	public int getID() {
		return this.id;
	}

	/**
	 *
	 */
	public InputVector getInputVector() {
		return this.inputVector;
	}

	/**
	 *
	 */
	public ClassLabel getTrueClass() {
		return this.trueClass;
	}

	@Override
	public String toString() {
		if(this.inputVector == null || this.trueClass == null) {
			return null;
		}

		String str = "id:" + String.valueOf(this.id);
		str += "," + "input:[" + this.inputVector.toString() + "]";
		str += "," + "class:[" + this.trueClass.toString() + "]";
		return str;
	}

	public static PatternBuilder builder() {
		return new PatternBuilder();
	}

	public static class PatternBuilder {
		private int id = -1;
		private InputVector inputVector;
		private ClassLabel trueClass;

		PatternBuilder() {}

		public Pattern.PatternBuilder id(int id) {
			this.id = id;
			return this;
		}

		public Pattern.PatternBuilder inputVector(InputVector inputVector) {
			this.inputVector = inputVector;
			return this;
		}

		public Pattern.PatternBuilder trueClass(ClassLabel trueClass) {
			this.trueClass = trueClass;
			return this;
		}

		/**
		 * @param id : int
		 * @param inputVector : InputVector
		 * @param trueClass : ClassLabel
		 */
		public Pattern build() {
			return new Pattern(id, inputVector, trueClass);
		}
	}


}
