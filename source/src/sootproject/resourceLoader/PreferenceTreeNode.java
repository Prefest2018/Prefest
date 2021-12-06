package sootproject.resourceLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.InterestValue;
import data.PreferenceAdaptData;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MyPreference;

public class PreferenceTreeNode {
	public String preferencetype = null;
	public int index = -1;
	public String title = null;
	public String key = null;
	public String dependency = null;
	public boolean shouldexplore = false;
	public String filename = null;
	protected String fragment = null;
	public Map<String, String> entryvalues = null;
	public String defaultvalue = null;
	public List<PreferenceTreeNode> childnodes = null;
	public PreferenceTreeNode parentnode = null;
	public boolean isheader = false;
	public String activityname = null;
	protected static int countindex = -1;
	public String catlog = null;
	public ArrayList<String> titles = null;
	protected ArrayList<String> titlesWithSamePage = null;
	public PreferenceAdaptData adaptData = null;
	public Map<String, Object> extrDatas = null;
	public static void initcount() {
		countindex = 0;
	}
	public PreferenceTreeNode() {
		
	}
	
	public PreferenceTreeNode(String preferencetype, String title, String filename, String activityname, String fragment, boolean isheader) {
		this.preferencetype = preferencetype;
		this.title = title;
		this.filename = filename;
		this.activityname = activityname;
		this.fragment = fragment;
		this.isheader = isheader;
		this.index = countindex++;
	}
	public PreferenceTreeNode(String preferencetype, String title, String key, String filename, String activityname, Map<String, String> entryvalues, String defaultvalue, String catlog, String dependency, boolean shouldexplore, Map<String, Object> extrDatas) {
		this.preferencetype = preferencetype;
		this.title = title;
		this.key = key;
		this.filename = filename;
		this.activityname = activityname;
		this.entryvalues = entryvalues;
		this.defaultvalue = defaultvalue;
		this.catlog = catlog;
		this.shouldexplore = shouldexplore;
		this.dependency = dependency;
		this.extrDatas = extrDatas;
		this.index = countindex++;
	}
	
	public void setChildren(List<PreferenceTreeNode> childnodes) {
		this.childnodes = childnodes;
		for (PreferenceTreeNode node : childnodes) {
			if (node.activityname == null) {
				node.activityname = this.activityname;
			}
			node.parentnode = this;
		}
	}
	
	public void addChild(PreferenceTreeNode childnode) {
		if (null == this.childnodes) {
			this.childnodes = new ArrayList<PreferenceTreeNode>();
		}
		this.childnodes.add(childnode);
		childnode.parentnode = this;
		if (childnode.activityname == null) {
			childnode.activityname = this.activityname;
		}
	}
	
	public void initTitles() {
		titles = new ArrayList<String>();
		titlesWithSamePage = new ArrayList<String>();
		boolean insamefile = true;
		PreferenceTreeNode nownode = this;
		while (nownode != null) {
			titles.add(0, nownode.title);
			if (null == filename) {
				System.out.println();
			}
			if (!filename.equals(nownode.filename)) {
				insamefile = false;
			}
			if (insamefile) {
				titlesWithSamePage.add(0, nownode.title);
			}
			nownode = nownode.parentnode;
			
		}
	}
	public ArrayList<String> getTitles() {
		return titles;
	}
	

	
	public ArrayList<String> getTitleWithinSamePage() {
		return titlesWithSamePage;
	}
	
	public void setTitles(Collection<String> array) {
		if (null == titles) {
			titles = new ArrayList<String>();
		}
		titles.clear();
		titles.addAll(array);
	}
	
	public String getEntryLabel(String truevalue) {
		return entryvalues.get(truevalue);
	}

	public List<PreferenceTreeNode> getChildnodes() {
		return childnodes;
	}

	public Map<String, String> getEntryvalues() {
		return entryvalues;
	}

	
	public InterestValue toInterestValueReverseDefault() {
		InterestValue interestvalue = toInterestValue();
		
		String value = null;
		if (preferencetype.equals("list") || preferencetype.equals("edit") || preferencetype.equals("seekbar") || preferencetype.equals("multilist")) {
			if (!entryvalues.isEmpty()) {
				if (null != defaultvalue) {
					Map<String, String> tempmap = new HashMap<String, String>(entryvalues);
					tempmap.remove(defaultvalue);
					if (tempmap.isEmpty()) {
						return null;
					}
					value = tempmap.values().toArray()[(int) (Math.random() * tempmap.keySet().size())].toString();
				} else {
					Object[] temparray = entryvalues.values().toArray();
					if (temparray.length > 1) {
						value = temparray[(int) ((temparray.length - 1)* Math.random() + 1)].toString();
					} else {
						value = temparray[0].toString();
					}
					
				}
				interestvalue.value = entryvalues.get(interestvalue.value);
			} else {
				value = null;
			}
		} else {
			if ("1".equals(defaultvalue)) {
				value = "0";
			} else {
				value = "1";
			}
		}
		interestvalue.value = value;
		
		return interestvalue;
	}
	
	public InterestValue toInterestValue() {
		InterestValue interestvalue = new InterestValue("preference", null, "boolean", key, null, -1, null);
		interestvalue.preferencesteps = titles;
		interestvalue.innertype = preferencetype; 
		interestvalue.index = index;
		interestvalue.activityname = activityname;
		interestvalue.catalog = catlog;
		return interestvalue;
	}
	
	public InterestValue toInterestValueDefault() {
		InterestValue interestvalue = toInterestValue();
		
		String value = null;
		if (preferencetype.equals("list") || preferencetype.equals("edit") || preferencetype.equals("seekbar") || preferencetype.equals("multilist")) {
			if (!entryvalues.isEmpty()) {
				if (null !=defaultvalue) {
					for (String key : entryvalues.keySet()) {
						if (key.equals(defaultvalue)) {
							value = entryvalues.get(key);
							break;
						}
					}
				} else {
					Object[] temparray = entryvalues.values().toArray();
					value = temparray[0].toString();
					
				}
				interestvalue.value = entryvalues.get(interestvalue.value);
			} else {
				value = null;
			}
		} else {
			if ("1".equals(defaultvalue)) {
				value = "1";
			} else {
				value = "0";
			}
		}
		interestvalue.value = value;
		
		return interestvalue;
	}
}
