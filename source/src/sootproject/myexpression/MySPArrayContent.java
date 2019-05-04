package sootproject.myexpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MySPArrayContent extends MyArrayContent{
	public String spname = null;
	public MySPArrayContent(String spname) {
		this.spname = spname;
		contentlist = new HashMap<Object, MyExpression>();
	}
	
	public MySPArrayContent(String spname, Map<Object, MyExpression> contentlist) {
		this.spname = spname;
		this.contentlist = new HashMap<Object, MyExpression>(contentlist);
	}
	
	public MyArrayContent getClone() {
		return new MySPArrayContent(spname , contentlist);
	}
}
