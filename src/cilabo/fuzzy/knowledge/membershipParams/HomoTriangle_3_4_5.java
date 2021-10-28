package cilabo.fuzzy.knowledge.membershipParams;

public class HomoTriangle_3_4_5 {

	static float[][] params = new float[][]
	{
		//3分割
		new float[] {0f, 0f, 0.5f},
		new float[] {0f, 0.5f, 1f},
		new float[] {0.5f, 1f, 1f},
		//4分割
		new float[] {0f, 0f, 1f/3f},
		new float[] {0f, 1f/3f, 2f/3f},
		new float[] {1f/3f, 2f/3f, 1f},
		new float[] {2f/3f, 1f, 1f},
		//5分割
		new float[] {0f, 0f, 0.25f},
		new float[] {0f, 0.25f, 0.5f},
		new float[] {0.25f, 0.5f, 0.75f},
		new float[] {0.5f, 0.75f, 1f},
		new float[] {0.75f, 1f, 1f}
	};
	public static float[][] getParams() {
		return params;
	}
}
