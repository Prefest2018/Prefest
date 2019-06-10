package uiautomationexploration;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Element;

import GUI.Main;

public class ScreenLocatingState extends ExploreState{
	 Stack<String> tobetestedtitles = null;
	 private Set<String> targettitles = null;
	 private Set<String> currenttitles = null;
	 public static ScreenLocatingState getScreenLocatingState(String currentactivity) {
		 return new ScreenLocatingState(currentactivity);
	 }
	 private ScreenLocatingState(String currentactivity) {
		 super();
		 this.currentactivity = currentactivity;
		 this.statetype = StateType.START;
	 }
	@Override
	public String getCommond() {
		String commond = "";
		switch(this.statetype) {
		case START: {
			commond = "start---" + Main.packagename + "/" + currentactivity;
			break;
		}
		case EXPLORING: {
			
			String currenttitle = tobetestedtitles.pop();
			if (!currentsteps.isEmpty()) {
				currentsteps.removeAllElements();
				if (!this.currenttitles.containsAll(this.targettitles)) {
					commond = "back|";
				}
			}
			currentsteps.push(currenttitle);
			commond += "touch---" + currenttitle;
			break;
		}
		case END: {
			commond = "stop---" + Main.packagename + "---" + numcount;
			numcount++;
			this.explorestates.pop();
			break;
		}
		}
		return commond;
	}
	@Override
	public void updatestate(String uicontent, boolean success) {
		Element inite = null;
		String filename = null;
		if (null != uicontent) {
			inite = readXML(uicontent);
			if (null != inite) {
				filename = (String)getPosition(inite);
				this.currenttitles = new HashSet<String>();
				this.currenttitles = getAllTitlesFromUiAutomation(false, currenttitles, inite);
			}
		}
		
		switch(this.statetype) {
		case START: {
			if (null == uicontent || !success) {
				this.statetype = StateType.END;
				explorestates.remove(this);
				break;
			}
			tobetestedtitles = new Stack<String>();
			if (null != filename) {
				updateAdapter(filename);
				NodeExploreState newstate = NodeExploreState.getNodeExploreState(filename, false);
				if (null != newstate) {
					explorestates.add(newstate);
					this.statetype = StateType.SUSPEND;
				} else {
					this.statetype = StateType.END;
				}
			} else {
				if (currenttitles.isEmpty()) {
					System.out.println("screen locating warning: no titles found!!");
					this.statetype = StateType.END;
				} else {
					tobetestedtitles.addAll(currenttitles);
					this.targettitles = new HashSet<String>();
					this.targettitles.addAll(currenttitles);
					this.statetype = StateType.EXPLORING;
				}
			}
			break;
		}
		case EXPLORING: {
			if (null == uicontent) {
				break;
			}
			if (null != filename) {
				if (this.currenttitles.containsAll(this.targettitles)) {
					break;
				}
				updateAdapter(filename);
				NodeExploreState newstate = NodeExploreState.getNodeExploreState(filename, false);
				if (null != newstate) {
					explorestates.add(newstate);
					this.statetype = StateType.SUSPEND;
				} else {
					if (tobetestedtitles.isEmpty()) {
						this.statetype = StateType.END;
					} else {
						this.statetype = StateType.EXPLORING;
					}
				}

			} else {
				if (tobetestedtitles.isEmpty()) {
					this.statetype = StateType.END;
				}
			}
			break;
		}
		case SUSPEND: {
			if (tobetestedtitles.isEmpty()) {
				this.statetype = StateType.END;
			} else {
				this.statetype = StateType.EXPLORING;
				this.currentsteps.clear();
			}

			break;
		}
		}

	}
	@Override
	public void reset() {
		 currenttitles = targettitles;
		 currentsteps.clear();
	}
	

}
