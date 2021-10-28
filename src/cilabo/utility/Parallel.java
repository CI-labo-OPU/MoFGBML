package cilabo.utility;

import java.util.concurrent.ForkJoinPool;

public class Parallel {
	// ************************************************************
	// Fields
	private static Parallel instance = new Parallel();

	private ForkJoinPool learningForkJoinPool = new ForkJoinPool(1);

	// ************************************************************
	// Constructor

	// ************************************************************
	// Methods

	public static Parallel getInstance() {
		if(instance == null) {
			instance = new Parallel();
		}
		return instance;
	}

	public void initLearningForkJoinPool(int core) {
		instance.learningForkJoinPool = new ForkJoinPool(core);
	}

	public ForkJoinPool getLearningForkJoinPool() {
		return instance.learningForkJoinPool;
	}

}
