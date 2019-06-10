package sootproject.myexpression;

import java.util.HashMap;
import java.util.Map;

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

public class MyExpression extends MyExpressionObject{
	protected ResultType type = ResultType.DEFAULT;
	protected ResultType resultType = ResultType.DEFAULT;
	protected boolean interestRelated = false;
	protected boolean unknown = false;
	protected OperationType operator = OperationType.DEFAULT;
	protected Object content = null;
	protected MyExpression param1 = null;
	protected MyExpression param2 = null;
	
	
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
		this.unknown = interest.unKnown;
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
	
	public MyExpression(MyInstanceContent instance) {
		this.type = ResultType.INSTANCE;
		this.content = instance;
	}
	
	
	public String getSPTag() {
		String tag = null;
		if (this.content != null && this.content instanceof MySPArrayContent) {
			tag = ((MySPArrayContent)this.content).spname;
		}
		return tag;
	}
	
	public MyExpression (OperationType operator, MyExpression param1, MyExpression param2) {
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
			unknown |= param2.interestRelated;
		}
	}
	
	
	public static MyExpression getNagExpression(MyExpression originExp) {
		MyExpression newExp = new MyExpression(OperationType.NEG, originExp, null);
		return newExp;
	}

	
	public static MyExpression getMyExpression(Expr exp, Map<Integer, MyVariable> locals, Map<Integer, MyVariable> refs) {
		OperationType optype = null;
		MyExpression var1 = null;
		MyExpression var2 = null;
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
				var1 = Util.getVal(((AbstractBinopExpr)exp).getOp1(), locals, refs);
				var2 = Util.getVal(((AbstractBinopExpr)exp).getOp2(), locals, refs);
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
			} else if (exp instanceof JNegExpr) {
				optype = OperationType.NEG;
			} else {
				System.out.println("error: unsolved unary operation: " + exp.getClass());
				Logger.log("error: unsolved unary operation: " + exp.getClass());
			}
			
			if (optype != null) {
				var1 =  Util.getVal(((AbstractUnopExpr)exp).getOp(), locals, refs);
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
				if (nowClass.hasSuperclass() && nowClass.getSuperclass().getName().equals("java.lang.Enum")) {
					MyEnumInstance myenum = new MyEnumInstance();
					return myenum.trueExp;
				} else if (nowClass.hasOuterClass() && nowClass.getOuterClass().hasSuperclass() && nowClass.getOuterClass().getSuperclass().getName().equals("java.lang.Enum")) {
					MyEnumInstance myenum = new MyEnumInstance();
					return myenum.trueExp;
				} else if (nowClass.getName().equals("java.lang.StringBuilder")) {
					return new MyExpression("", ResultType.STRING);
				} else {
					return new MyExpression(new MyInstanceContent());
				}
			}
		} else if (exp instanceof JCastExpr) {
			Value castOne = ((JCastExpr)exp).getOp();
			return Util.getVal(castOne, locals, refs);
		} else if (exp instanceof JNewArrayExpr) {
			return new MyExpression(new MyArrayContent());
		} else if (exp instanceof JInstanceOfExpr) {
			return null;
		} else {
			System.out.println("error: unsolved operation (not unary or binary operations):" + exp.getClass());
			Logger.log("error: unsolved operation (not unary or binary operations):" + exp.getClass());
		}
		
		

		return null;
	}
	
	private static HashMap<MyInterest, String> emptymap = new HashMap<MyInterest, String>();
	public Object calculate() {
		ExpressionValue value = calculate(emptymap);
		if (null != value) {
			return value.value;
		}
		return null;
	}
	
	public ExpressionValue calculate(Map<MyInterest, String> interestValueMap) {
		switch (this.type) {
		case EXPRESSION : {
			return dealWithExp(interestValueMap);
		}
		case INT : {
			return new ExpressionValue(content, ResultType.INT);
		}
		case BOOLEAN : {
			return new ExpressionValue(content, ResultType.BOOLEAN);
		}
		case FLOAT : {
			return new ExpressionValue(content, ResultType.FLOAT);
		}
		case STRING : {
			return new ExpressionValue(content, ResultType.STRING);
		}
		case ENUM : {
			return new ExpressionValue(content, ResultType.ENUM);
		}
		case INSTANCE : {
			return new ExpressionValue(content, ResultType.INSTANCE);
		}
		case ARRAY : {
			return new ExpressionValue(content, ResultType.ARRAY);
		}
		case INTEREST : {
			String valuestr = interestValueMap.get(content);
			ResultType interestType = ((MyInterest)content).getResultType();
			Object value = null;
			try {
				switch (interestType) {
				case INT : {
					value = Long.parseLong(valuestr);
					break;
				}
				case FLOAT : {
					value = Double.parseDouble(valuestr);
					break;
				}
				case BOOLEAN : {
					value = Integer.parseInt(valuestr);
					break;
				}
				case STRING : {
					value = valuestr;
					break;
				}
				}
				return new ExpressionValue(value, interestType);
			}catch (NumberFormatException e) {
					return new ExpressionValue(null, interestType);
			}
		}
		default : {
			return null;
		}
		}
	}
	
	private ExpressionValue dealWithExp(Map<MyInterest, String> interestValueMap) {
		Object returnValue = null;
		ResultType returnType = ResultType.DEFAULT;
		ExpressionValue leftValue = null;
		ExpressionValue rightValue = null;
		if (null != param1) leftValue = param1.calculate(interestValueMap);
		if (null != param2) rightValue = param2.calculate(interestValueMap);
		switch (operator) {
		case EQUAL : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = leftValue.value.equals(rightValue.value)?1:0;
			break;
		}
		case CONTAINS : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = leftValue.value.toString().contains(rightValue.value.toString())?1:0;
			break;
		}
		case NAEQ : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = leftValue.value.equals(rightValue.value)?0:1;
			break;
		}
		case ADD : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			ResultType lefttype = leftValue.type;
			ResultType righttype = rightValue.type;
			if (lefttype == ResultType.INT && righttype == ResultType.INT) {
				returnType = ResultType.INT;
				returnValue = Long.parseLong(leftValue.value.toString()) +  Long.parseLong(rightValue.value.toString());
			} else {
				returnType = ResultType.FLOAT;
				returnValue = Double.parseDouble(leftValue.value.toString()) + Double.parseDouble(rightValue.value.toString());
			}
			break;
		}
		case SUB : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			ResultType lefttype = leftValue.type;
			ResultType righttype = rightValue.type;
			if (lefttype == ResultType.INT && righttype == ResultType.INT) {
				returnType = ResultType.INT;
				returnValue =  Long.parseLong(leftValue.value.toString()) -  Long.parseLong(rightValue.value.toString());
			} else {
				returnType = ResultType.FLOAT;
				returnValue = Double.parseDouble(leftValue.value.toString()) - Double.parseDouble(rightValue.value.toString());
			}
			break;
		}
		case MUL : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			ResultType lefttype = leftValue.type;
			ResultType righttype = rightValue.type;
			if (lefttype == ResultType.INT && righttype == ResultType.INT) {
				returnType = ResultType.INT;
				returnValue =  Long.parseLong(leftValue.value.toString()) *  Long.parseLong(rightValue.value.toString());
			} else {
				returnType = ResultType.FLOAT;
				returnValue = Double.parseDouble(leftValue.value.toString()) * Double.parseDouble(rightValue.value.toString());
			}
			break;
		}
		case DIV : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			ResultType lefttype = leftValue.type;
			ResultType righttype = rightValue.type;
			if (lefttype == ResultType.INT && righttype == ResultType.INT) {
				returnType = ResultType.INT;
				returnValue =  Long.parseLong(leftValue.value.toString()) / Long.parseLong(rightValue.value.toString());
			} else {
				returnType = ResultType.FLOAT;
				returnValue = Double.parseDouble(leftValue.value.toString()) / Double.parseDouble(rightValue.value.toString());
			}
			break;
		}
		case MOD : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			ResultType lefttype = leftValue.type;
			ResultType righttype = rightValue.type;
			if (lefttype == ResultType.INT && righttype == ResultType.INT) {
				returnType = ResultType.INT;
				returnValue =  Long.parseLong(leftValue.value.toString()) % Long.parseLong(rightValue.value.toString());
			} else {
				returnType = ResultType.FLOAT;
				returnValue = Double.parseDouble(leftValue.value.toString()) % Double.parseDouble(rightValue.value.toString());
			}
			break;
		}
		case LT : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = Double.parseDouble(leftValue.value.toString()) < Double.parseDouble(rightValue.value.toString()) ? 1:0;
			break;
		}
		case LE : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = Double.parseDouble(leftValue.value.toString()) <= Double.parseDouble(rightValue.value.toString()) ? 1:0;
			break;
		}
		case GT : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = Double.parseDouble(leftValue.value.toString()) > Double.parseDouble(rightValue.value.toString()) ? 1:0;
			break;
		}
		case GE : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = Double.parseDouble(leftValue.value.toString()) >= Double.parseDouble(rightValue.value.toString()) ? 1:0;
			break;
		}
		case AND : {
			returnType = ResultType.BOOLEAN;
			int result1 = 0;
			if (null != param1) {
				if (rightValue.value instanceof Integer) {
					result1 = (int)rightValue.value;
				} else if (rightValue.value instanceof Long) {
					result1 = ((Long)rightValue.value).intValue();
				}
			}
			int result2 = 0;
			if (null != param2) {
				if (leftValue.value instanceof Integer) {
					result2 = (int)leftValue.value;
				} else if (leftValue.value instanceof Long) {
					result2 = ((Long)leftValue.value).intValue();
				}
			}
			if (result1 == 1 && result2 == 1) {
				returnValue = 1;
			} else {
				returnValue = 0;
			}
			break;
		}
		case OR : {
			returnType = ResultType.BOOLEAN;
			int result1 = 0;
			if (null != param1) {
				if (rightValue.value instanceof Integer) {
					result1 = (int)rightValue.value;
				} else if (rightValue.value instanceof Long) {
					result1 = ((Long)rightValue.value).intValue();
				}
			}
			int result2 = 0;
			if (null != param2) {
				if (leftValue.value instanceof Integer) {
					result2 = (int)leftValue.value;
				} else if (leftValue.value instanceof Long) {
					result2 = ((Long)leftValue.value).intValue();
				}
			}
			if (result1 == 1 || result2 == 1) {
				returnValue = 1;
			} else {
				returnValue = 0;
			}
			break;
		}
		case XOR : {
			returnType = ResultType.BOOLEAN;
			int result1 = 0;
			if (null != param1) {
				if (rightValue.value instanceof Integer) {
					result1 = (int)rightValue.value;
				} else if (rightValue.value instanceof Long) {
					result1 = ((Long)rightValue.value).intValue();
				}
			}
			int result2 = 0;
			if (null != param2) {
				if (leftValue.value instanceof Integer) {
					result2 = (int)leftValue.value;
				} else if (leftValue.value instanceof Long) {
					result2 = ((Long)leftValue.value).intValue();
				}
			}
			if (1 == (result1 + result2)) {
				returnValue = 1;
			} else {
				returnValue = 0;
			}
			break;
		}
		case NEG : {
			if (null != leftValue) {
				if (leftValue.type == ResultType.BOOLEAN) {
					returnType = ResultType.BOOLEAN;
					returnValue = 1 - (int)leftValue.value;
				} else {
					returnType = ResultType.INT;
					returnValue = - (int)leftValue.value;
				}
			}
			break;
		}
		case HASH : {
			returnType = ResultType.INT;
			returnValue = ((String)leftValue.value).hashCode();
			break;
		}
		case UPPERCASE : {
			if (null != leftValue && null != leftValue.value) {
				returnType = ResultType.STRING;
				returnValue = ((String)leftValue.value).toUpperCase();
			}
			break;
		}
		case LOWERCASE : {
			if (null != leftValue && null != leftValue.value) {
				returnType = ResultType.STRING;
				returnValue = ((String)leftValue.value).toLowerCase();
			}
			break;
		}
		case VALUEOF : {
			if (resultType == ResultType.ENUM) {
				String name = (String)leftValue.value;
				String enumname = content.toString();
				MyEnum muenum = ExpressionTranslator.getEnumMap().get(enumname);
				if (null != muenum) {
					MyArrayContent arraycontent = muenum.values;
					if (null != arraycontent && null != arraycontent.contentlist) {
						for (Object key : arraycontent.contentlist.keySet()) {
							MyEnumInstance myenuminstance = (MyEnumInstance)arraycontent.contentlist.get(key).calculate(interestValueMap).value;
							if (myenuminstance.name.equals(name)) {
								return new ExpressionValue(myenuminstance, ResultType.ENUM);
							}
						}
					}
					break;
				}
			} else if (resultType == ResultType.INT) {
				if (null != leftValue.value) {
					if (leftValue.value instanceof Integer) {
						returnType = ResultType.INT;
						returnValue = leftValue.value;
					}
				}
			}

			break;
		}
		case SAME : {
			if (null != leftValue.value) {
					returnType = leftValue.type;
					returnValue = leftValue.value;
			}
			break;
		}
		case PRASEINT : {
			if (null != leftValue && null != leftValue.value) {
				returnValue = null;
				returnType = ResultType.INT;
				try {
					returnValue = Integer.parseInt(leftValue.value.toString());
				} catch (NumberFormatException e) {
				}
				
			}
			break;
		}
		case SELECT : {
			if (null == leftValue) {
				break;
			}
			if (leftValue.type == ResultType.ARRAY) {
				MyArrayContent result = (MyArrayContent)leftValue.value;
				Map<Object, MyExpression> list = result.getContentList();
				if (null != rightValue && null != rightValue.value) {
					MyExpression trueexp = list.get(rightValue.value);
					if (null != trueexp) {
						return trueexp.calculate(interestValueMap);
					}
				}
			}
			break;
		}
		case PUT : {
			if (leftValue.type == ResultType.ARRAY) {
				MyArrayContent result = (MyArrayContent)leftValue.value;
				Map<Object, MyExpression> list = result.getContentList();
				if (null != rightValue && null != rightValue.value) {
					list.put(rightValue.value, (MyExpression)content);
				}
				return leftValue;
			}
			break;
		}
		case GETFIELD : {
			if (null == leftValue) {
				break;
			}
			if (leftValue.type == ResultType.INSTANCE) {
				MyInstanceContent result = (MyInstanceContent)leftValue.value;
				MyExpression trueexp = result.getfieldmap().get(content);
				if (null != trueexp) {
					return trueexp.calculate(interestValueMap);
				}

			}
			break;
		}
		case SETFIELD : {
			if (null == leftValue) {
				break;
			}
			if (leftValue.type == ResultType.INSTANCE) {
				MyInstanceContent result = (MyInstanceContent)leftValue.value;
				result.getfieldmap().put(content.toString(), param2);
				return leftValue;
			}
			break;
		}
		case ORDINAL : {
			if (null == leftValue) {
			} else {
				if (null == leftValue.value ||  !(leftValue.value instanceof MyEnumInstance)) {
					break;
				}
				MyEnumInstance myenuminstance = (MyEnumInstance)leftValue.value;
				if (null != myenuminstance) {
					return new ExpressionValue(myenuminstance.name, ResultType.STRING);
				}
			}
			break;
		}
		case APPEND : {
			returnType = ResultType.STRING;
			if (null == param1) {
				returnValue = rightValue.value;
			} else if (null == param2) {
				returnValue = leftValue.value;
			}else {
				returnValue = leftValue.value + "" + rightValue.value;
			}
			break;
		}
		}
		ExpressionValue finalReturn = new ExpressionValue(returnValue, returnType);
		return finalReturn;
	}
	

	public ResultType getType() {
		return type;
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
