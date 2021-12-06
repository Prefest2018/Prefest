package tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import GUI.Main;
import appiumscript.scriptexecutor.InterestAllPlan;
import appiumscript.scriptexecutor.InterestPlan;
import appiumscript.scriptexecutor.PWPlan;
import appiumscript.scriptexecutor.PWValue;
import appiumscript.scripttranslator.TestOperation;
import appiumscript.scripttranslator.TestOperationType;
import data.CoverData;
import data.ITScene;
import data.InterestValue;
import data.PlanState;
import data.PreferenceAdaptData;
import data.Scene;
import data.TestCaseData;
import sootproject.analysedata.MyInterest;
import sootproject.myexpression.ResultType;
import sootproject.preferenceAnalyse.PreferenceAnalyser;
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
		if (!new File(Main.apkinfo).exists()) {
			saveJsonFile(jsonObject, Main.apkinfo, false);
		}
		return jsonObject;
	}
	
	public static void getApkInfo() {
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
        if (apkinfo.containsKey("extrapackagename")) {
        	Main.extrapackagenames = new HashSet<String>();
        	JSONArray extrapackagenames = (JSONArray)apkinfo.get("extrapackagename");
        	for (Object o : extrapackagenames) {
        		Main.extrapackagenames.add(o.toString());
        	}
        }
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
			if (null != testcase.settinginterests && !testcase.settinginterests.isEmpty()) {
				JSONArray settinginterests = new JSONArray();
				for (InterestValue value : testcase.settinginterests) {
					JSONObject valueob = new JSONObject();
					valueob.put("name", value.name);
					if (!enumInterest.containsKey(value.name)) {
						enumInterest.put(value.name, value);
					}
					valueob.put("value", value.value);

					settinginterests.add(valueob);
				}
				caseobject.put("settinginterests", settinginterests);
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
		saveJsonFile(totaljson, filepath, false);
	}
	
	
	public static Map<String, TestCaseData> gettestcasesdataAdapt(String filepath, boolean shouldreadscenes) {
		Map<String, TestCaseData> testcases = gettestcasesdata(filepath, shouldreadscenes);
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
	

	
	public static Map<String, TestCaseData> gettestcasesdata(String filepath, boolean shouldreadscenes) {
		JSONObject jsonall = getJsonObject(filepath);
		Map<String, InterestValue> enumInterest = null;
		if (jsonall.containsKey("interestmap")) {
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
		} 
//			testcasearray = getJsonArray(filepath);
		
		Map<String, TestCaseData> testcases = new HashMap<String, TestCaseData>();
		for (int i = 0; i < testcasearray.size(); i++) {
			TestCaseData data = new TestCaseData();
			JSONObject nowone = (JSONObject)testcasearray.get(i);
			if (nowone.containsKey("tagname") && null != nowone.get("tagname")) {
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
			if (shouldreadscenes && nowone.containsKey("interestScenes")) {
				JSONArray scenearrays = (JSONArray)nowone.get("interestScenes");
				data.interestScenes = new ArrayList<Scene>();
				for (int j = 0; j < scenearrays.size(); j++) {
					JSONObject sceneob = (JSONObject)scenearrays.get(j);
					Scene scene = new Scene();
					scene.branchids = (String)sceneob.get("branchids");
					scene.changebranchids = (String)sceneob.get("changebranchids");
					scene.interests = new ArrayList<InterestValue>();
					if (sceneob.containsKey("interests")) {
						JSONArray interestob = (JSONArray)sceneob.get("interests");
						scene.interests = getInterestValueArrayFromEnum(interestob, enumInterest);
					}
					if (sceneob.containsKey("preinterests")) {
						JSONArray preinterestob = (JSONArray)sceneob.get("preinterests");
						scene.preinterests = getInterestValueArrayFromEnum(preinterestob, enumInterest);
					}
					data.interestScenes.add(scene);
				}
				
			}
			if (nowone.containsKey("settinginterests")) {
				JSONArray settinginterestsob = (JSONArray)nowone.get("settinginterests");
				data.settinginterests = getInterestValueArrayFromEnum(settinginterestsob, enumInterest);
			}
		}
		return testcases;
	}
	
	public static void saveJsonFile(JSONObject object, String filepath, boolean shouldBeautify) {
		File savedfile = new File(filepath);
		try {
			if (savedfile.exists()) {
				savedfile.delete();
			}
			savedfile.createNewFile();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(savedfile), "UTF-8"));
			String content = object.toJSONString();
			if (shouldBeautify) {
				content = JsonFormatTool.formatJson(content);
			}
			bw.write(content);
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
//		StringBuilder sb = new StringBuilder();
//			BufferedReader br = new BufferedReader(new FileReader(savedfile));
//			String line = null;
//				sb.append(line);
//			br.close();
//			// TODO Auto-generated catch block
//			e.printStackTrace();
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
//		StringBuilder sb = new StringBuilder();
//			BufferedReader br = new BufferedReader(new FileReader(savedfile));
//			String line = null;
//				sb.append(line);
//			br.close();
//			// TODO Auto-generated catch block
//			e.printStackTrace();
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
			int index = ((Long)valueobj.get("index")).intValue();
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
		setpwplan(plan ,filename);
		for (PWValue value : plan.values) {
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
		for (String key : plan.scriptmap.keySet()) {
			String filepath = plan.scriptmap.get(key);
			if (!filepath.startsWith(Main.home)) {
				filepath = Main.home + filepath;
				plan.scriptmap.put(key, filepath);
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
		saveJsonFile(pwplan ,filename, false);
	}
	
	public static InterestAllPlan getinterestallplanAdapt(String filename) {
		InterestAllPlan plan = getinterestallplan(filename);
		for (String key : plan.scriptmap.keySet()) {
			String filepath = plan.scriptmap.get(key);
			if (!filepath.startsWith(Main.home)) {
				filepath = Main.home + filepath;
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
		for (String key : plan.scriptmap.keySet()) {
			String filepath = plan.scriptmap.get(key);
			if (!filepath.startsWith(Main.home)) {
				filepath = Main.home + filepath;
				plan.scriptmap.put(key, filepath);
			}
		}
	}
	
	public static void setinterestallplan(InterestAllPlan plan, String filename) {
		JSONObject iaplan = new JSONObject();
		JSONObject scriptmap = new JSONObject();
		scriptmap.putAll(plan.scriptmap);
		iaplan.put("scriptmap", scriptmap);
		iaplan.put("nowtagname", plan.nowtagname);
		saveJsonFile(iaplan ,filename, false);
	}

	public static void saveadapterWithInterestValueSelfGen(Adapter adapter, String adapterfilepath) {
		JSONObject adapterob = new JSONObject();
		translateAdapter2JsonWithoutpreferencelist(adapterob, adapter);
		adapter.preferencelist = new HashMap<String, InterestValue>();
		Stack<PreferenceTreeNode> pstack = new Stack<PreferenceTreeNode>();

		for (List<PreferenceTreeNode> nodelists : adapter.xmlcontentlist.values()) {
			pstack.addAll(nodelists);
		}
		while (!pstack.isEmpty()) {
			PreferenceTreeNode nownode = pstack.pop();
			InterestValue value = nownode.toInterestValue();
			adapter.preferencelist.put(value.name, value);
			if (null != nownode.getChildnodes()) {
				pstack.addAll(nownode.getChildnodes());
			}
		}
		for (String systemeventname : PreferenceAnalyser.allsystemservices.keySet()) {
			MyInterest systemevent = PreferenceAnalyser.allsystemservices.get(systemeventname);
			adapter.preferencelist.put(systemeventname, systemevent.getInterestValueEnum());
		}
		
		
		JSONArray preferencelist = new JSONArray();
		for (InterestValue value : adapter.preferencelist.values()) {
			preferencelist.add(translatorInterestValue2Json(value));
		}
		adapterob.put("preferencelist", preferencelist);
		
		saveJsonFile(adapterob ,adapterfilepath, false);
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
		saveJsonFile(adapterob ,filename, false);
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
	
	public static JSONObject translatorInterestValue2Json(InterestValue value) {
		JSONObject valueob = new JSONObject();
		valueob.put("generaltype", value.generaltype);
		valueob.put("innertype", value.innertype);
		valueob.put("type", value.type);
		valueob.put("name", value.name);
		valueob.put("isadapted", value.isadapted);
		if (null != value.catalog) {
			valueob.put("catalog", value.catalog);
		}
//		valueob.put("value", value.value);
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
		if (null != value.extradatas && !value.extradatas.isEmpty()) {
			JSONObject extradatasob = new JSONObject();
			for (String key : value.extradatas.keySet()) {
				extradatasob.put(key, value.extradatas.get(key));
			}
			valueob.put("extradatas", extradatasob);
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
//				value.value = interestvalue.getString("value");
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
		if (interestvalue.containsKey("extradatas")) {
			value.extradatas = new HashMap<String, Object>();
			JSONObject extradatasob = (JSONObject)interestvalue.get("extradatas");
			for (Object key : extradatasob.keySet()) {
				value.extradatas.put(key.toString(), extradatasob.get(key));
			}
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
		nodeob.put("filename", nownode.filename);
		nodeob.put("preferencetype", nownode.preferencetype);
		nodeob.put("shouldexplore", nownode.shouldexplore);
		nodeob.put("isheader", nownode.isheader);
		nodeob.put("defaultvalue", nownode.defaultvalue);
		if (null != nownode.entryvalues) {
			JSONObject entryobs = new JSONObject();
			entryobs.putAll(nownode.entryvalues);
			nodeob.put("entryvalues", entryobs);
		}
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
		if (null != nownode.titles && !nownode.titles.isEmpty()) {
			JSONObject titleobs = new JSONObject();
			for (int i = 0; i < nownode.titles.size(); i++) {
				titleobs.put(i + "", nownode.titles.get(i));
			}
			nodeob.put("titles", titleobs);
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
				if (valueob.containsKey("filename")) {
					value.filename = (String)valueob.get("filename");
				}
				if (valueob.containsKey("defaultvalue")) {
					value.defaultvalue = (String)valueob.get("defaultvalue");
				}
				if (valueob.containsKey("entryvalues")) {
					JSONObject entryobs = (JSONObject)valueob.get("entryvalues");
					value.entryvalues = new HashMap<String, String>();
					for (Object keyob : entryobs.keySet()) {
						value.entryvalues.put(keyob.toString(), entryobs.get(keyob).toString());
					}
				}
				if (valueob.containsKey("titles")) {
					JSONObject entryobs = (JSONObject)valueob.get("titles");
					value.titles = new ArrayList<String>();
					Map<Integer, String> temptitles = new HashMap<Integer, String>();
					for (Object keyob : entryobs.keySet()) {
						if (entryobs.get(keyob) != null) {
							temptitles.put(Integer.parseInt(keyob.toString()), entryobs.get(keyob).toString());
						}
					}
					for (int i = 0; i < temptitles.size(); i++) {
						value.titles.add(temptitles.get(i));
					}
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
			
//					node.initTitles();
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
	
	public static InterestPlan getinterestplanAdapt(String filename) {
		InterestPlan interestplan = new InterestPlan();
		JSONObject interestplanob = getJsonObject(filename);
		interestplan.currentit = ((Long)interestplanob.get("currentit")).intValue();
		String statestr = (String)interestplanob.get("state");
		switch (statestr) {
		case "executing" : interestplan.state = PlanState.EXECUTING; break;
		case "analysing" : interestplan.state = PlanState.ANALYSING; break;
		case "end" : interestplan.state = PlanState.END; break;
		default : interestplan.state = PlanState.DEFAULT;
		}
		
		interestplan.allTestcasedatas = new HashMap<Integer, String>();
		JSONObject alltestcasedatasob = (JSONObject)interestplanob.get("allTestcasedatas");
		for (Object key : alltestcasedatasob.keySet()) {
			interestplan.allTestcasedatas.put(Integer.parseInt(key.toString()), (String)alltestcasedatasob.get(key));
		}
		
		interestplan.allAnalysistimes = new HashMap<Integer, Double>();
		JSONObject allanalysistimesob = (JSONObject)interestplanob.get("allAnalysistimes");
		for (Object key : allanalysistimesob.keySet()) {
			interestplan.allAnalysistimes.put(Integer.parseInt(key.toString()), (Double)allanalysistimesob.get(key));
		}
		
		Map<String, InterestValue> enumInterest = null;
		if (interestplanob.containsKey("interestmap")) {
			JSONArray interestmap = (JSONArray)interestplanob.get("interestmap");
			enumInterest = new HashMap<String, InterestValue>();
			for (int i = 0; i < interestmap.size(); i++) {
				JSONObject value = (JSONObject)interestmap.get(i);
				InterestValue interestvalue = translatorJson2InterestValue(value);
				enumInterest.put(interestvalue.name, interestvalue);
			}
		}
		
		interestplan.allScenes = new HashSet<ITScene>();
		JSONArray scenearrays = (JSONArray)interestplanob.get("allScenes");
		for (int j = 0; j < scenearrays.size(); j++) {
			JSONObject sceneob = (JSONObject)scenearrays.get(j);
			ITScene scene = new ITScene();
			interestplan.allScenes.add(scene);
			scene.discoverit = ((Long)sceneob.get("discoverit")).intValue();
			scene.branchids = (String)sceneob.get("branchids");
			scene.changebranchids = (String)sceneob.get("changebranchids");
			if (sceneob.containsKey("discovertag")) {
				scene.discovertag = new HashMap<Object, Scene>();
				JSONObject tagsob = ((JSONObject)sceneob.get("discovertag"));
				for (Object key : tagsob.keySet()) {
					JSONObject innersceneob = (JSONObject)tagsob.get(key);
					Scene innerscene = new Scene();
					if (innersceneob.containsKey("interests")) {
						JSONArray interestob = (JSONArray)innersceneob.get("interests");
						innerscene.interests = getInterestValueArrayFromEnum(interestob, enumInterest);
					}
					if (innersceneob.containsKey("preinterests")) {
						JSONArray preinterestob = (JSONArray)innersceneob.get("preinterests");
						innerscene.preinterests = getInterestValueArrayFromEnum(preinterestob, enumInterest);
					}
					innerscene.branchids = scene.branchids;
					innerscene.changebranchids = scene.changebranchids;
					scene.discovertag.put(key, innerscene);
				}
			}
			scene.covered = ((String)sceneob.get("covered")).equals("true");
			if (sceneob.containsKey("interests")) {
				JSONArray interestob = (JSONArray)sceneob.get("interests");
				scene.interests = getInterestValueArrayFromEnum(interestob, enumInterest);
			}
			if (sceneob.containsKey("preinterests")) {
				JSONArray preinterestob = (JSONArray)sceneob.get("preinterests");
				scene.preinterests = getInterestValueArrayFromEnum(preinterestob, enumInterest);
			}

		}
		return interestplan;
	}
	
	public static CoverData getFirstExeCoverData(String filename) {
		CoverData coverdata = null;
		File file = new File(filename);
		if (file.exists()) {
			coverdata = getCoverData(filename);
		} else {
			coverdata = new CoverData();
			coverdata.currentit = 0;
			coverdata.coveredTags = new HashSet<String>();
			File folder = new File(Main.firstcasesloc);
			for (File locfile : folder.listFiles()) {
				coverdata.coveredTags = readloclogs(coverdata.coveredTags, locfile);
			}
			setCoverData(filename, coverdata);
		}
		return coverdata;
	}
	
	public static void setCoverData(String filename, CoverData coverdata) {
		JSONObject coverdataob = new JSONObject();
		coverdataob.put("currentit", coverdata.currentit);
		JSONArray coveredTagarrayob = new JSONArray();
		for (String tag : coverdata.coveredTags) {
			coveredTagarrayob.add(tag);
		}
		coverdataob.put("coveredTags", coveredTagarrayob);
		saveJsonFile(coverdataob, filename, false);
	}
	
	public static CoverData getCoverData(String filename) {
		CoverData coverdata = new CoverData();
		coverdata.coveredTags = new HashSet<String>();
		if (new File(filename).exists()) {
			JSONObject interestcoverdataob = getJsonObject(filename);
			
			if (interestcoverdataob.containsKey("coveredTags")) {
				JSONArray tagarrayob = (JSONArray)interestcoverdataob.get("coveredTags");
				for (Object tag : tagarrayob) {
					coverdata.coveredTags.add((String)tag);
				}
			}
			if (interestcoverdataob.containsKey("currentit")) {
				coverdata.currentit = ((Long)interestcoverdataob.get("currentit")).intValue();
			}
		}
		return coverdata;
	}
	
	
	
	public static CoverData getInterestAndFirstCoverData() {
		CoverData coverdata = null;
		File file = new File(Main.interestcoverdata);
		if (file.exists()) {
			coverdata = getCoverData(Main.interestcoverdata);
		}
		if (null == coverdata){
			coverdata = getFirstExeCoverData(Main.firstcasecoverdata);
			File folder = new File(Main.interestcaseloc);
			for (File locfile : folder.listFiles()) {
				coverdata.coveredTags = readloclogs(coverdata.coveredTags, locfile);
			}
		}
		CoverData firstCover = getCoverData(Main.firstcasecoverdata);
		coverdata.merge(firstCover);
		return coverdata;
	}
	
	public static Set<String> readloclogs(Set<String> locset, File locfile) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(locfile), "UTF-8"));
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
					locset.add(logid);
				}
			}
			br.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return locset;
	}
	
	public static void setPreferenceAdaptData(String filename, PreferenceAdaptData data) {
		JSONObject dataob = new JSONObject();
		dataob.put("key", data.key);
		dataob.put("adapted", data.adapted);
		dataob.put("type", data.type.toString());

		if (null != data.presteps && !data.presteps.isEmpty()) {
			JSONObject prearray = new JSONObject();
			for (int i = 0; i < data.presteps.size(); i++) {
				TestOperation op = data.presteps.get(i);
				prearray.put(i, getTestOperationOb(op));
			}
			dataob.put("presteps", prearray);
		}
		if (null != data.consteps && !data.consteps.isEmpty()) {
			JSONObject conarray = new JSONObject();
			for (int i = 0; i < data.consteps.size(); i++) {
				TestOperation op = data.consteps.get(i);
				conarray.put(i, getTestOperationOb(op));
			}
			dataob.put("consteps", conarray);
		}
		if (null != data.valuestepmap && !data.valuestepmap.isEmpty()) {
			JSONObject valuearray = new JSONObject();
			for (String key : data.valuestepmap.keySet()) {
				ArrayList<TestOperation> oplist = data.valuestepmap.get(key);
				JSONObject valueinnerarray = new JSONObject();
				for (int i = 0; i < oplist.size(); i++) {
					valueinnerarray.put(i, getTestOperationOb(oplist.get(i)));
				}
				valuearray.put(key, valueinnerarray);
			}
			dataob.put("valuestepmap", valuearray);
		}
		saveJsonFile(dataob, filename, true);
	}
	
	public static PreferenceAdaptData getPreferenceAdaptData(String filename) {
		JSONObject dataob = getJsonObject(filename);
		PreferenceAdaptData data = new PreferenceAdaptData();
		if (dataob.containsKey("key")) {
			data.key = (String)dataob.get("key");
		}
		if (dataob.containsKey("adapted")) {
			data.adapted = (Boolean)dataob.get("adapted");
		}
		if (dataob.containsKey("type")) {
			data.type = ResultType.valueOf((String)dataob.get("type"));
		}
		if (dataob.containsKey("presteps")) {
			data.presteps = new ArrayList<TestOperation>();
			JSONObject prearray = (JSONObject)dataob.get("presteps");
			for (Object key : prearray.keySet()) {
				data.presteps.add(Integer.parseInt(key.toString()), getTestOperation((JSONObject)prearray.get(key)));
			}
		}
		if (dataob.containsKey("consteps")) {
			data.consteps = new ArrayList<TestOperation>();
			JSONObject conarray = (JSONObject)dataob.get("consteps");
			for (Object key : conarray.keySet()) {
				data.consteps.add(Integer.parseInt(key.toString()), getTestOperation((JSONObject)conarray.get(key)));
			}
		}
		if (dataob.containsKey("valuestepmap")) {
			data.valuestepmap = new HashMap<String, ArrayList<TestOperation>>();
			JSONObject valuemap = (JSONObject)dataob.get("valuestepmap");
			for (Object key : valuemap.keySet()) {
				ArrayList<TestOperation> oparray = new ArrayList<TestOperation>();
				JSONObject valueinnerarray = (JSONObject)valuemap.get(key);
				for (Object innerkey : valueinnerarray.keySet()) {
					oparray.add(Integer.parseInt(innerkey.toString()), getTestOperation((JSONObject)valueinnerarray.get(innerkey)));
				}
				data.valuestepmap.put(key.toString(), oparray);
			}
		}
		return data;
	}
	
	private static JSONObject getTestOperationOb(TestOperation operation) {
		JSONObject operationob = new JSONObject();
		if (null != operation.type) {
			operationob.put("type", operation.type.name());
		}
		if (null != operation.operationType) {
			operationob.put("operationType", operation.operationType);
		}
		if (null != operation.resourceId) {
			operationob.put("resourceId", operation.resourceId);
		}
		if (null != operation.className) {
			operationob.put("className", operation.className);
		}
		if (null != operation.contentDesc) {
			operationob.put("contentDesc", operation.contentDesc);
		}
		if (null != operation.direction) {
			operationob.put("direction", operation.direction);
		}
		if (0 != operation.fromx) {
			operationob.put("fromx", operation.fromx);
		}
		if (0 != operation.fromy) {
			operationob.put("fromy", operation.fromy);
		}
		if (0 != operation.tox) {
			operationob.put("tox", operation.tox);
		}
		if (0 != operation.toy) {
			operationob.put("toy", operation.toy);
		}
		if (null != operation.text) {
			operationob.put("text", operation.text);
		}
		operationob.put("instance", operation.instance);
		return operationob;
	}
	
	private static TestOperation getTestOperation(JSONObject operationob) {
		TestOperation operation = new TestOperation();
		if (operationob.containsKey("type")) {
			operation.type = TestOperationType.valueOf((String)operationob.get("type"));
		}
		if (operationob.containsKey("operationType")) {
			operation.operationType = (String)operationob.get("operationType");
		}
		if (operationob.containsKey("resourceId")) {
			operation.resourceId = (String)operationob.get("resourceId");
		}
		if (operationob.containsKey("className")) {
			operation.className = (String)operationob.get("className");
		}
		if (operationob.containsKey("contentDesc")) {
			operation.contentDesc = (String)operationob.get("contentDesc");
		}
		if (operationob.containsKey("direction")) {
			operation.direction = (String)operationob.get("direction");
		}
		if (operationob.containsKey("text")) {
			operation.text = (String)operationob.get("text");
		}
		if (operationob.containsKey("instance")) {
			operation.instance = ((Long)operationob.get("instance")).intValue();
		}
		if (operationob.containsKey("fromx")) {
			operation.fromx = (Double)operationob.get("fromx");
		}
		if (operationob.containsKey("fromy")) {
			operation.fromy = (Double)operationob.get("fromy");
		}
		if (operationob.containsKey("tox")) {
			operation.tox = (Double)operationob.get("tox");
		}
		if (operationob.containsKey("toy")) {
			operation.toy = (Double)operationob.get("toy");
		}
		return operation;
	}
	
	private static ArrayList<InterestValue> getInterestValueArrayFromEnum(JSONArray interestarray, Map<String, InterestValue> enumInterest) {
		ArrayList<InterestValue> interestlist = new ArrayList<InterestValue>();
		for (int k = 0; k < interestarray.size(); k++) {
			JSONObject interestvalue = (JSONObject)interestarray.get(k);
			InterestValue value = new InterestValue();
			value.name = (String)interestvalue.get("name");
			if (interestvalue.containsKey("value")) {
				value.value = (String)interestvalue.get("value");
			}
			InterestValue enumvalue = enumInterest.get(value.name);
			if (null == enumvalue) {
				System.out.println();
			}
			value.preferencesteps = enumvalue.preferencesteps;
			value.generaltype = enumvalue.generaltype;
			value.innertype = enumvalue.innertype;
			value.index = enumvalue.index;
			value.type = enumvalue.type;
			value.activityname = enumvalue.activityname;
			value.catalog = enumvalue.catalog;
			interestlist.add(value);
		}
		return interestlist;
	}
	
	public static void setinterestplanAdapt(InterestPlan interestplan, String filename) {
		JSONObject interestplanob = new JSONObject();
		interestplanob.put("currentit", interestplan.currentit);
		switch(interestplan.state) {
		case EXECUTING : interestplanob.put("state", "executing"); break;
		case ANALYSING : interestplanob.put("state", "analysing"); break;
		case END : interestplanob.put("state", "end");break;
		default : interestplanob.put("state", "default");
		}
		
		JSONObject alltestcasedatasob = new JSONObject();
		for (Integer key : interestplan.allTestcasedatas.keySet()) {
			alltestcasedatasob.put(key, interestplan.allTestcasedatas.get(key));
		}
		interestplanob.put("allTestcasedatas", alltestcasedatasob);
		
		JSONObject allanalysistimesob = new JSONObject();
		for (Integer key : interestplan.allAnalysistimes.keySet()) {
			allanalysistimesob.put(key, interestplan.allAnalysistimes.get(key));
		}
		interestplanob.put("allAnalysistimes", allanalysistimesob);
		
		Map<String, InterestValue> enumInterest = new HashMap<String, InterestValue>();
		JSONArray allscenesarray = new JSONArray();
		for (ITScene scene: interestplan.allScenes) {
			JSONObject sceneob = new JSONObject();
			sceneob.put("branchids", scene.branchids);
			sceneob.put("changebranchids", scene.changebranchids);
			sceneob.put("discoverit", scene.discoverit);
			sceneob.put("covered", scene.covered?"true":"false");
			JSONObject discoverTagob = new JSONObject();
			for (Object tag : scene.discovertag.keySet()) {
				Scene innerscene = scene.discovertag.get(tag);
				JSONObject innersceneob = new JSONObject();
				JSONArray interests = new JSONArray();
				for (InterestValue value : innerscene.interests) {
					JSONObject valueob = new JSONObject();
					valueob.put("name", value.name);
					if (!enumInterest.containsKey(value.name)) {
						enumInterest.put(value.name, value);
					}
					valueob.put("value", value.value);
					interests.add(valueob);
				}
				innersceneob.put("interests", interests);
				if (null != innerscene.preinterests && !innerscene.preinterests.isEmpty()) {
					JSONArray preinterests = new JSONArray();
					for (InterestValue value : innerscene.preinterests) {
						JSONObject valueob = new JSONObject();
						valueob.put("name", value.name);
						if (!enumInterest.containsKey(value.name)) {
							enumInterest.put(value.name, value);
						}
						valueob.put("value", value.value);

						preinterests.add(valueob);
					}
					innersceneob.put("preinterests", preinterests);
				}
				discoverTagob.put(tag, innersceneob);
			}
			sceneob.put("discovertag", discoverTagob);

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
			allscenesarray.add(sceneob);
		}
		interestplanob.put("allScenes", allscenesarray);
		
		JSONArray interestmap = new JSONArray();
		for (InterestValue value : enumInterest.values()) {
			JSONObject valueob = translatorInterestValue2Json(value);
			interestmap.add(valueob);
		}
		interestplanob.put("interestmap", interestmap);
		saveJsonFile(interestplanob ,filename, false);
	}
}
