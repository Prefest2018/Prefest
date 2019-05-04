package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;

public class Scene {
	public String branchids = null;
	public String changebranchids = null;
	public List<InterestValue> interests = null;
	public List<InterestValue> preinterests = null;
	public LinkedList<String> tagnames = new LinkedList<String>();
	public int trialtimes = 0;
	public Body body = null;
	
	public boolean equals(Object scene) {
		if (null != scene && scene instanceof Scene) {
			if (((Scene)scene).branchids.equals(branchids) && ((Scene)scene).changebranchids.equals(changebranchids)) {
				if (branchids.equals(changebranchids)) {
					return interests.get(0).equalsWithNameAndValue(((Scene)scene).interests.get(0));
				} else {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean equalsIgnoreSuffix(Object scene, char suffix) {
		if (null != scene && scene instanceof Scene) {
			int num = -1;
			String abranchids = branchids;
			num = abranchids.indexOf(suffix);
			if (num >= 0) {
				abranchids = abranchids.substring(0, num);
			}
			String bbranchids = ((Scene)scene).branchids;
			num = bbranchids.indexOf(suffix);
			if (num >= 0) {
				bbranchids = bbranchids.substring(0, num);
			}
			String achangebranchids = changebranchids;
			num = achangebranchids.indexOf(suffix);
			if (num >= 0) {
				achangebranchids = achangebranchids.substring(0, num);
			}
			String bchangebranchids = ((Scene)scene).changebranchids;
			num = bchangebranchids.indexOf(suffix);
			if (num >= 0) {
				bchangebranchids = bchangebranchids.substring(0, num);
			}
			
			if (abranchids.equals(bbranchids) && achangebranchids.equals(bchangebranchids)) {
				if (abranchids.equals(achangebranchids)) {
					if (branchids.equals(changebranchids)) {
						return interests.get(0).equalsWithNameAndValue(((Scene)scene).interests.get(0));
					} else {
						return true;
					}
				} else {
					return true;
				}
			}
		}
		return false;
	}
	public Scene() {
		
	}
	
	public Scene(String branchids, String changebranchids, List<InterestValue> interests, List<InterestValue> preinterests) {
		this.branchids = branchids;
		this.changebranchids = changebranchids;
		this.interests = interests;
		this.preinterests = preinterests;
	}
	
	
	public boolean isTrailScene() {
		for (InterestValue value : interests) {
			if (value.index != -1 || ("systemservice").equals(value.generaltype)) {
				return true;
			}
		}
		return false;
	}

	public void mixpreandinterests() {
		Map<String, InterestValue> resultmap = new HashMap<String, InterestValue>();
		Set<String> catelogsets = new HashSet<String>();
		for (InterestValue value : this.interests) {
			resultmap.put(value.name, value);
			if (null != value.catalog) {
				catelogsets.add(value.catalog);
			}
		}
		
		
		for (int i = this.preinterests.size() - 1; i >=0; i--) {
			InterestValue value = this.preinterests.get(i);
			if (resultmap.containsKey(value.name)) {
				continue;
			}
			if (catelogsets.isEmpty()) {
				resultmap.put(value.name, value);
			} else {
				if (catelogsets.contains(value.catalog)) {
					resultmap.put(value.name, value);
				}
			}
		}
		interests = new ArrayList<InterestValue>(resultmap.values());
		preinterests = null;
	}
	
}
