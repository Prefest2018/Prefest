package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MyEnumInstance extends MyExpressionTree{
	protected String name = null;
	protected int index = -1;
	public MyEnumInstance() {
		super();
		this.type = ResultType.ENUM;
		this.resultType = ResultType.ENUM;
		this.content =  new MyEnumInstanceContent(this);
	}
	
	public boolean equals(Object anotherinstance) {
		if (anotherinstance != null && anotherinstance instanceof MyEnumInstance) {
			if (((MyEnumInstance)anotherinstance).name.equals(name) && ((MyEnumInstance)anotherinstance).index == index) {
				return true;
			}
		}
		return false;
	}

}
