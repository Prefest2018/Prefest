package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import GUI.Main;

public class ProcessExecutor {
	public static List<String> processlogincmdlog(String... args) {
		Process p = null;
		ProcessBuilder builder = getPBInstance(args);
		String originlogfile = Logger.logPath;
		boolean origincontinue = Logger.shouldcontinue;
		Logger.setTempLogFile(Main.cmdlog, true);
		Logger.log("cmd:" + builder.command().toString() +"\n");
		List<String> resultstrs = new ArrayList<String>();
		try {
			p = builder.start();
	    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	String result = null;
	    	while(((result = p_stdout.readLine()) != null)) {
	    		System.out.println(result);
	    		resultstrs.add(result);
	    		Logger.log(result+"\n");
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Logger.setTempLogFile(originlogfile, origincontinue);
		return resultstrs;
	}
	
	public static List<String> process(String... args) {
		Process p = null;
		ProcessBuilder builder = getPBInstance(args);
		System.out.println("cmd:" + builder.command().toString());
		Logger.log("cmd:" + builder.command().toString() +"\n");
		List<String> resultstrs = new ArrayList<String>();
		try {
			p = builder.start();
	    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	String result = null;
	    	while(((result = p_stdout.readLine()) != null)) {
	    		System.out.println(result);
	    		resultstrs.add(result);
	    		Logger.log(result+"\n");
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultstrs;
	}
	
		public static List<String> processnolog(String... args) {
			Process p = null;
			ProcessBuilder builder = getPBInstance(args);
			System.out.println("cmd:" + builder.command().toString());
			List<String> resultstrs = new ArrayList<String>();
			try {
				p = builder.start();
		    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    	String result = null;
		    	while(((result = p_stdout.readLine()) != null)) {
		    		System.out.println(result);
		    		resultstrs.add(result);
		    	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resultstrs;
		}
	
		
		public static List<String> processnolognoprint(String... args) {
			Process p = null;
			ProcessBuilder builder = getPBInstance(args);
//			System.out.println("cmd:" + builder.command().toString());
			List<String> resultstrs = new ArrayList<String>();
			try {
				p = builder.start();
		    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    	String result = null;
		    	while(((result = p_stdout.readLine()) != null)) {
//		    		System.out.println(result);
		    		resultstrs.add(result);
		    	}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return resultstrs;
		}
		
		public static ProcessBuilder getPBInstance(String... args) {
			ProcessBuilder builder = new ProcessBuilder(args);
//			Map<String, String> envs = builder.environment();
//			envs.putAll(System.getenv());
			return builder;
		}
		
		public static void main(String[] arg) {
			ProcessExecutor a = new ProcessExecutor();
			Logger.setTempLogFile("C:\\Users\\yifeilu\\Documents\\test.log", false);
			ProcessExecutor.process("adb", "shell", "am", "instrument", "-w", "-r", "-e", "debug", "false", "-e", "class", "'AutoBackupTest#autoBackupPreferenceActivation'", "it.feio.android.omninotes.test/androidx.test.runner.AndroidJUnitRunner");
//			Main.packagename = "org.totschnig.myexpenses";
//			ProcessExecutor.process("adb", "root");
//			ProcessExecutor.process("adb", "shell", "rm", "/data/data/" + Main.packagename + "/shared_prefs/*");
		}
}
