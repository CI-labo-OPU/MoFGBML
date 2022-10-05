package cilabo.util.fileoutput;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.fileoutput.FileOutputContext;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;

import cilabo.gbml.solution.PittsburghSolution;

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

	public void printObjectivesToFile(FileOutputContext context, List<? extends Solution<?>> solutionList) {
		BufferedWriter bufferedWriter = context.getFileWriter();
		try {
			if(solutionList.size() > 0) {
				int numberOfObjectives = solutionList.get(0).getNumberOfObjectives();
				String str = "f0";
				for(int i = 1; i < numberOfObjectives; i++) {
					str += "," + "f"+i;
				}
				bufferedWriter.write(str);
				bufferedWriter.newLine();
				for(int i = 0; i < solutionList.size(); i++) {
					str = String.valueOf(solutionList.get(i).getObjective(0));
					for(int j = 1; j < numberOfObjectives; j++) {
						str += "," + solutionList.get(i).getObjective(j);
					}
					bufferedWriter.write(str);
					bufferedWriter.newLine();
				}
				bufferedWriter.close();
			}
		}
		catch (IOException e) {
			throw new JMetalException("Error writing data ", e);
		}
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


}
