package sootproject.data;

import soot.tagkit.Host;

public class MyTryCatch extends MyNode{
	private int branchid = -1;
	private String tag = null;

	public String getTag() {
		return tag;
	}

	public int getBranchid() {
		return branchid;
	}

	public void setId(int logid, int branchid) {
		this.logIndex = logid;
		this.branchid = branchid;
		this.tag = logIndex + "-" + branchid;
	}

	public MyTryCatch(Object node) {
		super(node);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void stub() {
		// TODO Auto-generated method stub
		
	}

}
