package cilabo.fuzzy.knowledge.membershipParams;

import java.util.ArrayList;

import cilabo.data.DataSet;

public class MakeParameter {
	private ArrayList<ArrayList<Double>> partitions;
	private int[] K;
	private int fuzzySetNum;

	/**
	 * 等分割の分割区間を生成する
	 * @param K 分割数のリスト
	 */
	public void makeHomePartition(int[] K) {
		this.partitions = new ArrayList<ArrayList<Double>>();
		this.K = K;
		fuzzySetNum = 0;
		for(int k: K) {
			ArrayList<Double> partition = new ArrayList<Double>();
			for(int i=0; i<=k; i++) {
				partition.add( (double)(2*i-1)/((k-1)*2) );
			}
			this.partitions.add(partition);
			fuzzySetNum += k;
		}
	}

	/**
	 * エントロピーに基づいた分割区間を生成する
	 * @param tra データセット
	 * @param K 分割数のリスト
	 * @param dim 導出する属性の次元を指定
	 */
	public void makePartition(DataSet tra, int[] K, int dim) {
		//boundaries[属性値][分割数][境界値]
		ArrayList<ArrayList<Double>> boundaries = new ArrayList<ArrayList<Double>>();

		//Step 0. Judge Categoric.
		if(tra.getPattern(0).getDimValue(dim) < 0) {
			//If it's categoric, do NOT partitinon.
			continue;
		}

		//Step 1. Sort patterns by attribute "dim_i"
		ArrayList<ForSortPattern> patterns = new ArrayList<ForSortPattern>();
		for(int p = 0; p < tra.getDataSize(); p++) {
			patterns.add( new ForSortPattern(tra.getPattern(p).getDimValue(dim),
					tra.getPattern(p).getTrueClass()));
		}
		Collections.sort(patterns, new Comparator<ForSortPattern>() {
			@Override
			//Ascending Order
			public int compare(ForSortPattern o1, ForSortPattern o2) {
				if(o1.getX() > o2.getX()) {return 1;}
				else if(o1.getX() < o2.getX()) {return -1;}
				else {return 0;}
			}
		});

		//Step 3. add boundaries
		for(int k: K) {
			// Optimal Splitting.
			ArrayList<Double> partitions = optimalSplitting(patterns, k, tra.getCnum());
			boundaries.get(dim_i).add(partitions);
		}
		this.partitions = FuzzyPartitioning.makePartition(tra, K);
		for(int dim_i=0; dim_i<this.Ndim; dim_i++) {
			this.numPartitions[dim_i] = numPartitions(dim_i);
	}
	/**
	 * 三角形型のパラメータを生成する．
	 *
	 * @return パラメータ[ファジイセットID][パラメータ]
	 */
	public float[][] triangle(){
		float[][] params = new float[this.fuzzySetNum][3];
		for(int K_i: this.K) {
			for(int i=0; i<K_i; i++) {
				if(i == 0) {
					params[i] = new float[] {0f, 0f, 2*(float)(double)partitions.get(K_i).get(i+1)};
				}else if(i == partitions.get(K_i).size()-2) {
					params[i] = new float[] {2*(float)(double)partitions.get(K_i).get(i) - 1f, 1f, 1f};
				}else if(0 < i && i < partitions.get(K_i).size()-2){
					float left = (float)(double)partitions.get(K_i).get(i), right = (float)(double)partitions.get(K_i).get(i+1);
					params[i] = new float[] {left*3f/2f - right/2f, (left+right)/2f, right*3f/2f - left/2f};
				}
			}
		}
		return params;
	}

	/**
	 * ガウシアン型のパラメータを生成する．
	 *
	 * @return パラメータ[ファジイセットID][パラメータ]
	 */
	public float[][] gaussian(){
		float[][] params = new float[this.fuzzySetNum][2];
		for(int K_i: this.K) {
			for(int i=0; i<K_i; i++) {
				//最初と最後だけ頂点が区間端になるようにする．
				if(i == 0){
					params[i] = calcGaussParam(0, (float)(double)partitions.get(K_i).get(i+1), 0.5f);
				}else if(i == partitions.get(K_i).size()-2) {
					params[i] = calcGaussParam(1, (float)(double)partitions.get(K_i).get(i), 0.5f);
				}else  if(0 < i && i < partitions.get(K_i).size()-2){
					double left = partitions.get(K_i).get(i), right = partitions.get(K_i).get(i+1);
					params[i] = calcGaussParam((float)(left + right)/2, (float)(double)partitions.get(K_i).get(i), 0.5f);
				}
			}
		}
		return params;
	}

	/**
	 * 平均 mean の正規分布(係数なし，x=meanのときvalue=1)について，
	 * 引数に与えられた，(x, value)を通る平均meanの正規分布の標準偏差を計算するメソッド
	 * @param mean
	 * @param x
	 * @param value
	 * @return
	 */
	public static float[] calcGaussParam(float mean, float x, float value) {
		float[] param;

		float variance;		//分散
		float deviation;	//標準偏差
		float numerator;	//分子
		float denominator;	//分母

		numerator = -((x - mean) * (x - mean));
		denominator = 2f * (float)Math.log(value);

		variance = numerator / denominator;
		deviation = (float)Math.sqrt(variance);

		param = new float[] {mean, deviation};

		return param;
	}

	/**
	 * 区間型のパラメータを生成する．
	 *
	 * @return パラメータ[ファジイセットID][パラメータ]
	 */
	public float[][] rectangle(){
		float[][] params = new float[this.fuzzySetNum][2];
		for(int K_i: this.K) {
			for(int i=0; i<K_i; i++) {
				params[i] = new float[] {(float)(double)partitions.get(K_i).get(i), (float)(double)partitions.get(K_i).get(i+1)};
			}
		}
		return params;
	}

	/**
	 * 台形型のパラメータを生成する．
	 *
	 * @return パラメータ[ファジイセットID][パラメータ]
	 */
	public float[][] trapezoid(){
		float[][] params = new float[this.fuzzySetNum][4];
		for(int K_i: this.K) {
			for(int i=0; i<K_i; i++) {
				if(i == 0) {
					params[i] = new float[] {0f, 0f, (float)0.5*(float)(double)partitions.get(K_i).get(i+1), (float)1.5*(float)(double)partitions.get(K_i).get(i+1)};
				}else if(i == partitions.get(K_i).size()-2) {
					params[i] = new float[] {(float)1.5*(float)(double)partitions.get(K_i).get(i) - 0.5f, (float)0.5*(float)(double)partitions.get(K_i).get(i) + 0.5f, 1f, 1f};
				}else {
					float left = (float)(double)partitions.get(K_i).get(i), right = (float)(double)partitions.get(K_i).get(i+1);
					params[i] = new float[] {left*5f/4f - right/4f, left*3f/4f + right/4f, right*3f/4f + left/4f, right*5f/4f - left/4f};
				}
			}
		}
		return params;
	}
}

class ForSortPattern{
	double x;
	double index;
	int conClass;

	ForSortPattern(double x, int conClass){
		this.x = x;
		this.conClass = conClass;
	}

	double getX(){
		return x;
	}

	int getConClass() {
		return conClass;
	}

	double getIndex() {
		return index;
	}
}
