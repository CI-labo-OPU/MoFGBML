package cilabo.labo.develop;

import java.io.File;

import cilabo.main.AbstractArgs;
import cilabo.main.Consts;
import cilabo.utility.Output;

public class CommandLineArgs extends AbstractArgs {
	// ************************************************************
	/** データセット名 */
	public static String dataName;
	/** 実験設定の識別用ID (results/*) */
	public static String algorithmID;
	/** 実験設定の識別用ID (results/*\/iris_*) */
	public static String experimentID;
	/** ForkJoinPoolの並列コア数 */
	public static int parallelCores;

	// ************************************************************
	@Override
	protected void load(String[] args) {
		int n = 1;
		if(args.length < n) {
			System.out.println("Need n=" + String.valueOf(n) + " arguments.");
			System.out.println("---");
			System.out.print(CommandLineArgs.getParamsString());
			System.out.println("---");
			return;
		}

		dataName = args[0];

		algorithmID = args[1];
		Consts.ALGORITHM_ID_DIR = Consts.ROOTFOLDER + File.separator + algorithmID;
		Output.mkdirs(Consts.ALGORITHM_ID_DIR);

		experimentID = args[2];
		Consts.EXPERIMENT_ID_DIR = Consts.ALGORITHM_ID_DIR + File.separator + dataName+"_"+experimentID;
		Output.mkdirs(Consts.EXPERIMENT_ID_DIR);


		parallelCores = Integer.parseInt(args[3]);

	}
}
