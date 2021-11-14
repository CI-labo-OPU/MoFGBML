package cilabo.main;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 各種定数 定義クラス
 * Consts.[変数名]でアクセス可能
 *
 * 本ソース(Consts.java)の以下で指定している値はデフォルト値
 * もし、jarエクスポート後に変更したい値が出てきた場合は、
 * consts.propertiesに変更したい変数を書けば良い
 *     例: 「WINDOWS = 1」(in consts.properties)
 * .propertiesファイルは、Consts.setConsts(String source)メソッドによって読み込まれる
 *     例: Consts.setConsts("consts");  // load consts.properties
 *
 */
public class Consts {

	//Experimental Settings *********************************
	public static int populationSize = 60;
	public static int offspringPopulationSize = 60;
	public static int terminateGeneration = 5000;
	public static int terminateEvaluation = 300000;
	public static int outputFrequency = 6000;

	//Random Number ***************************************
	public static int RAND_SEED = 2020;

	//OS ************************************
	public static int WINDOWS = 0;	//Windows
	public static int UNIX = 1;		//Unix or Mac

	//Fuzzy Clasifier ************************************
	/** don't care適応確率を定数にするかどうか */
	public static boolean IS_PROBABILITY_DONT_CARE = false;
	/** don't careにしない条件部の数 */
	public static int ANTECEDENT_LEN = 5;
	/** don't care適応確率 */
	public static double DONT_CARE_RT = 0.8;
	/** 初期ル―ル数 */
	public static int INITIATION_RULE_NUM = 30;
	/** 1識別器あたりの最大ルール数 */
	public static int MAX_RULE_NUM = 60;
	/** 1識別器あたりの最小ルール数 */
	public static int MIN_RULE_NUM = 1;

	//FGBML ************************************
	/** Michigan適用確率 */
	public static double MICHIGAN_OPE_RT = 0.5;	//元RULE_OPE_RT
	/** ルール入れ替え割合 */
	public static double RULE_CHANGE_RT = 0.2;
	/** Michigan交叉確率 */
	public static double MICHIGAN_CROSS_RT = 0.9;	//元RULE_CROSS_RT
	/** Pittsburgh交叉確率 */
	public static double PITTSBURGH_CROSS_RT = 0.9;	//元RULESET_CROSS_RT

	//Experiment ************************************
	/** ドット表示する世代間隔 */
	public static int PER_SHOW_DOT = 100;	//元PER_SHOW_GENERATION_NUM
	/** 現世代数表示する世代間隔 */
	public static int PER_SHOW_GENERATION_DETAIL = 10;

	//Index ************************************
	/** 学習用データ */
	public static int TRAIN = 0;
	/** 評価用データ */
	public static int TEST = 1;

	//Folders' Name ************************************
	public static String ROOTFOLDER = "results";
	public static String ALGORITHM_ID_DIR = "ALGORITHM_ID";
	public static String EXPERIMENT_ID_DIR = "EXPERIMENT_ID";

	public static String DATASET = "dataset";
	public static String RULESET = "ruleset";
	public static String INDIVIDUAL = "individual";
	public static String POPULATION = "population";
	public static String OFFSPRING = "offspring";
	public static String SUBDATA = "subdata";
	public static String TIMES = "times";


	/******************************************/

	public static void set(String source) {
		String dir = "./";
		URLClassLoader urlLoader = null;
		ResourceBundle bundle = null;
		try {
			urlLoader = new URLClassLoader(new URL[] {new File(dir).toURI().toURL()});
			bundle = ResourceBundle.getBundle(source, Locale.getDefault(), urlLoader);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		if(bundle.containsKey("populationSize")) { populationSize = Integer.parseInt(bundle.getString("populationSize")); }
		if(bundle.containsKey("offspringPopulationSize")) { offspringPopulationSize = Integer.parseInt(bundle.getString("offspringPopulationSize")); }
		if(bundle.containsKey("terminateGeneration")) { terminateGeneration = Integer.parseInt(bundle.getString("terminateGeneration")); }
		if(bundle.containsKey("terminateEvaluation")) { terminateEvaluation = Integer.parseInt(bundle.getString("terminateEvaluation")); }
		if(bundle.containsKey("outputFrequency")) { outputFrequency = Integer.parseInt(bundle.getString("outputFrequency")); }

		if(bundle.containsKey("RAND_SEED")) { RAND_SEED = Integer.parseInt(bundle.getString("RAND_SEED")); }
		if(bundle.containsKey("WINDOWS")) { WINDOWS = Integer.parseInt(bundle.getString("WINDOWS")); }
		if(bundle.containsKey("UNIX")) { UNIX = Integer.parseInt(bundle.getString("UNIX")); }
		if(bundle.containsKey("IS_PROBABILITY_DONT_CARE")) { IS_PROBABILITY_DONT_CARE = Boolean.parseBoolean(bundle.getString("IS_PROBABILITY_DONT_CARE")); }
		if(bundle.containsKey("ANTECEDENT_LEN")) { ANTECEDENT_LEN = Integer.parseInt(bundle.getString("ANTECEDENT_LEN")); }
		if(bundle.containsKey("DONT_CARE_RT")) { DONT_CARE_RT = Double.parseDouble(bundle.getString("DONT_CARE_RT")); }
		if(bundle.containsKey("INITIATION_RULE_NUM")) { INITIATION_RULE_NUM = Integer.parseInt(bundle.getString("INITIATION_RULE_NUM")); }
		if(bundle.containsKey("MAX_RULE_NUM")) { MAX_RULE_NUM = Integer.parseInt(bundle.getString("MAX_RULE_NUM")); }
		if(bundle.containsKey("MIN_RULE_NUM")) { MIN_RULE_NUM = Integer.parseInt(bundle.getString("MIN_RULE_NUM")); }
		if(bundle.containsKey("MICHIGAN_OPE_RT")) { MICHIGAN_OPE_RT = Double.parseDouble(bundle.getString("MICHIGAN_OPE_RT")); }
		if(bundle.containsKey("RULE_CHANGE_RT")) { RULE_CHANGE_RT = Double.parseDouble(bundle.getString("RULE_CHANGE_RT")); }
		if(bundle.containsKey("MICHIGAN_CROSS_RT")) { MICHIGAN_CROSS_RT = Double.parseDouble(bundle.getString("MICHIGAN_CROSS_RT")); }
		if(bundle.containsKey("PITTSBURGH_CROSS_RT")) { PITTSBURGH_CROSS_RT = Double.parseDouble(bundle.getString("PITTSBURGH_CROSS_RT")); }
		if(bundle.containsKey("PER_SHOW_DOT")) { PER_SHOW_DOT = Integer.parseInt(bundle.getString("PER_SHOW_DOT")); }
		if(bundle.containsKey("PER_SHOW_GENERATION_DETAIL")) { PER_SHOW_GENERATION_DETAIL = Integer.parseInt(bundle.getString("PER_SHOW_GENERATION_DETAIL")); }
		if(bundle.containsKey("ALGORITHM_ID_DIR")) { ALGORITHM_ID_DIR = bundle.getString("ALGORITHM_ID_DIR"); }
		if(bundle.containsKey("EXPERIMENT_ID_DIR")) { EXPERIMENT_ID_DIR = bundle.getString("EXPERIMENT_ID_DIR"); }
		if(bundle.containsKey("TRAIN")) { TRAIN = Integer.parseInt(bundle.getString("TRAIN")); }
		if(bundle.containsKey("TEST")) { TEST = Integer.parseInt(bundle.getString("TEST")); }
		if(bundle.containsKey("ROOTFOLDER")) { ROOTFOLDER = bundle.getString("ROOTFOLDER"); }
		if(bundle.containsKey("RULESET")) { RULESET = bundle.getString("RULESET"); }
		if(bundle.containsKey("INDIVIDUAL")) { INDIVIDUAL = bundle.getString("INDIVIDUAL"); }
		if(bundle.containsKey("POPULATION")) { POPULATION = bundle.getString("POPULATION"); }
		if(bundle.containsKey("OFFSPRING")) { OFFSPRING = bundle.getString("OFFSPRING"); }
		if(bundle.containsKey("SUBDATA")) { SUBDATA = bundle.getString("SUBDATA"); }
		if(bundle.containsKey("DATASET")) { DATASET = bundle.getString("DATA"); }
		if(bundle.containsKey("TIMES")) { TIMES = bundle.getString("TIMES"); }

		bundle = null;
	}

	public static String getString() {
		Consts consts = new Consts();
		StringBuilder sb = new StringBuilder();
		String ln = System.lineSeparator();
		sb.append("Class: " + consts.getClass().getCanonicalName() + ln);
		sb.append("Consts: " + ln);
		for(Field field : consts.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				sb.append( field.getName() + " = " + field.get(consts) + ln );
			}
			catch(IllegalAccessException e) {
				sb.append(field.getName() + " = " + "access denied" + ln);
			}
		}
		return sb.toString();
	}


}
