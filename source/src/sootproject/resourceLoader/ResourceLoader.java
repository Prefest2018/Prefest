package sootproject.resourceLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.AbstractAttribute;

import GUI.Main;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MyPreference;
import sootproject.myexpression.ResultType;
import tools.Logger;

public class ResourceLoader {
	private File resfolder = null;
	private File sourcefolder = null;
	private Map<String, String> stringMap = null;
	private Map<String, Long> idMap = null;
	private Map<String, List<String>> arrayMap = null;
	private Map<String, MyInterest> interestMap = null;
	private Map<String, String> preferencexmlMap = null;
	private Map<String, String> preferencesuperMap = null;
	private Map<Integer, String> preference2activitymap = null;
	private static final List<String> booleanList = new ArrayList<String>();
	private static final List<String> stringList = new ArrayList<String>();
	private static final List<String> numberList = new ArrayList<String>();
	private static HashMap<String, String> stringEntryMap = new HashMap<String, String>();
	private static HashMap<String, String> numberEntryMap = new HashMap<String, String>();
	private Map<String, List<PreferenceTreeNode>> filepreferencemap = null;
	private Map<String, String> preferencefilename2activitymap = null;


	static {
		booleanList.add("0");
		booleanList.add("1");
		stringList.add("");
		stringList.add("random");
//		numberList.add("-1");
		numberList.add("0");
		numberList.add("");
		numberList.add("1");
		numberList.add(Integer.MAX_VALUE + "");
		for (String str : stringList) {
			stringEntryMap.put(str, str);
		}
		for (String num : numberList) {
			numberEntryMap.put(num, num);
		}

	}
	public ResourceLoader(File resfolder, File sourcefolder, Map<String, String> preferencesupermap, Map<Integer, String> preference2activitymap) {
		this.resfolder = resfolder;
		this.sourcefolder = sourcefolder;
		this.preferencesuperMap = preferencesupermap;
		this.preference2activitymap = preference2activitymap;
		this.preferencefilename2activitymap = new HashMap<String, String>();
		this.preferencesuperMap.put("CheckBoxPreference", "CheckBoxPreference");
		this.preferencesuperMap.put("SwitchPreference", "SwitchPreference");
		this.preferencesuperMap.put("ListPreference", "ListPreference");
		this.preferencesuperMap.put("Preference", "Preference");
		this.preferencesuperMap.put("PreferenceCategory", "PreferenceCategory");
		this.preferencesuperMap.put("PreferenceScreen", "PreferenceScreen");
		this.preferencesuperMap.put("SwitchPreferenceCompat", "SwitchPreferenceCompat");
		this.preferencesuperMap.put("RingtonePreference", "ListPreference");
		this.preferencesuperMap.put("EditTextPreference", "EditTextPreference");
		this.preferencesuperMap.put("IntPreference", "IntPreference");
		this.preferencesuperMap.put("DialogPreference", "DialogPreference");
		this.preferencesuperMap.put("MultiSelectListPreference", "MultiSelectListPreference");
		this.stringMap = new HashMap<String, String>();
		this.arrayMap = new HashMap<String, List<String>>();
		this.interestMap = new HashMap<String, MyInterest>();
		this.idMap = new HashMap<String, Long>();
		this.preferencemap = new HashMap<String, Integer>();
	}
	
	public Map<String, MyInterest> getResourcePreferenceInfo(Map<String, Integer> preferencexmlmap) {
		fixRpoint(preferencexmlmap);

		for (File nowFolder : resfolder.listFiles()) {
			if (nowFolder.getName().equals("values")) {
				for (File innerFile : nowFolder.listFiles()) {
					if (innerFile.getName().endsWith(".xml")) {
						stringMap = readStringXML(innerFile);
						arrayMap = readArrayXML(innerFile);
					}
				}
			}
		}
		
		PreferenceTreeNode.initcount();
		filepreferencemap = new HashMap<String, List<PreferenceTreeNode>>();
		List<PreferenceTreeNode> headsnodes = new ArrayList<PreferenceTreeNode>();
		filepreferencemap.put("headers", headsnodes);
		for (File nowFolder : resfolder.listFiles()) {
			if (nowFolder.getName().equals("xml") || nowFolder.getName().startsWith("xml-")) {
				for (File xmlfile :nowFolder.listFiles()) {
					readPreferenceXML(xmlfile, filepreferencemap);
				}
			}
		}


		for (PreferenceTreeNode headers : headsnodes) {
			List<PreferenceTreeNode> children = filepreferencemap.get(preferencexmlMap.get(headers.fragment));
			if (children != null) {
				headers.setChildren(children);
			}

		}
		for (String key : filepreferencemap.keySet()) {
			List<PreferenceTreeNode> list = filepreferencemap.get(key);
			while (!list.isEmpty()) {
				List<PreferenceTreeNode > nextlist = new ArrayList<PreferenceTreeNode>();
				for (PreferenceTreeNode node : list) {
					node.initTitles();
					if (null != node.childnodes)nextlist.addAll(node.childnodes);
				}
				list = nextlist;
			}

		}
		MyPreference.initPreferenceMap(interestMap);
		
//		failurepreferences = new ArrayList<PreferenceTreeNode>();
//					failurepreferences.add(node);
//					failurepreferences.add(node);
//		
		return interestMap;
	}
	
	private void fixRpoint(Map<String, Integer> preferencexmltempmap) {
		Set<File> rlists = new HashSet<File>();
		preferencexmlMap = new HashMap<String, String>();
		Stack<File> folders = new Stack<File>();
		folders.push(sourcefolder);
		while (!folders.isEmpty()) {
			File nowFile = folders.pop();
			for (File innerfile : nowFile.listFiles()) {
				if (innerfile.isDirectory()) {
					folders.push(innerfile);
				} else if (innerfile.getName().equals("R.java")) {
					rlists.add(innerfile);
				} else if (innerfile.getName().startsWith("R$") && innerfile.getName().endsWith(".java")) {
					rlists.add(innerfile);
				}
			}
		}
		
		Map<Integer, String> preferencexmltempmapre = new HashMap<Integer, String>();
		for (String key : preferencexmltempmap.keySet()) {
			preferencexmltempmapre.put(preferencexmltempmap.get(key), key);
		}
		
		for (File rfile: rlists) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(rfile), "UTF-8"));
				String content = null;
				while ((content = br.readLine()) != null) {
					if (!content.contains("=")) {
						continue;
					}
					String[] list = content.split("=");
					if (list.length < 2) {
						continue;
					}
					String leftstr = list[0].replace("public static final int ", "").trim();
					String rightnumstr = list[1].replace(";", "").trim();
					int rightnum = -1;
					try {
						if (rightnumstr.contains("0x")) {
							rightnum = Integer.parseInt(rightnumstr.replace("0x", ""), 16);
						} else {
							rightnum = Integer.parseInt(rightnumstr);
						}
					} catch (NumberFormatException e1) {
						continue;
					}
					
					if (preferencexmltempmapre.containsKey(rightnum)) {
						preferencexmlMap.put(preferencexmltempmapre.get(rightnum), leftstr + ".xml");
					}
					if (preference2activitymap.containsKey(rightnum)) {
						preferencefilename2activitymap.put(leftstr + ".xml", preference2activitymap.get(rightnum));
					}
					
					idMap.put(leftstr, (long)rightnum);
//					
//					String content2 = content;
//					String content3 = content;
//						String hint = " = " + preferencexmltempmap.get(classname) + ";";
//						String hint2 = " = " + preferencexmltempmap.get(classname) + ";";
//							content = content.replace(hint, "").replace("public static final int ", "").trim() + ".xml";
//							preferencexmlMap.put(classname, content);
//							break;
//						String hint = " = " + id + ";";
//							content2 = content2.replace(hint, "").replace("public static final int ", "").trim() + ".xml";
//							preferencefilename2activitymap.put(content2, preference2activitymap.get(id));
//							break;
//							long strvalue = -1;
//								strvalue = Long.parseLong(valuetemp.replace("0x", ""), 16);
//								strvalue = Long.parseLong(valuetemp);
//							
//							idMap.put(strname, strvalue);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	private void readPreferenceXML(File xmlFile, Map<String, List<PreferenceTreeNode>> filepreferencemap) {
		Element initalElement = readXML(xmlFile);
		String filename =xmlFile.getName();
		String activityname = preferencefilename2activitymap.get(filename);


		if (initalElement.getName().equals("PreferenceScreen") || initalElement.getName().equals("android.support.v7.preference.PreferenceScreen") || initalElement.getName().equals("androidx.preference.PreferenceScreen")) {
			filepreferencemap.put(filename, new ArrayList<PreferenceTreeNode>());
//			System.out.println("------current xml file-----" + filename);
			Logger.log(("------current xml file-----" + filename));
			for (Element childe : (List<Element>)initalElement.elements()) {
				readPreferenceciterat(null, childe, filename, filepreferencemap, activityname, null);
			}
		} else if (initalElement.getName().equals("preference-headers")) {
			for (Element e : (List<Element>)initalElement.elements()) {
				if (e.getName().equals("header")) {
					String title = e.attributeValue("title");
					title = getOriginStr(title);
					String fragment = e.attributeValue("fragment");
					PreferenceTreeNode headnode = new PreferenceTreeNode("header", title, filename, activityname, fragment, true);
					List<PreferenceTreeNode> lists = filepreferencemap.get("headers");
					lists.add(headnode);
				}
			}
		}
		return;
	}
	private Map<String, Integer> preferencemap = null;
	public void printpreferencemap() {
		if (null != preferencemap) {
			for (String preferencekind:preferencemap.keySet()) {
				String result = preferencekind + " number is: " + preferencemap.get(preferencekind);
				System.out.println(result);
				Logger.log(result);
			}
		}
	}
	private void readPreferenceciterat(PreferenceTreeNode parentnode, Element e, String filename, Map<String, List<PreferenceTreeNode>> filepreferencemap, String activityname, String catlogname) {
		String preferencetype = null;
		String title = null;
		String key = null;
		Map<String, Object> extraDatas = new HashMap<String, Object>();
		boolean shouldexplore = false;
		Map<String, String> entryvalues = null;
		String defaultvalue = null;
		String newcatlog = null;
		boolean shouldadd = true;
		MyPreference myp = null;
		String adjustname = e.getName();
		if (adjustname.contains(".")) {
			String[] templist = adjustname.split("\\.");
			adjustname = templist[templist.length - 1];
		}
		String classkey = preferencesuperMap.get(adjustname);
		if (null == classkey) {
			return;
		}
		if (preferencemap.containsKey(classkey)) {
			preferencemap.put(classkey, preferencemap.get(classkey) + 1);
		} else {
			preferencemap.put(classkey, 1);
		}
		switch (classkey) {
		case "CheckBoxPreference" : {
			preferencetype = "checkbox";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
			}
			defaultvalue = ("true").equals(defaultvalue)?"1":"0";
			myp = new MyPreference(key, defaultvalue, booleanList, ResultType.BOOLEAN);
			interestMap.put(key, myp);
			break;
		}
		case "SwitchPreference" : {
			preferencetype = "switch";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
			}
			defaultvalue = ("true").equals(defaultvalue)?"1":"0";
			myp = new MyPreference(key, defaultvalue, booleanList, ResultType.BOOLEAN);
			interestMap.put(key, myp);
			break;
		}
		case "SwitchPreferenceCompat" : {
			preferencetype = "switch";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
			}
			defaultvalue = ("true").equals(defaultvalue)?"1":"0";
			myp = new MyPreference(key, defaultvalue, booleanList, ResultType.BOOLEAN);
			interestMap.put(key, myp);
			break;
		}
		case "MultiSelectListPreference" : {
			preferencetype = "multilist";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
			}
			String entryValuesstr = e.attributeValue("entryValues");
			List<String> entryValues = null;
			if (null != entryValuesstr && !entryValuesstr.equals("@null")) {
				entryValues = arrayMap.get(entryValuesstr.replace("@array/", ""));
				for (int i = 0; i < entryValues.size(); i++) {
					entryValues.set(i, getOriginStr(entryValues.get(i)));
				}
			} else {
				entryValues = new ArrayList<String>();
			}

			String entrysstr = e.attributeValue("entries");
			List<String> entries = null;
			if (null != entrysstr && !entrysstr.equals("@null")) {
				entries = arrayMap.get(entrysstr.replace("@array/", ""));
				for (int i = 0; i < entries.size(); i++) {
					entries.set(i, getOriginStr(entries.get(i)));
				}
			} else {
				entries = new ArrayList<String>();
			}
			entryvalues = new HashMap<String, String>();
			for (int i = 0; i < entryValues.size() && i < entries.size(); i++) {
				entryvalues.put(entryValues.get(i), entries.get(i));
			}
			myp = new MyPreference(key, defaultvalue, entryValues, ResultType.DEFAULT);
			interestMap.put(key, myp);
			break;
		}
		case "ListPreference" : {
			preferencetype = "list";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
			}
			String entryValuesstr = e.attributeValue("entryValues");
			List<String> entryValues = null;
			if (null != entryValuesstr && !entryValuesstr.equals("@null")) {
				entryValues = arrayMap.get(entryValuesstr.replace("@array/", ""));
				for (int i = 0; i < entryValues.size(); i++) {
					entryValues.set(i, getOriginStr(entryValues.get(i)));
				}
			} else {
				entryValues = new ArrayList<String>();
			}

			String entrysstr = e.attributeValue("entries");
			List<String> entries = null;
			if (null != entrysstr && !entrysstr.equals("@null")) {
				entries = arrayMap.get(entrysstr.replace("@array/", ""));
				for (int i = 0; i < entries.size(); i++) {
					entries.set(i, getOriginStr(entries.get(i)));
				}
			} else {
				entries = new ArrayList<String>();
			}
			entryvalues = new HashMap<String, String>();
			for (int i = 0; i < entryValues.size() && i < entries.size(); i++) {
				entryvalues.put(entryValues.get(i), entries.get(i));
			}
			myp = new MyPreference(key, defaultvalue, entryValues, ResultType.DEFAULT);
			interestMap.put(key, myp);
			break;
		}
		case "EditTextPreference": {
			preferencetype = "edit";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			ResultType thisresult = null;
			String inputTypestr = e.attributeValue("inputType");
			entryvalues = new HashMap<String, String>();
			if (null == inputTypestr || "text".equals(inputTypestr) || "none".equals(inputTypestr)) {
				thisresult = ResultType.STRING;
				entryvalues = new HashMap<String, String>(stringEntryMap);
			} else if ("number".equals(inputTypestr) || "numberDecimal".equals(inputTypestr) || "integer".equals(inputTypestr) || "numberSigned".equals(inputTypestr)) {
				thisresult = ResultType.INT;
				entryvalues = new HashMap<String, String>(numberEntryMap);
			} else {
				thisresult = ResultType.STRING;
				entryvalues = new HashMap<String, String>(stringEntryMap);
				System.out.println("error: unknown EditText input type: " + inputTypestr);
			}
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue && entryvalues.size() < 3) {
				defaultvalue = getOriginStr(defaultvalue);
				entryvalues.put(defaultvalue, defaultvalue);
			}
			
			myp = new MyPreference(key, defaultvalue, new ArrayList<String>(entryvalues.values()), thisresult);
			interestMap.put(key, myp);
			break;
		}
		case "IntPreference": {
			preferencetype = "edit";
			key = e.attributeValue("key");
			key = getOriginStr(key);
			entryvalues = new HashMap<String, String>(numberEntryMap);
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
				entryvalues.put(defaultvalue, defaultvalue);
			}
			myp = new MyPreference(key, defaultvalue, new ArrayList<String>(entryvalues.values()), ResultType.INT);
			interestMap.put(key, myp);
			break;
		}
		case "DialogPreference" : {
			preferencetype = "dialog";
			key = e.attributeValue("key");
			if (null != key) {
				key = getOriginStr(key);
				if (Main.USESEEKBAR && (adjustname.contains("seekbar") || adjustname.contains("Seekbar") || adjustname.contains("SeekBar") || adjustname.contains("SEEKBAR"))) {
					preferencetype = "seekbar";
					entryvalues = new HashMap<String, String>();
					defaultvalue = e.attributeValue("defaultValue");
					if (null != defaultvalue) {
						defaultvalue = getOriginStr(defaultvalue);
						entryvalues.put(defaultvalue, defaultvalue);
					}
					for (Object child : e.attributes()) {
						AbstractAttribute childe = (AbstractAttribute)child;
						String childname = childe.getName();
						if ((childname.contains("min") || childname.contains("Min") || childname.contains("MIN"))) {
							entryvalues.put(childe.getText(), childe.getText());
							extraDatas.put("min", Float.parseFloat(childe.getText()));
						} else if (childname.contains("max") || childname.contains("Max") || childname.contains("MAX")) {
							entryvalues.put(childe.getText(), childe.getText());
							extraDatas.put("max", Float.parseFloat(childe.getText()));
						}
					}
					myp = new MyPreference(key, defaultvalue, new ArrayList<String>(entryvalues.values()), ResultType.INT);
				} else {
					myp = new MyPreference(key, defaultvalue, new ArrayList<String>(), ResultType.DEFAULT);
					myp.requireAdapt = true;
				}
				interestMap.put(key, myp);
			}
			break;
		}
		case "Preference" : {
			key = e.attributeValue("key");
			if (null != key) {
				key = getOriginStr(key);
			}
			
			if (Main.USESEEKBAR && (adjustname.contains("seekbar") || adjustname.contains("Seekbar") || adjustname.contains("SeekBar") || adjustname.contains("SEEKBAR"))) {
				preferencetype = "seekbar";
				entryvalues = new HashMap<String, String>();
				defaultvalue = e.attributeValue("defaultValue");
				if (null != defaultvalue) {
					defaultvalue = getOriginStr(defaultvalue);
					entryvalues.put(defaultvalue, defaultvalue);
				}
				for (Object child : e.attributes()) {
					AbstractAttribute childe = (AbstractAttribute)child;
					String childname = childe.getName();
					if ((childname.contains("min") || childname.contains("Min") || childname.contains("MIN"))) {
						entryvalues.put(childe.getText(), childe.getText());
						extraDatas.put("min", Float.parseFloat(childe.getText()));
					} else if (childname.contains("max") || childname.contains("Max") || childname.contains("MAX")) {
						entryvalues.put(childe.getText(), childe.getText());
						extraDatas.put("max", Float.parseFloat(childe.getText()));
					}
					
				}
				myp = new MyPreference(key, defaultvalue, new ArrayList<String>(entryvalues.values()), ResultType.INT);
				interestMap.put(key, myp);
				break;
			}
			
			
			preferencetype = "preference";
			if ("preference".equalsIgnoreCase(e.getName()) || e.getName().endsWith(".Preference")) {
				shouldexplore = true;
			}
			
			String entryValuesstr = e.attributeValue("entryValues");
			List<String> entryValues = null;
			if (null != entryValuesstr && !entryValuesstr.equals("@null")) {
				entryValues = arrayMap.get(entryValuesstr.replace("@array/", ""));
				for (int i = 0; i < entryValues.size(); i++) {
					entryValues.set(i, getOriginStr(entryValues.get(i)));
				}
			} else {
				entryValues = new ArrayList<String>();
			}

			String entrysstr = e.attributeValue("entries");
			List<String> entries = null;
			if (null != entrysstr && !entrysstr.equals("@null")) {
				entries = arrayMap.get(entrysstr.replace("@array/", ""));
				for (int i = 0; i < entries.size(); i++) {
					entries.set(i, getOriginStr(entries.get(i)));
				}
			} else {
				entries = new ArrayList<String>();
			}
			entryvalues = new HashMap<String, String>();
			for (int i = 0; i < entryValues.size(); i++) {
				entryvalues.put(entryValues.get(i), entries.get(i));
			}
			if (!entryvalues.isEmpty()) {
				preferencetype = "list";
			} else {
				preferencetype = "other";
			}
			defaultvalue = e.attributeValue("defaultValue");
			if (null != defaultvalue) {
				defaultvalue = getOriginStr(defaultvalue);
			}
			myp = new MyPreference(key, defaultvalue, entryValues, ResultType.DEFAULT);
			if ("other".equals(preferencetype)) {
				myp.requireAdapt = true;
			}
			interestMap.put(key, myp);
			
			break;
		}
		case "PreferenceCategory" : {
			newcatlog = e.attributeValue("title");
			if (null != newcatlog) {
				newcatlog = getOriginStr(newcatlog);
			}
			shouldadd = false;
			break;
		}
		case "PreferenceScreen" : {
			preferencetype = "preferencescreen"; 
			if (e.elements().isEmpty()) {
				shouldexplore = true;
			}
			break;
		}
		default : {
			shouldadd = false;
		}
		}
		PreferenceTreeNode newNode = null;
		if (shouldadd) {
//			System.out.println(key);
			if (null != key) {
				Logger.log(key);
			}
			title = e.attributeValue("title");
			title = getOriginStr(title);
			if (null == title || "".equals(title)) {
				for (int i = 0; i < e.attributeCount(); i++) {
					Attribute a = e.attribute(i);
					if(a.getName().contains("summary")) {
						title = a.getValue();
						title = getOriginStr(title);
					}
				}
			}
			String dependency = e.attributeValue("dependency");
			dependency = getOriginStr(dependency);
			newNode = new PreferenceTreeNode(preferencetype, title, key, filename, activityname, entryvalues, defaultvalue, catlogname, dependency, shouldexplore, extraDatas);
			if (myp != null) {
				myp.setPreferencenode(newNode);
			}
			if (null == parentnode) {
				List<PreferenceTreeNode> treelist = filepreferencemap.get(filename);
				treelist.add(newNode);
			} else {
				parentnode.addChild(newNode);
			}
		}
		
		for (Element childe : (List<Element>)e.elements()) {
			readPreferenceciterat(shouldadd?newNode:parentnode, childe, filename, filepreferencemap, activityname, newcatlog);
		}
	}
	
	private String getOriginStr(String str) {
		if (null == str) {
			return null;
		}
		if (str.startsWith("@string/")) {
			return stringMap.get(str.replace("@string/", ""));
		} else {
			return str;
		}
	}
	
	
	private Map<String, String> readStringXML(File xmlFile) {
		Element initalElement = readXML(xmlFile);
		for (Element e : (List<Element>)initalElement.elements("string")) {
			String name = e.attribute("name").getText();
			String content = e.getText();
			if (!stringMap.containsKey(name)) {
				stringMap.put(name, content);
			}
		}
		return stringMap;
	}
	
	private Map<String, List<String>> readArrayXML(File xmlFile) {
		Element initalElement = readXML(xmlFile);
		for (Element e : (List<Element>)initalElement.elements("array")) {
			String name = e.attribute("name").getText();
			List<Element> content = e.elements("item");
			List<String> strlist = new ArrayList<String>();
			for (Element innere : content) {
				strlist.add(innere.getText());
			}
			arrayMap.put(name, strlist);
		}
		for (Element e : (List<Element>)initalElement.elements("string-array")) {
			String name = e.attribute("name").getText();
			List<Element> content = e.elements("item");
			List<String> strlist = new ArrayList<String>();
			for (Element innere : content) {
				strlist.add(innere.getText());
			}
			arrayMap.put(name, strlist);
		}
		return arrayMap;
	}
	
	public Map<Long, String> getStringIDmap() {
		Map<Long, String> stringidmap = new HashMap<Long, String>();
		for (String strname : this.stringMap.keySet()) {
			Long idins = this.idMap.get(strname);
			if (null != idins) {
				stringidmap.put(idins, this.stringMap.get(strname));
			}

		}
		return stringidmap;
	}
	
	
	
	private Element readXML(File xmlFile) {
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(xmlFile);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		Element root = document.getRootElement();
		return root;
	}

	public Map<String, List<PreferenceTreeNode>> getPrefereneceTree() {
		return this.filepreferencemap;
	}

	public Map<String, String> getPreferencefilename2activitymap() {
		return preferencefilename2activitymap;
	}

}
