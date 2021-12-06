package sootproject.myexpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import soot.Value;
import sootproject.analysedata.MyInterest;

public class MyArrayContent{
	protected Map<Object, MyExpressionTree> contentlist = null;

	public MyArrayContent() {
		contentlist = new HashMap<Object, MyExpressionTree>();
	}
	
	public boolean isEmpty() {
		return null == this.contentlist || this.contentlist.isEmpty();
	}
	
	private MyArrayContent(Map<Object, MyExpressionTree> contentlist) {
		this.contentlist = new HashMap<Object, MyExpressionTree>(contentlist);
	}
	
	public Map<Object, MyExpressionTree> getContentList() {
		return this.contentlist;
	}
	
	public MyExpressionTree get(Object key) {
		return contentlist.get(key);
	}
	
	public void put(Object key, MyExpressionTree value) {
		contentlist.put(key, value);
	}
	
	public MyArrayContent getClone() {

		return new MyArrayContent(contentlist);
	}
	
	public void addAll(MyArrayContent another) {
		contentlist.putAll(another.contentlist);
	}

//		this.index = oldOne.index;
//		this.indexlist = new LinkedList<MyExpression>(oldOne.indexlist);
//		this.contentlist = new LinkedList<MyExpression>(oldOne.contentlist);
	
//	
//			index++;
//			indexlist.add(indexexp);
//			contentlist.add(contentexp);
	
//			return null;
//		MyExpression exp = new MyExpression(OperationType.SELECT, indexexp, null);
//
//		return exp;
	
//				return indexlist.get(i);
//		return null;
}
