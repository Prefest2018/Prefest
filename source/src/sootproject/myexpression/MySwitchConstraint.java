package sootproject.myexpression;

import java.util.List;

import soot.Body;
import soot.Unit;

public class MySwitchConstraint extends MyConstraint{
	private String defaultloc = null;

	public MySwitchConstraint(MyExpression myexp, Unit unit, Body body, long originresult, String originloc, List<Long> targetresults, List<String> targetlocs, String defaultloc) {
		super(myexp,unit, body);
		this.originresult = originresult;
		this.originloc = originloc;
		this.targetlocs = targetlocs;
		this.targetresults = targetresults;
		this.defaultloc = defaultloc;
		if (null != defaultloc && !defaultloc.equals(originloc)) {
			targetlocs.add(defaultloc);
			targetresults.add(new Long(-10000));
		}
	}
}
