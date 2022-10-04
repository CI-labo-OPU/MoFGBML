package cilabo.fuzzy.classifier;

import java.util.ArrayList;

import org.w3c.dom.Element;

import cilabo.data.InputVector;
import cilabo.fuzzy.classifier.operator.classification.Classification;
import cilabo.fuzzy.rule.Rule;
import xml.XML_manager;

public class RuleBasedClassifier implements Classifier {
	// ************************************************************
	// Fields

	/**  */
	ArrayList<Rule> ruleSet = new ArrayList<>();

	/**  */
	Classification classification;

	// ************************************************************
	// Constructor
	public RuleBasedClassifier() {}

	/**  Copy constructor */
	public RuleBasedClassifier(RuleBasedClassifier classifier) {
		ruleSet = new ArrayList<>(classifier.getRuleNum());
		for(int i = 0; i < ruleSet.size(); i++) {
			ruleSet.add(i, classifier.getRule(i).deepcopy());
		}
		classification = classifier.getClassification();
	}

	// ************************************************************
	// Methods

	/**
	 *
	 */
	@Override
	public Rule classify(InputVector vector) {
		return (Rule)this.classification.classify(this, vector);
	}

	/**
	 *
	 */
	public int getRuleNum() {
		return this.ruleSet.size();
	}

	/**
	 *
	 */
	public int getRuleLength() {
		int length = 0;
		for(int i = 0; i < ruleSet.size(); i++) {
			length += ruleSet.get(i).getAntecedent().getRuleLength();
		}
		return length;
	}

	/**
	 *
	 */
	public void addRule(Rule rule) {
		this.ruleSet.add(rule);
	}

	public Rule getRule(int index) {
		return this.ruleSet.get(index);
	}

	public ArrayList<Rule> getRuleSet() {
		return this.ruleSet;
	}

	/**
	 *
	 */
	public Rule popRule(int index) {
		return this.ruleSet.remove(index);
	}

	/**
	 *
	 */
	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public Classification getClassification() {
		return this.classification;
	}

	public RuleBasedClassifier copy() {
		return new RuleBasedClassifier(this);
	}

	/**
	 *
	 */
	@Override
	public String toString() {
		String ln = System.lineSeparator();
		String str = "@classification: " + classification.toString() + ln;
		for(int i = 0; i < ruleSet.size(); i++) {
			str += ruleSet.get(i).toString() + ln;
		}
		return str;
	}

	public Element ClassifierToElemnt() {
		XML_manager xml_manager = XML_manager.getInstance();
		Element individualElement = XML_manager.createElement(xml_manager.classifierName);
		for(Rule rule: this.ruleSet) {
			Element singleRule = XML_manager.addChildNode(individualElement, xml_manager.ruleName);
			//前件部
			Element antecedent = XML_manager.addChildNode(singleRule, xml_manager.antecedentName);
			int[] AntecedentIndex = rule.getAntecedent().getAntecedentIndex();
			for(int i=0; i<AntecedentIndex.length; i++) {
				XML_manager.addChildNode(antecedent, xml_manager.fuzzyTermIDName, String.valueOf(AntecedentIndex[i]),
						xml_manager.dimentionIDName, String.valueOf(i));
			}

			//後件部
			Element consequent = XML_manager.addChildNode(singleRule, xml_manager.consequentName);

			//結論部クラス
			Element consequentClass = XML_manager.addChildNode(consequent, xml_manager.consequentClassVectorName);
			Integer[] classLabelVector = rule.getConsequent().getClassLabel().getClassVector();
			for(int i=0; i<classLabelVector.length; i++) {
				XML_manager.addChildNode(consequentClass, xml_manager.consequentClassName, String.valueOf(classLabelVector[i]),
						xml_manager.consequentClassIndexName, String.valueOf(i));
			}

			//ルール重み
			Element ruleWeight = XML_manager.addChildNode(consequent, xml_manager.ruleWeightVectorName);
			Double[] ruleWeightVector = rule.getConsequent().getRuleWeight().getRuleWeightVector();
			for(int i=0; i<ruleWeightVector.length; i++) {
				XML_manager.addChildNode(ruleWeight, xml_manager.ruleWeightName, String.valueOf(ruleWeightVector[i]),
						xml_manager.ruleWeightIndexName, String.valueOf(i));
			}

		}
		return individualElement;
	}

}
