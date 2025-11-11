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

/**
 * 命令行工具类
 * 提供命令解析、转换和执行相关的工具方法
 */
public class CMDUtils {
	/**
	 * 从文件中读取命令字符串并解析为命令列表
	 * 支持处理包含引号、括号等特殊字符的命令
	 * 
	 * @param filepath 命令文件的路径
	 * @return 解析后的命令列表
	 */
	public static List<String> readCMD(String filepath) {
		// This is a comment
		ArrayList<String> CMDs = new ArrayList<String>();
		File f = new File(filepath);
		String CMDstr = "";
		// 读取文件内容
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
		// 移除换行符和首尾空格
		CMDstr = CMDstr.replace("\r", "").replace("\n", "").trim();
		// 用于跟踪是否在引号、双引号或括号内
		int inQuota = 0;
		int inDoubleQuota = 0;
		int inBracket = 0;
		int previousIndex = 0;
		// 解析命令字符串，按空格分割，但需要考虑引号和括号
		for (int i = 0; i < CMDstr.length(); i++) {
			char c = CMDstr.charAt(i);
			// 检测单引号（不在双引号和括号内，且不是转义字符）
			if (inDoubleQuota%2 == 0 && inBracket == 0 && c == '\'' && (i==0 || CMDstr.charAt(i-1) != '\\')) {
				inQuota++;
			} else
			// 检测双引号（不在单引号和括号内，且不是转义字符）
			if (inQuota%2 == 0 && inBracket == 0 && c == '"' && (i==0 || CMDstr.charAt(i-1) != '\\')) {
				inDoubleQuota++;
			} else
			// 检测括号（不在引号内）
			if (inQuota%2 == 0 && inDoubleQuota%2 == 0 && (c == '(' || c == ')' )) {
				if (c == '('){
					inBracket++;
				} else {
					inBracket--;
				}
			} else
			// 如果不在引号或括号内，遇到空格或到达字符串末尾，则分割命令
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
	
	/**
	 * 将命令列表转换为字符串数组，并将占位符[name]替换为实际的测试用例名称
	 * 
	 * @param CMDs 命令列表
	 * @param testcaseName 测试用例名称
	 * @return 转换后的命令参数数组
	 */
	public static String[] translateCMD(List<String> CMDs, String testcaseName) {
		String[] CMDargs = CMDs.toArray(new String[CMDs.size()]);
		// 替换占位符[name]为实际的测试用例名称
		for (int i = 0; i < CMDargs.length; i++) {
			if (CMDargs[i].equals("[name]")) {
				CMDargs[i] = "'" + testcaseName + "'";
			}
		}
		return CMDargs;
	}
	
	
	/**
	 * 执行ADB命令清理应用数据
	 * 获取root权限并删除应用的shared_prefs目录下的所有文件
	 */
	public static void executeDataCleanADBCMD() {
//		ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
		// 获取root权限
		ProcessExecutor.processnolog("adb", "root");
		// 删除应用的shared_prefs目录下的所有文件
		ProcessExecutor.processnolog("adb", "shell", "rm", "/data/data/" + Main.packagename + "/shared_prefs/*");
	}
	
	/**
	 * 执行命令停止UIAutomator服务
	 * 通过停止atx-agent服务来实现
	 */
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
	
	/**
	 * 将字符串按空格分割，并移除空字符串
	 * 
	 * @param str 要分割的字符串
	 * @return 分割后的字符串列表
	 */
	private static List<String> getArgsWithoutBlank(String str) {
		List<String> args = new ArrayList<String>();
		String[] temps = str.split(" ");
		// 过滤掉空字符串
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
