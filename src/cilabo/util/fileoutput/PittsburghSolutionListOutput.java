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
