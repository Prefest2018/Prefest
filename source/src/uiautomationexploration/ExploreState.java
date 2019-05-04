package uiautomationexploration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import GUI.Main;
import data.InterestValue;
import sootproject.resourceLoader.PreferenceTreeNode;

public abstract class ExploreState {
	private static Set<String> listclasses = new HashSet<String>();
	private static Set<String> titleids = new HashSet<String>();
	private static Set<String> titleclasses = new HashSet<String>();
	public static int numcount = 0;
	static {
		listclasses.add("android.widget.ListView");
		listclasses.add("android.support.v7.widget.RecyclerView");
		listclasses.add("androidx.recyclerview.widget.RecyclerView");
		titleids.add("android:id/title");
		titleclasses.add("android.widget.TextView");
	}
	
	
	private static Map<Object, Set<String>> titlemap = null;
	protected static Map<String, Set<PreferenceTreeNode>> tobeexploredallnodes = null;
	protected static Map<String, Set<PreferenceTreeNode>> allnodes = null;
	protected Stack<String> currentsteps = null;
	protected String currentactivity = null;
	protected StateType statetype = StateType.DEFAULT;
	protected static Adapter adapter = null;
	protected static Stack<ExploreState> explorestates = null;
	public static void init(Map<String, List<PreferenceTreeNode>> tobeviewedpreferenceforests, Stack<ExploreState> explorestates, Adapter adapter) {
		ExploreState.explorestates = explorestates;
		ExploreState.adapter = adapter;
		titlemap = new HashMap<Object, Set<String>>();
		tobeexploredallnodes = new HashMap<String, Set<PreferenceTreeNode>>();
		allnodes = new HashMap<String, Set<PreferenceTreeNode>>();
		numcount = 0;
		for (String key : tobeviewedpreferenceforests.keySet()) {
			List<PreferenceTreeNode> nodes = tobeviewedpreferenceforests.get(key);
			Stack<PreferenceTreeNode> nodestack = new Stack<PreferenceTreeNode>();
			Set<String> titles = new HashSet<String>();
			Set<PreferenceTreeNode> allnode = new HashSet<PreferenceTreeNode>();
			Set<PreferenceTreeNode> tobeexplorednodes = new HashSet<PreferenceTreeNode>();
			nodestack.addAll(nodes);
			while(!nodestack.isEmpty()) {
				PreferenceTreeNode node = nodestack.pop();
				allnode.add(node);
				if (node.shouldexplore || node.isheader) {
					tobeexplorednodes.add(node);
				}
				if ("preferencescreen".equals(node.preferencetype)) {
					if (null != node.childnodes) {
						nodes.addAll(node.childnodes);
					}
				}
				if (null != node.title) {
					titles.add(node.title);
				}
			}
			if (!tobeexplorednodes.isEmpty()) {
				tobeexploredallnodes.put(key, tobeexplorednodes);
			}
			titlemap.put(key, titles);
			allnodes.put(key, allnode);
		}
	}
	
	public ExploreState() {
		currentsteps = new Stack<String>();
	}
	
	public abstract String getCommond();
	public abstract void reset();
	public abstract void updatestate(String uicontent, boolean success);
	
	protected static Element readXML(String uicontent) {
		SAXReader reader = new SAXReader();
		Document document = null;
		Element root = null;
		try {
			document = reader.read(new ByteArrayInputStream(uicontent.getBytes("UTF-8")));
			root = document.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	protected static Object getPosition(Element inite) {
		Set<String> screentitles = new HashSet<String>();
		screentitles = (Set<String>)getAllTitlesFromUiAutomation(false, screentitles, inite);
		
		int maxnum = 0;
		Object maxkey = null;
		for (Object key : titlemap.keySet()) {
			Set<String> keytitles = titlemap.get(key);
			int nownum = 0;
			for (String nowtitle : keytitles) {
				if (screentitles.contains(nowtitle)) {
					nownum++;
				}
			}
			if (nownum > maxnum) {
				maxnum = nownum;
				maxkey = key;
			}
		}
		if (null != maxkey && maxnum >=3) {
			return maxkey;
		}
		return null;
	}
	
	protected static <T extends Collection<String>> T getAllTitlesFromUiAutomation(boolean shouldadd, T tobetestedtitles, Element inite) {
		String classname = inite.attributeValue("class");
		String id = inite.attributeValue("resource-id");
		String title = inite.attributeValue("text");
		if (listclasses.contains(classname)) {
			for (Element child : (List<Element>)inite.elements()) {
				getAllTitlesFromUiAutomation(true, tobetestedtitles, child);
			}
		} else {
			if (titleids.contains(id) && titleclasses.contains(classname) && null != title) {
				if (shouldadd) {
					tobetestedtitles.add(title);
				}
			} else {
				for (Element child : (List<Element>)inite.elements()) {
					getAllTitlesFromUiAutomation(shouldadd, tobetestedtitles, child);
				}
			}
		}
		return tobetestedtitles;
	}
	
	protected static void updateAdapter(String filename) {
		String activityname = null;
		ArrayList<String> steps = new ArrayList<String>();
		for (int i = explorestates.size() -1; i >= 0; i--) {
			ExploreState nowstate = explorestates.get(i);
			if (i == 0) {
				activityname = nowstate.currentactivity;
			}
			if (!nowstate.currentsteps.isEmpty()) {
				for (int j = nowstate.currentsteps.size() - 1; j >=0; j--) {
					steps.add(nowstate.currentsteps.get(j));
				}
			}
		}
		
		Set<PreferenceTreeNode> nodes = allnodes.get(filename);
		for (PreferenceTreeNode node : nodes) {
			InterestValue value = adapter.preferencelist.get(node.key);
			if (null != value && !value.isadapted) {
				value.isadapted = true;
				value.activityname = activityname;
				value.preferencesteps.clear();
				value.preferencesteps.addAll(steps);
				value.preferencesteps.addAll(node.getTitles());
			}
		}
	}
	
	public String getResumeCom() {
		if (explorestates.isEmpty()) {
			System.out.println("error : the explorestates is empty when resume!!!");
			return null;
		}
		String cmd = "";
		String activityname = explorestates.get(0).currentactivity;
		cmd += "stop---" + Main.packagename + "|";
		cmd += "start---" + Main.packagename + "/" + activityname;
		ExploreState lastState = explorestates.peek();
		for (ExploreState nowState : explorestates) {
			if (lastState != nowState) {
				for (String step : nowState.currentsteps) {
					cmd += "|touch---" + step;
				}
			}
		}
		lastState.reset();
		return cmd;
	}
	
	
}