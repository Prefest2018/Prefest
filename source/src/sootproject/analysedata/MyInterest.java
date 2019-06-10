package sootproject.analysedata;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import data.InterestValue;
import sootproject.data.MyNode;
import sootproject.myexpression.MyExpression;
import sootproject.myexpression.MyVariable;
import sootproject.myexpression.ResultType;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;

public abstract class MyInterest implements Comparable<MyInterest>{
	protected Set<MyVariable> relatedVals = null;
	protected Set<MyExpression> constraints = null;
	protected String name = null;
	protected String defaultValue = null;
	protected MyExpression exp = null;
	protected ResultType type = ResultType.DEFAULT;
	public boolean unKnown = false;
	protected List<String> possibleValues = null;
	protected MyExpression selfexp = null;
	public MyInterest(String name) {
		this.name = name;
		this.exp = new MyExpression(this);
		this.selfexp = new MyExpression(this);
		relatedVals = new HashSet<MyVariable>();
	}

	
	public MyExpression getMyInterestExpression() {
		return exp;
	}
	
	abstract public InterestValue getInterestValue(String value);
	
	
	
	public MyVariable addAffectedVariable(MyVariable val) {
		relatedVals.add(val);
		return val;
	}

	public String getName() {
		return name;
	}
	
	public void setUnknown(boolean unknown) {
		this.unKnown = unknown;
		exp.setUnknown(unknown);
	}
	
	abstract public ResultType getResultType();
	abstract public void setResultType(String typestr);

	public List<String> getPossibleValues() {
		return possibleValues;
	}

	public MyExpression getSelfexp() {
		return selfexp;
	}

	public void setSelfexp(MyExpression selfexp) {
		this.selfexp = selfexp;
	}
	
	abstract public List<InterestValue> getallpossibleValues();
	
	@Override
	public int compareTo(MyInterest another) {
		return this.name.compareTo(another.name);
	}
}
