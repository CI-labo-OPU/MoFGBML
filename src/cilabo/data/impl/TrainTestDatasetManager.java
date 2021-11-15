package cilabo.data.impl;

import java.io.File;
import java.util.ArrayList;

import cilabo.data.DataSet;
import cilabo.data.DatasetManager;
import cilabo.labo.developing.fan2021.CommandLineArgs;
import cilabo.main.Consts;
import cilabo.utility.Input;

/**
 * 学習用データ1つ，評価用データ1つのシンプルなデータ分割を保持するクラス.
 */
public class TrainTestDatasetManager implements DatasetManager {
	// ************************************************************
	ArrayList<DataSet> trains = new ArrayList<>();
	ArrayList<DataSet> tests = new ArrayList<>();

	// ************************************************************
	public TrainTestDatasetManager() {
	}

	// ************************************************************
	public void addTrains(DataSet train) {
		this.trains.add(train);
	}

	public void addTests(DataSet test) {
		this.tests.add(test);
	}

	public ArrayList<DataSet> getTrains() {
		return this.trains;
	}

	public ArrayList<DataSet> getTests() {
		return this.tests;
	}

	/**
	 * ファイル名を指定してデータセットをロードする関数
	 * @param trainFile String
	 * @param testFile String
	 * @return DatasetManager
	 */
	public TrainTestDatasetManager loadTrainTestFiles(String trainFile, String testFile) {

		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, trainFile);
		addTrains(train);

		DataSet test = new DataSet();
		Input.inputSingleLabelDataSet(test, testFile);
		addTests(test);

		return this;
	}

	/**
	 * irisのtrial00をロードする関数.
	 * @return DatasetManager
	 */
	public TrainTestDatasetManager loadIrisTrial00() {
		String sep = File.separator;
		String fileName;

		// Training dataset
		fileName = Consts.DATASET;
		fileName += sep + CommandLineArgs.dataName;
		fileName += sep + "a0_0_" + CommandLineArgs.dataName + "-10tra.dat";
		DataSet train = new DataSet();
		Input.inputSingleLabelDataSet(train, fileName);
		addTrains(train);

		// Test dataset
		fileName = Consts.DATASET;
		fileName += sep + CommandLineArgs.dataName;
		fileName += sep + "a0_0_" + CommandLineArgs.dataName + "-10tst.dat";
		DataSet test = new DataSet();
		Input.inputSingleLabelDataSet(test, fileName);
		addTests(test);

		return this;
	}

}
