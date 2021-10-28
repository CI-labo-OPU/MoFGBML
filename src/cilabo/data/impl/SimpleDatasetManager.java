package cilabo.data.impl;

import java.util.ArrayList;

import cilabo.data.DataSet;
import cilabo.data.DatasetManager;

/**
 * 学習用データ1つ，評価用データ1つのシンプルなデータ分割を保持するクラス.
 */
public class SimpleDatasetManager implements DatasetManager {
	// ************************************************************
	ArrayList<DataSet> trains = new ArrayList<>();
	ArrayList<DataSet> tests = new ArrayList<>();

	// ************************************************************
	public SimpleDatasetManager() {
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

}
