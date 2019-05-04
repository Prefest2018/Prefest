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

//	public boolean equals(Object object) {
//		if (null != object && object instanceof MyNoBranchConstraint) {
//			MyNoBranchConstraint target = (MyNoBranchConstraint)object;
//			if (originloc.equals(target.originloc)) {
//				if (target.involvedInterest.containsAll(involvedInterest) && involvedInterest.containsAll(target.involvedInterest)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}
	
	private MyNoBranchConstraint(String originloc, Set<MyInterest> involvedInterest) {
		super(null, null);
		this.originloc = originloc;
		this.involvedInterest = involvedInterest;
		this.unknown = false;

	}
	
	public static MyNoBranchConstraint getInstance(String originloc, Set<MyExpression> exps, Map<String, MyNoBranchConstraint> instancecache) {
		Set<MyInterest> involvedInterest = new TreeSet<MyInterest>();
		for (MyExpression e : exps) {
			Set<MyInterest> tempinterests = getInvolvedInterest(e);
			for (MyInterest nowinterest : tempinterests) {
				if (nowinterest instanceof MyPreference) {
					if (!nowinterest.unKnown && (Main.blockmode || "edit".equals(((MyPreference)nowinterest).getpreferenceType()))) {
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
