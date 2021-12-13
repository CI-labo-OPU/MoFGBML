package cilabo.utility;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import cilabo.data.ClassLabel;
import cilabo.data.DataSet;
import cilabo.data.InputVector;
import cilabo.data.Pattern;
import cilabo.labo.developing.fairness.FairnessPattern;

public class InputTest {

	@Test
	public void testMakeFileNameOne() {
		String sep = File.separator;
		String actual;
		String expected;

		int cv_i = 0;
		int rep_i = 0;
		String dataName = "iris";

		// tra
		boolean isTra = true;
		actual = System.getProperty("user.dir")+sep+"dataset"+sep+"iris"+sep+"a0_0_iris-10tra.dat";
		expected = Input.makeFileNameOne(dataName, cv_i, rep_i, isTra);
		assertEquals(expected, actual);

		// tst
		isTra = false;
		actual = System.getProperty("user.dir")+sep+"dataset"+sep+"iris"+sep+"a0_0_iris-10tst.dat";
		expected = Input.makeFileNameOne(dataName, cv_i, rep_i, isTra);
		assertEquals(expected, actual);
	}

	@Test
	public void testInputSingleLabelDataSet() {
		int cv_i = 0;
		int rep_i = 0;
		String dataName = "iris";
		boolean isTra = true;
		String fileName = Input.makeFileNameOne(dataName, cv_i, rep_i, isTra);

		int id = 0;
		double[] vector = new double[] {0.222222222222222,
										0.625,
										0.0677966101694915,
										0.0416666666666667};
		InputVector inputVector = new InputVector(vector);
		Integer C = 0;
		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabel(C);
		Pattern actualPattern = new Pattern(id, inputVector, classLabel);

		DataSet dataset = new DataSet();
		Input.inputSingleLabelDataSet(dataset, fileName);
		Pattern expectedPattern = dataset.getPatternWithID(id);

		// id
		assertEquals(expectedPattern.getID(), actualPattern.getID());
		// input vector
		for(int i = 0; i < vector.length; i++) {
			assertEquals((Double)expectedPattern.getDimValue(i), (Double)actualPattern.getDimValue(i));
		}
		// class label
		assertEquals(expectedPattern.getTrueClass().getClassLabel(), actualPattern.getTrueClass().getClassLabel());
	}

	@Test
	public void testInputFairnessDataSet() {
		String sep = File.separator;
		String fileName = "dataset" + sep + "german" + sep + "a0_0_german-10tra.dat";

		DataSet dataset = new DataSet();
		Input.inputFairnessDataSet(dataset, fileName);

		int p = 0;
		String expected = "0";
		int actual = ((FairnessPattern)dataset.getPattern(p)).getA();
		assertEquals(expected, String.valueOf(actual));

		p = 1;
		expected = "1";
		actual = ((FairnessPattern)dataset.getPattern(p)).getA();
		assertEquals(expected, String.valueOf(actual));


	}

	@Test
	public void testInputMultiLabelDataSet() {
		int cv_i = 0;
		int rep_i = 0;
		String dataName = "flags";
		boolean isTra = true;
		String fileName = Input.makeFileNameOne(dataName, cv_i, rep_i, isTra);

		int id = 0;
		double[] vector = new double[] {-4.0, -2.0, 0.05566467279707169, 0.006944444444444444, -10.0,
										-6.0, 0.0, 0.14285714285714285, 0.2857142857142857, 0.0,
										0.0, 0.0, 0.0, 0.02,  -1.0,
										-1.0, -2.0, -1.0, -1.0};
		InputVector inputVector = new InputVector(vector);
		Integer[] cVec = new Integer[] {1, 0, 0, 1, 0, 1, 0};
		ClassLabel classLabel = new ClassLabel();
		classLabel.addClassLabels(cVec);
		Pattern actualPattern = new Pattern(id, inputVector, classLabel);

		DataSet dataset = new DataSet();
		Input.inputMultiLabelDataSet(dataset, fileName);
		Pattern expectedPattern = dataset.getPatternWithID(id);

		// id
		assertEquals(expectedPattern.getID(), actualPattern.getID());
		// input vector
		for(int i = 0; i < vector.length; i++) {
			assertEquals((Double)expectedPattern.getDimValue(i), (Double)actualPattern.getDimValue(i));
		}
		// class label
		for(int i = 0; i < cVec.length; i++) {
			Integer actual = actualPattern.getTrueClass().getClassVector()[i];
			Integer expected = expectedPattern.getTrueClass().getClassVector()[i];
			assertEquals(expected, actual);
		}
	}

}
