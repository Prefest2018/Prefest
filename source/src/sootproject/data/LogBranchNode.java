package sootproject.data;

import soot.Unit;

public class LogBranchNode {
	public int innerIndex = -1;
	public int branchIndex = -1;
	public int startLine = -1;
	public int length = -1;
	public Unit innerNode = null;
	public LogBranchNode(int innerIndex, int branchIndex, Unit innerNode, int startLine, int length) {
		this.innerIndex = innerIndex;
		this.branchIndex = branchIndex;
		this.innerNode = innerNode;
		this.startLine = startLine;
		this.length = length;
	}
	
	public LogBranchNode(int innerIndex, int branchIndex,  Unit innerNode) {
		this.innerIndex = innerIndex;
		this.innerNode = innerNode;
		this.branchIndex = branchIndex;
	}

}
