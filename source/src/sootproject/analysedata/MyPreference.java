package sootproject.analysedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.InterestValue;
import data.PreferenceAdaptData;
import sootproject.data.MyNode;
import sootproject.myexpression.ResultType;
import sootproject.resourceLoader.PreferenceTreeNode;


public class MyPreference extends MyInterest{
	private String typestr = null;
	private static Map<String, MyInterest> allInterestMap = null;
	public boolean requireAdapt = false;
	public MyPreference(String name, String typestr) {
		super(name);
		setResultType(typestr);
		this.possibleValues = new ArrayList<String>();
		this.setUnknown(true);
	}
	public static void initPreferenceMap(Map<String, MyInterest> allInterestMap) {
		MyPreference.allInterestMap = allInterestMap;
	}
	protected PreferenceTreeNode preferencenode = null;
	
	public MyPreference(String name, String defaultValue, List<String> possibleValues, ResultType type) {
		super(name);
		this.defaultValue = defaultValue;
		this.possibleValues = possibleValues;
		if (!this.possibleValues.isEmpty()) {
			this.setUnknown(false);
		} else {
			this.setUnknown(true);
		}
		this.type = type;
	}
	
	@Override
	public ResultType getResultType() {
		return this.type;
	}
	
	public void setResultType(String typestr) {
		this.typestr = typestr;
		if ("getString".equals(typestr)) {
			this.type = ResultType.STRING;
		} else if ("getInt".equals(typestr) || "getLong".equals(typestr) || "getBoolean".equals(typestr)) {
			this.type = ResultType.INT;
		} else if ("getFloat".equals(typestr)) {
			this.type = ResultType.FLOAT;
		} else if ("getAll".equals(typestr)) {
			//TODO
		} else if ("getStringSet".equals(typestr)) {
			//TODO
		}
	}
	
	@Override
	public InterestValue getInterestValueEnum() {
		String type = null;
		if (null != typestr) {
			switch (typestr) {
			case "getString" : type = "string";break;
			case "getInt" : type = "int";break;
			case "getLong" : type = "long";break;
			case "getBoolean" : {
				type = "boolean";
				break;
			}
			case "getFloat" : type = "float";break;
			}
		} else if (this.type != ResultType.DEFAULT) {
			switch (this.type) {
			case BOOLEAN : {
				type = "boolean";
				break;
			}
			}
		}
		InterestValue interestvalue = new InterestValue("preference", null, type, name, null, -1, null);
		if (null != preferencenode) {
			List<String> steps = preferencenode.getTitles();
			interestvalue.preferencesteps = steps;
			interestvalue.innertype = preferencenode.preferencetype; 
			interestvalue.index = preferencenode.index;
			interestvalue.activityname = preferencenode.activityname;
			interestvalue.catalog = preferencenode.catlog;
			if (preferencenode.preferencetype.equals("list")) {
				interestvalue.value = preferencenode.getEntryLabel(interestvalue.value);
			}
			if (preferencenode.preferencetype.equals("seekbar")) {
				interestvalue.extradatas = preferencenode.extrDatas;
			}
			if (null != preferencenode.dependency && null != allInterestMap) {
				MyInterest dependencyPreference = allInterestMap.get(preferencenode.dependency);
				if (null != dependencyPreference) {
					interestvalue.dependency = dependencyPreference.getInterestValue("true");
				}
			}
		}
		return null;
	}
	
	private Map<String, InterestValue> valuemapcache = new HashMap<String, InterestValue>();
	public InterestValue getInterestValue(String value) {
		if (valuemapcache.containsKey(value)) {
			return valuemapcache.get(value);
		}
		String type = null;
		if (null != typestr) {
			switch (typestr) {
			case "getString" : type = "string";break;
			case "getInt" : type = "int";break;
			case "getLong" : type = "long";break;
			case "getBoolean" : {
				type = "boolean";
				if (value.equals("true")) {value = "1";}else if (value.equals("false")) {value = "0";} 
				break;
			}
			case "getFloat" : type = "float";break;
			}
		} else if (this.type != ResultType.DEFAULT) {
			switch (this.type) {
			case BOOLEAN : {
				type = "boolean";
				if (value.equals("true")) {value = "1";}else if (value.equals("false")) {value = "0";} 
				break;
			}
			}
		}
		InterestValue interestvalue = new InterestValue("preference", null, type, name, value, -1, null);
		if (null != preferencenode) {
			List<String> steps = preferencenode.getTitles();
			interestvalue.preferencesteps = steps;
			interestvalue.innertype = preferencenode.preferencetype; 
			interestvalue.index = preferencenode.index;
			interestvalue.activityname = preferencenode.activityname;
			interestvalue.catalog = preferencenode.catlog;
			if (preferencenode.preferencetype.equals("list")) {
				interestvalue.value = preferencenode.getEntryLabel(interestvalue.value);
			}
			if (preferencenode.preferencetype.equals("seekbar")) {
				interestvalue.extradatas = preferencenode.extrDatas;
			}
			if (null != preferencenode.dependency && null != allInterestMap) {
				MyInterest dependencyPreference = allInterestMap.get(preferencenode.dependency);
				if (null != dependencyPreference) {
					interestvalue.dependency = dependencyPreference.getInterestValue("true");
				}
			}
		}
		valuemapcache.put(value, interestvalue);
		return interestvalue;
	}
	public PreferenceTreeNode getPreferencenode() {
		return preferencenode;
	}
	public void setPreferencenode(PreferenceTreeNode preferencenode) {
		this.preferencenode = preferencenode;
	}
	public String getpreferenceType() {
		if (this.preferencenode != null) {
			return this.preferencenode.preferencetype;
		}
		return null;
	}
	
	private List<InterestValue> allpossiblevaluescache = null;
	
	@Override
	public List<InterestValue> getallpossibleValues() {
		if (null != allpossiblevaluescache) {
			return allpossiblevaluescache;
		}
		String typestr = null;
		switch (type) {
		case STRING: typestr = "string";break;
		case INT : typestr = "int";break;
//		case LONG : typestr = "long";break;
		case BOOLEAN : typestr = "boolean";break;
		case FLOAT : typestr = "float";break;
		default:
			break;
		}
		
		List<InterestValue> interestvalues = new ArrayList<InterestValue>();
		for (String value: possibleValues) {
			InterestValue interestvalue = new InterestValue("preference", null, typestr, name, value, -1, null);
			if (null != preferencenode) {
				List<String> steps = preferencenode.getTitles();
				interestvalue.preferencesteps = steps;
				interestvalue.innertype = preferencenode.preferencetype; 
				interestvalue.index = preferencenode.index;
				interestvalue.activityname = preferencenode.activityname;
				interestvalue.catalog = preferencenode.catlog;
				if (preferencenode.preferencetype.equals("list")) {
					interestvalue.value = preferencenode.getEntryLabel(interestvalue.value);
				}
				if (preferencenode.preferencetype.equals("seekbar")) {
					interestvalue.extradatas = preferencenode.extrDatas;
				}
			}
			
			interestvalues.add(interestvalue);
		}
		allpossiblevaluescache = interestvalues;
		return interestvalues;
	}
	
	public void updateWithAdapt(PreferenceAdaptData data) {
		this.type = data.type;
		setUnknown(false);
		if (null != this.exp) {
			this.exp.setUnknown(false);
		}
		this.possibleValues = new ArrayList<String>(data.valuestepmap.keySet());
		if (null != this.preferencenode) {
			this.preferencenode.adaptData = data;
		}

	}


}
