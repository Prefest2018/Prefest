package espresso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GUI.Main;
import appiumscript.scriptexecutor.ErrorCollectThread;
import appiumscript.scriptexecutor.LocThread;
import appiumscript.scriptexecutor.TestType;
import data.CoverData;
import data.TestCaseData;
import exception.EspressoTagnameNotFoundException;
import tools.CMDUtils;
import tools.GeneralUtils;
import tools.JsonHelper;
import tools.Logger;
import tools.ProcessExecutor;

public class EspressoScriptExecutor {
	private static LocThread locthread = null;
//	private static final char nobranchsuffix = '_';
//	private static final String ittagsuffix = "_it_";
	private static ErrorCollectThread errorthread = null;
	private static String packagename = null;
	private static String androidversion = "ANDROID";

//	public final static boolean shouldreduce = true;
//	public final static boolean shouldnobranchintotriallist = false;
	public static void init(String packagename) {
		EspressoScriptExecutor.packagename = packagename;
	}
	
	public static List<EspressoScriptName> getScriptNames(File scriptNameFile) {
		List<EspressoScriptName> scriptNames = new ArrayList<EspressoScriptName>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(scriptNameFile)));
			String content = null;
			while((content = br.readLine()) != null) {
				if (content.contains(" ")) {
					String[] temp = content.split(" ");
					EspressoScriptName scriptName = new EspressoScriptName(temp[0], temp[1]);
					scriptNames.add(scriptName);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return scriptNames;
	}
	
	public static void scriptexecute(File scriptNameFile, String packageName) {
		//
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		Logger.setTempLogFile(Main.firstcasesexeresultfile, true);
		Map<String, TestCaseData> testcases = null;
		List<String> newfaillist = new ArrayList<String>();
		File testcasedatafile = new File(Main.testcaseinfofile);
		if (testcasedatafile.exists()) {
			testcases = JsonHelper.gettestcasesdataAdapt(testcasedatafile.getAbsolutePath(), false);
		} else {
			testcases = new HashMap<String, TestCaseData>();
		}
	
		List<EspressoScriptName> scriptNames = getScriptNames(scriptNameFile);
		for (int j = 0; j < scriptNames.size(); j++) {
			EspressoScriptName scriptName = scriptNames.get(j);
			if (null != testcases.get(scriptName.shortName)) {
				continue;
			}
			
			TestCaseData data = new TestCaseData();
			data.firsttestcasepath = scriptName.longName;
			data.tagname = scriptName.shortName;
			Logger.log("firsttestcase " + scriptName.shortName + " start:");
			if (Main.resetForEachRun) {
				CMDUtils.executeDataCleanADBCMD();
			}
			ProcessExecutor.processnolog("adb", "logcat", "-c");
			
			locthread = new LocThread();
			errorthread = new ErrorCollectThread();
			locthread.setFile(Main.firstcasesloc + File.separator  + scriptName.shortName + ".txt");
			data.firstlocpath = Main.firstcasesloc + File.separator  + scriptName.shortName + ".txt";
			errorthread.setErrorFile(Main.firstcaseerror);
			errorthread.start();
			errorthread.addIndex(scriptName.shortName);
			locthread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			data.firstconsumedtime = 0;
			data = executeEspressoTestCase(data);
			data.firstexecutionsuccess &= locthread.locstop();
			boolean hasbug = errorthread.errorlogstop();
			if (hasbug && Main.resetWhenError) {
				ProcessExecutor.processnolog("adb", "shell", "am", "force-stop", packageName);
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				CMDUtils.executeDataCleanADBCMD();
			}
			String coveragefilename = "coverage" + scriptName + ".ec";
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + coveragefilename, Main.firstcasescoverage + File.separator  + coveragefilename);
			data.firstcoveragepath = Main.firstcasescoverage + File.separator  + coveragefilename;
			Logger.log("espresso firsttestcase " + scriptName + " end\n");
			
			testcases.put(data.tagname, data);
			if (j % 10 == 0) {
				JsonHelper.savetestcasesdataAdapt(testcases, Main.testcaseinfofile);
			}
		}
		JsonHelper.savetestcasesdataAdapt(testcases, Main.testcaseinfofile);
		Logger.log("execution end: " + newfaillist.size() + " failed" + "\n");
	}
	
	
	public static TestCaseData executeTestCaseForEspresso(String prescriptfilepath, Object tagname, TestCaseData data, TestType testtype, CoverData coverdata) {
		String locfilepath = null;
		String errorfilepath = null;
		
		String ecfileinsd = null;
		String preecfileinsd = null;
		String ecfileforstore = null;
		String preecfileforstore = null;
		switch (testtype) {
		case PREFEST_T :{
			locfilepath = Main.interestcaseloc + File.separator + data.tagname + ".txt";
			errorfilepath = Main.interesterror;
			
			
			String pretagname = GeneralUtils.getTagnameInPreTurn(tagname.toString());
			String originaltagname = GeneralUtils.removeSuffixInEspressoTagname(tagname);
			ecfileinsd = "coverage" + originaltagname + ".ec";
			ecfileforstore = Main.interestcasescoverage + File.separator + "coverage" + data.tagname + ".ec";
			preecfileinsd = "coverage" + pretagname + "_pre.ec";
			preecfileforstore = Main.interestcasescoverage + File.separator + "coverage" + data.tagname + "_pre.ec";

			break;
		}
		case PREFEST_N :{
//			locfilepath = Main.templocfile;
//			errorfilepath = Main.interestallerror;
//			String origintagname = tagname.toString().substring(0, tagname.toString().lastIndexOf('_'));
//			ecfileinsd = "coverage" + origintagname + ".ec";
//			ecfileforstore = Main.interestallcasescoverage + File.separator  + "coverage" + tagname + "_ia.ec";
//			preecfileinsd = "coverage" + origintagname + "_pre.ec";
//			preecfileforstore = Main.interestallcasescoverage + File.separator + "coverage" + tagname + "_pre_ia.ec";
			break;
		}
		case NONDEFAULT :{
//			locfilepath = Main.allpreferencecaseloc + File.separator + data.tagname + ".txt";
//			errorfilepath = Main.allpreferenceerror;
//			
//			ecfileinsd = "coverage" + tagname + ".ec";
//			ecfileforstore = Main.allpreferencecoverage + File.separator + "coverage" + tagname + "_all.ec";
//			preecfileinsd = "coverage" + Main.PRESCRIPT + ".ec";
//			preecfileforstore = Main.allpreferencecoverage + File.separator + "coverage" + tagname + "_pre_all.ec";
			break;
		}
		case PAIRWISE : {
//			locfilepath = Main.templocfile;
//			errorfilepath = Main.pwpreferenceerror;
//			
//			ecfileinsd = "coverage" + data.tagname + ".ec";
//			ecfileforstore = Main.pwpreferencecoverage + File.separator + "coverage" + tagname + "_pw.ec";
//			preecfileinsd = "coverage" + Main.PRESCRIPT + ".ec";
//			preecfileforstore = Main.pwpreferencecoverage + File.separator + "coverage" + tagname + "_pre_pw.ec";
			break;
		}
		}
		if (Main.resetForEachRun) {
			CMDUtils.executeDataCleanADBCMD();
//			ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
		}

		ProcessExecutor.processnolog("adb", "logcat", "-c");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		locthread = new LocThread();
		locthread.setFile(locfilepath);
		data.firstlocpath = locfilepath;
		data.firstconsumedtime = 0;
		data.firstjacocotime = 0;
		errorthread = new ErrorCollectThread();
		errorthread.setErrorFile(errorfilepath);
		errorthread.addIndex(data.tagname);
		errorthread.start();
		locthread.start();
		data.firstconsumedtime = 0;
		if (null != prescriptfilepath) {
			data = executeAppiumTestCase(prescriptfilepath, data);
		}
		CMDUtils.executeKillUiautomatorCMD();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
//			ProcessExecutor.processnolog("adb", "shell", "mkdir", "/data/data/" + Main.packagename + ".test" + "/shared_prefs");
//			ProcessExecutor.processnolog("adb", "shell", "cp", "/data/data/" + Main.packagename + "/shared_prefs/*",
//															"/data/data/" + Main.packagename + ".test" + "/shared_prefs");
//			
		data = executeEspressoTestCase(data);

		data.firstexecutionsuccess &= locthread.locstop();
		boolean hasbug = errorthread.errorlogstop();
		if (hasbug && Main.resetWhenError) {
			CMDUtils.executeDataCleanADBCMD();
//			ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + ecfileinsd, ecfileforstore);
		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + preecfileinsd, preecfileforstore);
		data.firstcoveragepath = preecfileforstore;
		coverdata.update(data.firstlocpath);
		return data;
	}
	
	private static TestCaseData executeAppiumTestCase(String scriptfilepath, TestCaseData data) {
		List<String> pythonlogs = ProcessExecutor.process("python", scriptfilepath);
		boolean shouldadd = false;
		String errorlog = "";
		for (String log : pythonlogs) {
			if (log.equals("FAIL")) {
				shouldadd = true;
				data.firstexecutionsuccess = false;
			} else if (log.startsWith("consumed time: ")) {
				shouldadd = false;
				log = log.replace("consumed time: ", "");
				log = log.replace(" s", "");
				data.firstconsumedtime += Float.parseFloat(log);
			} else if (log.startsWith("jacoco time: ")) {
				log = log.replace("jacoco time: ", "");
				log = log.replace(" s", "");
				data.firstjacocotime += Float.parseFloat(log);
			}
			if (shouldadd) {
				errorlog = errorlog + log;
			}
		}
		if (!data.firstexecutionsuccess) {
			data.firsterrorlog = errorlog;
		}
		return data;
	}
	
	private static TestCaseData executeEspressoTestCase(TestCaseData data) {
		List<String> pythonlogs = null;
		String testScriptname = data.firsttestcasepath;
		String[] CMDargs = CMDUtils.translateCMD(Main.espressoCMDs, testScriptname);
		pythonlogs = ProcessExecutor.process(CMDargs);
//			pythonlogs = ProcessExecutor.process("adb", "shell", "am", "instrument", "-w", "-r", "-e", "debug", "false", "-e", "class",  "'" + data.firsttestcasepath + "'",  packagename + ".test/android.support.test.runner.AndroidJUnitRunner");
//			pythonlogs = 
		boolean shouldadd = false; 
		String errorlog = "";
		for (String log : pythonlogs) {
			if (log.equals("FAILURES!!!")) {
				shouldadd = false;
				data.firstexecutionsuccess = false;
			} else if (log.startsWith("There was ") && log.contains("failure:")) {
				shouldadd = true;
			}
			if (shouldadd) {
				errorlog = errorlog + "\r\n" + log;
			}
			if (log.startsWith("Time:")) {
				if(log.contains(",")) {
					log = log.replace(",", ".");
				}
				float executionTime = Float.parseFloat(log.substring(6));
				data.firstconsumedtime += executionTime;
			}
		}
		if (!data.firstexecutionsuccess) {
			data.firsterrorlog = errorlog;
		}
		return data;
	}
	

}
