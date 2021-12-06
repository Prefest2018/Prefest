package sootproject.myexpression;

import java.util.HashMap;

public class MyEnumInstanceContent extends MyArrayContent{
	protected MyEnumInstance instance = null;
	public MyEnumInstanceContent(MyEnumInstance instance) {
		super();
		this.instance = instance;
	}
	public MyEnumInstance getEnum() {
		return instance;
	}
}
