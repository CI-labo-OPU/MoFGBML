package cilabo.fuzzy.knowledge.membershipParams;

import org.junit.Before;
import org.junit.Test;

import cilabo.data.DataSet;
import cilabo.data.impl.TrainTestDatasetManager;
import cilabo.labo.developing.takigawa.CommandLineArgs;

public class MakeParameterTest {

	private MakeParameter makeHomeParameter;
	private MakeParameter makeInhomeParameter;
	DataSet train;

	@Before
	public void SetUp() {
		String[] args = {"iris", "FAN2021", "trial00", "12", "dataset\\iris\\a0_0_iris-10tra.dat", "dataset\\iris\\a0_0_iris-10tst.dat"};
		// set command arguments to static variables
		CommandLineArgs.loadArgs(CommandLineArgs.class.getCanonicalName(), args);

		/* Load Dataset ======================== */
		TrainTestDatasetManager datasetManager = new TrainTestDatasetManager();
		datasetManager.loadTrainTestFiles(CommandLineArgs.trainFile, CommandLineArgs.testFile);

		/* Run MoFGBML algorithm =============== */
		train = datasetManager.getTrains().get(0);

		makeHomeParameter = new MakeParameter();
		makeInhomeParameter = new MakeParameter();

		makeHomeParameter.makeHomePartition(new int[]{2, 3, 4, 5});
		makeInhomeParameter.makePartition(train, new int[]{2, 3, 4, 5}, 0);
	}


	@Test
	public void testTriangle() {
		makeHomeParameter.triangle();
		makeInhomeParameter.triangle();
	}

	@Test
	public void testLinerShape() {
		makeHomeParameter.linerShape(1.0);
		makeInhomeParameter.linerShape(1.0);
	}

	@Test
	public void testGaussian() {
		makeHomeParameter.gaussian();
		makeInhomeParameter.gaussian();
	}

	@Test
	public void testRectangle() {
		makeHomeParameter.rectangle();
		makeInhomeParameter.rectangle();
	}

	@Test
	public void testTrapezoid() {
		makeHomeParameter.trapezoid();
		makeInhomeParameter.trapezoid();
	}

}
