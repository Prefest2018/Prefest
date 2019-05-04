package sootproject.data;

import java.util.HashMap;
import java.util.Map;

import soot.Body;
import soot.SootMethod;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;

public class MyMethodDeclaration extends MyNode{
	private Body body = null;
	private Map<Integer, ParameterRef> params = null;
	private ThisRef thisref = null;
	private Map<String, MyReturn> returns = null;
	private Map<String, MyTryCatch> traps = null;

	public MyMethodDeclaration(SootMethod method, Body body) {
		super(method);
		this.body = body;
		this.returns = new HashMap<String, MyReturn>();
		this.traps = new HashMap<String, MyTryCatch>();
		this.params = new HashMap<Integer, ParameterRef>();
	}

	@Override
	public void stub() {
		// TODO Auto-generated method stub
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}
	
	public void addReturn(MyReturn myreturn) {
		this.returns.put(myreturn.getTag(), myreturn);
	}
	
	public void addTrap(MyTryCatch mytrap) {
		this.traps.put(mytrap.getTag(), mytrap);
	}
	
	public MyTryCatch getTrap(int logidNum, int branchNum) {
		String key = logidNum + "-" + branchNum;
		return this.traps.get(key);
	}
	
	public MyReturn getReturn(int logidNum, int branchNum) {
		String key = logidNum + "-" + branchNum;
		return this.returns.get(key);
	}

	public Map<Integer, ParameterRef> getParams() {
		return params;
	}
	
	public ParameterRef getParam(int index) {
		return params.get(index);
	}

	public void addParams(ParameterRef param) {
		this.params.put(param.getIndex(), param);
	}

	public ThisRef getThisref() {
		return thisref;
	}

	public void setThisref(ThisRef thisref) {
		this.thisref = thisref;
	}
	
}
