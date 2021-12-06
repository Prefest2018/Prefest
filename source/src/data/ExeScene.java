package data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import GUI.Main;
import appiumscript.scriptexecutor.ScriptExecutor;

public class ExeScene {
	public List<String> changebranchids = null;
	public boolean istrial = false;
//	private boolean hassystemservice = false;

	
	public LinkedHashMap<Object, ArrayList<InterestValue>> taglist = null;
	public ExeScene() {
		this.taglist = new LinkedHashMap<Object, ArrayList<InterestValue>>();
		this.changebranchids = new ArrayList<String>();
//		this.interests = new ArrayList<InterestValue>();
//		this.txtinterests = new ArrayList<InterestValue>();
	}
	
	public boolean isEmtpy() {
		return this.changebranchids.isEmpty();
	}
	
	public ExeScene(Object tagname) {
		this.taglist = new LinkedHashMap<Object, ArrayList<InterestValue>>();
		this.changebranchids = new ArrayList<String>();
		this.taglist.put(tagname, new ArrayList<InterestValue>());
	}
	
	public ExeScene(ITScene singlescene, boolean firstit) {
		this.istrial = true;
		this.changebranchids = new ArrayList<String>();
		this.changebranchids.add(singlescene.changebranchids);
		this.taglist = new LinkedHashMap<Object, ArrayList<InterestValue>>();
		for (Object key : singlescene.discovertag.keySet()) {
			Scene innerscene = singlescene.discovertag.get(key);
			ArrayList<InterestValue> newlist = new ArrayList<InterestValue>();
			if (firstit) {
				if (null != innerscene.preinterests) {
					for (InterestValue value : innerscene.preinterests) {
						if (!newlist.contains(value)) {
							newlist.add(value);
						}
					}
				}
			}
			for (InterestValue value : innerscene.interests) {
				if (!newlist.contains(value)) {
					newlist.add(value);
				}
			}
			this.taglist.put(key, newlist);
		}
	}
	
	public boolean addScene(ITScene scene) {
		LinkedHashMap<Object, ArrayList<InterestValue>> newtaglist = new LinkedHashMap<Object, ArrayList<InterestValue>>();
		if (taglist.isEmpty()) {
			for (Object tag : scene.discovertag.keySet()) {
				Scene innerscene = scene.discovertag.get(tag);
				newtaglist.put(tag, innerscene.interests);
			}
		} else {
			for (Object tag : taglist.keySet()) {
				Scene innerscene = scene.discovertag.get(tag);
				if (null != innerscene) {
					List<InterestValue> originalValues = this.taglist.get(tag);
					List<InterestValue> newValues = innerscene.interests;
					
					ArrayList<InterestValue> finalValues = interestCheck(originalValues, newValues);
					if (null != finalValues && !finalValues.isEmpty()) {
						newtaglist.put(tag, finalValues);
					}
				} 
			}
		}

		//testlist
//			return false;
//			Scene innerscene = scene.discovertag.get(ScriptExecutor.testList.get(ScriptExecutor.currentid));
//				return false;
		
		if (newtaglist.size() >= Main.lestTagNum || this.changebranchids.isEmpty()) {
			this.taglist = newtaglist;
			this.changebranchids.add(scene.changebranchids);
			return true;
		} else {
			return false;
		}

	}

	
	private ArrayList<InterestValue> interestCheck(List<InterestValue> ininterests, List<InterestValue> outinterests) {
		ArrayList<InterestValue> returnlist = new ArrayList<InterestValue>(ininterests);
		
		total: for (InterestValue interest : outinterests) {
			for (InterestValue inner : ininterests) {
				if (interest.name.equals(inner.name) && (null != interest.value)) {
					if (interest.value.equals(inner.value)) {
						continue total;
					} else {
						return null;
					}
				}
			}
			returnlist.add(interest);
		}
		int systemnum = 0;
		for (InterestValue value : returnlist) {
			if ("systemservice".equals(value.generaltype)) {
				systemnum++;
			}
		}
		if (systemnum > 1) {
			return null;
		}
		return returnlist;
	}
	
	public void addInterest(InterestValue value) {
		for (Object tag : this.taglist.keySet()) {
			this.taglist.get(tag).add(value);
		}
//		this.interests.add(value);
//			this.hassystemservice = true;
	}
	
	public ArrayList<InterestValue> getFirstInterestValueArray() {
		for (Object tag : this.taglist.keySet()) {
			return this.taglist.get(tag);
		}
		return null;
	}
	
}
