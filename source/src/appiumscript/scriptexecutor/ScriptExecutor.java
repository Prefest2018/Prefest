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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import GUI.Main;
import appiumscript.util.InterestTranslator;
import appiumscript.util.ScriptGenerationUtil;
import data.ExeScene;
import data.InterestValue;
import data.Scene;
import data.TestCaseData;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.JsonHelper;
import tools.Logger;
import tools.PWCounter;
import tools.ProcessExecutor;
import tools.TagnameComparator;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ScriptExecutor {
	private static LocThread locthread = null;
	private static final char nobranchsuffix = '_';
	private static ErrorCollectThread errorthread = null;

	public final static boolean shouldreduce = true;
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
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", packagename);
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
			locthread.locstop();
			boolean hasbug = errorthread.errorlogstop();
			if (hasbug && Main.resetWhenError) {
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", packagename);
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

	

	private static Map<String, Scene> tobecoveredlogs = null;
	private static List<Scene> scenelist = null;
	private static List<String> tagnamelist = null;
	private static Map<String, Scene> triallists = null;
	private static List<String> pasttags = null;

	
	public static void scriptexecuteforPREFEST_T(Map<String, TestCaseData> testcasesdata, String packagename) {

		Logger.setTempLogFile(Main.interestplan, true);
		scenelist = interestpreparation(testcasesdata);
		tagnamelist = new LinkedList<String>(testcasesdata.keySet());
		tagnamelist.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				TestCaseData case1 = testcasesdata.get(o1);
				TestCaseData case2 = testcasesdata.get(o2);
				if (case1.firstexecutionsuccess && !case2.firstexecutionsuccess) {
					return -1;
				} else if (case2.firstexecutionsuccess && !case1.firstexecutionsuccess) {
					return 1;
				} else {

					File locfile1 = new File(case1.firstlocpath);
					File locfile2 = new File(case2.firstlocpath);
					if (locfile1.length() > locfile2.length()) {
						return -1;
					} else if (locfile1.length() == locfile2.length()){
						return 0;
					} else {
						return 1;
					}
				}
			}
			
		});
		tobecoveredlogs = new HashMap<String, Scene>();
		for (Scene scene : scenelist) {
			if(null == tobecoveredlogs.get(scene.changebranchids)) {
				tobecoveredlogs.put(scene.changebranchids, scene);
			}
		}
		triallists = new HashMap<String, Scene>();
		pasttags = new ArrayList<String>();
		if (shouldreduce) {
			reduce();
		}


		ExeScene exescene = null;
		int i = 0;

		Map<Integer, TestCaseData> testcases = new HashMap<Integer, TestCaseData>();
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		while ((exescene = getNo1Scene(testcasesdata)) != null) {
			i++;
			String tagname = exescene.tagname;
			Set<String> pastcases = new HashSet<String>();

			TestCaseData data = new TestCaseData();
			pastcases.add(tagname);
			Logger.log("index." + i + ",  interestcase " + tagname + " start:");
			Logger.log("tagetlogs are:" + exescene.changebranchids.toString());

			if (Main.resetForEachRun) {
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", packagename);
			}
			ProcessExecutor.processnolog("adb", "logcat", "-c");

			data.firsttestcasepath = preferencetextgenerateForPREFEST(i, exescene, testcasesdata.get(tagname), packagename, false);

			locthread = new LocThread();
			locthread.setFile(Main.interestcaseloc + File.separator  + i + "_" + tagname + ".txt");
			data.firstlocpath = Main.interestcaseloc + File.separator  + i + "_" + tagname + ".txt";
			errorthread = new ErrorCollectThread();
			errorthread.setErrorFile(Main.interesterror);
			errorthread.addIndex(i + "_" + tagname);
			errorthread.start();
			locthread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			

			List<String> pythonlogs = ProcessExecutor.process("python", data.firsttestcasepath);
			boolean shouldadd = false;
			String errorlog = "";
			data.firstconsumedtime = 0;
			data.firstjacocotime = 0;
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
			locthread.locstop();
			boolean hasbug = errorthread.errorlogstop();
			if (hasbug && Main.resetWhenError) {
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", packagename);
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			checkloc(exescene, data.firstlocpath, data.firstexecutionsuccess);


			String origincoveragefilename = "coverage" + tagname + ".ec";
			String coveragefilename = "coverage" + i + "_" + tagname + ".ec";
			String originprecoveragefilename = "coverage" + tagname + "_pre.ec";
			String precoveragefilename = "coverage" + i + "_" + tagname + "_pre.ec";
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.interestcasescoverage + File.separator  + coveragefilename);
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + originprecoveragefilename, Main.interestcasescoverage + File.separator  + precoveragefilename);
			data.firstcoveragepath = Main.interestcasescoverage + File.separator  + coveragefilename;
			Logger.log("interestcase " + tagname + " end\n");
			testcases.put(i, data);
			JsonHelper.savetestcasesdataAdapt(testcases, Main.interestcaseinfofile);

		}
	}

	
	private static boolean hasconscript = false;
	private static String conscript = null;
	private static String conscripthandler = null;
	private static void readscripts() {
		File conscriptfile = new File(Main.extra + "\\conscript.py");
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
	}

	private static String preferencetextgenerateForPREFEST(int index, ExeScene exescene, TestCaseData origintestcase, String packagename, boolean isall) {
		readscripts();
		File testcasefile = new File(origintestcase.firsttestcasepath);
		StringBuilder totalsb = new StringBuilder();
		try {
			totalsb.append(ScriptGenerationUtil.getPrefixFunctions_General(true, hasconscript, conscript));
			String settingScript = ScriptGenerationUtil.getPreferenceSetting_General(
					origintestcase.tagname + "_pre",
					InterestTranslator.generatetotalpreferences(Main.packagename, exescene.interests, hasconscript, conscripthandler),
					InterestTranslator.generatesystemservicestr(exescene.interests),
					InterestTranslator.getsystemservicerestorestr(),
					true);
			totalsb.append(settingScript);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(testcasefile), "UTF-8"));
			String content = null;
			boolean isoriginaltest = false;
			while ((content = br.readLine()) != null) {
				if (isoriginaltest) {
					totalsb.append(content + "\r\n");
				} else {
					if (content.startsWith("# testcase")) {
						isoriginaltest = true;
						totalsb.append(content + "\r\n");
					}
				}
			}
			br.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		String newtestcasepath = null;
		if (isall) {
			newtestcasepath = Main.interestallcases + "\\testcase" + origintestcase.tagname  + "_" + index + ".py";
		} else {
			newtestcasepath = Main.interestcases + "\\testcase" + index + "_" + origintestcase.tagname + ".py";
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

	private static void reduce() {
		Map<String, Scene> toberemovedlist = new HashMap<String, Scene>();
		for (String logloc : tobecoveredlogs.keySet()) {
			for (Scene scene : scenelist) {
				if (scene.branchids.equals(logloc) && !scene.branchids.equals(scene.changebranchids)) {
					toberemovedlist.put(logloc, tobecoveredlogs.get(logloc));
					break;
				}
			}
		}
		for (String logloc : toberemovedlist.keySet()) {
			tobecoveredlogs.remove(logloc);
			scenelist.remove(toberemovedlist.get(logloc));
		}
	}
	
	private static ExeScene getNo1Scene(Map<String, TestCaseData> testcasesdata) {
		ExeScene bestScene = null;

		for (String nowtagname : tagnamelist) {
			if (pasttags.contains(nowtagname)) {
				continue;
			}
			ExeScene nowScene = new ExeScene(nowtagname);
			for (String loclog : tobecoveredlogs.keySet()) {
				Scene scene = tobecoveredlogs.get(loclog);
				if (scene.tagnames.contains(nowtagname)) {
					nowScene.addScene(scene);
				}
			}

			if (null != bestScene) {
				if (nowScene.getSize() > bestScene.getSize()) {
					bestScene = nowScene;
				}
			} else {
				if (nowScene.getSize() > 0) {
					bestScene = nowScene;
				}
			}
		}
		if (null == bestScene) {
			for (String nowtagname : pasttags) {
				ExeScene nowScene = new ExeScene(nowtagname);
				for (String loclog : tobecoveredlogs.keySet()) {
					Scene scene = tobecoveredlogs.get(loclog);
					if (scene.tagnames.contains(nowtagname)) {
						nowScene.addScene(scene);
					}
				}

				if (null != bestScene) {
					if (nowScene.getSize() > bestScene.getSize()) {
						bestScene = nowScene;
					}
				} else {
					if (nowScene.getSize() > 0) {
						bestScene = nowScene;
					}
				}
			}
		}
		if (null == bestScene) {
			for (String locname: triallists.keySet()) {
				String tagname = triallists.get(locname).tagnames.get(0);
				List<Scene> scenes = testcasesdata.get(tagname).interestScenes;
				Scene truescene = null;
				for (Scene scene : scenes) {
					if (scene.changebranchids.equals(locname)) {
						truescene = scene;
						break;
					}
				}
				if (null == truescene) {
					Scene tempscene = nobranchscenemap.get(locname);
					for (Scene scene : scenes) {
						if (scene.equalsIgnoreSuffix(tempscene, nobranchsuffix)) {
							truescene = scene;
							truescene.branchids = locname;
							truescene.changebranchids = locname;
							break;
						}
					}
				}
				bestScene = new ExeScene(tagname);
				if (null != truescene.preinterests && !truescene.preinterests.isEmpty()) {
					truescene.mixpreandinterests();
				}
				bestScene.addScene(truescene);
				break;
			}
		}
			
			
		return bestScene;
	}
	
	private static void checkloc(ExeScene originscene, String locpath, boolean exesuccess) {
		Set<String> coveredlocs = new HashSet<String>();
		File locfile = new File(locpath);
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(locfile)));
			String content = null;
			while((content = br.readLine()) != null) {
				if (!content.contains("V/loc")) {
					continue;
				}
				int startNum = content.indexOf(':', 15);
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
			scenelist.remove(tobecoveredlogs.get(toberemovedlog));
			tobecoveredlogs.remove(toberemovedlog);
			if (null != triallists.get(toberemovedlog)) {
				triallists.remove(toberemovedlog);
				Logger.log("target: " + toberemovedlog + " covered after retrial!");
			}
		}
		for (String uncoveredlog : uncoveredlogs) {
			if (toberemovedlogs.isEmpty()) {
				if (null == triallists.get(uncoveredlog)) {
					Scene uncoveredscene = tobecoveredlogs.get(uncoveredlog);
					if (uncoveredscene.isTrailScene()) {
						triallists.put(uncoveredlog, uncoveredscene);
						Logger.log("target: " + uncoveredlog + " uncovered, added to then retrial list!");
					} else {
						Logger.log("loclog: " + uncoveredlog + " deleted !");
					}
					scenelist.remove(uncoveredscene);
					tobecoveredlogs.remove(uncoveredlog);
					
				} else {
					triallists.remove(uncoveredlog);
					Logger.log("loclog: " + uncoveredlog + " uncovered after retrial, deleted!");
				}

			}
		}
		
		pasttags.add(originscene.tagname);
	}
	
	private static Map<String, Scene> nobranchscenemap = null;
	private static int nobranchscenesuffixid = 0;
	private static List<Scene> interestpreparation(Map<String, TestCaseData> testcasedata) {
		nobranchscenemap = new HashMap<String, Scene>();
		nobranchscenesuffixid = 0;
		List<Scene> scenes = new ArrayList<Scene>();
		for (String tagname : testcasedata.keySet()) {
			TestCaseData nowdata = testcasedata.get(tagname);
			if (nowdata.interestScenes != null && !nowdata.interestScenes.isEmpty()) {
				for (Scene innerscene : nowdata.interestScenes) {
					boolean hasadded = false;
					for (int i = 0; i < scenes.size(); i++) {
						Scene scene = scenes.get(i);
						if (scene.equalsIgnoreSuffix(innerscene, nobranchsuffix)) {
							hasadded = true;
							scene.tagnames.add(tagname);
							if (null != innerscene.preinterests && !innerscene.preinterests.isEmpty()) {
								if (null == scene.preinterests || scene.preinterests.isEmpty() || innerscene.preinterests.size() > scene.preinterests.size()) {
									scene.preinterests = innerscene.preinterests;
									scenes.set(i, scene);
								}
							}
							break;
						}
					}
					if (!hasadded) {
						if (innerscene.branchids.equals(innerscene.changebranchids)) {
							innerscene.branchids = innerscene.branchids + nobranchsuffix + nobranchscenesuffixid;
							innerscene.changebranchids = innerscene.branchids;
							nobranchscenesuffixid++;
							nobranchscenemap.put(innerscene.branchids, innerscene);
						}
						scenes.add(innerscene);
						innerscene.tagnames.add(tagname);
					}
					
				}
			}
		}
		for (Scene scene : scenes) {
			scene.tagnames.sort(new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					TestCaseData case1 = testcasedata.get(o1);
					TestCaseData case2 = testcasedata.get(o2);
					if (case1.firstexecutionsuccess && !case2.firstexecutionsuccess) {
						return -1;
					} else if (case2.firstexecutionsuccess && !case1.firstexecutionsuccess) {
						return 1;
					} else {
						File locfile1 = new File(case1.firstlocpath);
						File locfile2 = new File(case2.firstlocpath);
						if (locfile1.length() > locfile2.length()) {
							return -1;
						} else if (locfile1.length() == locfile2.length()){
							return 0;
						} else {
							return 1;
						}
					}
				}
				
			});
		}
		
		for (Scene scene : scenes) {
			scene.trialtimes = 0;

		}
		
		for (int i = 0; i < scenes.size(); i++) {
			Scene s = scenes.get(i);
			Logger.log("NO." + i + " Scene:");
			Logger.log("origin branch is: " + s.branchids + " ,target branch is: " + s.changebranchids);
			Logger.log("interests are:");
			for (InterestValue value :s.interests) {
				Logger.log(value.name + "      " + value.value + "    " + value.index + "     " + value.catalog);
			}
			if (null != s.preinterests) {
				Logger.log("preinterests are : ");
				for (InterestValue value :s.preinterests) {
					Logger.log(value.name + "      " + value.value + "    " + value.index + "      " + value.catalog);
				}
			}
			Logger.log(s.tagnames.toString());
			Logger.log("--------------------------------");
		}
		return scenes;
	}
	
	public static void scriptexecuteforec(Map<String, List<PreferenceTreeNode>> preferencetree) {
		File preferencelogscriptfile = new File(Main.allpreferenceprecaselog);
		File preferencescriptfile = new File(Main.allpreferenceprecase);
		if (!preferencescriptfile.exists() && !preferencelogscriptfile.exists()) {
			ArrayList<InterestValue> values = getbasevalues(preferencetree);
			generatepreferencecaseforNonDefault(values, preferencelogscriptfile, true);
			generatepreferencecaseforNonDefault(values, preferencescriptfile, false);
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
			predata.firsttestcasepath = Main.allpreferenceprecaselog;
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
		for (TestCaseData origincase : origintestdates) {
			String tagname = origincase.tagname;
			TestCaseData data = new TestCaseData();
			data.tagname = tagname;
			Logger.log("  interestcase " + tagname + " start:");
			if (Main.resetForEachRun) {
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
			}
			ProcessExecutor.process("python", Main.allpreferenceprecase);
			ProcessExecutor.processnolog("adb", "logcat", "-c");
			locthread = new LocThread();
			locthread.setFile(Main.allpreferencecaseloc + File.separator  + tagname + ".txt");
			data.firstlocpath = Main.allpreferencecaseloc + File.separator  + tagname + ".txt";
			locthread.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			List<String> pythonlogs = ProcessExecutor.process("python", origincase.firsttestcasepath);
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
			locthread.locstop();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String origincoveragefilename = "coverage" + tagname + ".ec";
			String coveragefilename = "coverage" + tagname + "_all.ec";
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.allpreferencecoverage + File.separator  + coveragefilename);
			data.firstcoveragepath = Main.allpreferencecoverage + File.separator  + coveragefilename;
			Logger.log("interestcase " + tagname + " end\n");
			datas.put(tagname, data);
			i++;
			if (i % 3 == 0) {
				JsonHelper.savetestcasesdataAdapt(datas, Main.allpreferenceinfofile);

			}
		}
		JsonHelper.savetestcasesdataAdapt(datas, Main.allpreferenceinfofile);
	}
	
	private static ArrayList<InterestValue> getbasevalues(Map<String, List<PreferenceTreeNode>> preferencetree) {
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
		int i = 0;
		for (PreferenceTreeNode node : allnodes) {
			values.add(node.toInterestValueReverseDefault());
			i++;
			System.out.println("id " + i + ", " + "type: " + node.preferencetype + ",  title: " + node.title + ",  catelog: " + node.catlog + ", defaultvalue: " + node.defaultvalue);
		}
		return values;
	}
	
	public static TestCaseData scriptexecuteforinterestall(String nowtagname, String scriptfile) {
		TestCaseData data = new TestCaseData();
		data.tagname = nowtagname;
		data.firsttestcasepath = scriptfile;
		if (Main.resetForEachRun) {
			ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
		}
		ProcessExecutor.processnolog("adb", "logcat", "-c");
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		locthread = new LocThread();
		locthread.setFile(Main.interestallcaseloc + File.separator  + nowtagname + ".txt");
		data.firstlocpath = Main.interestallcaseloc + File.separator  + nowtagname + ".txt";
		locthread.start();
		errorthread = new ErrorCollectThread();
		errorthread.setErrorFile(Main.interestallerror);
		errorthread.addIndex(nowtagname);
		errorthread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<String> pythonlogs = ProcessExecutor.process("python", scriptfile);
		boolean shouldadd = false;
		String errorlog = "";
		data.firstconsumedtime = 0;
		data.firstjacocotime = 0;
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
		locthread.locstop();
		errorthread.errorlogstop();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String origintagname = nowtagname.substring(0, nowtagname.lastIndexOf('_'));
		String origincoveragefilename = "coverage" + origintagname + ".ec";
		String coveragefilename = "coverage" + nowtagname + "_ia.ec";
		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.interestallcasescoverage + File.separator  + coveragefilename);
		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + "coverage" + origintagname + "_pre.ec", Main.interestallcasescoverage + File.separator  + "coverage" + nowtagname + "_pre_ia.ec");
		data.firstcoveragepath = Main.interestallcasescoverage + File.separator  + coveragefilename;
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

			String casename = preferencetextgenerateForPREFEST(i, exescene, data, Main.packagename, true);
			generatedcases.put(data.tagname + "_" + i, casename);
		}
		
		return generatedcases;
	}
	
	private static PreferenceTreeNode getPrefereceNode(String key, Map<String, List<PreferenceTreeNode>> preferencetree) {
		for (List<PreferenceTreeNode> nodelist : preferencetree.values()) {
			for (PreferenceTreeNode node : nodelist) {
				if (key.equals(node.key)) {
					return node;
				}
			}
		}
		return null;
	}
	
	public static TestCaseData scriptexecuteforpw(PWValue value, String nowtagname, String scriptfile) {
		TestCaseData data = new TestCaseData();
		data.tagname = nowtagname;
		data.firsttestcasepath = scriptfile;
		if (Main.resetForEachRun) {
			ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
		}
		ProcessExecutor.process("python", value.preferencescriptfile);
		ProcessExecutor.processnolog("adb", "logcat", "-c");
//		locthread = new LocThread();
//		locthread.setFile(Main.allpreferencecaseloc + File.separator  + tagname + ".txt");
//		data.firstlocpath = Main.allpreferencecaseloc + File.separator  + tagname + ".txt";
//		locthread.start();
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		List<String> pythonlogs = ProcessExecutor.process("python", scriptfile);
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
//		locthread.locstop();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String origincoveragefilename = "coverage" + nowtagname + ".ec";
		String coveragefilename = "coverage" + value.index + "_" + nowtagname + "_pw.ec";
		ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + origincoveragefilename, Main.pwpreferencecoverage + File.separator  + coveragefilename);
		data.firstcoveragepath = Main.pwpreferencecoverage + File.separator  + coveragefilename;
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
					InterestTranslator.generatetotalpreferences(Main.packagename, values, hasconscript, conscripthandler),
					InterestTranslator.generatesystemservicestr(values),
					InterestTranslator.getsystemservicerestorestr(),
					shouldjacoco);
			totalsb.append(settingScript);
		
			bw.write(settingScript.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
