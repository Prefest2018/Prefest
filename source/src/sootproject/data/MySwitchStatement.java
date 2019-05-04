package sootproject.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.tagkit.AbstractHost;

public class MySwitchStatement extends MyNode implements MyBranch{
	private Map<String, LogBranchNode> switchNodes = null;
	public MySwitchStatement(JLookupSwitchStmt node, MyNode parent) {
		super(node);
		parent.childNodes.add(this);
		switchNodes = new HashMap<String, LogBranchNode>();
	}

	public String getloc(Unit unit) {
		String resultloc = null;
		for (String loc : switchNodes.keySet()) {
			if (switchNodes.get(loc).innerNode == unit) {
				resultloc = loc;
			}
		}
		return resultloc;
	}
	
	public String getdefaultloc(List<String> locs) {
		String resultloc = null;
		for (String loc : switchNodes.keySet()) {
			if (!locs.contains(loc)) {
				resultloc = loc;
				break;
			}
		}
		return resultloc;
	}

	@Override
	public void stub() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateBranch(int logid, int branchid, LogBranchNode node) {
		String logstr = logid + "-" + branchid;
		switchNodes.put(logstr, node);
	}

	@Override
	public Collection<LogBranchNode> getBranches() {
		// TODO Auto-generated method stub
		return switchNodes.values();
	}

	@Override
	public LogBranchNode getBranch(int logid, int branchid) {
		String logstr = logid + "-" + branchid;
		return switchNodes.get(logstr);
	}
	
	@Override
	public Unit getBranchUnit(int logid, int branchid) {
		// TODO Auto-generated method stub
		String logstr = logid + "-" + branchid;
		LogBranchNode node = switchNodes.get(logstr);
		Unit unit = null;
		if (node != null) {
			unit = node.innerNode;
		}
		return unit;
	}
	
}
