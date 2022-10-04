package test;

import java.io.FileNotFoundException;

import org.junit.Test;
import org.uma.jmetal.util.JMetalException;

public class SampleTest {


	@Test
	public void test() throws JMetalException, FileNotFoundException {
		String[] args = {"iris", "test", "trial00", "12", "dataset\\iris\\a0_0_iris-10tra.dat", "dataset\\iris\\a0_0_iris-10tst.dat"};
//		FAN2021_Main.main(args);
	}

}
