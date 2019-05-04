package sootproject.preferenceAnalyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import GUI.Main;
import data.InterestValue;
import data.Scene;
import data.TestCaseData;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MySystemService;
import sootproject.analysedata.TrailState;
import sootproject.data.MyMethodDeclaration;
import sootproject.data.MyNode;
import sootproject.resourceLoader.PreferenceTreeNode;
import sootproject.resourceLoader.ResourceLoader;
import sootproject.soot.PreferenceAnalyseTransformer;
import tools.JsonHelper;
import tools.Logger;
import uiautomationexploration.Adapter;
import soot.Body;
import soot.SootMethod;
import soot.Unit;

public class PreferenceAnalyser {
	private List<MyNode> allNodesList = null;
	private Map<SootMethod, MyMethodDeclaration> methodMap = null; 
	private Map<SootMethod, Map<Integer, MyMethodDeclaration>> overrideMap = null;
	private Map<Integer, MyNode> logMap = null;
	private static Map<Unit, MyNode> nodeMap = null;
	private String resFolderpath = null;
	private String sourceFolderpath = null;
	private ResourceLoader resLoader = null;
	private static Map<String, TestCaseData> testcasedata = null;
	private Map<String, Integer> preferencexmlmap = null;
	private Map<String, String> preferencesuperclassmap = null;
	private Map<Integer, String> preference2activitymap = null;
	private static Map<Long, String> stridmap = null;
	private Set<String> preferenceactivitynames = null;
	public Map<SootMethod, Set<String>> skipmethodmap = null;
	private Set<String> skiplocs = null;
	
	private static Map<String, MyInterest> interestMaps = null;
	public static Map<String, MyInterest> allsystemservices = null;
	static {
		String[] servicelist = new String[] {"ass_location_gps", "ass_location_network", "ass_wifi", "ass_bluetooth", "ass_musicactive", "ass_mobiledata"};
		allsystemservices = new HashMap<String, MyInterest>();
		for (String servicename : servicelist) {
			MySystemService service = new MySystemService(servicename, "boolean");
			allsystemservices.put(servicename, service);
		}
	}
	
	
	public PreferenceAnalyser(PreferenceAnalyseTransformer transformer, Map<String, TestCaseData> testcasedata, String resFolderpath, String sourceFolderpath) {
		this.allNodesList = transformer.allNodesList;
		this.methodMap = transformer.methodMap;
		this.logMap = transformer.logMap;
		this.preferencexmlmap = transformer.preferencexmlmap;
		this.preferencesuperclassmap = transformer.preferencesupermap;
		PreferenceAnalyser.nodeMap = transformer.nodeMap;
		this.preference2activitymap = transformer.preference2activitymap;
		PreferenceAnalyser.testcasedata = testcasedata;
		this.resFolderpath = resFolderpath;
		this.sourceFolderpath = sourceFolderpath;
		this.overrideMap = transformer.overridemap;
		this.interestMaps = new HashMap<String, MyInterest>();
		this.preferenceactivitynames = transformer.preferenceactivitynames;
		
		this.skipmethodmap = transformer.skipmethodmap;
		this.skiplocs = new HashSet<String>();
		for (Set<String> locs : this.skipmethodmap.values()) {
			this.skiplocs.addAll(locs);
		}
		
	}
	
	public static Map<String, TestCaseData> getTestcasedata() {
		return testcasedata;
	}
	
	public static Map<Unit, MyNode> getNodeMap() {
		return nodeMap;
	}
	
	public void analysePreferenceOnlyFromCode() {
		readPreferenceInfo();
		addSystemServiceInterest();
	}
	

	public void analysePreferenceFromLogs() {
		TrailState.setLogMaps(logMap, nodeMap, methodMap, overrideMap, skipmethodmap);
		readPreferenceInfo();
		addSystemServiceInterest();
		analyse();
		JsonHelper.savetestcasesdataAdapt(testcasedata, Main.testcaseinfofile);
		Map<Body, Set<String>> logmaps = new HashMap<Body, Set<String>>();
		for (String tagname : testcasedata.keySet()) {
			TestCaseData data = testcasedata.get(tagname);
			if (null != data.interestScenes) {
				for (Scene scene : data.interestScenes) {
					Set<String> map = logmaps.get(scene.body);
					if (null == map) {
						map = new HashSet<String>();
						logmaps.put(scene.body, map);
					}
					map.add(scene.branchids);
				}
			}
		}
		Logger.setTempLogFile(Main.home + "//log//preferenceanalysis.txt", true);
		for (Body body : logmaps.keySet()) {
			String preferencebranchstr = "";
			for (String key : logmaps.get(body)) {
				preferencebranchstr += key + "  ";
			}
			if (null == body) {
				Logger.log("current Body is null, for preference branch: " + preferencebranchstr + "\n\n");
			} else {
				Logger.log("Body class: " + body.getMethod().getDeclaringClass().getName() + "\n" + 
						"Body method:  " + body.getMethod().getName() + "\n" + 
						"for preference branch: " + preferencebranchstr + "\n" +
						body.toString() + "\n\n");
			}

		}
	}

	public List<String> readLogs(TestCaseData data) {
		List<String> strList = new ArrayList<String>();
		try {

			File locfile = new File(data.firstlocpath);
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(locfile), "UTF-8"));
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
					strList.add(logid);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strList;
	}
	
	public void readPreferenceInfo() {
		Logger.setTempLogFile(Main.home + "//log//preferenceresanalysis.txt", true);
		File resfolder = new File(resFolderpath);
		File sourcefolder = new File(sourceFolderpath);
		resLoader = new ResourceLoader(resfolder, sourcefolder, preferencesuperclassmap, this.preference2activitymap);
		this.interestMaps = resLoader.getResourcePreferenceInfo(preferencexmlmap);
		this.stridmap = resLoader.getStringIDmap();
		resLoader.printpreferencemap();
	}
	
	public Adapter getBasicAdapter() {
		Adapter basicAdapter = new Adapter(this.preferenceactivitynames, this.resLoader.getPreferencefilename2activitymap(), this.resLoader.getPrefereneceTree());
		return basicAdapter;
	}
	
	private void addSystemServiceInterest() {
		this.interestMaps.putAll(allsystemservices);

	}
	
	private static final int ACTIVETHREADNUM = 4;
	
	private void analyse() {
		ThreadPoolExecutor executor = new ThreadPoolExecutor(ACTIVETHREADNUM, ACTIVETHREADNUM, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(30000));
		List<Future<?>> futures = Collections.synchronizedList(new ArrayList<Future<?>>());
		
		
		for (String tag : testcasedata.keySet()) {

			TestCaseData data = testcasedata.get(tag);
			futures.add(executor.submit(new AnalysisCallable(data)));
		}
		for (int i = 0 ; i < futures.size(); i++) {
			try {
				Future<?> future = futures.get(i);
				String tag = (String)future.get();
				System.out.println("current completed tag is: " + tag + " , progress: " + (i + 1) + "/" +testcasedata.size());

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
	
	class AnalysisCallable implements Callable<String> {
		private TestCaseData data = null;
		public AnalysisCallable(TestCaseData data) {
			this.data = data;
		}
		@Override
		public String call() {
			List<String> logs = readLogs(data);
			TrailState nowState = null;
			nowState = new TrailState(interestMaps, data.tagname, stridmap);
			int length = logs.size();
//			int inneri = 0;
			for (String log : logs) {
				if (skiplocs.contains(log)) {
					continue;
				}
				int logidNum = -1;
				int branchidNum = -1;
				if (log.contains("-")) {
					String[] strs = log.split("-");
					logidNum = Integer.parseInt(strs[0]);
					branchidNum = Integer.parseInt(strs[1]);
				} else {
					logidNum = Integer.parseInt(log);
				}
				nowState.next(logidNum, branchidNum);
			}

			nowState.analysePreference();
			return data.tagname;
		}
	}


	public Map<String, List<PreferenceTreeNode>> analysepreferencetree() {
		File resfolder = new File(resFolderpath);
		File sourcefolder = new File(sourceFolderpath);
		resLoader = new ResourceLoader(resfolder, sourcefolder, preferencesuperclassmap, this.preference2activitymap);
		this.interestMaps = resLoader.getResourcePreferenceInfo(preferencexmlmap);
		Map<String, List<PreferenceTreeNode>> preferencetree = resLoader.getPrefereneceTree();
		return preferencetree;
	}
	
	public void updateTestDataWithDependency() {
		
	}
}