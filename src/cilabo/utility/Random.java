package cilabo.utility;

import random.MersenneTwisterFast;

public class Random {
	// ************************************************************
	private static Random instance = new Random();

	private int RAND_SEED = 2020;
	private MersenneTwisterFast GEN = new MersenneTwisterFast(0);

	// ************************************************************

	// ************************************************************
	public static Random getInstance() {
		if(instance == null) {
			instance = new Random();
		}
		return instance;
	}

	public void initRandom(int seed) {
		this.RAND_SEED = seed;
		this.GEN = new MersenneTwisterFast(this.RAND_SEED);
	}

	public MersenneTwisterFast getGEN() {
		return this.GEN;
	}

}
