package appiumscript.scriptexecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import GUI.Main;
import appiumscript.util.InterestTranslator;
import appiumscript.util.ScriptGenerationUtil;
import data.CoverData;
import data.ExeScene;
import data.ITScene;
import data.InterestValue;
import data.Scene;
import data.TestCaseData;
import espresso.EspressoScriptExecutor;
import espresso.EspressoScriptName;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.CMDUtils;
import tools.GeneralUtils;
import tools.JsonHelper;
import tools.Logger;
import tools.PWCounter;
import tools.ProcessExecutor;
import tools.TagnameComparator;
import tools.tagselector.EffectTagSelector;
import tools.tagselector.MostLogicSelector;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ScriptExecutor {
	private static LocThread locthread = null;
	private static final char nobranchsuffix = '_';
	private static final String ittagsuffix = "_it_";
	private static ErrorCollectThread errorthread = null;

	public final static boolean shouldreduce = true;
	public final static boolean shouldnobranchintotriallist = false;
	public static void scriptexecute(List<File> pythonscripts, String packagename) {
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");

		Logger.setTempLogFile(Main.firstcasesexeresultfile, true);
		Map<String, TestCaseData> testcases = null;
		List<File> newfaillist = new ArrayList<File>();
		File testcasedatafile = new File(Main.testcaseinfofile);
		if (testcasedatafile.exists()) {
			testcases = JsonHelper.gettestcasesdataAdapt(testcasedatafile.getAbsolutePath(), false);
		} else {
			testcases = new HashMap<String, TestCaseData>();
		}
		
		
		
		for (int j = 0; j < pythonscripts.size(); j++) {
			File pythonscript = pythonscripts.get(j);
			String tagname = pythonscript.getName().replace(".py", "").replace("testcase", "");
			if (null != testcases.get(tagname)) {
				continue;
			}
			
			TestCaseData data = new TestCaseData();
			data.firsttestcasepath = pythonscript.getAbsolutePath();
			data.tagname = tagname;
			Logger.log("firsttestcase " + tagname + " start:");
			if (Main.resetForEachRun) {
				CMDUtils.executeDataCleanADBCMD();
			}
			ProcessExecutor.processnolog("adb", "logcat", "-c");
			
			locthread = new LocThread();
			errorthread = new ErrorCollectThread();
			locthread.setFile(Main.firstcasesloc + File.separator  + tagname + ".txt");
			data.firstlocpath = Main.firstcasesloc + File.separator  + tagname + ".txt";
			errorthread.setErrorFile(Main.firstcaseerror);
			errorthread.start();
			errorthread.addIndex(tagname);
			locthread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<String> pythonlogs = ProcessExecutor.process("python", pythonscript.getAbsolutePath());
			boolean shouldadd = false;
			String errorlog = "";
			for (String log : pythonlogs) {
				if (log.equals("FAIL")) {
					shouldadd = true;
					data.firstexecutionsuccess = false;
					newfaillist.add(pythonscript);
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
			data.firstexecutionsuccess &= locthread.locstop();
			boolean hasbug = errorthread.errorlogstop();
			if (hasbug && Main.resetWhenError) {
				ProcessExecutor.processnolog("adb", "shell", "am", "force-stop", packagename);
				try {
					Thread.currentThread().sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				CMDUtils.executeDataCleanADBCMD();
			}
			String coveragefilename = "coverage" + tagname + ".ec";
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + coveragefilename, Main.firstcasescoverage + File.separator  + coveragefilename);
			data.firstcoveragepath = Main.firstcasescoverage + File.separator  + coveragefilename;
			Logger.log("firsttestcase " + tagname + " end\n");
			
			testcases.put(data.tagname, data);
			if (j % 10 == 0) {
				JsonHelper.savetestcasesdataAdapt(testcases, Main.testcaseinfofile);
			}
		}
		JsonHelper.savetestcasesdataAdapt(testcases, Main.testcaseinfofile);
		Logger.log("execution end: " + newfaillist.size() + " failed" + "\n");
	}

	
	
	private static boolean hasconscript = false;
	private static String conscript = null;
	private static String conscripthandler = null;
	private static Map<String, String> failureacitivitiesmap = null;
	private static void readscripts() {
		File conscriptfile = new File(Main.extra + File.separator + "conscript.py");
		if (conscriptfile.exists()) {
			hasconscript = true;
			StringBuilder sb = new StringBuilder();
			sb.append("def conscript(driver):\r\n");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(conscriptfile)));
				String content = null;
				while((content = br.readLine()) != null) {
					sb.append("\t" + content + "\r\n");
				}
				br.close();
				conscript = sb.toString();
				conscripthandler = "\tconscript(driver)\r\n";
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		failureacitivitiesmap = new HashMap<String, String>();
		File failureactivityfolder = new File(Main.extra + File.separator + "failureactivities");
		if (!failureactivityfolder.exists()) {
			return;
		}
		for (File failfile : failureactivityfolder.listFiles()) {
			try {
				if (!failfile.getName().endsWith(".txt")) {
					continue;
				}
				String activityname = failfile.getName().replace(".txt", "");
				StringBuilder sb = new StringBuilder();
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(failfile)));
				String content = null;
				while((content = br.readLine()) != null) {
					sb.append("\t" + content + "\r\n");
				}
				br.close();
				failureacitivitiesmap.put(activityname, sb.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	
	private static Map<String, ITScene> tobecoveredscenes = null;
	private static Map<String, ITScene> allscenes = null;
	private static ArrayList<String> taglist = null;
	private static List<Object> pasttags = null;
	private static Map<String, Scene> nobranchscenemap = null;
	private static int nobranchscenesuffixid = 0;
	private static Stack<ITScene> triallists = null;


	private static String preferencetextgenerateForPREFEST(String suffix, ArrayList<InterestValue> values, TestCaseData origintestcase, String packagename, TestType testtype, boolean shouldAppendPre) {
		readscripts();
		File testcasefile = new File(origintestcase.firsttestcasepath);
		 
		StringBuilder totalsb = new StringBuilder();
		try {
			totalsb.append(ScriptGenerationUtil.getPrefixFunctions_General(true, hasconscript, conscript));
			String settingScript = ScriptGenerationUtil.getPreferenceSetting_General(
					origintestcase.tagname + "_pre",
					InterestTranslator.generatetotalpreferences(Main.packagename, values, hasconscript, conscripthandler, failureacitivitiesmap),
					InterestTranslator.generatesystemservicestr(values),
					true);
			totalsb.append(settingScript);
			if (shouldAppendPre) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testcasefile), "UTF-8"));
				String content = null;
				boolean isoriginaltest = false;
				while ((content = br.readLine()) != null) {
					if (isoriginaltest) {
						if (content.contains("4723") &&  !content.contains("14723")){
							content = content.replace("4723", "14723");
						}
						totalsb.append(content + "\r\n");
						if (hasconscript && content.contains("driver = webdriver.Remote")) {
							totalsb.append("	time.sleep(1)\r\n");
							totalsb.append("	conscript(driver)\r\n");
						}
					} else {
						if (content.startsWith("# testcase")) {
							isoriginaltest = true;
							totalsb.append(content + "\r\n");
						}
					}
				}
				br.close();
			}
			totalsb.append(InterestTranslator.getsystemservicerestorestr());
		} catch(IOException e) {
			e.printStackTrace();
		}
		String newtestcasepath = null;
		
		
		switch(testtype) {
		case PREFEST_T : {
			newtestcasepath = Main.interestcases + File.separator + "testcase" + suffix + ".py";
			break;
		}
		case PREFEST_N : {
			newtestcasepath = Main.interestallcases + File.separator + "testcase" + suffix + ".py";
			break;
		}
		
			
		}
		File outputfile = new File(newtestcasepath);
		try {
			if (!outputfile.exists()) {
				outputfile.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), "UTF-8"));
			bw.write(totalsb.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newtestcasepath;
	}

	public static void scriptexecuteforPREFEST_T(ArrayList<String> taglist, final Set<ITScene> allscenes, Map<String, TestCaseData> currenttestcasedatas, String newdatafilename, int currentit, InterestPlanMode mode) {
//		initTestList();
		Logger.setTempLogFile(Main.interestplan, true);
		tobecoveredscenes = new HashMap<String, ITScene>();
		ScriptExecutor.taglist = taglist;
		pasttags = new ArrayList<Object>();
		triallists = new Stack<ITScene>();
		initNobranchScene(allscenes);
		initSortedTagList(currenttestcasedatas);
		printAllScenes(allscenes);
		boolean isfirstit = currentit==0?true:false;
		ScriptExecutor.allscenes = new HashMap<String, ITScene>();
		for (ITScene scene : allscenes) {
			ScriptExecutor.allscenes.put(scene.changebranchids, scene);
		}
		if (shouldreduce) {
			reduce();
		}
		if (isfirstit) {
			for (ITScene scene : ScriptExecutor.allscenes.values()) {
				tobecoveredscenes.put(scene.changebranchids, scene);
			}
		} else {
			for (ITScene scene : ScriptExecutor.allscenes.values()) {
				triallists.add(scene);
			}
		}

		ExeScene exescene = null;
		int i = 0;
		Map<Object, TestCaseData> testcases = new HashMap<Object, TestCaseData>();
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		CoverData coverdata = JsonHelper.getCoverData(Main.interestallcoveragedata);
		while (!(exescene = getNo1Scene(isfirstit)).isEmtpy()) {
			i++;
			Object tagname = getATag(exescene);
			Set<Object> pastcases = new HashSet<Object>();

			TestCaseData data = new TestCaseData();
			pastcases.add(tagname);
			Logger.log("index." + i + ",  interestcase " + tagname + " start:");
			Logger.log("tagetlogs are:" + exescene.changebranchids.toString());
			String suffixTagname = currentit + "_" + i + "_" + tagname;
			ArrayList<InterestValue> newinterests = exescene.taglist.get(tagname);
			ArrayList<InterestValue> originalinterests = currenttestcasedatas.get(tagname).settinginterests;
			if (null == originalinterests) {
				data.settinginterests = newinterests;
			} else {
				data.settinginterests = new ArrayList<InterestValue>(originalinterests);
				settingchecking: for (InterestValue value : newinterests) {
					for (int j = 0; j < data.settinginterests.size(); j++) {
						InterestValue settingvalue = data.settinginterests.get(j);
						if (settingvalue.name.equals(value.name)) {
							data.settinginterests.set(j, value);
							continue settingchecking;
						}
					}
					data.settinginterests.add(value);
				}
			}
			switch (mode) {
			case APPIUM : {
				data.firsttestcasepath = preferencetextgenerateForPREFEST(suffixTagname, data.settinginterests, currenttestcasedatas.get(tagname), Main.packagename, TestType.PREFEST_T, true);
				data.tagname = suffixTagname;
				executeTestCaseForAppium(null, data.firsttestcasepath, tagname, data, TestType.PREFEST_T, coverdata);
				break;
			}
			case ESPRESSO : {
				String preScriptPath = preferencetextgenerateForPREFEST(suffixTagname, data.settinginterests, currenttestcasedatas.get(tagname), Main.packagename, TestType.PREFEST_T, false);
				data.tagname = suffixTagname;
				data.firsttestcasepath = EspressoScriptName.getLongName(GeneralUtils.removeSuffixInEspressoTagname(tagname.toString()));
				EspressoScriptExecutor.executeTestCaseForEspresso(preScriptPath, data.tagname, data, TestType.PREFEST_T, coverdata);
				break;
			}
			}
			

			checkloc(exescene, data.firstlocpath, data.firstexecutionsuccess);

//			
//			String origincoveragefilename = "coverage" + originaltagname + ".ec";
//			String coveragefilename = "coverage" + suffixTagname + ".ec";
//			String originprecoveragefilename = "coverage" + tagname + "_pre.ec";
//			String precoveragefilename = "coverage" + suffixTagname + "_pre.ec";
//			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.interestcasescoverage + File.separator  + coveragefilename);
//			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + originprecoveragefilename, Main.interestcasescoverage + File.separator  + precoveragefilename);
//			data.firstcoveragepath = Main.interestcasescoverage + File.separator  + coveragefilename;
			Logger.log("interestcase " + suffixTagname + " end\n");
			testcases.put(suffixTagname, data);
			JsonHelper.savetestcasesdataAdapt(testcases, newdatafilename);
		}
		
	}
	
	private static ArrayList<String> sortedTagList = null;
	private static void initSortedTagList(Map<String, TestCaseData> datas) {
		EffectTagSelector selector = EffectTagSelector.getInstance();
		sortedTagList = selector.sort(datas);

	}
	
	
	private static void initNobranchScene(Set<ITScene> allscenes) {
		nobranchscenesuffixid = 0;
		nobranchscenemap = new HashMap<String, Scene>();
		for (ITScene scene : allscenes) {
			if (scene.branchids.equals(scene.changebranchids)) {
				scene.branchids = scene.branchids + nobranchsuffix + nobranchscenesuffixid;
				scene.changebranchids = scene.branchids;
				nobranchscenesuffixid++;
				nobranchscenemap.put(scene.branchids, scene);
			}
		}
	}
	
	private static void printAllScenes(Set<ITScene> allscenes) {
		int i = 0;
		for (ITScene scene : allscenes) {
			Logger.log("NO." + i + " Scene:");
			Logger.log("discovered in ineration NO. " + scene.discoverit);
			Logger.log("origin branch is: " + scene.branchids + " ,target branch is: " + scene.changebranchids);
			Logger.log("interests are:");
			for (InterestValue value :scene.interests) {
				Logger.log(value.name + "      " + value.value + "    " + value.index + "     " + value.catalog);
			}
//				Logger.log("preinterests are : ");
//					Logger.log(value.name + "      " + value.value + "    " + value.index + "      " + value.catalog);
			String tagstr = "";
			for (Object tag : scene.discovertag.keySet()) {
				tagstr += tag + " ";
			}
			Logger.log("discovered tags are : " + tagstr);
			Logger.log("--------------------------------");
			i++;
		}
	}
	
	
	private static ExeScene getNo1Scene(boolean firstit) {
		ExeScene bestScene = new ExeScene();
		for (String loclog : tobecoveredscenes.keySet()) {
			bestScene.addScene(tobecoveredscenes.get(loclog));
		}
		if (!bestScene.isEmtpy()) {
			return bestScene;
		}
		if (!triallists.isEmpty()) {
			bestScene = new ExeScene(triallists.peek(), firstit);
		}
		return bestScene;
	}
	
	private static void reduce() {
		Map<String, ITScene> toberemovedlist = new HashMap<String, ITScene>();
		for (String logloc : allscenes.keySet()) {
			for (ITScene scene : allscenes.values()) {
				if (scene.branchids.equals(logloc) && !scene.branchids.equals(scene.changebranchids)) {
					toberemovedlist.put(logloc, allscenes.get(logloc));
					break;
				}
			}
		}
		for (String logloc : toberemovedlist.keySet()) {
			toberemovedlist.get(logloc).covered = true;
			allscenes.remove(logloc);
		}
	}
	
	//TOBE Removed
//	public static ArrayList<String> testList = null;
//	public static int currentid = 0;
//		testList = new ArrayList<String>();
//		testList.add("6_006");
//		testList.add("5_005");
//		testList.add("7_021");
	
	private static Object getATag(ExeScene scene) {
		String newtag = null;
		if (!scene.istrial) {
			LinkedHashMap<Object, ArrayList<InterestValue>>  untestedtags = new LinkedHashMap<Object, ArrayList<InterestValue>>();
			for (Object temp : scene.taglist.keySet()) {
				if (!pasttags.contains(temp)) {
					untestedtags.put(temp, scene.taglist.get(temp));
//					pasttags.add(temp);
//					return temp;
				}
			}
			if (untestedtags.isEmpty()) {
				untestedtags = scene.taglist;
			}
//					newtag = test;
//					break;
			if (null == newtag) {
				int randomNum = (int)Math.round((Math.random() * (untestedtags.size() - 1)));
				newtag = (String) untestedtags.keySet().toArray()[randomNum];
			}
//			currentid++;
			pasttags.add(newtag);
		} else {
			for (String tag : sortedTagList) {
				if (scene.taglist.containsKey(tag)) {
					newtag = tag;
					break;
				}
			}
		}
		return newtag; 
	}
	
	private static void checkloc(ExeScene originscene, String locpath, boolean exesuccess) {
		Set<String> coveredlocs = new HashSet<String>();
		File locfile = new File(locpath);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(locfile)));
			String content = null;
			while((content = br.readLine()) != null) {
				if (!content.contains("V/loc") && !content.contains("V loc")) {
					continue;
				}
				int startNum = content.lastIndexOf(':');
				if (startNum < 0) {
					continue;
				}
				String logids = content.substring(startNum + 1);
				String logid = "";
				for (int i = 0; i < logids.length(); i++) {
					char nowChar = logids.charAt(i);
					if (0 == nowChar || ' ' == nowChar) {
						continue;
					}
					logid += nowChar;
				}
				if (logid.equals("-1")) {
					// not add it
				} else {
					coveredlocs.add(logid);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> toberemovedlogs = new ArrayList<String>();
		for (String logloc: originscene.changebranchids) {
			String adjustedlogloc = logloc;
			if (adjustedlogloc.contains(nobranchsuffix + "")) {
				adjustedlogloc = adjustedlogloc.substring(0, adjustedlogloc.indexOf(nobranchsuffix));
			}
			if (coveredlocs.contains(adjustedlogloc)) {
				toberemovedlogs.add(logloc);
			}
		}
		Logger.log("covered targets are: " + toberemovedlogs.toString());
		List<String> uncoveredlogs = new ArrayList<String>(originscene.changebranchids);
		uncoveredlogs.removeAll(toberemovedlogs);
		Logger.log("uncovered targets are: " + uncoveredlogs.toString());
		
		for (String toberemovedlog : toberemovedlogs) {
			ITScene toberemovedscene = allscenes.get(toberemovedlog);
			tobecoveredscenes.remove(toberemovedlog);
			if (triallists.contains(toberemovedscene)) {
				triallists.remove(toberemovedscene);
				Logger.log("target: " + toberemovedlog + " covered after retrial!");
			}
			toberemovedscene.covered = true;
		}
		for (String uncoveredlog : uncoveredlogs) {
			ITScene uncoveredscene = allscenes.get(uncoveredlog);
			if (toberemovedlogs.isEmpty()) {
				if (!triallists.contains(uncoveredscene)) {
					if (uncoveredscene.isTrailScene() && (!uncoveredscene.isnobranchScene() || shouldnobranchintotriallist)) {
						triallists.add(uncoveredscene);
						Logger.log("target: " + uncoveredlog + " uncovered, added to then retrial list!");
					} else {
						Logger.log("loclog: " + uncoveredlog + " deleted !");
					}
					tobecoveredscenes.remove(uncoveredlog);
					
				} else {
					triallists.remove(uncoveredscene);
					Logger.log("loclog: " + uncoveredlog + " uncovered after retrial, deleted!");
				}

			}
		}
	}

	
	public static void scriptexecuteforNonDefault(Map<String, List<PreferenceTreeNode>> preferencetree, Map<String, InterestValue> adaptedValues) {
		File preferencelogscriptfile = new File(Main.allpreferenceprecaselog_reverse);
//		File preferencescriptfile = new File(Main.allpreferenceprecase_reverse);
		if (!preferencelogscriptfile.exists()) {
			ArrayList<InterestValue> values = getbasevalues(preferencetree, true, adaptedValues);
			generatepreferencecaseforNonDefault(values, preferencelogscriptfile, true);
//			generatepreferencecaseforNonDefault(values, preferencescriptfile, false);
		}
		Map<String, TestCaseData> datas = null;
		File testcaseall = new File(Main.allpreferenceinfofile);
		if (testcaseall.exists()) {
			datas = JsonHelper.gettestcasesdataAdapt(Main.allpreferenceinfofile, false);
		} else {
			datas = new HashMap<String, TestCaseData>();
		}
		Map<String, TestCaseData> origindata = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, false);
		if (!datas.containsKey(Main.PRESCRIPT)) {
			TestCaseData predata = new TestCaseData();
			predata.tagname = Main.PRESCRIPT;
			predata.firsttestcasepath = Main.allpreferenceprecaselog_reverse;
			origindata.put(Main.PRESCRIPT, predata);
		}
		int i = 0;
		ArrayList<TestCaseData> origintestdates = new ArrayList<TestCaseData>();
		for (String tagname : origindata.keySet()) {
			if (!datas.containsKey(tagname)) {
				origintestdates.add(origindata.get(tagname));
			}
		}
		origintestdates.sort(new TagnameComparator<TestCaseData>());
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		CoverData coverdata = JsonHelper.getCoverData(Main.allpreferencecoveragedata);
		for (TestCaseData origincase : origintestdates) {
			String tagname = origincase.tagname;
			TestCaseData data = new TestCaseData();
			data.tagname = tagname;
			Logger.log("  interestcase " + tagname + " start:");
			
			executeTestCaseForAppium(Main.allpreferenceprecaselog_reverse, origincase.firsttestcasepath, tagname, data, TestType.NONDEFAULT, coverdata);

			
			if (Main.resetForEachRun) {
				ProcessExecutor.processnolog("adb", "root");
				ProcessExecutor.processnolog("adb", "shell", "rm", "/data/data/" + Main.packagename + "/shared_prefs/*");
//				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
			}
//			ProcessExecutor.process("python", Main.allpreferenceprecase_reverse);
//			ProcessExecutor.processnolog("adb", "logcat", "-c");
//			locthread = new LocThread();
//			locthread.setFile(Main.allpreferencecaseloc + File.separator  + tagname + ".txt");
//			data.firstlocpath = Main.allpreferencecaseloc + File.separator  + tagname + ".txt";
//			locthread.start();
//				Thread.sleep(1000);
//				e.printStackTrace();
//			
//			List<String> pythonlogs = ProcessExecutor.process("python", origincase.firsttestcasepath);
//			boolean shouldadd = false;
//			String errorlog = "";
//					shouldadd = true;
//					data.firstexecutionsuccess = false;
//					shouldadd = false;
//					log = log.replace("consumed time: ", "");
//					log = log.replace(" s", "");
//					data.firstconsumedtime += Float.parseFloat(log);
//					log = log.replace("jacoco time: ", "");
//					log = log.replace(" s", "");
//					data.firstjacocotime += Float.parseFloat(log);
//					errorlog = errorlog + log;
//				data.firsterrorlog = errorlog;
//			locthread.locstop();
//				Thread.sleep(2000);
//				e.printStackTrace();
//			String origincoveragefilename = "coverage" + tagname + ".ec";
//			String coveragefilename = "coverage" + tagname + "_all.ec";
//			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.allpreferencecoverage + File.separator  + coveragefilename);
//			data.firstcoveragepath = Main.allpreferencecoverage + File.separator  + coveragefilename;
			Logger.log("interestcase " + tagname + " end\n");
			datas.put(tagname, data);
			i++;
			if (i % 3 == 0) {
				JsonHelper.savetestcasesdataAdapt(datas, Main.allpreferenceinfofile);
				coverdata.save(Main.allpreferencecoveragedata);

			}
		}
		JsonHelper.savetestcasesdataAdapt(datas, Main.allpreferenceinfofile);
		coverdata.save(Main.allpreferencecoveragedata);
	}
	
	public static void scriptexecuteforNonDefault_testingSettings(Map<String, List<PreferenceTreeNode>> preferencetree, Map<String, InterestValue> adaptedValues) {
		File preferencereverselogscriptfile = new File(Main.allpreferenceprecaselog_reverse);
		File preferencedefaultlogscriptfile = new File(Main.allpreferenceprecaselog_default);
		if (!preferencereverselogscriptfile.exists() && !preferencedefaultlogscriptfile.exists()) {
			ArrayList<InterestValue> values = getbasevalues(preferencetree, true, adaptedValues);
			generatepreferencecaseforNonDefault(values, preferencereverselogscriptfile, true);
			values = getbasevalues(preferencetree, false, adaptedValues);
			generatepreferencecaseforNonDefault(values, preferencedefaultlogscriptfile, true);
		}
		Map<String, TestCaseData> datas = null;
		File testcaseall = new File(Main.interestcaseinfofile);
		if (testcaseall.exists()) {
			datas = JsonHelper.gettestcasesdataAdapt(Main.interestcaseinfofile, false);
		} else {
			datas = new HashMap<String, TestCaseData>();
		}

		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		String[] tag2list = {"reverse", "default"};
		for (String tag2: tag2list) {
			TestCaseData data = new TestCaseData();
			data.tagname = Main.PRESCRIPT + "_" + tag2;
			data.firsttestcasepath = tag2.equals("default")?Main.allpreferenceprecaselog_default:Main.allpreferenceprecaselog_reverse;
			data.firstlocpath = Main.allpreferencecaseloc + File.separator  + data.tagname + ".txt";
			
			Logger.log("  interestcase " + data.tagname + " start:");
			if (Main.resetForEachRun) {
				CMDUtils.executeDataCleanADBCMD();
			}
			
			ProcessExecutor.processnolog("adb", "logcat", "-c");
			
			locthread = new LocThread();
			locthread.setFile(data.firstlocpath);
			locthread.start();
			errorthread = new ErrorCollectThread();
			errorthread.setErrorFile(Main.interesterror);
			errorthread.start();
			errorthread.addIndex(data.tagname);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			List<String> pythonlogs = ProcessExecutor.process("python", data.firsttestcasepath);
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
					data.firstconsumedtime = Float.parseFloat(log);
				} else if (log.startsWith("jacoco time: ")) {
					log = log.replace("jacoco time: ", "");
					log = log.replace(" s", "");
					data.firstjacocotime = Float.parseFloat(log);
				}
				if (shouldadd) {
					errorlog = errorlog + log;
				}
			}
			if (!data.firstexecutionsuccess) {
				data.firsterrorlog = errorlog;
			}
			data.firstexecutionsuccess &= locthread.locstop();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String origincoveragefilename = "coverage" + Main.PRESCRIPT + ".ec";
			String coveragefilename = "coverage" + data.tagname + ".ec";
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.allpreferencecoverage + File.separator  + coveragefilename);
			data.firstcoveragepath = Main.interestallcasescoverage + File.separator  + coveragefilename;
			Logger.log("interestcase " + data.tagname + " end\n");
			datas.put(data.tagname, data);
			JsonHelper.savetestcasesdataAdapt(datas, Main.interestcaseinfofile);
		}

	}
	
	private static ArrayList<InterestValue> getbasevalues(Map<String, List<PreferenceTreeNode>> preferencetree, boolean isreverse, Map<String, InterestValue> adaptedValues) {
		Set<PreferenceTreeNode> allnodes = new LinkedHashSet<PreferenceTreeNode>();
		Stack<PreferenceTreeNode> nownodes = new Stack<PreferenceTreeNode>();
		for (String key : preferencetree.keySet()) {
			List<PreferenceTreeNode> nodes = preferencetree.get(key);
			for (PreferenceTreeNode node : nodes) {
				nownodes.push(node);
			}
		}
		while (!nownodes.isEmpty()) {
			PreferenceTreeNode nowNode = nownodes.pop();
			String type = nowNode.preferencetype;
			switch (type) {
			case "checkbox": {
				allnodes.add(nowNode);
				break;
			}
			case "switch" : {
				allnodes.add(nowNode);
				break;
			}
			case "list" : {
				Map<String, String> entrymap = nowNode.getEntryvalues();
				if (null != entrymap && !entrymap.isEmpty()) {
					allnodes.add(nowNode);
				}
				break;
			}
			case "multilist" : {
				Map<String, String> entrymap = nowNode.getEntryvalues();
				if (null != entrymap && !entrymap.isEmpty()) {
					allnodes.add(nowNode);
				}
				break;
			}
			case "seekbar" : {
				Map<String, String> entrymap = nowNode.getEntryvalues();
				if (null != entrymap && !entrymap.isEmpty()) {
					allnodes.add(nowNode);
				}
				break;
			}
			case "edit" : {
				allnodes.add(nowNode);
				break;
			}

			}
			List<PreferenceTreeNode> childnodes = nowNode.getChildnodes();
			if (null != childnodes) {
				for (int i = childnodes.size()-1; i >=0; i--) {
					PreferenceTreeNode childnode = childnodes.get(i);
					nownodes.push(childnode);
				}
			}
		}
		System.out.println("preference num: " + allnodes.size());
		
		ArrayList<InterestValue> values = new ArrayList<InterestValue>();
//		int i = 0;
		for (PreferenceTreeNode node : allnodes) {
			InterestValue nowvalue = null;
			if (isreverse) {
				nowvalue = node.toInterestValueReverseDefault();
			} else {
				nowvalue = node.toInterestValueDefault();
			}
			if (null != nowvalue) {
				values.add(nowvalue);
			}
//			i++;
//			System.out.println("id " + i + ", " + "type: " + node.preferencetype + ",  title: " + node.title + ",  catelog: " + node.catlog + ", defaultvalue: " + node.defaultvalue);
		}
		
		for (int i = 0; i < values.size(); i++) {
			InterestValue value = values.get(i);
			if (adaptedValues.containsKey(value.name)) {
				InterestValue adaptedValue = adaptedValues.get(value.name);
				value.activityname = adaptedValue.activityname;
				value.activityextra = adaptedValue.activityextra;
				value.preferencesteps = adaptedValue.preferencesteps;
			}
		}
		return values;
	}
	
	public static TestCaseData scriptexecuteforPREFEST_N(String nowtagname, String scriptfile, CoverData coverdata) {
		TestCaseData data = new TestCaseData();
		data.tagname = nowtagname;
		data.firsttestcasepath = scriptfile;
//			ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
//		ProcessExecutor.processnolog("adb", "logcat", "-c");
//		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
//		locthread = new LocThread();
		executeTestCaseForAppium(null, data.firsttestcasepath, nowtagname, data, TestType.PREFEST_N, coverdata);

//		locthread.setFile(Main.interestallcaseloc + File.separator  + nowtagname + ".txt");
//		data.firstlocpath = Main.interestallcaseloc + File.separator  + nowtagname + ".txt";
//		locthread.start();
//		errorthread = new ErrorCollectThread();
//		errorthread.setErrorFile(Main.interestallerror);
//		errorthread.addIndex(nowtagname);
//		errorthread.start();
//			Thread.sleep(1000);
//			e.printStackTrace();
//		
//		List<String> pythonlogs = ProcessExecutor.process("python", scriptfile);
//		boolean shouldadd = false;
//		String errorlog = "";
//		data.firstconsumedtime = 0;
//		data.firstjacocotime = 0;
//				shouldadd = true;
//				data.firstexecutionsuccess = false;
//				shouldadd = false;
//				log = log.replace("consumed time: ", "");
//				log = log.replace(" s", "");
//				data.firstconsumedtime += Float.parseFloat(log);
//				log = log.replace("jacoco time: ", "");
//				log = log.replace(" s", "");
//				data.firstjacocotime += Float.parseFloat(log);
//				errorlog = errorlog + log;
//			data.firsterrorlog = errorlog;
//		locthread.locstop();
//		errorthread.errorlogstop();
//			Thread.sleep(2000);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		String origintagname = nowtagname.substring(0, nowtagname.lastIndexOf('_'));
//		String origincoveragefilename = "coverage" + origintagname + ".ec";
//		String coveragefilename = "coverage" + nowtagname + "_ia.ec";
//		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.interestallcasescoverage + File.separator  + coveragefilename);
//		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + "coverage" + origintagname + "_pre.ec", Main.interestallcasescoverage + File.separator  + "coverage" + nowtagname + "_pre_ia.ec");
//		data.firstcoveragepath = Main.interestallcasescoverage + File.separator  + coveragefilename;
		Logger.log("interestcase " + nowtagname + " end\n");
		return data;
	}

	public static Map<String, String> generateinterestcaseforPREFEST_N(TestCaseData data, PWCounter pwcounter, Map<String, List<PreferenceTreeNode>> preferencetree) {
		HashMap<String, String> generatedcases = new HashMap<String, String>();
		Map<String, InterestValue> allinterests = new HashMap<String, InterestValue>();
		if (null != data.interestScenes) {
			for (Scene scene : data.interestScenes) {
				for (InterestValue value :scene.interests) {
					if (!allinterests.containsKey(value.name) && value.notinject()) {
						allinterests.put(value.name, value);
					}
				}
			}
		}
		if (allinterests.isEmpty()) {
			return null;
		}
		ArrayList<InterestValue> defautlist = new ArrayList<InterestValue>();
		ArrayList<InterestValue> notdefautlist = new ArrayList<InterestValue>();
		for (InterestValue value : allinterests.values()) {
			if (!"systemservice".equals(value.generaltype)) {
				String key = value.name;
				PreferenceTreeNode truenode = getPrefereceNode(key, preferencetree);
				if (null != truenode) {
					InterestValue newvalue = truenode.toInterestValueDefault();
					if (null != newvalue) {
						defautlist.add(newvalue);

					}
					newvalue = truenode.toInterestValueReverseDefault();
					if (null != newvalue)
					notdefautlist.add(newvalue);
				}
			} else {
				defautlist.add(new InterestValue(value, "1"));
				notdefautlist.add(new InterestValue(value, "0"));
			}
		}
		
		int allinterestnum = defautlist.size();
		int pwtimes = pwcounter.initfromPICT(allinterestnum);

		for (int i = 0; i < pwtimes; i++) {
			Boolean[] pwlist = pwcounter.getValues(allinterestnum, i);
			ExeScene exescene = new ExeScene(data.tagname);
			for (int k = 0; k < defautlist.size(); k++) {
				if (pwlist[k]) {
					exescene.addInterest(defautlist.get(k));
				} else {
					exescene.addInterest(notdefautlist.get(k));
				}
			}
			String suffix = data.tagname + "_" + i;
			String casename = preferencetextgenerateForPREFEST(suffix, exescene.getFirstInterestValueArray(), data, Main.packagename, TestType.PREFEST_N, true);
			generatedcases.put(suffix, casename);
		}
		
		return generatedcases;
	}
	
	private static PreferenceTreeNode getPrefereceNode(String key, Map<String, List<PreferenceTreeNode>> preferencetree) {
		for (List<PreferenceTreeNode> nodelist : preferencetree.values()) {
			for (PreferenceTreeNode node : nodelist) {
				if (null != node) {
					if (key.equals(node.key)) {
						return node;
					}	
				}
				
			}
		}
		return null;
	}
	
	public static TestCaseData scriptexecuteforpw(PWValue value, String nowtagname, String scriptfile, CoverData coverdata) {
		TestCaseData data = new TestCaseData();
		data.tagname = nowtagname;
		data.firsttestcasepath = scriptfile;
		executeTestCaseForAppium(value.preferencescriptlogfile, scriptfile, value.index + "_" + nowtagname, data, TestType.PAIRWISE, coverdata);

		
		
//			ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
//		ProcessExecutor.process("python", value.preferencescriptfile);
//		ProcessExecutor.processnolog("adb", "logcat", "-c");
//		locthread = new LocThread();
//		locthread.setFile(Main.allpreferencecaseloc + File.separator  + tagname + ".txt");
//		data.firstlocpath = Main.allpreferencecaseloc + File.separator  + tagname + ".txt";
//		locthread.start();
//			Thread.sleep(1000);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		List<String> pythonlogs = ProcessExecutor.process("python", scriptfile);
//		boolean shouldadd = false;
//		String errorlog = "";
//				shouldadd = true;
//				data.firstexecutionsuccess = false;
//				shouldadd = false;
//				log = log.replace("consumed time: ", "");
//				log = log.replace(" s", "");
//				data.firstconsumedtime = Float.parseFloat(log);
//				log = log.replace("jacoco time: ", "");
//				log = log.replace(" s", "");
//				data.firstjacocotime = Float.parseFloat(log);
//				errorlog = errorlog + log;
//			data.firsterrorlog = errorlog;
////		locthread.locstop();
//			Thread.sleep(2000);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		String origincoveragefilename = "coverage" + nowtagname + ".ec";
//		String coveragefilename = "coverage" + value.index + "_" + nowtagname + "_pw.ec";
//		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.pwpreferencecoverage + File.separator  + coveragefilename);
//		data.firstcoveragepath = Main.pwpreferencecoverage + File.separator  + coveragefilename;
		Logger.log("interestcase " + nowtagname + " end\n");
		return data;
	}
	
	public static void generatepreferencecaseforNonDefault(ArrayList<InterestValue> values, File preferencescriptfile, boolean shouldjacoco) {
		readscripts();
		try {
			preferencescriptfile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(preferencescriptfile), "UTF-8"));
			StringBuilder totalsb = new StringBuilder();
			String preScript = ScriptGenerationUtil.getPrefixFunctions_General(shouldjacoco, hasconscript, conscript);
			totalsb.append(preScript);
			
			String settingScript = ScriptGenerationUtil.getPreferenceSetting_General(
					Main.PRESCRIPT,
					InterestTranslator.generatetotalpreferences(Main.packagename, values, hasconscript, conscripthandler, failureacitivitiesmap),
					InterestTranslator.generatesystemservicestr(values),
					shouldjacoco);
			totalsb.append(settingScript);
		
			bw.write(totalsb.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	
	private static TestCaseData executeTestCaseForAppium(String prescriptfilepath, String scriptfilepath, Object tagname, TestCaseData data, TestType testtype, CoverData coverdata) {
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
			
			
			String[] templist = tagname.toString().split("_");
			String originaltagname = templist[templist.length -2] + "_" + templist[templist.length - 1];
			ecfileinsd = "coverage" + originaltagname + ".ec";
			ecfileforstore = Main.interestcasescoverage + File.separator + "coverage" + data.tagname + ".ec";
			preecfileinsd = "coverage" + tagname + "_pre.ec";
			preecfileforstore = Main.interestcasescoverage + File.separator + "coverage" + data.tagname + "_pre.ec";

			break;
		}
		case PREFEST_N :{
			locfilepath = Main.templocfile;
			errorfilepath = Main.interestallerror;
			String origintagname = tagname.toString().substring(0, tagname.toString().lastIndexOf('_'));
			ecfileinsd = "coverage" + origintagname + ".ec";
			ecfileforstore = Main.interestallcasescoverage + File.separator  + "coverage" + tagname + "_ia.ec";
			preecfileinsd = "coverage" + origintagname + "_pre.ec";
			preecfileforstore = Main.interestallcasescoverage + File.separator + "coverage" + tagname + "_pre_ia.ec";
			break;
		}
		case NONDEFAULT :{
			locfilepath = Main.allpreferencecaseloc + File.separator + data.tagname + ".txt";
			errorfilepath = Main.allpreferenceerror;
			
			ecfileinsd = "coverage" + tagname + ".ec";
			ecfileforstore = Main.allpreferencecoverage + File.separator + "coverage" + tagname + "_all.ec";
			preecfileinsd = "coverage" + Main.PRESCRIPT + ".ec";
			preecfileforstore = Main.allpreferencecoverage + File.separator + "coverage" + tagname + "_pre_all.ec";
			break;
		}
		case PAIRWISE : {
			locfilepath = Main.templocfile;
			errorfilepath = Main.pwpreferenceerror;
			
			ecfileinsd = "coverage" + data.tagname + ".ec";
			ecfileforstore = Main.pwpreferencecoverage + File.separator + "coverage" + tagname + "_pw.ec";
			preecfileinsd = "coverage" + Main.PRESCRIPT + ".ec";
			preecfileforstore = Main.pwpreferencecoverage + File.separator + "coverage" + tagname + "_pre_pw.ec";
			break;
		}
		}
		if (Main.resetForEachRun) {
			CMDUtils.executeDataCleanADBCMD();
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
		if (null != prescriptfilepath) {
			data = executeTestCase(prescriptfilepath, data);
		}
		data = executeTestCase(scriptfilepath, data);

		data.firstexecutionsuccess &= locthread.locstop();
		boolean hasbug = errorthread.errorlogstop();
		if (hasbug && Main.resetWhenError) {
			CMDUtils.executeDataCleanADBCMD();
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
	
	private static TestCaseData executeTestCase(String scriptfilepath, TestCaseData data) {
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
}
