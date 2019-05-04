package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sootproject.analysedata.MyInterest;
import soot.Local;
import soot.Value;
import soot.jimple.ParameterRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;
import tools.Logger;
import sootproject.tool.Util;

public class MyVariable extends MyExpressionObject{
	protected boolean interestRelated = false;
	protected Set<MyInterest> affectedInterests = null;
	protected boolean unknown = false;
	protected Value value = null;
	protected boolean isArrayRef = false;
	public boolean isInstance = false;
	protected boolean isInstanceRef = false;
	private ResultType resultType = ResultType.DEFAULT;
	protected MyExpression trueExp = null;
	protected MyVariable parentVal = null;
	public Set<MyVariable> instanceceqVals = null;
	//private IBinding bind = null;

	public MyVariable(Value firstValue, MyVariable parentVal) {
		this.value = firstValue;
		this.parentVal = parentVal;
		this.instanceceqVals = new HashSet<MyVariable>();
		instanceceqVals.add(this);
		affectedInterests = new HashSet<MyInterest>();
	}
	
	
	
	public boolean isInterestRelated() {
		return interestRelated;
	}

	public void setInterestRelated(boolean interestRelated) {
		this.interestRelated = interestRelated;
	}
	public MyExpression getTrueExp() {
		return trueExp;
	}

	public void setTrueExp(MyVariable trueVal, MyExpression trueExp, Map<Integer, MyVariable> leftlocalMaps, Map<Integer, MyVariable> rightlocalMaps,  Map<Integer, MyVariable> refMaps) {
		if (isArrayRef) {
			Value indexvalue = ((JArrayRef)value).getIndex();
			MyExpression indexexp = Util.getVal(indexvalue, leftlocalMaps, refMaps);
			((MyArray)parentVal).setVal(indexexp, trueExp);
		} else if (trueVal != null && trueVal.isArrayRef) {
			Value indexvalue = ((JArrayRef)trueVal.value).getIndex();
			MyExpression indexexp = Util.getVal(indexvalue, rightlocalMaps, refMaps);
			this.trueExp = ((MyArray)trueVal.parentVal).getVal(indexexp);
			if (null != this.trueExp) {
				this.interestRelated = this.trueExp.interestRelated;
			}
		} else if (isInstanceRef) {
			String fieldname = ((JInstanceFieldRef)value).getField().getName();
			if (parentVal.isInstance) {
				parentVal.instanceSetVal(fieldname, trueExp);
			} else {
				System.out.println("error: the parent node of an instance ref is not an instance!!");
				Logger.log("error: the parent node of an instance ref is not an instance!!");
			}
		} else {
			this.trueExp = trueExp;
			if (null != trueExp) {
				this.interestRelated = trueExp.interestRelated;
				this.resultType = trueExp.resultType;
				if (trueExp.content instanceof ContentInterface) {
					this.isInstance = true;

				}
			}
		}
		
		if (this instanceof MyArray && (null != trueVal) && trueVal instanceof MyArray) {
			((MyArray)this).eqVals.remove(this);
			((MyArray)trueVal).eqVals.add((MyArray)this);
			((MyArray)this).eqVals = ((MyArray)trueVal).eqVals;
		}
		if (null != trueVal) {
			this.instanceceqVals.remove(this);
			trueVal.instanceceqVals.add(this);
			this.instanceceqVals = trueVal.instanceceqVals;
		} else {
			if (!isInstanceRef) {
				this.instanceceqVals.remove(this);
				this.instanceceqVals = new HashSet<MyVariable>();
				this.instanceceqVals.add(this);
			}
		}
	}
	
	public void setTrueExp(MyExpression trueExp, Map<Integer, MyVariable> localMaps,  Map<Integer, MyVariable> refMaps) {
		setTrueExp(null, trueExp, localMaps, localMaps, refMaps);
	}
	
	public void setTrueExp(MyExpression trueExp) {
		this.trueExp = trueExp;
	}

	public boolean ifEquiv(Value value) {
		return this.value.equivTo(value);
	}

	public Set<MyInterest> getAffectedInterests() {
		return affectedInterests;
	}

	public void setAffectedInterests(Set<MyInterest> affectedInterests) {
		this.affectedInterests = affectedInterests;
	}

	protected String instancefieldname = null;
	public MyVariable(Value firstValue, MyVariable parentVal, boolean isinstance) {
		this.value = firstValue;
		this.parentVal = parentVal;
		this.isInstance = true;
		this.instanceceqVals = new HashSet<MyVariable>();
		instanceceqVals.add(this);
		affectedInterests = new HashSet<MyInterest>();
		this.trueExp = new MyExpression(new MyInstanceContent());
	}

	public MyVariable instanceGetInnerVal(JInstanceFieldRef ref) {
		String fieldname = ref.getField().getName();
		MyVariable returnVal = new MyVariable(ref, this);
		returnVal.isInstanceRef = true;
		returnVal.instancefieldname = fieldname;
		returnVal.trueExp = instancegetVal(fieldname);
		return returnVal;
	}
	

	public void instanceSetVal(String fieldname, MyExpression setexp) {
		if (null == trueExp) {
			trueExp = new MyExpression(new MyInstanceContent());
		}
		
		if (null != setexp) {
			if (this.trueExp.content instanceof ContentInterface) {
				ContentInterface newexp = ((ContentInterface)this.trueExp.content).getClone();
				newexp.getfieldmap().put(fieldname, setexp);
				this.trueExp.content = newexp;
			} else {
				MyExpression newexp = new MyExpression(OperationType.SETFIELD, this.trueExp, setexp);
				newexp.content = fieldname;
				newexp.interestRelated = setexp.interestRelated || trueExp.interestRelated;
				newexp.unknown = setexp.unknown || trueExp.unknown;
				this.trueExp = newexp;
			}

		}
		Set<MyVariable> parentones = new HashSet<MyVariable>();
		String nowfieldname = null;
		for (MyVariable instance : instanceceqVals) {
			instance.trueExp = this.trueExp;
			if (instance.isInstanceRef) {
				parentones.addAll(instance.parentVal.instanceceqVals);
				nowfieldname = instance.instancefieldname;
			}
		}
		if (!parentones.isEmpty()) {
			MyExpression newparentexp = null;
			for (MyVariable nowval : parentones) {
				if (null == newparentexp) {
					if (null == nowval.trueExp) {
						nowval.trueExp = new MyExpression(new MyInstanceContent());
					}
					if (null != this.trueExp) {
						if (nowval.trueExp.content instanceof ContentInterface) {
							ContentInterface newexp = ((ContentInterface)nowval.trueExp.content).getClone();
							newexp.getfieldmap().put(nowfieldname, this.trueExp);
							nowval.trueExp.content = newexp;
						} else {
							MyExpression newexp = new MyExpression(OperationType.SETFIELD, nowval.trueExp, this.trueExp);
							newexp.content = nowfieldname;
							newexp.interestRelated = nowval.interestRelated || this.trueExp.interestRelated;
							newexp.unknown = nowval.unknown || this.trueExp.unknown;
							nowval.trueExp = newexp;
						}

					}
					newparentexp = nowval.trueExp;
				} else {
					nowval.trueExp = newparentexp;
				}
			}
		}
		
	}
	
	public MyExpression instancegetVal(String fieldname) {
		if (null == trueExp) {
			trueExp = new MyExpression(new MyInstanceContent());
		}
		MyExpression exp = null;
		if (this.trueExp.content instanceof ContentInterface) {
			exp = ((ContentInterface)this.trueExp.content).getfieldmap().get(fieldname);
		}else {
			exp = new MyExpression(OperationType.GETFIELD, this.trueExp, null);
			exp.content = fieldname;
			exp.interestRelated = trueExp.interestRelated;
			exp.unknown = trueExp.unknown;
		}
		return exp;
	}

}
