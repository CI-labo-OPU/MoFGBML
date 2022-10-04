package cilabo.fuzzy.knowledge.membershipParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import cilabo.data.ClassLabel;
import cilabo.data.DataSet;
import cilabo.main.Consts;
import cilabo.utility.Parallel;

public class MakeParameter {
	private ArrayList<ArrayList<Double>> partitions;
	private int[] K;
	private int fuzzySetNum;
	private int divisionType; //1 = equalDivision, 2 = inEqualDivision

	/**
	 * 等分割の分割区間を生成する
	 * @param K 分割数のリスト
	 */
	public void makeHomePartition(int[] K) {
		this.partitions = new ArrayList<ArrayList<Double>>();
		this.K = K;
		this.fuzzySetNum = 0;
		for(int k: K) {
			ArrayList<Double> partition = new ArrayList<Double>();
			partition.add(0d);
			for(int i=1; i<k; i++) {
				partition.add( (double)(2*i-1)/((k-1)*2) );
			}
			partition.add(1d);
			this.partitions.add(partition);
			this.fuzzySetNum += k;
		}
		this.divisionType = 1;
	}

	/**
	 * エントロピーに基づいた分割区間を生成する
	 * @param tra データセット
	 * @param K 分割数のリスト
	 * @param dim 導出する属性の次元を指定
	 */
	public void makePartition(DataSet tra, int[] K, int dim) {
		this.partitions = new ArrayList<ArrayList<Double>>();
		this.K = K;
		this.fuzzySetNum = 0;

		//Step 0. Judge Categoric.
		if(tra.getPattern(0).getDimValue(dim) < 0) {
			//If it's categoric, do NOT partitinon.
			return;
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
			ArrayList<Double> boundaries = optimalSplitting(patterns, k, tra.getCnum());
			partitions.add(boundaries);
			this.fuzzySetNum += k;
		}
		this.divisionType = 2;
	}

	/**
	 * 三角形型のパラメータを生成する．
	 *
	 * @return パラメータ[ファジイセットID][パラメータ]
	 */
	public float[][] triangle(){
		float[][] params = new float[this.fuzzySetNum][3];
		for(int K_i=0, cnt=0; K_i<this.K.length; K_i++) {
			for(int i=0; i<K[K_i]; i++, cnt++) {
				if(i == 0) {
					params[cnt] = new float[] {0f, 0f, 2*(float)(double)partitions.get(K_i).get(i+1)};
				}else if(i == partitions.get(K_i).size()-2) {
					params[cnt] = new float[] {2*(float)(double)partitions.get(K_i).get(i) - 1f, 1f, 1f};
				}else if(0 < i && i < partitions.get(K_i).size()-2){
					float left = (float)(double)partitions.get(K_i).get(i), right = (float)(double)partitions.get(K_i).get(i+1);
					params[cnt] = new float[] {left*3f/2f - right/2f, (left+right)/2f, right*3f/2f - left/2f};
				}
			}
		}
		return params;
	}

	/**
	 * 不均一な線形型のパラメータを生成する．
	 *
	 * @return パラメータ[ファジイセットID][パラメータ](台形型パラメータ)
	 */
	public float[][] linerShape(double F){
		float[][] params = new float[this.fuzzySetNum][4];
		for(int K_i=0, cnt=0; K_i<this.K.length; K_i++) {

			ArrayList<Float> newPoints = new ArrayList<>();
			//領域左端の点を追加
			newPoints.add(0f);
			newPoints.add(0f);

			//Step 1. Fuzzify each partition without edge of domain.
			for(int i = 1; i < this.partitions.get(K_i).size() - 1; i++) {
				double left = this.partitions.get(K_i).get(i - 1);
				double point = this.partitions.get(K_i).get(i);
				double right = this.partitions.get(K_i).get(i + 1);
				newPoints.addAll(fuzzify(left, point, right, F));
			}

			//Step 2. Take 4 points as trapezoids in order from head of newPoints.
			//領域右端の点を追加
			newPoints.add(1f);
			newPoints.add(1f);

			for(int i = 0; i < (newPoints.size() - 2) / 2; i++, cnt++) {
				float[] trapezoid = new float[4];
				for(int j = 0; j < 4; j++) {
					trapezoid[j] = newPoints.get(i*2 + j);
				}
				params[cnt] = trapezoid;
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
		for(int K_i=0, cnt=0; K_i<this.K.length; K_i++) {
			for(int i=0; i<K[K_i]; i++, cnt++) {
				//最初と最後だけ頂点が区間端になるようにする．
				if(i == 0){
					params[cnt] = calcGaussParam(0, (float)(double)partitions.get(K_i).get(i+1), 0.5f);
				}else if(i == partitions.get(K_i).size()-2) {
					params[cnt] = calcGaussParam(1, (float)(double)partitions.get(K_i).get(i), 0.5f);
				}else  if(0 < i && i < partitions.get(K_i).size()-2){
					double left = partitions.get(K_i).get(i), right = partitions.get(K_i).get(i+1);
					params[cnt] = calcGaussParam((float)(left + right)/2, (float)(double)partitions.get(K_i).get(i), 0.5f);
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
		for(int K_i=0, cnt=0; K_i<this.K.length; K_i++) {
			for(int i=0; i<K[K_i]; i++, cnt++) {
				params[cnt] = new float[] {(float)(double)partitions.get(K_i).get(i), (float)(double)partitions.get(K_i).get(i+1)};
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
		for(int K_i=0, cnt=0; K_i<this.K.length; K_i++) {
			for(int i=0; i<K[K_i]; i++, cnt++) {
				if(i == 0) {
					params[cnt] = new float[] {0f, 0f, (float)0.5*(float)(double)partitions.get(K_i).get(i+1), (float)1.5*(float)(double)partitions.get(K_i).get(i+1)};
				}else if(i == partitions.get(K_i).size()-2) {
					params[cnt] = new float[] {(float)1.5*(float)(double)partitions.get(K_i).get(i) - 0.5f, (float)0.5*(float)(double)partitions.get(K_i).get(i) + 0.5f, 1f, 1f};
				}else {
					float left = (float)(double)partitions.get(K_i).get(i), right = (float)(double)partitions.get(K_i).get(i+1);
					params[cnt] = new float[] {left*5f/4f - right/4f, left*3f/4f + right/4f, right*3f/4f + left/4f, right*5f/4f - left/4f};
				}
			}
		}
		return params;
	}

	/**
	 * <h1>Class-entropy based searching optimal-partitionings</h1>
	 * @param patterns : {@literal ArrayList<ForSortPattern>} :
	 * @param K : int : Given number of partitions
	 * @param Cnum : int : #of classes
	 * @return
	 */
	public static ArrayList<Double> optimalSplitting(ArrayList<ForSortPattern> patterns, int K, int Cnum) {
		double D = patterns.size();

		ArrayList<Double> partitions = new ArrayList<>();
		partitions.add(0.0);
		partitions.add(1.0);

		//Step 1. Collect class changing point.
		ArrayList<Double> candidate = new ArrayList<>();
		double point = 0;
//		candidate.add(point);
		for(int p = 1; p < patterns.size(); p++) {
			if(patterns.get(p-1).getConClass() != patterns.get(p).getConClass()) {
				point = 0.5 * (patterns.get(p-1).getX() + patterns.get(p).getX());
			}

			if(!candidate.contains(point) && point != 0 && point != 1) {
				candidate.add(point);
			}
		}
//		candidate.remove(0);

		//Step 2. Search K partitions which minimize class-entropy.
		for(int k = 2; k <= K; k++) {
			double[] entropy = new double[candidate.size()];

			//Calculate class-entropy for all candidates.
			for(int i = 0; i < candidate.size(); i++) {
				point = candidate.get(i);

				//Step 1. Count #of patterns in each partition.
				//D_jh means #of patterns which is in partition j and whose class is h.
				double[][] Djh = new double[k][Cnum];
				double[] Dj = new double[k];

				ArrayList<Double> range = new ArrayList<>();
				Collections.sort(partitions);	//Ascending Order
				boolean yetContain = true;
				for(int r = 0; r < partitions.size(); r++) {
					if(yetContain && point < partitions.get(r)) {
						range.add(point);
						yetContain = false;
					}
					range.add(partitions.get(r));
				}
				for(int part = 0; part < k; part++) {
					final double LEFT = range.get(part);
					final double RIGHT = range.get(part+1);
					for(int c = 0; c < Cnum; c++) {
						final int CLASSNUM = c;
						try {
							Optional<Double> partSum = Parallel.getInstance().getLearningForkJoinPool().submit( () ->
							patterns.parallelStream()
									.filter(p -> p.getConClass().getClassLabel() == CLASSNUM)
									.filter(p -> LEFT <= p.getX() && p.getX() <= RIGHT)
									.map(p -> {
										if(p.getX() == 0.0 || p.getX() == 1.0) {return 1.0;}
										else if(p.getX() == LEFT || p.getX() == RIGHT) {return 0.5;}
										else {return 1.0;}
									})
									.reduce( (l,r) -> l+r)
									).get();
							Djh[part][c] = partSum.orElse(0.0);
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
						//Without Classes
						Dj[part] += Djh[part][c];
					}
				}

				//Step 2. Calculate class-entropy.
				double sum = 0.0;
				for(int j = 0; j < k; j++) {
					double subsum = 0.0;
					for(int h = 0; h < Cnum; h++) {
						if(Dj[j] != 0.0 && (Djh[j][h] / Dj[j]) > 0.0) {
							subsum += (Djh[j][h] / Dj[j]) * log( (Djh[j][h] / Dj[j]), 2.0);
						}
					}
					sum += (Dj[j] / D) * subsum;
				}
				entropy[i] = -sum;
			}

			//Find minimize class-entropy.
			double min = entropy[0];
			int minIndex = 0;
			for(int i = 1; i < candidate.size(); i++) {
				if(entropy[i] < min) {
					min = entropy[i];
					minIndex = i;
				}
			}
			partitions.add(candidate.get(minIndex));
			candidate.remove(minIndex);
			if(candidate.size() == 0) {
				break;
			}
		}
		if(partitions.size() < K+1) {
			for(int i=2; i<=K; i++) {
				for(int j=1; j<i; j++) {
					double tmp = (double)j/i;
					if(partitions.size() < K+1 && !partitions.contains(tmp)) {
						partitions.add(tmp);
					}
				}
			}
		}
		Collections.sort(partitions);	//Ascending Order
		return partitions;
	}

	public float[][] getParameter(String fuzzyTermShapeName){
		switch(fuzzyTermShapeName) {
			case "gaussian":
				return this.gaussian();

			case "trapezoid":
				return this.trapezoid();

			case "interval":
				return this.rectangle();

			case "triangle":
			default:
				if(this.divisionType == 1) {
					return this.triangle();
				}else if(this.divisionType == 2){
					return this.linerShape(Consts.FUZZY_GRADE);
				}
		}
		return null;

	}

	/**
	 * <h1>Fuzzifying Partition</h1>
	 * Fuzzify two partitions [left, point] and [point, right].<br>
	 *
	 * @param left : double : Domain Left
	 * @param point : double : Crisp Point
	 * @param right : double : Domain Right
	 * @param F : double : Grade of overwraping
	 * @return {@literal ArrayList<Double} : fuzzfied two point
	 */
	public static ArrayList<Float> fuzzify(double left, double point, double right, double F) {
		ArrayList<Float> two = new ArrayList<>();

		//Step 1. Minimize Range (left-point) or (point-right)
		if( (point-left) < (right-point) ) {
			//point is closer to left than right, then right moves.
			right = point + (point-left);
		} else {
			//point is closer to right than left, then left moves.
			left = point - (right-point);
		}

		//Step 2. Make most fuzzified partition and most crisp partition.
		double ac_F0 = point;
		double ac_F1 = 0.5 * (left + point);
		double bd_F0 = point;
		double bd_F1 = 0.5 * (right + point);

		//Step 3. Make F graded fuzzified partition
		double ac_F = ac_F0 + (ac_F1 - ac_F0)*F;
		double bd_F = bd_F0 + (bd_F1 - bd_F0)*F;

		//Step 4. Get Fuzzified two point which has membership value 1.0.
		two.add((float)ac_F);
		two.add((float)bd_F);

		return two;
	}

	/**
	 * <h1>log関数 底の変換公式</h1>
	 * @param a : double : 引数
	 * @param b : double : 底
	 * @return double : log_b (a)
	 */
	public static double log(double a, double b) {
		return (Math.log(a) / Math.log(b));
	}
}


class ForSortPattern{
	double x;
	double index;
	ClassLabel trueClass;

	ForSortPattern(double x, ClassLabel conClass){
		this.x = x;
		this.trueClass = conClass;
	}

	double getX(){
		return x;
	}

	ClassLabel getConClass() {
		return trueClass;
	}

	double getIndex() {
		return index;
	}
}

