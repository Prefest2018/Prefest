package appiumscript.scripttranslator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class StoatScriptLoader {
	public static List<File> loadStoatScript(File stoatScript, File testfileFolder) {
		LinkedHashMap<Integer, TestFile> testfiles = new LinkedHashMap<Integer, TestFile>();
		List<File> testoutputfiles = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(stoatScript), "UTF-8"));
			String str = null;
			TestFile nowtestfile = null;
			while((str=br.readLine()) != null) {
				if (str.matches("the [0-9]*th test suite")) {
					int num = Integer.parseInt(str.substring(4, str.length()-13));
					nowtestfile = new TestFile(num);
					testfiles.put(num, nowtestfile);
					br.readLine();
					br.readLine();
				} else if (!str.equals("")) {
					nowtestfile.addTestCase(str);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (!testfileFolder.exists() || !testfileFolder.isDirectory()) {
			testfileFolder.mkdir();
		}
		testoutputfiles = new LinkedList<File>();
		for (int index : testfiles.keySet()) {
			String testfilename = testfileFolder.getAbsolutePath() + File.separator + "testcase" + index;
			testoutputfiles.addAll(testfiles.get(index).outputtestfile(testfilename));
		}
		return testoutputfiles;
	}
	
}
