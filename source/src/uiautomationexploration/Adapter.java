package uiautomationexploration;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.InterestValue;
import sootproject.resourceLoader.PreferenceTreeNode;

public class Adapter {
	public boolean explored = false;
	public Map<String, InterestValue> preferencelist = null;
	public Set<String> possibleactivities = null;
	public Map<String, String> preferencefilename2activity = null;
	public Map<String, List<PreferenceTreeNode>> xmlcontentlist = null;
	public Adapter() {}
	public Adapter(Set<String> possibleactivities, Map<String, String> preferencefilename2activity, Map<String, List<PreferenceTreeNode>> xmlcontentlist) {
		this.possibleactivities = possibleactivities;
		this.preferencefilename2activity = preferencefilename2activity;
		this.xmlcontentlist = xmlcontentlist;
	}
}
