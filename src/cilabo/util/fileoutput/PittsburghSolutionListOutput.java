package cilabo.util.fileoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.w3c.dom.Element;

import cilabo.fuzzy.classifier.RuleBasedClassifier;
import cilabo.gbml.solution.PittsburghSolution;
import cilabo.metric.ErrorRate;
import cilabo.metric.Metric;
import xml.XML_manager;

public class PittsburghSolutionListOutput extends SolutionListOutput {

	private FileOutputContext pittsburghFileContext;
	private FileOutputContext solutionFileContext;
	private List<? extends Solution<?>> solutionList;

	public PittsburghSolutionListOutput(List<? extends Solution<?>> solutionList) {
		super(solutionList);
		this.solutionList = solutionList;
	}

	public PittsburghSolutionListOutput setPittsburghFileOutputContext(FileOutputContext fileContext) {
		this.pittsburghFileContext = fileContext;
		return this;
	}

	public PittsburghSolutionListOutput setSolutionFileOutputContext(FileOutputContext fileContext) {
		this.solutionFileContext = fileContext;
		return this;
	}

	@Override
	public void print() {
		this.printPittsburghSolutionFormatsToFile(pittsburghFileContext, solutionList);
		this.printSolutionsToFile(solutionFileContext, solutionList);
	}

	public void printSolutionsToFile(FileOutputContext context, List<? extends Solution<?>> solutionList) {
		BufferedWriter bufferedWriter = context.getFileWriter();
		try {
			if(solutionList.size() > 0) {
				for(int i = 0; i < solutionList.size(); i++) {
					bufferedWriter.write(solutionList.get(i).toString());
				}
				bufferedWriter.close();
			}
		}
		catch (IOException e) {
			throw new JMetalException("Error writing data ", e);
		}
	}

	public void printPittsburghSolutionFormatsToFile(FileOutputContext context, List<? extends Solution<?>> solutionList) {
		BufferedWriter bufferedWriter = context.getFileWriter();
		try {
			if(solutionList.size() > 0) {
				for(int i = 0; i < solutionList.size(); i++) {
					bufferedWriter.write("----");
					PittsburghSolution solution = (PittsburghSolution)solutionList.get(i);
					bufferedWriter.write(solution.getClassifier().toString());
					bufferedWriter.newLine();
				}
				bufferedWriter.close();
			}
		}
		catch (IOException e) {
			throw new JMetalException("Error writing data ", e);
		}
	}

	/**
	 * population を構成するElementを返す
	 *
	 * @param xml_manager
	 * @param solutionList
	 * @return population(Element型)
	 */
	public Element printSolutionsToElement(List<? extends Solution<?>> solutionList) {
		XML_manager xml_manager = XML_manager.getInstance();
		Element population = XML_manager.createElement(xml_manager.populationName);
		if(solutionList.size() > 0) {
			for(int i = 0; i < solutionList.size(); i++) {
				Element individual = XML_manager.addChildNode(population, xml_manager.individualName);
				PittsburghSolution solution = (PittsburghSolution)solutionList.get(i);
				// solution を individual に書き込む(未実装)

				//if-then rule一覧
				RuleBasedClassifier classifier = (RuleBasedClassifier) solution.getClassifier();
				XML_manager.addElement(individual, classifier.ClassifierToElemnt());


				//各目的関数の結果
				for(int j=0; j<solution.getNumberOfObjectives(); j++) {
					XML_manager.addChildNode(individual, xml_manager.objectivesName[j+1], String.valueOf(solution.getObjectives()[j]));
				}
				Metric metric = new ErrorRate();
				double errorRate = (double)metric.metric(classifier, xml_manager.getDtst());
				XML_manager.addChildNode(individual, xml_manager.objectivesName[0], String.valueOf(errorRate));
			}
		}
		return population;
	}


}
