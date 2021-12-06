package sootproject.myexpression;

import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;

import soot.Body;
import soot.Unit;

public class MyIfConstraint extends MyConstraint{
	public MyIfConstraint(MyExpressionInterface myexp, Unit unit, Body body, int originresult, String originloc, long targetresult, String targetloc) {
		super(myexp, unit, body);
		this.originresult = originresult;
		this.originloc = originloc;
		this.targetresults = new ArrayList<Long>();
		this.targetresults.add(targetresult);
		this.targetlocs = new ArrayList<String>();
		this.targetlocs.add(targetloc);
	}
}
