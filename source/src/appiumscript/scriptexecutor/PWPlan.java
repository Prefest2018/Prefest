package appiumscript.scriptexecutor;

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
import data.InterestValue;
import data.TestCaseData;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.JsonHelper;
import tools.Logger;
import tools.PWCounter;
import tools.ProcessExecutor;
import tools.TagnameComparator;

public class PWPlan {

	public LinkedList<PWValue> values = null;
	public Map<String, String> scriptmap = null;

	public LinkedList<String> taglist = null;

	public PWPlan(Map<String, List<PreferenceTreeNode>> preferencetree, Map<String, TestCaseData> origintestcases) {

		Set<PreferenceTreeNode> allpreferencetree = getNodeSet(preferencetree);
		int maxnum = allpreferencetree.size() + 6;

		System.out.println("maxnum : " + maxnum);
		PWCounter counter = new PWCounter();
		int maxsize = counter.initfromPICT(maxnum);
		scriptmap = new HashMap<String, String>();
		taglist = new LinkedList<String>();
		for (String tagname : origintestcases.keySet()) {
			scriptmap.put(tagname, origintestcases.get(tagname).firsttestcasepath);
			taglist.add(tagname);
		}
		taglist.sort(new TagnameComparator<String>());
		values = new LinkedList<PWValue>();
		for (int index = 0; index < maxsize; index++) {
			PWValue value = new PWValue(this, index, counter, scriptmap, allpreferencetree, taglist);
			values.add(value);
		}
		JsonHelper.setpwplanAdapt(this, Main.pwpreferenceplanfile);
	}
	
	public PWPlan(Map<String, String> scriptmap, LinkedList<PWValue> values) {
		this.scriptmap = scriptmap;
		this.taglist = new LinkedList<String>(scriptmap.keySet());
		this.taglist.sort(new TagnameComparator<String>());
		this.values = values;
		this.values.sort(new Comparator<PWValue>() {
			@Override
			public int compare(PWValue o1, PWValue o2) {
				if (o1.index > o2.index) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		for (PWValue value : this.values) {
			value.scriptmap = this.scriptmap;
			value.taglist = this.taglist;
			value.plan = this;
		}
	}
	
	
	private Set<PreferenceTreeNode> getNodeSet(Map<String, List<PreferenceTreeNode>> preferencetree){
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
				} else {

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
		System.out.println("preference num:" + allnodes.size());
		return allnodes;
	}

	public void execute() {
		Logger.setTempLogFile(Main.pwpreferenceresultfile, true);
		for (PWValue value : this.values) {
			switch (value.state) {
			case "unstart": {
				value.state = "processing";
				value.execute();
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
				break;
			}
			case "processing": {
				value.execute();
				value.state = "end";
				ProcessExecutor.processnolog("adb", "shell", "pm", "clear", Main.packagename);
				break;
			}
			case "end": {
				break;
			}
			}
		}
	}

}
