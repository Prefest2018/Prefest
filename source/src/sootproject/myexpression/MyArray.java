package sootproject.myexpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Value;

public class MyArray extends MyVariable{
	public MyArray(Value firstValue, MyVariable parentVal) {
		super(firstValue, parentVal);
		this.trueExp = new MyExpression(new MyArrayContent());
		this.eqVals = new HashSet<MyArray>();
		this.eqVals.add(this);
	}


	
	public MyVariable getInnerVal(Value value, MyExpression exp) {
//		MyVariable returnVal = arrayList.get(value.equivHashCode());
//		if (null == returnVal) {
		if (null == this.trueExp) {
			this.trueExp = new MyExpression(new MyArrayContent());
		}
			MyVariable returnVal = new MyVariable(value, this);
			returnVal.isArrayRef = true;
			returnVal.trueExp = getVal(exp);
//			arrayList.put(value.equivHashCode(), returnVal);
//		}
		return returnVal;
	}
	
	protected Set<MyArray> eqVals = null;

	public void setVal(MyExpression indexexp, MyExpression setexp) {
		if (null == indexexp) {
			return;
		}
		if (this.trueExp.content instanceof MyArrayContent && !indexexp.interestRelated) {
			Object indexvalue = indexexp.calculate();
			if (null == indexvalue) {
				return;
			} else {
				MyArrayContent newexp = ((MyArrayContent)this.trueExp.content).getClone();
				newexp.contentlist.put(indexvalue, setexp);
				this.trueExp.content = newexp;
				return;
			}
		}

		MyExpression newexp = new MyExpression(OperationType.PUT, this.trueExp, indexexp);
		newexp.content = setexp;
		newexp.interestRelated = trueExp.interestRelated || indexexp.interestRelated;
		newexp.unknown = trueExp.unknown || indexexp.unknown;
		if (null != setexp) {
			newexp.interestRelated |= setexp.interestRelated;
			newexp.unknown |= setexp.unknown;
		}
		for (MyArray arrayref : eqVals) {
			arrayref.trueExp = newexp;
		}
	}
	
	public MyExpression getVal(MyExpression indexexp) {
		if (null == indexexp) {
			return null;
		}
		if (this.trueExp.content instanceof MyArrayContent && !indexexp.interestRelated) {
			Object indexvalue = indexexp.calculate();
			if (null == indexvalue) {
				return null;
			} else {
				return ((MyArrayContent)this.trueExp.content).contentlist.get(indexvalue);
			}
		}

		MyExpression exp = new MyExpression(OperationType.SELECT, this.trueExp, indexexp);
		exp.content = null;
		exp.interestRelated = trueExp.interestRelated || indexexp.interestRelated;
		exp.unknown = trueExp.unknown || indexexp.unknown;
		return exp;
	}
}
