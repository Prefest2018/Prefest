package sootproject.myexpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import GUI.Main;
import soot.Body;
import soot.Unit;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MyPreference;
import sootproject.analysedata.MySystemService;


public class MyNoBranchConstraint extends MyConstraint{

//			MyNoBranchConstraint target = (MyNoBranchConstraint)object;
//					return true;
//		return false;
	
	private MyNoBranchConstraint(String originloc, Set<MyInterest> involvedInterest) {
		super(null, null);
		this.originloc = originloc;
		this.involvedInterest = involvedInterest;
		this.unknown = false;

	}
	
	public static MyNoBranchConstraint getInstance(String originloc, Set<MyExpressionInterface> exps, Map<String, MyNoBranchConstraint> instancecache) {
		Set<MyInterest> involvedInterest = new TreeSet<MyInterest>();
		for (MyExpressionInterface e : exps) {
			Set<MyInterest> tempinterests = getInvolvedInterest(e);
			for (MyInterest nowinterest : tempinterests) {
				if (nowinterest instanceof MyPreference) {
					String type = ((MyPreference)nowinterest).getpreferenceType();
					if (!nowinterest.isUnKnown() && ("edit".equals(type) || "seekbar".equals(type) || (null != ((MyPreference)nowinterest).getPossibleValues()) && ((MyPreference)nowinterest).getPossibleValues().size() == 1)) {
						involvedInterest.add(nowinterest);
					}
				} else if (nowinterest instanceof MySystemService) {
				}
			}
			//involvedInterest.addAll(getInvolvedInterest(e));
		}
		if (involvedInterest.isEmpty()) {
			return null;
		}
		String key = originloc;
		for (MyInterest i:involvedInterest) {
			key += i.getName();
		}
		MyNoBranchConstraint returnconstrain = instancecache.get(key);
		if (null == returnconstrain) {
			returnconstrain = new MyNoBranchConstraint(originloc, involvedInterest);
			instancecache.put(key, returnconstrain);
		}
		return returnconstrain;
	}

}
