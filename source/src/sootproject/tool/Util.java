package sootproject.tool;

import java.util.HashSet;
import java.util.Map;

import org.objectweb.asm.Type;

import GUI.Main;
import data.LocalValMap;
import data.RefValMap;
import sootproject.myexpression.MyExpressionTree;
import sootproject.myexpression.ExpressionValue;
import sootproject.myexpression.MyArrayContent;
import sootproject.myexpression.MyExpression;
import sootproject.myexpression.MyExpressionInterface;
import sootproject.myexpression.MyVariable;
import sootproject.myexpression.ResultType;
import soot.ArrayType;
import soot.Local;
import soot.PrimType;
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

	public final static MyVariable getParam(final Value value, LocalValMap localMap, final RefValMap refMap) {
		MyVariable myVal = null;
		if (value instanceof Local) {
			myVal = localMap.get(value);
			if (null == myVal) {
				if (value.getType() instanceof PrimType){
					myVal = new MyVariable(value, false);
				} else {
					//if (value.getType() instanceof ArrayType) 
					MyExpressionTree arrayTree = MyExpressionTree.createInitTree();
					myVal = new MyVariable(value, arrayTree, false);
				}
				localMap.put(value, myVal);
			}
		} else if (value instanceof Ref) {
			if (value instanceof JArrayRef) {
				Value arrayBase = ((JArrayRef)value).getBase();
				MyVariable arrayVal = null;
				if (arrayBase instanceof Local) {
					arrayVal = localMap.get(arrayBase);
					if (null == arrayVal) {
						MyExpressionTree arrayTree = MyExpressionTree.createInitTree();
						arrayVal = new MyVariable(arrayBase, arrayTree, false);
						localMap.put(arrayBase, arrayVal);
					}
				} else if (arrayBase instanceof Ref) {
					arrayVal = refMap.get(arrayBase);
					if (null == arrayVal) {
						MyExpressionTree arrayTree = refMap.getTree(value);
						if (null == arrayTree) {
							arrayTree = MyExpressionTree.createInitTree();
							refMap.setTree(value, arrayTree);
						}
						arrayVal = new MyVariable(arrayBase, arrayTree, false);
						refMap.put(arrayBase, arrayVal);
					}
				}
				MyExpressionInterface indexexp = Util.getVal(((JArrayRef)value).getIndex(), localMap, refMap);
				MyExpressionTree tree = (MyExpressionTree) arrayVal.getTrueExp();
				return new MyVariable(value, tree.getChild(indexexp, value.getType() instanceof ArrayType), true);
			} else if (value instanceof JInstanceFieldRef) {
				JInstanceFieldRef nowref = ((JInstanceFieldRef)value);
				Value instancebase = nowref.getBase();
				String fieldname = nowref.getField().getName();
				MyVariable instanceval = null;
				if (instancebase instanceof Local) {
					instanceval = localMap.get(instancebase);
					if (null == instanceval) {
						MyExpressionTree tree = MyExpressionTree.createInitTree();
						instanceval = new MyVariable(instancebase, tree, false);
						localMap.put(instancebase, instanceval);
					}

				} else if (instancebase instanceof Ref) {
					instanceval = refMap.get(instancebase);
					if (null == instanceval) {
						MyExpressionTree tree = refMap.getTree(value);
						if (null == tree) {
							tree = MyExpressionTree.createInitTree();
							refMap.setTree(value, tree);
						}
						instanceval = new MyVariable(instancebase, tree, false);
						refMap.put(instancebase, instanceval);
					}
				}
				MyExpressionTree parenttree = null;
				if (null == instanceval.getTrueExp() || instanceval.getTrueExp() instanceof MyExpression) {
					parenttree = MyExpressionTree.createInitTree();
					instanceval.setTrueExp(parenttree);
				} else {
					parenttree = (MyExpressionTree)instanceval.getTrueExp();
				}
				if (null == parenttree) {
					System.out.println();
				}
				MyExpressionTree tree = parenttree.getChild(fieldname, value, refMap);
				return new MyVariable(value, tree, true); 

			} else {
				if (value instanceof ParameterRef || value instanceof ThisRef) {
					myVal = localMap.get(value);
					if (null == myVal) {
						MyExpressionTree tree = null;
						String typename = value.getType().toString();
						if (typename.contains(Main.packagename)) {
							tree = refMap.getOrCreateSPTree(value.getType().toString());
						} else {
							tree = MyExpressionTree.createInitTree();
						}
						myVal = new MyVariable(value, tree, false);
						localMap.put(value, myVal);
					}
				} else {
					myVal = refMap.get(value);
					if (null == myVal) {
						MyExpressionTree tree = MyExpressionTree.createInitTree();
						refMap.setTree(value, tree);
						myVal = new MyVariable(value, tree, false);
						refMap.put(value, myVal);
					}
				}
				
				
//				myVal = refMap.get(value);
//						MyExpressionTree tree = MyExpressionTree.createInitTree();
//						myVal = new MyVariable(value, tree, true);
//						MyExpressionTree tree = null;
//						tree = refMap.getTree(value, isSP);
//							tree = MyExpressionTree.createInitTree();
//							refMap.setTree(value, tree);
//						myVal = new MyVariable(value, tree, !isSP);
//					refMap.put(value, myVal);
			}
		} else {
			System.out.println("error: 'Util' exists parameters are neither 'Local' nor 'Ref'!!");
		}
		return myVal;
	}
	
	
	
	public final static MyExpressionInterface getConstant(final Constant constant) {
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
	
	public final static MyExpressionInterface getVal(final Value value, LocalValMap localMap, final RefValMap refMap) {
		if (value instanceof Local || value instanceof Ref) {
			MyVariable val =  getParam(value, localMap, refMap);
			if (val != null) {
				return val.getTrueExp();
			}
		} else if (value instanceof Constant) {
			return getConstant((Constant)value);
		} 
		
		return null;
	}
}
