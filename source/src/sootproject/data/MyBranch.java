package sootproject.data;

import java.util.Collection;
import java.util.Set;

import soot.Unit;

public interface MyBranch {
	public void updateBranch(int logid, int branchid, LogBranchNode node);
	public Collection<LogBranchNode> getBranches();
	public LogBranchNode getBranch(int logid, int branchid);
	public Unit getBranchUnit(int logid, int branchid);
}
