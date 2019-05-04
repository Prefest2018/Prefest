package uiautomationexploration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Element;

import GUI.Main;
import sootproject.resourceLoader.PreferenceTreeNode;

public class NodeExploreState extends ExploreState{
	private String currentpreferencefile = null;
	private Stack<PreferenceTreeNode> tobelocatenodes = null;
	private PreferenceTreeNode currentnode = null;
	private PreferenceTreeNode currenttargetnode = null;
	private boolean shouldstartactivity = false;
	public static NodeExploreState getNodeExploreState(String xmlfilename, boolean shouldstartactivity) {
		NodeExploreState newState = null;
		if (!shouldstartactivity) {
			newState = new NodeExploreState(xmlfilename);
		} else {
			if (null != ExploreState.adapter.preferencefilename2activity) {
				Map<String, String> preferencefilename2activity = ExploreState.adapter.preferencefilename2activity;
				if (null != preferencefilename2activity) {
					String activityname = preferencefilename2activity.get(xmlfilename);
					if (null != activityname) {
						newState = new NodeExploreState(xmlfilename, activityname);
					}
				}
			}
		}
		return newState;
	}
	private NodeExploreState(String xmlfilename) {
		super();
		this.currentsteps = new Stack<String>();
		this.currentpreferencefile = xmlfilename;
		this.statetype = StateType.START;
		this.shouldstartactivity = false;
	}
	
	private NodeExploreState(String xmlfilename, String activityname) {
		super();
		this.currentsteps = new Stack<String>();
		this.currentpreferencefile = xmlfilename;
		this.currentactivity = activityname;
		this.statetype = StateType.START;
		this.shouldstartactivity = true;
	}
	@Override
	public String getCommond() {
		String commond = "";
		switch (this.statetype) {
		case START: {
			Set<PreferenceTreeNode> tobeexploredones = tobeexploredallnodes.get(currentpreferencefile);
			if (null == tobeexploredones || tobeexploredones.isEmpty()) {
				this.statetype = StateType.END;
				explorestates.pop();
				commond = "back";
			} else {
				tobelocatenodes = new Stack<PreferenceTreeNode>();
				tobeexploredallnodes.remove(currentpreferencefile);
				tobelocatenodes.addAll(tobeexploredones);
				sortFortobelocatenodes(tobelocatenodes);
				this.statetype = StateType.EXPLORING;
				commond = targetNextNode();
				if (shouldstartactivity) {
					commond = "start---" + Main.packagename + "/" + currentactivity + "|"+ commond;
				}
			}
			break;
		}
		case EXPLORING: {
			if (this.tobelocatenodes.isEmpty()) {
				commond = generateComplexCommond(getBackCommonds());
				this.statetype = StateType.END;
				explorestates.pop();
			} else {
				commond = targetNextNode();
			}
			break;
		}
		case RESUME : {
			commond = "back";
			break;
		}
		case RESUMEALL : {
			commond = getResumeCom();
			break;
		}
		case END : {
			if (!shouldstartactivity) {
				commond = "back";
			} else {
				commond = "stop---" + Main.packagename + "---" + numcount;
				numcount++;
			}
			explorestates.pop();
			break;
		}
		}
		return commond;
	}
	@Override
	public void updatestate(String uicontent, boolean success) {
		Element inite = null;
		Object position = null;
		if (null != uicontent) {
			inite = readXML(uicontent);
			if (null != inite) {
				position = getPosition(inite);
			}
		}

		switch (this.statetype) {
		case EXPLORING: {
			if (success) {

				if (null != position) {
					if (position instanceof String) {
						if (this.currentnode == null && (position.equals(this.currentpreferencefile))) {
							break;
						} else {
							updateAdapter(position.toString());
							this.statetype = StateType.SUSPEND;
							NodeExploreState newstate = new NodeExploreState(position.toString());
							explorestates.add(newstate);
							this.currentnode = this.currenttargetnode;
							this.currenttargetnode = null;
							this.currentsteps = new Stack<String>();
							this.currentsteps.addAll(this.currentnode.getTitles());
						}

					} else if (position instanceof PreferenceTreeNode){
						System.out.println("error : 'NodeExplore' unnecessary elements!");
					}
				} else {
					this.statetype = StateType.RESUME;
				}

			} else {
				if (null != position) {
					relocate(position);
				}
			}
			break;
		}
		case SUSPEND: {
			relocate(position);
			this.statetype = StateType.EXPLORING;

			break;
		}
		case RESUME : {
			boolean resumesuccess = relocate(position);
			if (resumesuccess) {
				this.statetype = StateType.EXPLORING;
			} else {
				this.statetype = StateType.RESUMEALL;
			}
			break;
		}
		case RESUMEALL : {
			if (this.tobelocatenodes.isEmpty()) {
				this.statetype = StateType.END;
			} else {
				this.statetype = StateType.EXPLORING;
			}
			break;
		}
		}
	}
	
	private boolean relocate(Object position) {
		if (null == position) {
			return false;
		}
		if (position instanceof String) {
			if (position.toString().equals(this.currentpreferencefile)) {
				this.currentnode = null;
				this.currentsteps.clear();
				return true;
			} else {
				return false;
			}
		} else if (position instanceof PreferenceTreeNode){
			this.currentnode = (PreferenceTreeNode)position;
			this.currentsteps = new Stack<String>();
			this.currentsteps.addAll(this.currentnode.getTitles());
			return true;
		}
		return false;
	}
	
	
	private String targetNextNode() {
		PreferenceTreeNode nownode = tobelocatenodes.pop();
		List<String> commonds = getNodeTransferCommonds(nownode);
		String commond = generateComplexCommond(commonds);
		this.currentsteps = new Stack<String>();
		this.currentsteps.addAll(nownode.getTitles());
		this.currenttargetnode = nownode;
		return commond;
	}
	
	private String generateComplexCommond(List<String> commonds) {
		String newcommond = "";
		boolean first = true;
		for (String cmd : commonds) {
			if (!first) {
				newcommond += "|";
			} else {
				first = false;
			}
			newcommond += cmd;
		}
		return newcommond;
	}
	
	private List<String> getNodeTransferCommonds(PreferenceTreeNode targetnode) {
		ArrayList<String> commonds = new ArrayList<String>();
		if (null == this.currentnode) {
			for (String title : targetnode.getTitles()) {
				commonds.add("touch---" + title);
			}
		} else {
			ArrayList<String> currenttitles = this.currentnode.getTitles();
			ArrayList<String> targettitles = targetnode.getTitles();
			int index = 0;
			for (index = 0; index < currenttitles.size() && index < targettitles.size(); index++) {
				String currenttitle = currenttitles.get(index);
				String targettitle = targettitles.get(index);
				if (!currenttitle.equals(targettitle)) {
					break;
				}
			}
			for (int i = index; i < currenttitles.size(); i++) {
				commonds.add("back");
			}
			for (int i = index; i < targettitles.size(); i++) {
				commonds.add("touch---" + targettitles.get(i));
			}
		}
		return commonds;
	}
	
	private List<String> getBackCommonds() {
		ArrayList<String> backcommonds = new ArrayList<String>();
		backcommonds.add("back");
		if (null != this.currentnode) {
			int num = this.currentnode.getTitles().size();
			num--;
			while (num > 0) {
				backcommonds.add("back");
				num--;
			}
		}
		return backcommonds;
	}
	@Override
	public void reset() {
		this.currentsteps.clear();
		this.currentnode = null;
	}
	
	private void sortFortobelocatenodes(Stack<PreferenceTreeNode> tobellocatenodes) {
		for (int i = 0; i < tobellocatenodes.size(); i++) {
			for (int j = i; j < tobellocatenodes.size(); j++) {
				PreferenceTreeNode node1 = tobellocatenodes.get(i);
				PreferenceTreeNode node2 = tobellocatenodes.get(j);
				if (node1.index < node2.index) {
					tobellocatenodes.set(i, node2);
					tobellocatenodes.set(j, node1);
				}
			}
		}
	}
	
}
