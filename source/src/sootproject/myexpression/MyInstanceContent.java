package sootproject.myexpression;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class MyInstanceContent implements ContentInterface{
	public Map<String, MyExpression> fieldmap = null;
	public MyInstanceContent() {
		fieldmap = new HashMap<String, MyExpression>();
	}
	
	public MyInstanceContent(Map<String, MyExpression> fieldmap) {
		this.fieldmap = new HashMap<String, MyExpression>(fieldmap);
	}
	
	public MyEnumInstance forcetobeenum() {
		MyEnumInstance newone = new MyEnumInstance();
		newone.fieldmap = this.fieldmap;
		return newone;
	}

	@Override
	public Map<String, MyExpression> getfieldmap() {
		return fieldmap;
	}
	@Override
	public ContentInterface getClone() {
		return new MyInstanceContent(fieldmap);
	}
}
