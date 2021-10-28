package cilabo.fuzzy.classifier.operator.postProcessing.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cilabo.fuzzy.classifier.Classifier;
import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.fuzzy.classifier.operator.postProcessing.PostProcessing;
import cilabo.fuzzy.rule.Rule;
import cilabo.fuzzy.rule.antecedent.Antecedent;

public class SimplePostProcessing implements PostProcessing {

	/**
	 *
	 */
	@Override
	public Classifier postProcess(Classifier classifier) {
		try {
			if(classifier.getClass() != RuleBasedClassifier.class) throw new IllegalArgumentException("argument must be [FuzzyClassifier]");
		}
		catch(IllegalArgumentException e) {
			System.out.println(e);
			e.printStackTrace();
			return null;
		}

		classifier = remove((RuleBasedClassifier)classifier);
		classifier = removeSameAntecedent((RuleBasedClassifier)classifier);
		classifier = radixSort((RuleBasedClassifier)classifier);

		return classifier;
	}

	/**
	 *
	 * @param classifier
	 * @return
	 */
	public RuleBasedClassifier remove(RuleBasedClassifier classifier) {
		int originalRuleNum = classifier.getRuleNum();
		for(int i = originalRuleNum-1; i >= 0; i--) {
			Rule rule = classifier.getRule(i);
			if( rule.getConsequent().getRuleWeight().getRuleWeight() <= 0 ||
				rule.getAntecedent().getRuleLength() == 0) {
				classifier.popRule(i);
			}
		}
		return classifier;
	}

	/**
	 *
	 * @param classifier
	 * @return
	 */
	public RuleBasedClassifier removeSameAntecedent(RuleBasedClassifier classifier) {
		ArrayList<Integer> sameList = new ArrayList<>();
		// Trace
		for(int i = 0; i < classifier.getRuleNum(); i++) {
			for(int j = 0; j < i; j++) {
				if(!sameList.contains(j)) {
					Antecedent origin = classifier.getRule(i).getAntecedent();
					Antecedent object = classifier.getRule(j).getAntecedent();
					if(origin.toString().equals( object.toString() )) {
						sameList.add(i);
					}
				}
			}
		}
		// Remove
		for(int i = 0; i < sameList.size(); i++) {
			classifier.popRule(sameList.get(i) - i);
		}
		return classifier;
	}

	/**
	 *
	 * @param classifier
	 * @return
	 */
	public RuleBasedClassifier radixSort(RuleBasedClassifier classifier) {
		Collections.sort(classifier.getRuleSet(), new Comparator<Rule>() {
			@Override
			public int compare(Rule aa, Rule bb) {
				Antecedent a = aa.getAntecedent();
				Antecedent b = bb.getAntecedent();
				int dimension = a.getDimension();

				for(int i = 0; i < dimension; i++) {
					if(a.getAntecedentIndexAt(i) < b.getAntecedentIndexAt(i)) {
						return -1;
					}
					else if(a.getAntecedentIndexAt(i) > b.getAntecedentIndexAt(i)) {
						return 1;
					}
					else {
						continue;
					}
				}
				return 0;
			}
		});
		return classifier;
	}

}
