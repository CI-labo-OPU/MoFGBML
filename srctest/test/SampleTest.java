package test;

import static org.junit.Assert.*;

import org.junit.Test;

public class SampleTest {


	@Test
	public void testSum() {
		int a = 3;
		int b = 4;
		int c = a+b;
		assertEquals(c, Sample.sum(a,  b));
	}

}
