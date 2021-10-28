package cilabo.fuzzy.knowledge.membershipParams;

public class HomoTriangle_2_3 {
	static float[][] params = new float[][]
	{
		//2分割
		new float[] {0f, 0f, 1f},
		new float[] {0f, 1f, 1f},
		//3分割
		new float[] {0f, 0f, 0.5f},
		new float[] {0f, 0.5f, 1f},
		new float[] {0.5f, 1f, 1f},
	};

	public static float[][] getParams(){
		return params;
	}
}
