package data;

import java.util.ArrayList;
import java.util.List;

public class ExeScene {
	public List<String> changebranchids = null;
	public ArrayList<InterestValue> interests = null;
	public ArrayList<InterestValue> txtinterests = null;
	private boolean hassystemservice = false;
	
	public String tagname = null;
	public ExeScene(String tagname) {
		this.tagname = tagname;
		this.changebranchids = new ArrayList<String>();
		this.interests = new ArrayList<InterestValue>();
		this.txtinterests = new ArrayList<InterestValue>();
	}
	
//	public ExeScene(Scene singlescene) {
//		this.tagname = singlescene.tagnames.get(0);
//		this.changebranchids = new ArrayList<String>();;
//		this.changebranchids.add(singlescene.changebranchids);
//		this.interests = new ArrayList<InterestValue>();
//		this.txtinterests = new ArrayList<InterestValue>();
//		if (null != singlescene.preinterests) {
//			singlescene.interests.addAll(singlescene.preinterests);
//			singlescene.preinterests.clear();
//		}
//		addScene(singlescene);
//
//	}
	
	
	public int getSize() {
		return this.interests.size() + this.txtinterests.size();
	}
	
	public boolean addScene(Scene scene) {
		if (interestCheck(scene.interests)) {
			for (InterestValue interest : scene.interests) {
				if (interest.generaltype.equals("systemservice")) {
					this.hassystemservice = true;
				}
				if (interest.index == -1 && !"systemservice".equals(interest.generaltype)) {
					if (!txtinterests.contains(interest)) {
						txtinterests.add(interest);
					}
				} else {
					if (!interests.contains(interest)) {
						interests.add(interest);
					}
				}
			}
			changebranchids.add(scene.changebranchids);
			return true;
		} else {
			return false;
		}
	}
	
	private boolean interestCheck(List<InterestValue> outinterests) {
		if (hassystemservice) {
			for (InterestValue interest : outinterests) {
				if ("systemservice".equals(interest.generaltype)) {
					return false;
				}
			}
		}
		
		for (InterestValue interest : outinterests) {
			for (InterestValue inner : this.interests) {
				if (interest.name.equals(inner.name) && (null != interest.value) && !interest.value.equals(inner.value)) {
					return false;
				}
			}
			for (InterestValue inner : this.txtinterests) {
				if (interest.name.equals(inner.name) && !interest.value.equals(inner.value)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void addInterest(InterestValue value) {
		this.interests.add(value);
		if (value.generaltype.equals("systemservice")) {
			this.hassystemservice = true;
		}
	}
	
}
