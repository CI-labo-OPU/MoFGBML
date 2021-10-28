package cilabo.main;

import static org.junit.Assert.*;

import org.junit.Test;

public class ArgsSampleTest {
	@Test
	public void testArgs() {
		assertNull(ArgsSample.getParamsString());
		assertEquals(0, ArgsSample.id);
		assertNull(ArgsSample.testString);

		String[] args = new String[] {"1", "aaa"};
		/* ************************************************************* */
		ArgsSample.loadArgs(ArgsSample.class.getCanonicalName(), args);
		/* ************************************************************* */
		assertNotNull(ArgsSample.getParamsString());
		assertEquals(1, ArgsSample.id);
		assertEquals("aaa", ArgsSample.testString);

		String firstArgs = ArgsSample.getParamsString();

		args = new String[] {"2", "bbb"};
		/* ************************************************************* */
		ArgsSample.loadArgs(ArgsSample.class.getCanonicalName(), args);
		/* ************************************************************* */
		assertEquals(2, ArgsSample.id);
		assertEquals("bbb", ArgsSample.testString);

		String secondArgs = ArgsSample.getParamsString();

		assertNotSame(secondArgs, firstArgs);
	}
}
