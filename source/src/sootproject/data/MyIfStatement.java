package sootproject.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import soot.Unit;
import soot.jimple.internal.JIfStmt;

public class MyIfStatement extends MyNode implements MyBranch{
	// then is 0, else is 1
	private Map<String, LogBranchNode> thenelseNodes = null;

	public MyIfStatement(JIfStmt node, MyNode parent) {
		super(node);
		thenelseNodes = new HashMap<String, LogBranchNode>();
		parent.childNodes.add(this);
	}

	public String getloc(Unit unit) {
		String loc = null;
		for (String tag : thenelseNodes.keySet()) {
			if (thenelseNodes.get(tag).innerNode == unit) {
				loc = tag;
				break;
			}
		}
		return loc;
	}
	
	public String getanotherloc(Unit unit) {
		String loc = null;
		for (String tag : thenelseNodes.keySet()) {
			if (thenelseNodes.get(tag).innerNode != unit) {
				loc = tag;
				break;
			}
		}
		return loc;
	}

	@Override
	public void stub() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBranch(int logid, int branchid, LogBranchNode node) {
		String logstr = logid + "-" + branchid;
		thenelseNodes.put(logstr, node);
	}

	@Override
	public Collection<LogBranchNode> getBranches() {
		// TODO Auto-generated method stub
		return thenelseNodes.values();
	}

	@Override
	public LogBranchNode getBranch(int logid, int branchid) {
		String logstr = logid + "-" + branchid;
		return thenelseNodes.get(logstr);
	}

	@Override
	public Unit getBranchUnit(int logid, int branchid) {
		// TODO Auto-generated method stub
		String logstr = logid + "-" + branchid;
		LogBranchNode node = thenelseNodes.get(logstr);
		Unit unit = null;
		if (node != null) {
			unit = node.innerNode;
		}
		return unit;
	}
	


}
