package cilabo.fuzzy.knowledge;

import org.w3c.dom.Element;

import jfml.term.FuzzyTermType;
import xml.XML_manager;


/**
 * singletoneに変更，アプリケーション内で唯一のインスタンスを持ちます．≒グローバル変数
 * @author hirot
 *
 */
public class Knowledge {
	// ************************************************************
	// Fields
	private static Knowledge instace = new Knowledge();

	/** */
	private FuzzyTermType[][] fuzzySets;

	// ************************************************************
	// Constructor
	private Knowledge() {}


	// ************************************************************
	// Methods

	public static Knowledge getInstace() {
		return instace;
	}

	public FuzzyTermType getFuzzySet(int dimension, int H) {
		return fuzzySets[dimension][H];
	}

	/**
	 *
	 * @param x
	 * @param dimension
	 * @param H
	 * @return
	 */
	public double getMembershipValue(double x, int dimension, int H) {
		return (double)fuzzySets[dimension][H].getMembershipValue((float)x);
	}


	/**
	 *
	 */
	public int getDimension() {
		return fuzzySets.length;
	}

	/**
	 *
	 */
	public int getFuzzySetNum(int dimension) {
		return fuzzySets[dimension].length;
	}

	/**
	 * Shallow copy
	 */
	public void setFuzzySets(FuzzyTermType[][] fuzzySets) {
		this.fuzzySets = fuzzySets;
	}

	@Override
	public String toString() {
		String ln = System.lineSeparator();
		String str = "";

		for(int i = 0; i < fuzzySets.length; i++) {
			for(int j = 0; j < fuzzySets[i].length; j++) {
				str += fuzzySets[i][j].toString() + ln;
			}
		}

		return str;
	}

	public Element knowledgeToElement() {
		XML_manager xml_manager = XML_manager.getInstance();
		Element knowledge = XML_manager.createElement(xml_manager.knowledgeName);
		for(int dim_i=0; dim_i<this.getDimension(); dim_i++) {
			FuzzyTermType[] fuzzyTermTypeAtDim = this.fuzzySets[dim_i];
			Element fuzzySets = XML_manager.addChildNode(knowledge, xml_manager.fuzzySetAtDimName,
					xml_manager.dimentionIDName, String.valueOf(dim_i));
			for(int j=0; j<fuzzyTermTypeAtDim.length; j++) {
				FuzzyTermType fuzzyTerm = fuzzyTermTypeAtDim[j];
				Element fuzzyTermElement = XML_manager.addChildNode(fuzzySets, xml_manager.fuzzyTermName);
				XML_manager.addChildNode(fuzzyTermElement, xml_manager.fuzzyTermIDName, String.valueOf(j));
				XML_manager.addChildNode(fuzzyTermElement, xml_manager.fuzzyTermNameTagName, fuzzyTerm.getName());
				XML_manager.addChildNode(fuzzyTermElement, xml_manager.fuzzyTermShapeTypeIDName, String.valueOf(fuzzyTerm.getType()));
				XML_manager.addChildNode(fuzzyTermElement, xml_manager.fuzzyTermShapeTypeNameTagName, this.getFuzzyTermShapeTypeNameFromID(fuzzyTerm.getType()));
				Element parameters = XML_manager.addChildNode(fuzzyTermElement, xml_manager.parametersName);
				float[] parametersList = fuzzyTerm.getParam();
				for(int k=0; k<parametersList.length; k++) {
					XML_manager.addChildNode(parameters, xml_manager.parametersAtDimName, String.valueOf(parametersList[k]), xml_manager.dimentionIDName, String.valueOf(k));
				}
			}
		}
		return knowledge;
	}

	private String getFuzzyTermShapeTypeNameFromID(int id) {
		String ShapeName =null;
		switch(id) {
			case 0: ShapeName = "rightLinearShape"; break;
			case 1: ShapeName = "leftLinearShape"; break;
			case 2: ShapeName = "piShape"; break;
			case 3: ShapeName = "triangularShape"; break;
			case 4: ShapeName = "gaussianShape"; break;
			case 5: ShapeName = "rightGaussianShape"; break;
			case 6: ShapeName = "leftGaussianShape"; break;
			case 7: ShapeName = "trapezoidShape"; break;
			case 8: ShapeName = "singletonShape"; break;
			case 9: ShapeName = "rectangularShape"; break;
			case 10: ShapeName = "zShape"; break;
			case 11: ShapeName = "sShape"; break;
			case 12: ShapeName = "pointSetShape"; break;
			case 13: ShapeName = "pointSetMonotonicShape"; break;
			case 14: ShapeName = "circularDefinition"; break;
			case 15: ShapeName = "customShape"; break;
			case 16: ShapeName = "customMonotonicShape"; break;
			case 99: ShapeName = "DontCare"; break;
		}
		return ShapeName;
	}
}
