package uiautomationexploration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import GUI.Main;
import data.InterestValue;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.JsonHelper;


public class PreferenceExplorer {
	private Stack<String> tobeviewedpreferenceactivities = null;
	private Map<String, List<PreferenceTreeNode>> tobeviewedpreferenceforests = null;
	private Stack<ExploreState> explorestates = null;
	
	private Adapter adapter = null;
	

	
	public boolean shouldstop = false;
	
	public PreferenceExplorer(Adapter adapter) {
		this.adapter = adapter;
		inittoviewlist();
	}

	private void inittoviewlist() {
		tobeviewedpreferenceactivities = new Stack<String>();
		for (String activityname : this.adapter.possibleactivities) {
			tobeviewedpreferenceactivities.push(activityname);
		}
		tobeviewedpreferenceforests = new HashMap<String, List<PreferenceTreeNode>>(this.adapter.xmlcontentlist);
		explorestates = new Stack<ExploreState>();
		ExploreState.init(tobeviewedpreferenceforests, explorestates, adapter);
	}
	
	
//	private boolean checkConsistenty(Set<String> titles, PreferenceTreeNode preferencexml) {
//		return false;
//	}
	
	public String givecommond() {
		while(true) {
			if (explorestates.isEmpty()) {
				boolean newstatesuccess = getNewState();
				if (!newstatesuccess) {
					adapter.explored = true;
					return "stop";
				}
			}
			ExploreState state = explorestates.peek();
			String commond = state.getCommond();
			return commond;
		}
	}
	
	private boolean getNewState() {
		if (!tobeviewedpreferenceactivities.isEmpty()) {
			String activity = tobeviewedpreferenceactivities.pop();
			ScreenLocatingState newstate = ScreenLocatingState.getScreenLocatingState(activity);
			explorestates.push(newstate);
			return true;
		} else  {
			while (!tobeviewedpreferenceforests.isEmpty()) {
				String nowkey = null;
				for (String key : tobeviewedpreferenceforests.keySet()) {
					nowkey = key;
					break;
				}
				tobeviewedpreferenceforests.remove(nowkey);
				NodeExploreState newstate = NodeExploreState.getNodeExploreState(nowkey, true);
				if (null != newstate) {
					explorestates.push(newstate);
					return true;
				}
			}
			return false;
		}
	}

	public void updatestate(String uicontent, boolean success) {
		if (!explorestates.isEmpty()) {
			ExploreState state = explorestates.peek();
			state.updatestate(uicontent, success);
		}
	}
	
	public void saveAdater() {
		adapter.explored = true;
		JsonHelper.saveadapter(adapter, Main.testadapter);
	}
	
	
	
	public void adaptData() {
		JSONObject testcaseorigin = JsonHelper.getJsonObject(Main.testcaseinfofile);
		if (!new File(Main.testcaseinfofileold).exists()) {
			JsonHelper.saveJsonFile(testcaseorigin, Main.testcaseinfofileold);
		}
		
		Map<String, InterestValue> adaptInterests = adapter.preferencelist;
		JSONArray interestmap = (JSONArray)testcaseorigin.get("interestmap");
		for (int i = 0; i < interestmap.size(); i++) {
			JSONObject interestvalue = (JSONObject)interestmap.get(i);
			String name = (String)interestvalue.get("name");
			InterestValue value = adaptInterests.get(name);

			if (null == value) {
				continue;
			}
			boolean isadapted = false;
			if (interestvalue.containsKey("isadapted")) {
				isadapted = (Boolean)interestvalue.get("isadapted");
			}
			if (isadapted) {
				continue;
			}
			interestvalue.put("isadapted", isadapted);
			if (null != value.activityextra) {
				interestvalue.put("activityextra", value.activityextra);
			}
			if (null != value.activityname) {
				interestvalue.put("activityname", value.activityname);
			}
			if (null != value.preferencesteps && !value.preferencesteps.isEmpty()) {
				JSONArray preferencesteps = new JSONArray();
				preferencesteps.addAll(value.preferencesteps);
				interestvalue.put("preferencesteps", preferencesteps);
			}
		}
		JsonHelper.saveJsonFile(testcaseorigin, Main.testcaseinfofile);
		
	}

}
