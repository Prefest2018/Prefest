package sootproject.myexpression;

import java.util.HashMap;
import java.util.Map;

import sootproject.analysedata.MyInterest;

public abstract class MyExpressionInterface {
	protected ResultType type = ResultType.DEFAULT;
	protected ResultType resultType = ResultType.DEFAULT;
	protected Object content = null;
	protected boolean interestRelated = false;
	protected boolean unknown = false;
	private static HashMap<MyInterest, String> emptymap = new HashMap<MyInterest, String>();
	public Object calculate() {
		ExpressionValue value = calculate(emptymap);
		if (null != value && !(value.value instanceof MyArrayContent)) {
			return value.value;
		}
		return null;
	}
	
	protected OperationType operator = OperationType.DEFAULT;
	protected MyExpressionInterface param1 = null;
	protected MyExpressionInterface param2 = null;
	public ExpressionValue calculate(Map<MyInterest, String> interestValueMap) {
		switch (this.type) {
		case EXPRESSION : {
			return dealWithExp(interestValueMap);
		}
		case INT : {
			return new ExpressionValue(Long.parseLong(content + ""), ResultType.INT);
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
					value = Long.parseLong(valuestr);
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
			returnValue = leftValue.value.toString().equals(rightValue.value.toString())?1:0;
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
		case STARTWITH : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = leftValue.value.toString().startsWith(rightValue.value.toString())?1:0;
			break;
		}
		case ENDWITH : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = leftValue.value.toString().endsWith(rightValue.value.toString())?1:0;
			break;
		}
		case NAEQ : {
			if (null == leftValue || null == rightValue || null == leftValue.value || null == rightValue.value) {
				break;
			}
			returnType = ResultType.BOOLEAN;
			returnValue = leftValue.value.toString().equals(rightValue.value.toString())?0:1;
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
				try {
					returnValue = Double.parseDouble(leftValue.value.toString()) + Double.parseDouble(rightValue.value.toString());
				} catch (NumberFormatException e) {
					return null;
				}
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
				try {
					returnValue = Double.parseDouble(leftValue.value.toString()) - Double.parseDouble(rightValue.value.toString());
				} catch (NumberFormatException e) {
					return null;
				}
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
				try {
					returnValue = Double.parseDouble(leftValue.value.toString()) * Double.parseDouble(rightValue.value.toString());
				} catch (NumberFormatException e) {
					return null;
				}
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
				try {
					returnValue = Double.parseDouble(leftValue.value.toString()) / Double.parseDouble(rightValue.value.toString());
				} catch (NumberFormatException e) {
					return null;
				}
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
			try {
				returnType = ResultType.BOOLEAN;
				returnValue = Double.parseDouble(leftValue.value.toString()) < Double.parseDouble(rightValue.value.toString()) ? 1:0;
				
			} catch (NumberFormatException e) {
				returnValue = null;
				returnType = ResultType.DEFAULT;
			}
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
				if (null == rightValue) {
					result1 = 1;
				} else if (rightValue.value instanceof Integer) {
					result1 = (int)rightValue.value;
				} else if (rightValue.value instanceof Long) {
					result1 = ((Long)rightValue.value).intValue();
				}
			}
			int result2 = 0;
			if (null != param2) {
				if (null == leftValue) {
					result2 = 1;
				} else if (leftValue.value instanceof Integer) {
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
					returnValue = - (long)leftValue.value;
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
				if (null != leftValue) {
					if (!(leftValue.value instanceof String)) {
						break;
					}
					String name = (String)leftValue.value;
					String enumname = content.toString();
					MyEnum muenum = ExpressionTranslator.getEnumMap().get(enumname);
					if (null != muenum) {
						MyArrayContent arraycontent = muenum.values;
						if (null != arraycontent && null != arraycontent.contentlist) {
							for (Object key : arraycontent.contentlist.keySet()) {
								MyExpressionTree innerexp = arraycontent.contentlist.get(key);
								if (null != innerexp) {
									if (innerexp.operator == OperationType.VALUEOF) {
										continue;
									}
									Object result = innerexp.calculate(interestValueMap).value;
									MyEnumInstanceContent myenuminstance = (MyEnumInstanceContent)result;
									MyEnumInstance parent = myenuminstance.getEnum();
									if (parent.name.equals(name)) {
										return new ExpressionValue(myenuminstance, ResultType.ENUM);
									}
								}
							}
						}
					}
				}
			} else if (resultType == ResultType.INT) {
				if (null != leftValue && null != leftValue.value) {
					if (leftValue.value instanceof Integer || leftValue.value instanceof Long) {
						returnType = ResultType.INT;
						returnValue = leftValue.value;
					}
				}
			}

			break;
		}
		case SAME : {
			if (null != leftValue && null != leftValue.value) {
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
					returnValue = Long.parseLong(leftValue.value.toString());
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
				MyArrayContent arrraycontent = (MyArrayContent)leftValue.value;
				if (null != rightValue && null != rightValue.value) {
					MyExpressionInterface trueexp = arrraycontent.get(rightValue.value);
					if (null != trueexp) {
						return trueexp.calculate(interestValueMap);
					}
				}
			}
			break;
		}
		case PUT : {
			if (leftValue.type == ResultType.ARRAY) {
				MyArrayContent arrraycontent = (MyArrayContent)leftValue.value;
				if (null != rightValue && null != rightValue.value) {
					MyExpressionTree tree = MyExpressionTree.createLeaf((MyExpressionInterface)content);
					arrraycontent.put(rightValue.value, tree);
				}
				return leftValue;
			}
			break;
		}
//				break;
//				MyInstanceContent result = (MyInstanceContent)leftValue.value;
//				MyExpression trueexp = result.getfieldmap().get(content);
//					return trueexp.calculate(interestValueMap);
//
//			break;
//				break;
//				MyInstanceContent result = (MyInstanceContent)leftValue.value;
//				result.getfieldmap().put(content.toString(), param2);
//				return leftValue;
//			break;
		case ORDINAL : {
			if (null == leftValue) {
			} else {
				if (null == leftValue.value ||  !(leftValue.value instanceof MyEnumInstanceContent)) {
					break;
				}
				MyEnumInstanceContent myenuminstancecontent = (MyEnumInstanceContent)leftValue.value;
				MyEnumInstance myenuminstance = myenuminstancecontent.getEnum();
				if (null != myenuminstance) {
					return new ExpressionValue(myenuminstance.name, ResultType.STRING);
				}
			}
			break;
		}
		case NAME : {
			if (null == leftValue) {
			} else {
				if (null == leftValue.value ||  !(leftValue.value instanceof MyEnumInstanceContent)) {
					break;
				}
				MyEnumInstanceContent myenuminstancecontent = (MyEnumInstanceContent)leftValue.value;
				MyEnumInstance myenuminstance = myenuminstancecontent.getEnum();
				if (null != myenuminstance) {
					return new ExpressionValue(myenuminstance.name, ResultType.STRING);
				}
			}
			break;
		}
		case APPEND : {
			returnType = ResultType.STRING;
			if (null == leftValue) {
				returnValue = rightValue.value;
			} else if (null == rightValue) {
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
	
}
