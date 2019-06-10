package sootproject.myexpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import soot.Value;
import sootproject.analysedata.MyInterest;

public class MyArrayContent{
	protected Map<Object, MyExpression> contentlist = null;

	public MyArrayContent() {
		contentlist = new HashMap<Object, MyExpression>();
	}
	
	private MyArrayContent(Map<Object, MyExpression> contentlist) {
		this.contentlist = new HashMap<Object, MyExpression>(contentlist);
	}
	
	public Map<Object, MyExpression> getContentList() {
		return this.contentlist;
	}
	
	public MyArrayContent getClone() {

		return new MyArrayContent(contentlist);
	}

}
