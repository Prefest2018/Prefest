package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import GUI.Main;

public class CMDUtils {
	public static List<String> readCMD(String filepath) {
		ArrayList<String> CMDs = new ArrayList<String>();
		File f = new File(filepath);
		String CMDstr = "";
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String content = null;
			while((content=br.readLine()) != null) {
				CMDstr += content;
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		CMDstr = CMDstr.replace("\r", "").replace("\n", "").trim();
		int inQuota = 0;
		int inDoubleQuota = 0;
		int inBracket = 0;
		int previousIndex = 0;
		for (int i = 0; i < CMDstr.length(); i++) {
			char c = CMDstr.charAt(i);
			if (inDoubleQuota%2 == 0 && inBracket == 0 && c == '\'' && (i==0 || CMDstr.charAt(i-1) != '\\')) {
				inQuota++;
			} else
			if (inQuota%2 == 0 && inBracket == 0 && c == '"' && (i==0 || CMDstr.charAt(i-1) != '\\')) {
				inDoubleQuota++;
			} else
			if (inQuota%2 == 0 && inDoubleQuota%2 == 0 && (c == '(' || c == ')' )) {
				if (c == '('){
					inBracket++;
				} else {
					inBracket--;
				}
			} else
			if (inQuota%2 == 0 && inDoubleQuota%2 == 0 && inBracket == 0 && c == ' ' || i == CMDstr.length() - 1) {
				String newSubCMD = CMDstr.substring(previousIndex, i + 1).trim();
				if (!newSubCMD.equals("")) {
					CMDs.add(newSubCMD);
				}
				previousIndex = i;
			}
		}
		return CMDs;
	}
	
	public static String[] translateCMD(List<String> CMDs, String testcaseName) {
		String[] CMDargs = CMDs.toArray(new String[CMDs.size()]);
		for (int i = 0; i < CMDargs.length; i++) {
			if (CMDargs[i].equals("[name]")) {
				CMDargs[i] = "'" + testcaseName + "'";
			}
		}
		return CMDargs;
	}
	
	
	public static void executeDataCleanADBCMD() {
//		ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
		ProcessExecutor.processnolog("adb", "root");
		ProcessExecutor.processnolog("adb", "shell", "rm", "/data/data/" + Main.packagename + "/shared_prefs/*");
	}
	
	public static void executeKillUiautomatorCMD() {
		ProcessExecutor.process("adb", "shell", "/data/local/tmp/atx-agent", "server", "--stop");
//		List<String> results = ProcessExecutor.processnolog("adb", "shell", "ps");
//		if (results.isEmpty()) return;
//			result = result.trim();
//			List<String> args = getArgsWithoutBlank(result);
//			if (args.size() < 2) continue;
//			String id = args.get(1);
//				Integer.parseInt(id);
//				continue;
//			ProcessExecutor.processnolog("adb", "shell", "kill", id);
	}
	
	private static List<String> getArgsWithoutBlank(String str) {
		List<String> args = new ArrayList<String>();
		String[] temps = str.split(" ");
		for (String temp : temps) {
			if (!temp.equals("")) {
				args.add(temp);
			}
		}
		return args;
	}

//
//		executeKillUiautomatorCMD();
////		CMDUtils a = new CMDUtils();
////		ProcessExecutor.process(translatedCMDs);
}
