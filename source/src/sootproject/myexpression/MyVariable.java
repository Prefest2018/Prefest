package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.LocalValMap;
import data.RefValMap;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MyPreference;
import soot.Local;
import soot.Value;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;
import tools.Logger;
import sootproject.tool.Util;

public class MyVariable implements MyExpressionObject{
	public Value value = null;
	public boolean isRef;
	protected MyExpressionInterface trueExp = null;

	public MyVariable(Value firstValue, boolean isRef) {
		this.value = firstValue;
		this.isRef = isRef;
	}

	public MyVariable(Value firstValue, MyExpressionInterface initExp, boolean isRef) {
		this.value = firstValue;
		trueExp = initExp;
		this.isRef = isRef;
	}
	
//		return null != this.trueExp && this.trueExp instanceof MyExpressionTree;
	
	
	
//		return interestRelated;
	public MyExpressionInterface getTrueExp() {
		return trueExp;
	}
	

	public void setTrueExp(MyExpressionInterface assignedExp, LocalValMap leftlocalMap, LocalValMap rightlocalMap,  RefValMap refMap) {		
		if (isRef && null != trueExp) {
			MyExpressionTree currenttree = (MyExpressionTree)trueExp;
			Set<MyExpressionTree> parenttrees = currenttree.parents;
			if (value instanceof JArrayRef) {
				Value indexvalue = ((JArrayRef)value).getIndex();
				MyExpressionInterface indexexp = Util.getVal(indexvalue, leftlocalMap, refMap);
				for (MyExpressionTree parenttree : new HashSet<MyExpressionTree>(parenttrees)) {
					parenttree.setChild(indexexp, assignedExp);
				}
			} else if (value instanceof JInstanceFieldRef) {
				String fieldname = ((JInstanceFieldRef)value).getField().getName();
				for (MyExpressionTree parenttree : new HashSet<MyExpressionTree>(parenttrees)) {
					MyExpressionTree childtree = parenttree.setChild(fieldname, assignedExp);
					refMap.setSPTree(value.getType().toString(), childtree);
				}
			}
		} else {
			if (trueExp instanceof MyExpressionTree && null == assignedExp) {
				trueExp = MyExpressionTree.createInitTree();
			} else {
				trueExp = assignedExp;	
			}		
			if (value instanceof StaticFieldRef && null != trueExp) {
				if (trueExp instanceof MyExpressionTree) {
					refMap.setTree(value, (MyExpressionTree)trueExp);
				} else {
					refMap.setTree(value, MyExpressionTree.createLeaf(trueExp));
				}
			}
		}
	}
	
	public void setTrueExp(MyExpressionInterface assignedExp, LocalValMap localMap,  RefValMap refMap) {
		setTrueExp(assignedExp, localMap, localMap, refMap);
	}
	
	public void setTrueExp(MyExpressionInterface trueExp) {
		this.trueExp = trueExp;
	}

	public boolean ifEquiv(Value value) {
		return this.value.equivTo(value);
	}
	

//	protected String instancefieldname = null;
//		this.value = firstValue;
//		this.parentVal = parentVal;
//		this.isInstance = true;
//		this.instanceceqVals = new HashSet<MyVariable>();
//		instanceceqVals.add(this);
//		affectedInterests = new HashSet<MyInterest>();
//		this.trueExp = new MyExpression(new MyInstanceContent());
//
//		String fieldname = ref.getField().getName();
//		MyVariable returnVal = new MyVariable(ref, this);
//		returnVal.isInstanceRef = true;
//		returnVal.instancefieldname = fieldname;
//		returnVal.trueExp = instancegetVal(fieldname);
//		return returnVal;
//	
//
//			trueExp = new MyExpression(new MyInstanceContent());
//		
//				ContentInterface newexp = ((ContentInterface)this.trueExp.content).getClone();
//				newexp.getfieldmap().put(fieldname, setexp);
//				this.trueExp.content = newexp;
//				MyExpression newexp = new MyExpression(OperationType.SETFIELD, this.trueExp, setexp);
//				newexp.content = fieldname;
//				this.trueExp = newexp;
//
//		Set<MyVariable> parentones = new HashSet<MyVariable>();
//		String nowfieldname = null;
//			instance.trueExp = this.trueExp;
//				parentones.addAll(instance.parentVal.instanceceqVals);
//				nowfieldname = instance.instancefieldname;
//			MyExpression newparentexp = null;
//						nowval.trueExp = new MyExpression(new MyInstanceContent());
//							ContentInterface newexp = ((ContentInterface)nowval.trueExp.content).getClone();
//							newexp.getfieldmap().put(nowfieldname, this.trueExp);
//							nowval.trueExp.content = newexp;
//							MyExpression newexp = new MyExpression(OperationType.SETFIELD, nowval.trueExp, this.trueExp);
//							newexp.content = nowfieldname;
//							nowval.trueExp = newexp;
//
//					newparentexp = nowval.trueExp;
//					nowval.trueExp = newparentexp;
//		
//	
//			trueExp = new MyExpression(new MyInstanceContent());
//		MyExpression exp = null;
//			exp = ((ContentInterface)this.trueExp.content).getfieldmap().get(fieldname);
//			exp = new MyExpression(OperationType.GETFIELD, this.trueExp, null);
//			exp.content = fieldname;
//			exp.interestRelated = trueExp.interestRelated;
//			exp.unknown = trueExp.unknown;
//		return exp;

}
