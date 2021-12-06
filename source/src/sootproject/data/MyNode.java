package sootproject.data;

import java.util.HashSet;
import java.util.Set;

public abstract class MyNode {
	protected static int indexCount = 0;
	protected Object innerNode = null;
	protected MyNode parentNode = null;
	protected Set<MyNode> childNodes = null;
	protected int startLine = 0;
	protected int length = 0;
	protected int logIndex = -1;
	protected boolean visited = false;
	//only for MyCompilationUnit
	public MyNode(Object node) {
		this.innerNode = node;
    	this.childNodes = new HashSet<MyNode>();
	}
	public MyNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(MyNode parentNode) {
		this.parentNode = parentNode;
	}
	public boolean isVisited() {
		return visited;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public Object getInnerNode() {
		return innerNode;
	}
	public int getStartLine() {
		return startLine;
	}
	public int getLength() {
		return length;
	}
	public int getLogIndex() {
		return logIndex;
	}
	public void setLogIndex(int logIndex) {
		this.logIndex = logIndex;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public abstract void stub();
	public Set<MyNode> getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(Set<MyNode> childNodes) {
		this.childNodes = childNodes;
	}
	

}
