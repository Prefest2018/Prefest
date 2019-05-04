package tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import GUI.Main;
import appiumscript.scriptexecutor.InterestAllPlan;
import appiumscript.scriptexecutor.PWPlan;
import appiumscript.scriptexecutor.PWValue;
import data.InterestValue;
import data.Scene;
import data.TestCaseData;
import sootproject.resourceLoader.PreferenceTreeNode;
import uiautomationexploration.Adapter;


public class JsonHelper {
	public static JSONObject saveApkInfo(List<String> apkinfolist) {
		String packagename = null;
		String luanchactivity = null;
		JSONObject jsonObject = new JSONObject();
		for (String line : apkinfolist) {
			if (line.startsWith("package: name='")) {
				int begin = line.indexOf('\'', 0);
				int end = line.indexOf('\'', begin +1);
				packagename = line.substring(begin + 1, end);
			} else if (line.startsWith("launchable-activity: name='")) {
				int begin = line.indexOf('\'', 0);
				int end = line.indexOf('\'', begin + 1);
				luanchactivity = line.substring(begin + 1, end);
			}
		}
		jsonObject.put("packagename", packagename);
		jsonObject.put("luanchactivity", luanchactivity);
		saveJsonFile(jsonObject, Main.apkinfo);
		return jsonObject;
	}
	
	public static void savetestcasesdataAdapt(Map<?, TestCaseData> testcases, String filepath) {
		for (TestCaseData data : testcases.values()) {
			if (null != data.firstcoveragepath && data.firstcoveragepath.startsWith(Main.home)) {
				data.firstcoveragepath = data.firstcoveragepath.substring(Main.home.length());
			}
			if (null != data.firstlocpath && data.firstlocpath.startsWith(Main.home)) {
				data.firstlocpath = data.firstlocpath.substring(Main.home.length());
			}
			if (null != data.firsttestcasepath && data.firsttestcasepath.startsWith(Main.home)) {
				data.firsttestcasepath = data.firsttestcasepath.substring(Main.home.length());
			}
		}
		savetestcasesdata(testcases, filepath);
	}
	
	public static void savetestcasesdata(Map<?, TestCaseData> testcases, String filepath) {
		JSONObject totaljson = new JSONObject();
		JSONArray testcasearray = new JSONArray();
		Map<String, InterestValue> enumInterest = new HashMap<String, InterestValue>();
		for (TestCaseData testcase : testcases.values()) {
			JSONObject caseobject = new JSONObject();
			caseobject.put("tagname", testcase.tagname);
			caseobject.put("firstconsumedtime", testcase.firstconsumedtime);
			caseobject.put("firstcoveragepath", testcase.firstcoveragepath);
			caseobject.put("firsterrorlog", testcase.firsterrorlog);
			caseobject.put("firstjacocotime", testcase.firstjacocotime);

			caseobject.put("firstlocpath", testcase.firstlocpath);
			caseobject.put("firsttestcasepath", testcase.firsttestcasepath);
			caseobject.put("firstexecutionsuccess", testcase.firstexecutionsuccess);
			if ((testcase.interestScenes != null) && !testcase.interestScenes.isEmpty()) {
				JSONArray interestScenes = new JSONArray();
				for (Scene scene: testcase.interestScenes) {
					JSONObject sceneob = new JSONObject();
					sceneob.put("branchids", scene.branchids);
					sceneob.put("changebranchids", scene.changebranchids);
					JSONArray interests = new JSONArray();
					for (InterestValue value : scene.interests) {
						JSONObject valueob = new JSONObject();

						valueob.put("name", value.name);
						if (!enumInterest.containsKey(value.name)) {
							enumInterest.put(value.name, value);
						}
						valueob.put("value", value.value);

						interests.add(valueob);
					}
					sceneob.put("interests", interests);
					if (null != scene.preinterests && !scene.preinterests.isEmpty()) {
						JSONArray preinterests = new JSONArray();
						for (InterestValue value : scene.preinterests) {
							JSONObject valueob = new JSONObject();

							valueob.put("name", value.name);
							if (!enumInterest.containsKey(value.name)) {
								enumInterest.put(value.name, value);
							}
							valueob.put("value", value.value);

							preinterests.add(valueob);
						}
						sceneob.put("preinterests", preinterests);
					}
					interestScenes.add(sceneob);
				}
				caseobject.put("interestScenes", interestScenes);
			}
			testcasearray.add(caseobject);
		}
		JSONArray interestmap = new JSONArray();
		for (InterestValue value : enumInterest.values()) {
			JSONObject valueob = translatorInterestValue2Json(value);
			interestmap.add(valueob);
		}
		
		totaljson.put("testcasedatas", testcasearray);
		totaljson.put("interestmap", interestmap);
		saveJsonFile(totaljson, filepath);
	}
	
	
	public static Map<String, TestCaseData> gettestcasesdataAdapt(String filepath, boolean shouldreadinterest) {
		Map<String, TestCaseData> testcases = gettestcasesdata(filepath, shouldreadinterest);
		for (TestCaseData data : testcases.values()) {
			if (null != data.firstcoveragepath && !data.firstcoveragepath.startsWith(Main.home)) {
				data.firstcoveragepath = Main.home + data.firstcoveragepath;
			}
			if (null != data.firstlocpath && !data.firstlocpath.startsWith(Main.home)) {
				data.firstlocpath = Main.home + data.firstlocpath;
			}
			if (null != data.firsttestcasepath && !data.firsttestcasepath.startsWith(Main.home)) {
				data.firsttestcasepath = Main.home + data.firsttestcasepath;
			}
		}
		return testcases;
	}
	

	
	public static Map<String, TestCaseData> gettestcasesdata(String filepath, boolean shouldreadinterest) {
		JSONObject jsonall = getJsonObject(filepath);
		Map<String, InterestValue> enumInterest = null;
		if (shouldreadinterest && jsonall.containsKey("interestmap")) {
			JSONArray interestmap = (JSONArray)jsonall.get("interestmap");
			enumInterest = new HashMap<String, InterestValue>();
			for (int i = 0; i < interestmap.size(); i++) {
				JSONObject value = (JSONObject)interestmap.get(i);
				InterestValue interestvalue = translatorJson2InterestValue(value);
				enumInterest.put(interestvalue.name, interestvalue);
			}
		}
		JSONArray testcasearray = null;
		if (null != jsonall) {
			testcasearray = (JSONArray)jsonall.get("testcasedatas");
		} else {
			testcasearray = getJsonArray(filepath);
		}
		
		Map<String, TestCaseData> testcases = new HashMap<String, TestCaseData>();
		for (int i = 0; i < testcasearray.size(); i++) {
			TestCaseData data = new TestCaseData();
			JSONObject nowone = (JSONObject)testcasearray.get(i);
			if (nowone.containsKey("tagname")) {
				data.tagname = (String)nowone.get("tagname");
			} else {
				data.tagname = i + "";
			}

			data.firstconsumedtime = Float.parseFloat(nowone.get("firstconsumedtime")+"");
			if (nowone.containsKey("firstcoveragepath")) {
				String temp = (String)nowone.get("firstcoveragepath");
				data.firstcoveragepath = temp;
			}
			
			if (nowone.containsKey("firsterrorlog")) {
				data.firsterrorlog = (String)nowone.get("firsterrorlog");
			}
			data.firstjacocotime = Float.parseFloat(nowone.get("firstjacocotime")+"");
			if (nowone.containsKey("firstlocpath")) {
				String temp = (String)nowone.get("firstlocpath");
				data.firstlocpath = temp;
			}
			if (nowone.containsKey("firsttestcasepath")) {
				String temp = (String)nowone.get("firsttestcasepath");
				data.firsttestcasepath = temp;
			} else {
				data.firsttestcasepath = File.separator + "testcase" + File.separator + "firstcases" + File.separator + "testcase" + data.tagname + ".py";
			}

			data.firstexecutionsuccess = (Boolean)nowone.get("firstexecutionsuccess");
			testcases.put(data.tagname, data);
			if (!shouldreadinterest) {
				continue;
			}
			if (nowone.containsKey("interestScenes")) {
				JSONArray scenearrays = (JSONArray)nowone.get("interestScenes");
				data.interestScenes = new ArrayList<Scene>();
				for (int j = 0; j < scenearrays.size(); j++) {
					JSONObject sceneob = (JSONObject)scenearrays.get(j);
					Scene scene = new Scene();
					scene.branchids = (String)sceneob.get("branchids");
					scene.changebranchids = (String)sceneob.get("changebranchids");
					scene.interests = new ArrayList<InterestValue>();
					JSONArray interestob = (JSONArray)sceneob.get("interests");
					for (int k = 0; k < interestob.size(); k++) {
						JSONObject interestvalue = (JSONObject)interestob.get(k);
						InterestValue value = new InterestValue();

						value.name = (String)interestvalue.get("name");
						if (interestvalue.containsKey("value")) {
							value.value = (String)interestvalue.get("value");
						}
						InterestValue enumvalue = enumInterest.get(value.name);
						value.preferencesteps = enumvalue.preferencesteps;
						value.generaltype = enumvalue.generaltype;
						value.innertype = enumvalue.innertype;
						value.index = enumvalue.index;
						value.type = enumvalue.type;
						value.activityname = enumvalue.activityname;
						value.catalog = enumvalue.catalog;
						
						scene.interests.add(value);
					}
					if (sceneob.containsKey("preinterests")) {
						JSONArray preinterestob = (JSONArray)sceneob.get("preinterests");
						for (int k = 0; k < preinterestob.size(); k++) {
							JSONObject interestvalue = (JSONObject)preinterestob.get(k);
							InterestValue value = new InterestValue();

							value.name = (String)interestvalue.get("name");
							if (interestvalue.containsKey("value")) {
								value.value = (String)interestvalue.get("value");
							}
							InterestValue enumvalue = enumInterest.get(value.name);
							value.preferencesteps = enumvalue.preferencesteps;
							value.generaltype = enumvalue.generaltype;
							value.innertype = enumvalue.innertype;
							value.index = enumvalue.index;
							value.type = enumvalue.type;
							value.activityname = enumvalue.activityname;
							value.catalog = enumvalue.catalog;
							if (null == scene.preinterests) {
								scene.preinterests = new ArrayList<InterestValue>();
							}
							scene.preinterests.add(value);
						}
					}
					data.interestScenes.add(scene);
				}
				
			}
		}
		return testcases;
	}
	
	public static void saveJsonFile(JSONObject object, String filepath) {
		File savedfile = new File(filepath);
		try {
			if (savedfile.exists()) {
				savedfile.delete();
			}
			savedfile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(savedfile), "UTF-8"));
			bw.write(object.toJSONString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void saveJsonFile(JSONArray jarray, String filepath) {
		File savedfile = new File(filepath);
		try {
			if (savedfile.exists()) {
				savedfile.delete();
			}
			savedfile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(savedfile), "UTF-8"));
			bw.write(jarray.toJSONString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject getJsonObject(String filepath) {
		File savedfile = new File(filepath);
		JSONParser parser = new JSONParser();
		JSONObject jsonobject = null;
			try {
				jsonobject = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(savedfile), "UTF-8"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return jsonobject;
	}
	
	public static JSONArray getJsonArray(String filepath) {
		File savedfile = new File(filepath);
		JSONParser parser = new JSONParser();
		JSONArray jsonarray = null;
		try {
			jsonarray = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream(savedfile), "UTF-8"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonarray;
	}
	
	public static PWPlan getpwplanAdapt(String filename) {
		PWPlan pwplan = getpwplan(filename);
		for (String key : pwplan.scriptmap.keySet()) {
			String filepath = pwplan.scriptmap.get(key);
			if (!filepath.startsWith(Main.home)) {
				filepath = Main.home + filepath;
				pwplan.scriptmap.put(key, filepath);
			}
		}
		for (PWValue value : pwplan.values) {
			if (!value.preferencescriptfile.startsWith(Main.home)) {
				value.preferencescriptfile = Main.home + value.preferencescriptfile;
			}
			if (!value.preferencescriptlogfile.startsWith(Main.home)) {
				value.preferencescriptlogfile = Main.home + value.preferencescriptlogfile;
			}
			if (!value.preferencetestcaseinfofile.startsWith(Main.home)) {
				value.preferencetestcaseinfofile = Main.home + value.preferencetestcaseinfofile;
			}
		}
		return pwplan;
	}
	
	
	public static PWPlan getpwplan(String filename) {
		JSONObject pwplan = getJsonObject(filename);
		JSONObject scriptmapobj = (JSONObject)pwplan.get("scriptmap");
		Map<String, String> scriptmap = new HashMap<String, String>();
		for (Object key : scriptmapobj.keySet()) {
			scriptmap.put((String)key, (String)scriptmapobj.get((String)key));
		}
		JSONArray pwvaluearray = (JSONArray)pwplan.get("values");
		LinkedList<PWValue> values = new LinkedList<PWValue>();
		for (int i = 0; i < pwvaluearray.size(); i++) {
			JSONObject valueobj = (JSONObject)pwvaluearray.get(i);
			int index = (int)valueobj.get("index");
			String state = (String)valueobj.get("state");
			String preferencescriptfile = (String)valueobj.get("preferencescriptfile");
			String preferencescriptlogfile = (String)valueobj.get("preferencescriptlogfile");
			String preferencetestcaseinfofile = (String)valueobj.get("preferencetestcaseinfofile");
			String nowtagname = (String)valueobj.get("nowtagname");
			values.add(new PWValue(index, state, preferencescriptfile, preferencescriptlogfile, preferencetestcaseinfofile, nowtagname));
		}
		PWPlan plan = new PWPlan(scriptmap, values);
		return plan;
	}
	
	public static void setpwplanAdapt(PWPlan plan, String filename) {
		for (String key : plan.scriptmap.keySet()) {
			String filepath = plan.scriptmap.get(key);
			if (filepath.startsWith(Main.home)) {
				filepath = filepath.substring(Main.home.length());
				plan.scriptmap.put(key, filepath);
			}
		}
		for (PWValue value : plan.values) {
			if (value.preferencescriptfile.startsWith(Main.home)) {
				value.preferencescriptfile = value.preferencescriptfile.substring(Main.home.length());
			}
			if (value.preferencescriptlogfile.startsWith(Main.home)) {
				value.preferencescriptlogfile = value.preferencescriptlogfile.substring(Main.home.length());
			}
			if (value.preferencetestcaseinfofile.startsWith(Main.home)) {
				value.preferencetestcaseinfofile = value.preferencetestcaseinfofile.substring(Main.home.length());
			}
		}
	}
	
	public static void setpwplan(PWPlan plan, String filename) {
		JSONObject pwplan = new JSONObject();
		JSONObject scriptmap = new JSONObject();
		scriptmap.putAll(plan.scriptmap);
		pwplan.put("scriptmap", scriptmap);
		JSONArray pwvaluearray = new JSONArray();
		for (PWValue value : plan.values) {
			JSONObject valueobj = new JSONObject();
			valueobj.put("index", value.index);
			valueobj.put("nowtagname", value.nowtagname);
			valueobj.put("preferencescriptfile", value.preferencescriptfile);
			valueobj.put("preferencescriptlogfile", value.preferencescriptlogfile);
			valueobj.put("preferencetestcaseinfofile", value.preferencetestcaseinfofile);
			valueobj.put("state", value.state);
			pwvaluearray.add(valueobj);
		}
		pwplan.put("values", pwvaluearray);
		saveJsonFile(pwplan ,filename);
	}
	
	public static InterestAllPlan getinterestallplanAdapt(String filename) {
		InterestAllPlan plan = getinterestallplan(filename);
		for (String key : plan.scriptmap.keySet()) {
			String filepath = plan.scriptmap.get(key);
			if (!filepath.startsWith(Main.home)) {
				filepath = filepath + Main.home;
				plan.scriptmap.put(key, filepath);
			}
		}
		return plan;
	}
	
	public static InterestAllPlan getinterestallplan(String filename) {
		JSONObject iaplan = getJsonObject(filename);
		JSONObject scriptmapobj = (JSONObject)iaplan.get("scriptmap");
		Map<String, String> scriptmap = new HashMap<String, String>();
		for (Object key : scriptmapobj.keySet()) {
			scriptmap.put((String)key, (String)scriptmapobj.get((String)key));
		}
		String nowtagname = (String)iaplan.get("nowtagname");
		InterestAllPlan plan = new InterestAllPlan(scriptmap, nowtagname);
		return plan;
	}
	
	public static void setinterestallplanAdapt(InterestAllPlan plan, String filename) {
		for (String key : plan.scriptmap.keySet()) {
			String filepath = plan.scriptmap.get(key);
			if (filepath.startsWith(Main.home)) {
				filepath = filepath.substring(Main.home.length());
				plan.scriptmap.put(key, filepath);
			}
		}
		setinterestallplan(plan, filename);
	}
	
	public static void setinterestallplan(InterestAllPlan plan, String filename) {
		JSONObject iaplan = new JSONObject();
		JSONObject scriptmap = new JSONObject();
		scriptmap.putAll(plan.scriptmap);
		iaplan.put("scriptmap", scriptmap);
		iaplan.put("nowtagname", plan.nowtagname);
		saveJsonFile(iaplan ,filename);
	}
	
	public static void saveadapterWithpreferencelistFromtestcasedata(Adapter adapter, String adapterfilepath, String testcasedatapath) {
		JSONObject adapterob = new JSONObject();
		translateAdapter2JsonWithoutpreferencelist(adapterob, adapter);
		JSONObject testcasedate = getJsonObject(testcasedatapath);
		if (testcasedate.containsKey("interestmap")) {
			JSONArray preferencelist = (JSONArray)testcasedate.get("interestmap");
			adapterob.put("preferencelist", preferencelist);
		}
		saveJsonFile(adapterob ,adapterfilepath);
	}
	
	public static void saveadapter(Adapter adapter, String filename) {
		JSONObject adapterob = new JSONObject();
		translateAdapter2JsonWithoutpreferencelist(adapterob, adapter);
		if (null != adapter.preferencelist) {
			JSONArray preferencelist = new JSONArray();
			adapterob.put("preferencelist", preferencelist);
			for (String key : adapter.preferencelist.keySet()) {
				InterestValue value = adapter.preferencelist.get(key);
				JSONObject valueob = translatorInterestValue2Json(value);
				preferencelist.add(valueob);
			}
		}
		saveJsonFile(adapterob ,filename);
	}
	
	private static void translateAdapter2JsonWithoutpreferencelist(JSONObject adapterob, Adapter adapter) {
		adapterob.put("explored", adapter.explored);
		if (null != adapter.possibleactivities) {
			JSONArray activities = new JSONArray();
			activities.addAll(adapter.possibleactivities);
			adapterob.put("possibleactivities", activities);
		}
		JSONObject xmlpreferencenodemap = new JSONObject();
		JSONArray preferecenodelist = new JSONArray();
		for (String xmlfilename : adapter.xmlcontentlist.keySet()) {
			List<PreferenceTreeNode> nownodes = adapter.xmlcontentlist.get(xmlfilename);
			JSONArray nodearray = new JSONArray();
			for (PreferenceTreeNode nownode : nownodes) {
				nodearray.add(nownode.index);
				settrees(nownode, preferecenodelist);
			}
			xmlpreferencenodemap.put(xmlfilename, nodearray);
		}
		adapterob.put("xmlpreferencenodemap", xmlpreferencenodemap);
		adapterob.put("preferecenodelist", preferecenodelist);
		
		if (null != adapter.preferencefilename2activity) {
			JSONObject preferencefilename2activityob = new JSONObject();
			preferencefilename2activityob.putAll(adapter.preferencefilename2activity);
			adapterob.put("preferencefilename2activity", preferencefilename2activityob);
		}
	}
	
	private static JSONObject translatorInterestValue2Json(InterestValue value) {
		JSONObject valueob = new JSONObject();
		valueob.put("generaltype", value.generaltype);
		valueob.put("innertype", value.innertype);
		valueob.put("type", value.type);
		valueob.put("name", value.name);
		valueob.put("isadapted", value.isadapted);
		if (null != value.catalog) {
			valueob.put("catalog", value.catalog);
		}
		valueob.put("index", value.index);
		if (value.preferencesteps != null && !value.preferencesteps.isEmpty()) {
			JSONArray preferencesteps = new JSONArray();
			preferencesteps.addAll(value.preferencesteps);
			valueob.put("preferencesteps", preferencesteps);
		}
		if (null != value.activityname) {
			valueob.put("activityname", value.activityname);
		}
		if (null != value.activityextra) {
			valueob.put("activityextra", value.activityextra);
		}
		return valueob;
	}
	
	private static InterestValue translatorJson2InterestValue(JSONObject interestvalue) {
		InterestValue value = new InterestValue();
		if (interestvalue.containsKey("isadapted")) {
			value.isadapted = (Boolean)interestvalue.get("isadapted");
		}
		if (interestvalue.containsKey("preferencesteps")) {
			JSONArray preferencesteps = (JSONArray)interestvalue.get("preferencesteps");
			value.preferencesteps = new ArrayList<String>();
			for (int m = 0; m < preferencesteps.size(); m++) {
				value.preferencesteps.add((String)preferencesteps.get(m));
			}
		}
		if (interestvalue.containsKey("generaltype")) {
			value.generaltype = (String)interestvalue.get("generaltype");
		}
		
		if (interestvalue.containsKey("innertype")) {
			value.innertype = (String)interestvalue.get("innertype");
		}
		value.name = (String)interestvalue.get("name");
		value.type = (String)interestvalue.get("type");

		value.index = ((Long)interestvalue.get("index")).intValue();
		if (interestvalue.containsKey("activityname")) {
			value.activityname = (String)interestvalue.get("activityname");
		}
		if (interestvalue.containsKey("activityextra")) {
			value.activityextra = (String)interestvalue.get("activityextra");
		}
		if (interestvalue.containsKey("catalog")) {
			value.catalog = (String)interestvalue.get("catalog");
		}
		return value;
	}
	
	private static void settrees(PreferenceTreeNode nownode, JSONArray preferecenodelist) {
		JSONObject nodeob = new JSONObject();
		nodeob.put("index", nownode.index);
		nodeob.put("activityname", nownode.activityname);
		nodeob.put("catlog", nownode.catlog);
		nodeob.put("key", nownode.key);
		nodeob.put("title", nownode.title);
		nodeob.put("preferencetype", nownode.preferencetype);
		nodeob.put("shouldexplore", nownode.shouldexplore);
		nodeob.put("isheader", nownode.isheader);
		if (null != nownode.parentnode) {
			nodeob.put("parentnode", nownode.parentnode.index);
		}
		JSONArray childlist = new JSONArray();
		if (null != nownode.childnodes) {
			for (PreferenceTreeNode child : nownode.childnodes) {
				settrees(child, preferecenodelist);
				childlist.add(child.index);
			}
			nodeob.put("childnodes", childlist);
		}
		preferecenodelist.add(nodeob);
	}
	
	public static Adapter getadapter(String filename) {
		JSONObject adapterob = getJsonObject(filename);
		Adapter adapter = new Adapter();
		adapter.explored = (Boolean)adapterob.get("explored");
		adapter.possibleactivities = new HashSet<String>();
		if (adapterob.containsKey("possibleactivities")) {
			for (Object content : ((JSONArray)adapterob.get("possibleactivities"))) {
				adapter.possibleactivities.add(content.toString());
			}
		}
		if (adapterob.containsKey("preferencelist")) {
			adapter.preferencelist = new HashMap<String, InterestValue>();
			JSONArray preferencelist = (JSONArray)adapterob.get("preferencelist");
			for (Object content : preferencelist) {
				InterestValue value = translatorJson2InterestValue((JSONObject)content);
				adapter.preferencelist.put(value.name, value);
			}
		}
		if (adapterob.containsKey("xmlpreferencenodemap") && adapterob.containsKey("preferecenodelist")) {
			adapter.xmlcontentlist = new HashMap<String, List<PreferenceTreeNode>>();
			JSONObject xmlpreferencenodemap = (JSONObject)adapterob.get("xmlpreferencenodemap");
			JSONArray preferecenodelist = (JSONArray)adapterob.get("preferecenodelist");
			Map<Integer, PreferenceTreeNode> tempmap = new HashMap<Integer, PreferenceTreeNode>();
			for (Object content : preferecenodelist) {
				JSONObject valueob = (JSONObject)content;
				PreferenceTreeNode value = new PreferenceTreeNode();
				value.index = ((Long)valueob.get("index")).intValue();
				if (valueob.containsKey("activityname")) {
					value.activityname = (String)valueob.get("activityname");
				}
				if (valueob.containsKey("catlog")) {
					value.catlog = (String)valueob.get("catlog");
				}
				if (valueob.containsKey("key")) {
					value.key = (String)valueob.get("key");
				}
				if (valueob.containsKey("title")) {
					value.title = (String)valueob.get("title");
				}
				value.preferencetype = (String)valueob.get("preferencetype");
				value.shouldexplore = (Boolean)valueob.get("shouldexplore");
				value.isheader = (Boolean)valueob.get("isheader");
				tempmap.put(value.index, value);
			}
			for (Object content : preferecenodelist) {
				JSONObject valueob = (JSONObject)content;
				PreferenceTreeNode nownode = tempmap.get(((Long)valueob.get("index")).intValue());
				if (valueob.containsKey("parentnode")) {
					PreferenceTreeNode parent = tempmap.get(((Long)valueob.get("parentnode")).intValue());
					nownode.parentnode = parent;
				}
				if (valueob.containsKey("childnodes")) {
					JSONArray children = (JSONArray)valueob.get("childnodes");
					nownode.childnodes = new ArrayList<PreferenceTreeNode>();
					for (Object child : children) {
						int childnum = ((Long)child).intValue();
						nownode.childnodes.add(tempmap.get(childnum));
					}
				}
			}
			for (PreferenceTreeNode node : tempmap.values()) {
				node.initTitles();
			}
			adapter.xmlcontentlist = new HashMap<String, List<PreferenceTreeNode>>();
			for (Object key : xmlpreferencenodemap.keySet()) {
				String xmlfilename = (String)key;
				JSONArray nodearray = (JSONArray)xmlpreferencenodemap.get(key);
				List<PreferenceTreeNode> nodemap = new ArrayList<PreferenceTreeNode>();
				for (Object nodenum : nodearray) {
					nodemap.add(tempmap.get(((Long)nodenum).intValue()));
				}
				adapter.xmlcontentlist.put(xmlfilename, nodemap);
			}
//			nodeob.put("parentnode", nownode.parentnode.index);
		}
		if (adapterob.containsKey("preferencefilename2activity")) {
			JSONObject preferencefilename2activityob = (JSONObject)adapterob.get("preferencefilename2activity");
			adapter.preferencefilename2activity = new HashMap<String, String>();
			for (Object value : preferencefilename2activityob.keySet()) {
				adapter.preferencefilename2activity.put(value.toString(), preferencefilename2activityob.get(value).toString());
			}
		}
		return adapter;
	}
}
