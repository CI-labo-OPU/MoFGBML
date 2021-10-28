package cilabo.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Output {
	/**
	 * Making new directory "dirName" into "path" by "mkdirs()".<br>
	 * If parent directory does not exist, this method makes parent directory simultaneously.<br>
	 * @param path
	 * @param dirName
	 */
	public static void makeDir(String path, String dirName) {
		String sep = File.separator;
		mkdirs(path + sep + dirName);
	}

	public static void mkdirs(String dirName) {
		File newdir = new File(dirName);
		newdir.mkdirs();
	}

	/**
	 * String用
	 * @param fileName
	 * @param str : String
	 * @param append : boolean : true=append, false=rewrite
	 */
	public static void writeln(String fileName, String str, boolean append) {
		String[] array = new String[] {str};
		writeln(fileName, array, append);
	}

	/**
	 * ArrayList用
	 * @param fileName
	 * @param strs : ArrayList{@literal <String>}
	 * @param append : boolean : true=append, false=rewrite
	 */
	public static void writeln(String fileName, ArrayList<String> strs, boolean append) {
		String[] array = (String[]) strs.toArray(new String[0]);
		writeln(fileName, array, append);
	}

	/**
	 * 配列用
	 * @param fileName
	 * @param array : String[]
	 * @param append : boolean : true=append, false=rewrite
	 */
	public static void writeln(String fileName, String[] array, boolean append){
		try {
			FileWriter fw = new FileWriter(fileName, append);
			PrintWriter pw = new PrintWriter( new BufferedWriter(fw) );
			for(int i=0; i<array.length; i++){
				 pw.println(array[i]);
			}
			pw.close();
	    }
		catch (IOException ex){
			ex.printStackTrace();
	    }
	}
}
