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

//	private MyArrayContent(MyArrayContent oldOne) {
//		this.index = oldOne.index;
//		this.indexlist = new LinkedList<MyExpression>(oldOne.indexlist);
//		this.contentlist = new LinkedList<MyExpression>(oldOne.contentlist);
//	}
	
//	
//	public void setVal(MyExpression indexexp, MyExpression contentexp) {
//		if (indexexp != null && contentexp != null) {
//			index++;
//			indexlist.add(indexexp);
//			contentlist.add(contentexp);
//		}
//	}
	
//	public MyExpression getVal(MyExpression indexexp) {
//		if (null == indexexp) {
//			return null;
//		}
//		MyExpression exp = new MyExpression(OperationType.SELECT, indexexp, null);
//		exp.content = new MyArrayContent(this);
//		for (int i = 0; i < index; i++) {
//			exp.interestRelated |= indexlist.get(i).interestRelated || contentlist.get(i).interestRelated;
//			exp.unknown |= indexlist.get(i).unknown || contentlist.get(i).unknown;;
//		}
//		return exp;
//	}
	
//	public MyExpression getIndex(MyExpression contentExp) {
//		for (int i = 0; i < contentlist.size(); i++) {
//			if (contentExp.equals(contentlist.get(i))) {
//				return indexlist.get(i);
//			}
//		}
//		return null;
//	}
}
