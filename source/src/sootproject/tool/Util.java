package sootproject.tool;

import java.util.HashSet;
import java.util.Map;

import org.objectweb.asm.Type;

import sootproject.myexpression.ExpressionValue;
import sootproject.myexpression.MyArray;
import sootproject.myexpression.MyArrayContent;
import sootproject.myexpression.MyExpression;
import sootproject.myexpression.MyInstanceContent;
import sootproject.myexpression.MyVariable;
import sootproject.myexpression.ResultType;
import soot.ArrayType;
import soot.Local;
import soot.RefType;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JInstanceFieldRef;

public class Util {
	public final static MyVariable getParam(final Value value, Map<Integer, MyVariable> localMaps, final Map<Integer, MyVariable> refMaps) {
		MyVariable myVal = null;
		if (value instanceof Local) {
			myVal = localMaps.get(value.equivHashCode());
			if (null == myVal) {
				if (value.getType() instanceof ArrayType) {
					myVal = new MyArray(value, null);
				} else {
					myVal = new MyVariable(value, null);
				}
				localMaps.put(value.equivHashCode(), myVal);
			}
		} else if (value instanceof Ref) {
			if (value instanceof JArrayRef) {
				Value arrayBase = ((JArrayRef)value).getBase();
				MyVariable arrayVal = null;
				if (arrayBase instanceof Local) {
					arrayVal = localMaps.get(arrayBase.equivHashCode());
					if (null == arrayVal) {
						arrayVal = new MyArray(arrayBase, null);
						localMaps.put(arrayBase.equivHashCode(), arrayVal);
					}
				} else if (arrayBase instanceof Ref) {
					arrayVal = refMaps.get(arrayBase.equivHashCode());
					if (null == arrayVal) {
						arrayVal = new MyArray(arrayBase, null);
						refMaps.put(arrayBase.equivHashCode(), arrayVal);
					}
				}
				if (null == arrayVal.getTrueExp()) {
					arrayVal.setTrueExp(new MyExpression(new MyArrayContent()));
				}
				MyExpression indexexp = Util.getVal(((JArrayRef)value).getIndex(), localMaps, refMaps);
				return ((MyArray)arrayVal).getInnerVal(value, indexexp);
			} else if (value instanceof JInstanceFieldRef) {
				JInstanceFieldRef nowref = ((JInstanceFieldRef)value);
				Value instancebase = nowref.getBase();
				MyVariable instanceval = null;
				if (instancebase instanceof Local) {
					instanceval = localMaps.get(instancebase.equivHashCode());
					if (null == instanceval) {
						instanceval = new MyVariable(instancebase, null, true);
						localMaps.put(instancebase.equivHashCode(), instanceval);
					}
				} else if (instancebase instanceof Ref) {
					instanceval = refMaps.get(instancebase.equivHashCode());
					if (null == instanceval) {
						instanceval = new MyVariable(instancebase, null, true);
						refMaps.put(instancebase.equivHashCode(), instanceval);
					}
				}
				if (null == instanceval.getTrueExp()) {
					instanceval.setTrueExp(new MyExpression(new MyInstanceContent()));
				}
				instanceval.isInstance = true;
				return instanceval.instanceGetInnerVal(nowref);

			} else {
				myVal = refMaps.get(value.equivHashCode());
				if (null == myVal) {
					if (value.getType() instanceof ArrayType) {
						myVal = new MyArray(value, null);
					} else {

							myVal = new MyVariable(value, null);
					}
					refMaps.put(value.equivHashCode(), myVal);
				}
			}
		} else {
			System.out.println("error: 'Util' exists parameters are neither 'Local' nor 'Ref'!!");
		}
		return myVal;
	}
	
	
	
	public final static MyExpression getConstant(final Constant constant) {
		MyExpression exp = null;
		if (constant instanceof StringConstant) {
			exp = new MyExpression(((StringConstant)constant).value, ResultType.STRING);
		} else if (constant instanceof IntConstant) {
			exp = new MyExpression(((IntConstant)constant).value, ResultType.INT);
		} else if (constant instanceof FloatConstant){
			exp = new MyExpression(((FloatConstant)constant).value, ResultType.FLOAT);
		} else if (constant instanceof DoubleConstant) {
			exp = new MyExpression(((DoubleConstant)constant).value, ResultType.FLOAT);
		} else if (constant instanceof LongConstant) {
			exp = new MyExpression(((LongConstant)constant).value, ResultType.INT);
		} else if (constant instanceof NullConstant) {
			exp = new MyExpression(null, ResultType.NULL);
		}
		
		return exp;
	}
	
	public final static MyExpression getVal(final Value value, Map<Integer, MyVariable> localMaps, final Map<Integer, MyVariable> refMaps) {
		if (value instanceof Local || value instanceof Ref) {
			MyVariable val =  getParam(value, localMaps, refMaps);
			if (val != null) {
				return val.getTrueExp();
			}
		} else if (value instanceof Constant) {
			return getConstant((Constant)value);
		} 
		
		return null;
	}
}
