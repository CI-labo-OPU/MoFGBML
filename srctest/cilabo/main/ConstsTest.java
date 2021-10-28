package cilabo.main;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConstsTest {

	@Test
	public void testSetConsts() {
		int UNIX = Consts.UNIX;

		String originConsts = Consts.getString();

		String source = "testConsts";
		Consts.set(source);

		// Not change (not writen in .properties file)
		int nowUNIX = Consts.UNIX;
		assertEquals(nowUNIX, UNIX);

		// Integer
		assertEquals(Consts.ANTECEDENT_LEN, -1);

		// Double
		double delta = 0.000000001;
		assertEquals(Consts.DONT_CARE_RT, -0.4, delta);

		// String
		assertEquals(Consts.ROOTFOLDER, "testConstsForJUnit");

		String loadedConsts = Consts.getString();
		assertNotSame(loadedConsts, originConsts);
	}


}
