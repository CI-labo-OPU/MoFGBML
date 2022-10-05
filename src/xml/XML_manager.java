package xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import cilabo.data.DataSet;
import cilabo.main.Consts;

public class XML_manager {
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private DOMImplementation domImpl;
	protected static Document document;
	private TransformerFactory transFactory;
	private static Transformer transformer;
	private static String xmlFileName;

	//xmlファイル用パラメータ
		//ruleSet
		public final String populationName = "population";
		public final String generationName = "evaluations";
		public final String individualName = "individual";
		public final String classifierName = "classifier";
		public final String ruleName = "singleRule";
		public final String antecedentName = "antecedent";
		public final String attributeName = "attribute";
		public final String dimentionIDName = "dimentionID";
		public final String consequentName = "consequent";
		public final String consequentClassVectorName = "consequentClasses";
		public final String consequentClassName = "consequentClass";
		public final String consequentClassIndexName = "ClassID";
		public final String ruleWeightVectorName = "ruleWeights";
		public final String ruleWeightName = "ruleWeight";
		public final String ruleWeightIndexName = "ruleWeightID";
		public final String[] objectivesName = {"accuracyRate_Dtst", "accuracyRate_Dtra", "ruleNum"};

		//knowledge
		public final String knowledgeName = "knowledge";
		public final String fuzzySetAtDimName = "fuzzySets";
		public final String partionNumName = "partionNum";
		public final String partion_iName = "partion_i";
		public final String partionTypeName = "partionType";
		public final String fuzzyTermName = "fuzzyTerm";
		public final String fuzzyTermIDName = "fuzzyTermID";
		public final String fuzzyTermNameTagName = "fuzzyTermName";
		public final String fuzzyTermShapeTypeIDName = "ShapeTypeID";
		public final String fuzzyTermShapeTypeNameTagName = "ShapeTypeName";
		public final String parametersName = "parameters";
		public final String parametersAtDimName = "parameter";
		public final String constsName = "consts";
	//xmlファイル用パラメータ

	private static XML_manager instance = new XML_manager();
	private DataSet Dtst;


	public static XML_manager getInstance() {
		return instance;
	}

	public static void setInstance(XML_manager instance) {
		XML_manager.instance = instance;
	}

	public DataSet getDtst() {
		return Dtst;
	}


	public void setDtst(DataSet dtst) {
		Dtst = dtst;
	}

	/**
	 * @param rb rulebase
	 * @throws Exception
	 */
	private XML_manager(){
		xmlFileName = Consts.XML_FILE_NAME;
		if(!(xmlFileName.endsWith(".xml"))) {
			xmlFileName = xmlFileName +".xml";
		}
		factory = DocumentBuilderFactory.newInstance();
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		domImpl = builder.getDOMImplementation();
		document = domImpl.createDocument("", xmlFileName, null);
		transFactory = TransformerFactory.newInstance();
		try {
			transformer = transFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 書き込まれたdocファイルをxml形式に書き出す
	 *
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static void output(String savePath) throws TransformerException, IOException {
		DOMSource source = new DOMSource(document);
		File newXML = new File(savePath + File.separator + xmlFileName);
		Path path = Paths.get(savePath);
		Files.createDirectories(path);
		FileOutputStream os = new FileOutputStream(newXML);
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result);
	}

	/**
	 * 親ノードに子ノードを追加する．生成された子ノードを返す
	 * @param nodeName 生成したい子ノードの名
	 * @param parent 追加先の親ノード
	 * @return 生成された子ノード(Element型)
	 */
	public static Element addChildNode(Element parent, String nodeName) {
		Element v = document.createElement(nodeName);
		parent.appendChild(v);
		return v;
	}

	/**
	 * 親ノードに子ノードを追加する．子ノードに値を追加 生成された子ノードを返す
	 * @param parent 追加先の親ノード
	 * @param nodeName 生成したい子ノードの名
	 * @param nodeValue 要素の持つ値
	 * @return 生成された子ノード(Element型)
	 */
	public static Element addChildNode(Element parent, String nodeName, String nodeValue) {
		Element v = document.createElement(nodeName);
		Text textContents = document.createTextNode(nodeValue);
		v.appendChild(textContents);
		parent.appendChild(v);
		return v;
	}

	/**
	 * 親ノードに子ノードを追加する. 属性値を追加 生成された子ノードを返す
	 * @param parent 追加先の親ノード
	 * @param nodeName 生成したい子ノードの名
	 * @param attributeName 属性値名
	 * @param attributeValue 属性の持つ値
	 * @return 生成された子ノード(Element型)
	 */
	public static Element addChildNode(Element parent, String nodeName, String attributeName, String attributeValue) {
		Element v = document.createElement(nodeName);
		v.setAttribute(attributeName, attributeValue);
		parent.appendChild(v);
		return v;
	}

	/**
	 * 親ノードに子ノードを追加する. 値を追加．属性値を追加  生成された子ノードを返す
	 * @param parent 追加先の親ノード
	 * @param nodeName 生成したい子ノードの名
	 * @param nodeValue 要素の持つ値
	 * @param attributeName 属性値名
	 * @param attributeValue 属性の持つ値
	 * @return 生成された子ノード(Element型)
	 */
	public static Element addChildNode(Element parent, String nodeName, String nodeValue, String attributeName, String attributeValue) {
		Element v = document.createElement(nodeName);
		Text textContents = document.createTextNode(nodeValue);
		v.appendChild(textContents);
		v.setAttribute(attributeName, attributeValue);
		parent.appendChild(v);
		return v;
	}

	/**
	 *ツリーの根を返す
	 */
	public static Element getRoot() {
		return document.getDocumentElement();
	}

	//新規のElementを追加する
	public static Element createElement(String nodeName) {
		return document.createElement(nodeName);
	}

	//Elementを追加する
	public static Element addElement(Element parent, Element child) {
		parent.appendChild(child);
		return parent;
	}


	//Elementを追加する
	public static Element addElement(Element parent, Element child, String attributeName, String attributeValue) {
		child.setAttribute(attributeName, attributeValue);
		parent.appendChild(child);
		return parent;
	}

	/**
	 * 子ノードのリストを返す
	 * @param parent
	 * @return 子ノードのリスト(NodeList型)
	 */
	public static NodeList getChildNodeList(Element parent) {
		return parent.getChildNodes();
	}

	/**
	 * 子ノードのリストを返す
	 * @param parent
	 * @return 子ノードのリスト(Element[]型)
	 */
	public static Element[] getChildElementList(Element parent) {
		NodeList nodelist = parent.getChildNodes();
		int len = nodelist.getLength();
		Element[] elementList = new Element[len];
		for(int i=0; i<nodelist.getLength(); i++) {
			Node node = nodelist.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				elementList[i] = (Element)node;
			}
		}
		return elementList;
	}

	/**
	 * node名が一致する子ノードを返す
	 * @param parent
	 * @return 子ノード(Element型)
	 */
	public static Element getChildElement(Element parent, String nodeName) {
		NodeList nodelist = parent.getChildNodes();
		for(int i=0; i<nodelist.getLength(); i++) {
			Node node = nodelist.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				if(element.getNodeName().equals(nodeName)) {
					return element;
				}
			}
		}
		return null;
	}

	/**
	 * node名とAttributeが一致する子ノードを返す
	 * @param parent
	 * @return 子ノード(Element型)
	 */
	public static Element getChildElement(Element parent, String nodeName, String attributeName, String attributeValue) {
		NodeList nodelist = parent.getChildNodes();
		for(int i=0; i<nodelist.getLength(); i++) {
			Node node = nodelist.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				if(element.getNodeName().equals(nodeName) && element.getAttribute(attributeName).equals(attributeValue)) {
					return element;
				}
			}
		}
		return null;
	}
}
