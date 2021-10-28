package cilabo.main;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * メンバ変数はpublic static修飾子をつける
 * コマンドライン引数をグローバル変数化するためのインターフェース
 * 実験ごとにArgsを実装して、グローバル変数を生成できる
 *
 */
public abstract class AbstractArgs {
	private static AbstractArgs instance = null;

	protected abstract void load(String[] args);

	public static void loadArgs(String argsName, String[] args) {
		try {
			instance = (AbstractArgs)Class.forName(argsName).getConstructor().newInstance();
		} catch (InstantiationException |
				 IllegalAccessException |
				 IllegalArgumentException |
				 InvocationTargetException |
				 NoSuchMethodException |
				 SecurityException |
				 ClassNotFoundException e) {
			e.printStackTrace();
		}
		instance.load(args);
	}

	public static String getParamsString() {
		if(instance == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		String ln = System.lineSeparator();
		sb.append("Class: " + instance.getClass().getCanonicalName() + ln);
		sb.append("Parameters: " + ln);
		for(Field field : instance.getClass().getDeclaredFields()) {
			try {
				field.setAccessible(true);
				sb.append( field.getName() + " = " + field.get(instance) + ln );
			}
			catch(IllegalAccessException e) {
				sb.append(field.getName() + " = " + "access denied" + ln);
			}
		}
		return sb.toString();
	}
}
