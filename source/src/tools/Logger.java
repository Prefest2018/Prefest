package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Logger {
	protected static String logPath = null;
	protected static File logFile = null;
	protected static boolean shouldcontinue = true;

	public static void setTempLogFile(String nowlogpath, boolean shouldcontinue) {
		logPath = nowlogpath;
		if (null == logPath) {
			return;
		}
		logFile = new File(logPath);
		Logger.shouldcontinue = shouldcontinue;
		if (logFile.exists() && !shouldcontinue) {
			logFile.delete();
		} else {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public static void log(String content, String nowlogpath, boolean shouldcontinue) {
		File logFile = new File(nowlogpath);
		if (logFile.exists() && !shouldcontinue) {
			logFile.delete();
		} else {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, shouldcontinue), "UTF-8"));
			bw.write(content);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void log(String content) {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, shouldcontinue), "UTF-8"));
			bw.write(content);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static StringBuilder sb = null;
	
	public static void logadd(String content) {
		if (sb == null) {
			sb = new StringBuilder();
		}
		sb.append(content + "\n");
		
	}
	
	public static void logoutput() {
		String content = sb.toString();
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, shouldcontinue), "UTF-8"));
			bw.write(content);
			bw.newLine();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
