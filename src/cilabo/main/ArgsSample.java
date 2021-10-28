package cilabo.main;

public class ArgsSample extends AbstractArgs {
	// ************************************************************
	// Fields
	public static int id;
	public static String testString;

	// ************************************************************
	// Methods

	@Override
	protected void load(String[] args) {
		id = Integer.parseInt(args[0]);
		testString = args[1];
	}


}
