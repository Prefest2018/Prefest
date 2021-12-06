package sootproject.myexpression;

import java.util.HashMap;
import java.util.Map;

import data.LocalValMap;
import data.RefValMap;
import sootproject.analysedata.MyInterest;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.jimple.Expr;
import soot.jimple.Ref;
import soot.jimple.internal.AbstractBinopExpr;
import soot.jimple.internal.AbstractUnopExpr;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JAndExpr;
import soot.jimple.internal.JCastExpr;
import soot.jimple.internal.JCmpExpr;
import soot.jimple.internal.JCmpgExpr;
import soot.jimple.internal.JCmplExpr;
import soot.jimple.internal.JDivExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JGtExpr;
import soot.jimple.internal.JInstanceOfExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JLengthExpr;
import soot.jimple.internal.JLtExpr;
import soot.jimple.internal.JMulExpr;
import soot.jimple.internal.JNeExpr;
import soot.jimple.internal.JNegExpr;
import soot.jimple.internal.JNewArrayExpr;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JOrExpr;
import soot.jimple.internal.JRemExpr;
import soot.jimple.internal.JShlExpr;
import soot.jimple.internal.JShrExpr;
import soot.jimple.internal.JSubExpr;
import soot.jimple.internal.JUshrExpr;
import soot.jimple.internal.JXorExpr;
import sootproject.tool.Util;
import tools.Logger;

public class MyExpression extends MyExpressionInterface implements MyExpressionObject{
	
	public boolean equals(Object another) {
		if (another instanceof MyExpression) {
			if (((MyExpression)another).content!= null && content != null) {
				return ((MyExpression)another).content.equals(content) && ((MyExpression)another).type == type;
			} else {
				return this == another;
			}

		} else {
			return this == another;
		}
	}
	
	public MyExpression (Object content, ResultType resultType) {
		this.content = content;
		this.type = resultType;
		this.resultType = resultType;
		this.unknown = false;
	}
	
	public MyExpression (MyInterest interest) {
		this.type = ResultType.INTEREST;
		this.content = interest;
		this.interestRelated = true;
		this.unknown = interest.isUnKnown();
	}
	
	public MyExpression (MyEnumInstance myenum) {
		this.type = ResultType.ENUM;
		this.content = myenum;
	}
	
	public boolean isenum() {
		if (this.type == ResultType.ENUM) {
			return true;
		}
		return false;
	}
	
	public MyExpression(MyArrayContent array) {
		this.type = ResultType.ARRAY;
		this.content = array;
	}
//	
//		this.type = ResultType.INSTANCE;
//		this.content = instance;
	
	public MyExpression(Map<Object, MyExpressionInterface> array) {
		this.type = ResultType.ARRAY;
		this.content = array;
	}
	
	
//		String tag = null;
//			tag = ((MySPArrayContent)this.content).spname;
//		return tag;
	
	public MyExpression (OperationType operator, MyExpressionInterface param1, MyExpressionInterface param2) {
		this.type = ResultType.EXPRESSION;
		this.operator = operator;
		this.param1 = param1;
		this.param2 = param2;
		if (null != param1) {
			this.interestRelated = param1.interestRelated;
			this.unknown = param1.unknown;
		}
		if (null != param2) {
			interestRelated |= param2.interestRelated;
			unknown |= param2.unknown;
		}
	}
	
	
	public static MyExpression getNagExpression(MyExpression originExp) {
		MyExpression newExp = new MyExpression(OperationType.NEG, originExp, null);
		return newExp;
	}

	
	public static MyExpressionInterface getMyExpression(Expr exp, LocalValMap local, RefValMap ref) {
		OperationType optype = null;
		MyExpressionInterface var1 = null;
		MyExpressionInterface var2 = null;
		if (exp instanceof AbstractBinopExpr) {
			if (exp instanceof JCmpExpr) {
				optype = OperationType.EQUAL;
			} else if (exp instanceof JEqExpr) {
				optype = OperationType.EQUAL;
			} else if (exp instanceof JNeExpr) {
				optype = OperationType.NAEQ;
			} else if (exp instanceof JAddExpr) {
				optype = OperationType.ADD;
			} else if (exp instanceof JAndExpr) {
				optype = OperationType.AND;
			} else if (exp instanceof JOrExpr) {
				optype = OperationType.OR;
			} else if (exp instanceof JXorExpr){
				optype = OperationType.XOR;
			} else if (exp instanceof JSubExpr) {
				optype = OperationType.SUB;
			} else if (exp instanceof JMulExpr) {
				optype = OperationType.MUL;
			} else if (exp instanceof JDivExpr) {
				optype = OperationType.DIV;
			} else if (exp instanceof JLeExpr) {
				optype = OperationType.LE;
			} else if (exp instanceof JLtExpr) {
				optype = OperationType.LT;
			} else if (exp instanceof JGeExpr) {
				optype = OperationType.GE;
			} else if (exp instanceof JGtExpr) {
				optype = OperationType.GT;
			} else if (exp instanceof JCmplExpr) {
				optype = OperationType.LT;
			} else if (exp instanceof JCmpgExpr) {
				optype = OperationType.GT;
			} else if (exp instanceof JRemExpr) {
				optype = OperationType.MOD;
			} else if (exp instanceof JShlExpr) {
				optype = null;
			} else if (exp instanceof JShrExpr) {
				optype = null;
			} else if (exp instanceof JUshrExpr) {
				optype = null;
			} else {
				System.out.println("error: unsolved binary operation:" + exp.getClass());
				Logger.log("error: unsolved binary operation:" + exp.getClass());
			}
			if (optype != null) {
				var1 = Util.getVal(((AbstractBinopExpr)exp).getOp1(), local, ref);
				var2 = Util.getVal(((AbstractBinopExpr)exp).getOp2(), local, ref);
				MyExpression newExp = new MyExpression(optype, var1, var2);
				if (var1 != null && var2 != null) {
					return newExp;
				} else if (var1 == null && var2 ==null) {
					return null;
				} else {
					if (optype != OperationType.OR && optype != OperationType.AND) {
						return null;
					}
				}
			}
		} else if (exp instanceof AbstractUnopExpr) {
			if (exp instanceof JLengthExpr) {
				optype = OperationType.LENGTH;
			} else if (exp instanceof JCastExpr) {
				//TODO
			} else if (exp instanceof JNegExpr) {
				optype = OperationType.NEG;
			} else {
				System.out.println("error: unsolved unary operation: " + exp.getClass());
				Logger.log("error: unsolved unary operation: " + exp.getClass());
			}
			
			if (optype != null) {
				var1 =  Util.getVal(((AbstractUnopExpr)exp).getOp(), local, ref);
				if (var1 != null) {
					MyExpression newExp = new MyExpression(optype, var1, null);
					newExp.interestRelated = var1.interestRelated;
					newExp.unknown = var1.unknown;
					return newExp;
				} else {
					return null;
				}
			}
		} else if (exp instanceof JNewExpr) {
			Type exptype = exp.getType();
			if (exptype instanceof RefType) {
				SootClass nowClass = ((RefType)exptype).getSootClass();
				if (nowClass.hasSuperclass() && nowClass.getSuperclass().getName().equals("java.lang.Enum")
						|| nowClass.hasOuterClass() && nowClass.getOuterClass().hasSuperclass() && nowClass.getOuterClass().getSuperclass().getName().equals("java.lang.Enum")) {
					return MyExpressionTree.createEnumInstance();
				} else if (nowClass.getName().equals("java.lang.StringBuilder")) {
					return new MyExpression("", ResultType.STRING);
				} else {
					String typename = nowClass.getName();
					MyExpressionTree tree = ref.getOrCreateSPTree(typename);
					return tree;
				}
			}
		} else if (exp instanceof JCastExpr) {
			Value castOne = ((JCastExpr)exp).getOp();
			return Util.getVal(castOne, local, ref);
		} else if (exp instanceof JNewArrayExpr) {
			return MyExpressionTree.createInitTree();
		} else if (exp instanceof JInstanceOfExpr) {
			return null;
		} else {
			System.out.println("error: unsolved operation (not unary or binary operations):" + exp.getClass());
			Logger.log("error: unsolved operation (not unary or binary operations):" + exp.getClass());
		}
		
		

		return null;
	}
	
	public void setUnknown(boolean unknown) {
		this.unknown = unknown;
	}

	public Object getContent() {
		return content;
	}

	public boolean isUnknown() {
		return unknown;
	}
	
}
