package cilabo.fuzzy.rule.consequent.factory;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import cilabo.data.ClassLabel;
import cilabo.data.DataSet;
import cilabo.fuzzy.rule.antecedent.Antecedent;
import cilabo.fuzzy.rule.consequent.Consequent;
import cilabo.fuzzy.rule.consequent.ConsequentFactory;
import cilabo.fuzzy.rule.consequent.RuleWeight;
import cilabo.utility.Parallel;

public class MoFGBML_Learning implements ConsequentFactory {
	// ************************************************************
	// Fields
	/**  */
	DataSet train;

	// ************************************************************
	// Constructor
	public MoFGBML_Learning(DataSet train) {
		this.train = train;
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	@Override
	public Consequent learning(Antecedent antecedent) {
		double[] confidence = this.calcConfidence(antecedent);
		ClassLabel classLabel = this.calcClassLabel(confidence);
		RuleWeight ruleWeight = this.calcRuleWeight(classLabel, confidence);

		Consequent consequent = Consequent.builder()
								.consequentClass(classLabel)
								.ruleWeight(ruleWeight)
								.build();
		return consequent;
	}

	/**
	 * 各クラスへの信頼度を返す
	 * @return 信頼度
	 */
	public double[] calcConfidence(Antecedent antecedent) {
		int Cnum = train.getCnum();
		double[] confidence = new double[Cnum];

		// 各クラスのパターンに対する適合度の総和
		double[] sumCompatibleGradeForEachClass = new double[Cnum];

		for(int c = 0; c < Cnum; c++) {
			final Integer CLASSNUM = c;
			Optional<Double> partSum = null;
			try {
				partSum = Parallel.getInstance().getLearningForkJoinPool().submit( () ->
					train.getPatterns().parallelStream()
						// 正解クラスが「CLASS == c」のパターンを抽出
						.filter(pattern -> pattern.getTrueClass().getClassLabel() == CLASSNUM)
						// 各パターンの入力ベクトルを抽出
						.map(pattern -> pattern.getInputVector().getVector())
						// 各入力ベクトルとantecedentのcompatible gradeを計算
						.map(x -> antecedent.getCompatibleGrade(x))
						// compatible gradeを総和する
						.reduce( (sum, grade) -> sum+grade)
				).get();
			}
			catch (InterruptedException | ExecutionException e) {
				System.out.println(e);
				return null;
			}

			sumCompatibleGradeForEachClass[c] = partSum.orElse(0.0);
		}

		// 全パターンに対する適合度の総和
		double allSum = Arrays.stream(sumCompatibleGradeForEachClass).sum();
		if(allSum != 0) {
			for(int c = 0; c < Cnum; c++) {
				confidence[c] = sumCompatibleGradeForEachClass[c] / allSum;
			}
		}
		return confidence;
	}

	/**
	 * <h1>結論部クラス</h1></br>
	 * 信頼度から結論部クラスを決定する</br>
	 * confidence[]が最大となるクラスを結論部クラスとする</br>
	 * </br>
	 * もし、同じ値をとるクラスが複数存在する場合、
	 * もしくは最大の信頼度が0.5以下になる場合は生成不可能なルール(-1)とする．</br>
	 * </br>
	 *
	 * @param confidence
	 * @return
	 */
	public ClassLabel calcClassLabel(double[] confidence) {
		double max = -Double.MAX_VALUE;
		int consequentClass = -1;

		for(int i = 0; i < confidence.length; i++) {
			if(max < confidence[i]) {
				max = confidence[i];
				consequentClass = i;
			}
			else if(max == confidence[i]) {
				consequentClass = -1;
			}
		}
		if(max <= 0.5) {
			consequentClass = -1;
		}

		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabel(consequentClass);

		return classLabel;
	}

	public RuleWeight calcRuleWeight(ClassLabel consequentClass, double[] confidence) {
		// 生成不可能ルール判定
		if(consequentClass.getClassLabel() == -1) {
			RuleWeight zeroWeight = new RuleWeight();
			zeroWeight.addRuleWeight(0.0);
			return zeroWeight;
		}

		int C = consequentClass.getClassLabel();
		double sumConfidence = Arrays.stream(confidence).sum();
		double CF = confidence[C] - (sumConfidence - confidence[C]);
		RuleWeight ruleWeight = new RuleWeight();
		ruleWeight.addRuleWeight(CF);
		return ruleWeight;
	}

	public static MoFGBML_Learning.MoFGBML_LearningBuilder builder() {
		return new MoFGBML_LearningBuilder();
	}

	public static class MoFGBML_LearningBuilder {
		protected DataSet train;

		MoFGBML_LearningBuilder() {}

		public MoFGBML_Learning.MoFGBML_LearningBuilder train(DataSet train) {
			this.train = train;
			return this;
		}

		/**
		 * @param train : DataSet
		 */
		public MoFGBML_Learning build() {
			return new MoFGBML_Learning(train);
		}
	}
}
