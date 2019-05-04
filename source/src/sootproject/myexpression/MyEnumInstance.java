package sootproject.myexpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MyEnumInstance extends MyVariable implements ContentInterface{
	protected String name = null;
	protected int index = -1;
	public Map<String, MyExpression> fieldmap = null;
	public MyEnumInstance() {
		super(null, null);	
		trueExp = new MyExpression(this);
		this.isInstance = true;
		this.instanceceqVals = new HashSet<MyVariable>();
		instanceceqVals.add(this);
		fieldmap = new HashMap<String, MyExpression>();
	}
	
	public boolean equals(Object anotherinstance) {
		if (anotherinstance != null && anotherinstance instanceof MyEnumInstance) {
			if (((MyEnumInstance)anotherinstance).name.equals(name) && ((MyEnumInstance)anotherinstance).index == index) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map<String, MyExpression> getfieldmap() {
		return fieldmap;
	}

	@Override
	public ContentInterface getClone() {
		return this;
	}

}
