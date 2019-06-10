package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GUI.Main;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MyPreference;
import sootproject.analysedata.TrailState;
import sootproject.data.LogBranchNode;
import sootproject.data.MyIfStatement;
import sootproject.data.MyMethodDeclaration;
import sootproject.data.MySwitchStatement;
import sootproject.preferenceAnalyse.PreferenceAnalyser;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.Expr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.jimple.SwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JAddExpr;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JNewArrayExpr;
import tools.Logger;
import sootproject.tool.Util;



public class ExpressionTranslator {
	private static Map<String, MyEnum> enumMap = new HashMap<String, MyEnum>();
	private static Map<Long, String> stridmap = null;

	private ConstraintSolver constraintSolver = null;
	private Map<String, MyInterest> involvedInterest = null;
	private Map<Integer, MyVariable> refMaps = null;
	private String tagname = null;
	
	private static String[] getValuesStr = {"getInt", "getBoolean", "getFloat", "getLong", "getString", "getStringSet", "getAll"};
	private static String[] putValuesStr = {"putInt", "putBoolean", "putFloat", "putLong", "putString"};
	public ExpressionTranslator(Map<String, MyInterest> involvedInterest, String tagname, Map<Long, String> stridmap) {
		this.involvedInterest = involvedInterest;
		this.refMaps = new HashMap<Integer, MyVariable>();
		constraintSolver= new ConstraintSolver();
		
		this.tagname = tagname;
		this.stridmap = stridmap;
	}
	public void analysePreference() {
		constraintSolver.analyse(tagname);
	}
	
	public boolean dealWithStmts(LinkedList<CMD> cmds) {
		boolean result = true;
		for (CMD cmd : cmds) {

			switch (cmd.type) {
			case IF: {
				result &= dealWithIfStatement((IfStmt)cmd.instrument, (Unit)cmd.arg, cmd.body, cmd.localmap);
				break;
			}
			case SWITCH: {
				result &= dealWithSwitchStatement((LookupSwitchStmt)cmd.instrument, (Unit)cmd.arg, cmd.body, cmd.localmap);
				break;
			}
			case ASSIGN: {
				result &= dealWithAssignment((AbstractDefinitionStmt)cmd.instrument, cmd.localmap);
				break;
			}
			case INVOKE: {
				result &= dealWithMethodInvoke((InvokeExpr)cmd.instrument, cmd.invokemethod, cmd.localmap, cmd.templocalmap);
				result &= dealWithLoggedSpecialInvokes((Value)cmd.arg, (InvokeExpr)cmd.instrument, cmd.localmap);
				break;
			}
			case SPINVOKE: {
				result &= dealWithUnLoggedSpecialInvokes((Value)cmd.instrument, (InvokeExpr)cmd.arg, cmd.localmap, cmd.preloc);
				break;
			}
			default:{
				
			}
			}
		}
		return result;
	}
	
	private boolean dealWithIfStatement(IfStmt sta, Unit nextUnit, Body body, HashMap<Integer, MyVariable> localMaps) {
		Value val = sta.getCondition();
		MyExpression conditionExp = null;
		MyConstraint constraint = null;
		if (val instanceof Expr) {
			conditionExp = MyExpression.getMyExpression((Expr)val, localMaps, refMaps);
		} else if (val instanceof Local || val instanceof Ref) {
			MyVariable var = Util.getParam(val, localMaps, refMaps);
			conditionExp = var.getTrueExp();
		}
		
		if ((null != conditionExp) && conditionExp.interestRelated) {
			MyIfStatement myif = (MyIfStatement)(PreferenceAnalyser.getNodeMap().get(sta));
			
			if (sta.getTarget() == nextUnit) {
				constraint = new MyIfConstraint(conditionExp, sta, body, 1, myif.getloc(nextUnit), 0, myif.getanotherloc(nextUnit));
			} else {
				constraint = new MyIfConstraint(conditionExp, sta, body, 0, myif.getloc(nextUnit), 1, myif.getanotherloc(nextUnit));
			}
			constraintSolver.addConstraint(constraint);
		}
		return true;
	}
	
	private boolean dealWithSwitchStatement(LookupSwitchStmt sta, Unit nextUnit, Body body, HashMap<Integer, MyVariable> localMaps) {
		Value val = sta.getKey();
		MyExpression conditionExp = null;
		MyConstraint constraint = null;
		if (val instanceof Expr) {
			conditionExp = MyExpression.getMyExpression((Expr)val, localMaps, refMaps);
		} else if (val instanceof Local || val instanceof Ref) {
			MyVariable var = Util.getParam(val, localMaps, refMaps);
			conditionExp = var.getTrueExp();
		}

		if ((null != conditionExp) && conditionExp.interestRelated) {
			MySwitchStatement mysw = (MySwitchStatement)(PreferenceAnalyser.getNodeMap().get(sta));
			List<IntConstant> lookupvalues = sta.getLookupValues();
			List<Unit> lookupunits = sta.getTargets();
			String originloc = null;
			List<String> targetlocs = new ArrayList<String>();
			List<Long> targetresults = new ArrayList<Long>();
			long originreuslt = -10000;
			for (int i = 0; i < lookupvalues.size(); i++) {
				long nowInt = lookupvalues.get(i).value;
				if (lookupunits.get(i) == nextUnit) {
					originreuslt = nowInt;
					originloc = mysw.getloc(nextUnit);
				} else {
					targetresults.add((long)nowInt);
					targetlocs.add(mysw.getloc(lookupunits.get(i)));
				}
			}
			if (originreuslt == -10000) {
				originloc = mysw.getloc(nextUnit);
				constraint = new MySwitchConstraint(conditionExp, sta, body, originreuslt, originloc, targetresults, targetlocs, originloc);
			} else {
				String defaultloc = mysw.getdefaultloc(targetlocs);
				constraint = new MySwitchConstraint(conditionExp, sta, body, originreuslt, originloc, targetresults, targetlocs, defaultloc);
			}
			constraintSolver.addConstraint(constraint);
		}
		return true;
	}
	
	public boolean dealWithReturnStatement(ReturnStmt sta, JAssignStmt originInvoke, HashMap<Integer, MyVariable> nowlocalmap, HashMap<Integer, MyVariable> nextlocalmap) {
		Value left = originInvoke.getLeftOp();
		Value right = sta.getOp();
		return dealWithAssignment(left, right, nextlocalmap, nowlocalmap);
	}
	
	private boolean dealWithAssignment(AbstractDefinitionStmt assign, HashMap<Integer, MyVariable> nowlocalmap) {
		Value left = assign.getLeftOp();
		Value right = assign.getRightOp();
		return dealWithAssignment(left, right, nowlocalmap, nowlocalmap);
	}
	
	private boolean dealWithAssignment(Value left, Value right, HashMap<Integer, MyVariable> leftlocalmap, HashMap<Integer, MyVariable> rightlocalmap) {
		MyVariable leftVal = Util.getParam(left, leftlocalmap, refMaps);
		MyVariable rightVal = null;
		MyExpression nowExpression = null;
		if (right instanceof Expr) {
			nowExpression = MyExpression.getMyExpression((Expr)right, rightlocalmap, refMaps);
		} else {
			if ((right instanceof Local) || (right instanceof Ref)) {
				rightVal = Util.getParam(right, rightlocalmap, refMaps);
				if (null != rightVal.getTrueExp()) {
					nowExpression = rightVal.getTrueExp();
					for (MyInterest interest : rightVal.getAffectedInterests()) {
						interest.addAffectedVariable(leftVal);
					}
				}
			} else if (right instanceof Constant) {
				nowExpression = Util.getConstant((Constant)right);
			} else {
				
			}
		}
		leftVal.setTrueExp(rightVal, nowExpression, leftlocalmap, rightlocalmap, refMaps);


		dealWithSpecialVal(leftVal, rightVal);
		return true;
	}
	
	private void dealWithSpecialVal(MyVariable leftVal, MyVariable rightVal) {
		if (leftVal.value instanceof StaticFieldRef) {
			SootField nowfield = ((StaticFieldRef)leftVal.value).getField();
			SootClass nowclass = nowfield.getDeclaringClass();
			if (nowclass.getSuperclass().getName().equals("java.lang.Enum")) {
				String enumName= nowclass.getName().replace('.', '$');
				MyEnum myenum = enumMap.get(enumName);
				if (null == myenum) {
					myenum = new MyEnum();
					enumMap.put(enumName, myenum);
					myenum.enumclass = nowclass;
				}
				if (nowfield.getName().equals("$VALUES")) {
					ExpressionValue value = rightVal.getTrueExp().calculate(new HashMap<MyInterest,String>());
					if (value.type == ResultType.ARRAY) {
						myenum.values = (MyArrayContent)value.value;
					}
				}
			}
//			} else if (nowfield.getName().startsWith("$SwitchMap")) {
//				String enumName = nowfield.getName().replace("$SwitchMap$", "");
//				MyEnum myenum = enumMap.get(enumName);
//				if (null != myenum) {
//					ExpressionValue value = rightVal.getTrueExp().calculate(new HashMap<MyInterest,String>());
//					if (value.type == ResultType.ARRAY) {
//						myenum.switchMap = (MyArrayContent)value.value;
//					}
//				}
//			}
		}
	}
	
	private boolean dealWithMethodInvoke(InvokeExpr exp, MyMethodDeclaration myMethod, HashMap<Integer, MyVariable> nowlocalmap, HashMap<Integer, MyVariable> nextlocalmap) {
		if (myMethod == null) {
			System.out.println("error: 'dealWithMethodInvoke' method is not found!");
		} else {
			List<Value> params = exp.getArgs();
			for (int i = 0; i < params.size(); i++) {
				dealWithAssignment(myMethod.getParam(i), params.get(i), nextlocalmap, nowlocalmap);
			}
			ThisRef thisref = myMethod.getThisref();
			if (null != thisref) {
				List<ValueBox> values = exp.getUseBoxes();
				dealWithAssignment(thisref, values.get(values.size() - 1).getValue(), nextlocalmap, nowlocalmap);
			}
		}
		return false;
	}
	private boolean dealWithLoggedSpecialInvokes(Value leftValue, InvokeExpr exp, HashMap<Integer, MyVariable> nowlocalmap) {
		SootMethod method = exp.getMethod();
		SootClass myclass = method.getDeclaringClass();
		SootClass superclass = myclass.getSuperclass();
		String methodName = method.getName();
		if (superclass.getName().equals("java.lang.Enum")) {
			if (methodName.equals("<init>")) {
				Value target = exp.getUseBoxes().get(exp.getUseBoxes().size() - 1).getValue();
				MyVariable variable = Util.getParam(target, nowlocalmap, refMaps);
				if (null != variable.trueExp) {
					MyEnumInstance myenuminstance = null;
					if ((variable.trueExp.content instanceof MyInstanceContent)) {
						myenuminstance = ((MyInstanceContent)variable.trueExp.content).forcetobeenum();
					} else {
						myenuminstance = (MyEnumInstance)variable.trueExp.content;
					}
					
					if ((exp.getArg(0) instanceof StringConstant) && (exp.getArg(1) instanceof IntConstant)) {
						myenuminstance.name = ((StringConstant)exp.getArg(0)).value;
						myenuminstance.index = ((IntConstant)exp.getArg(1)).value;		
						
					} else {
						MyExpression nameexp = Util.getVal(exp.getArg(0), nowlocalmap, refMaps);
						if (null != nameexp) {
							Object result = nameexp.calculate();
							if (null != result) {
								myenuminstance.name = result.toString();
							}
						}
						MyExpression indexexp = Util.getVal(exp.getArg(1), nowlocalmap, refMaps);
						if (null != indexexp) {
							Object result = indexexp.calculate();
							if (null != result) {
								myenuminstance.index = (int)result;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	private boolean dealWithUnLoggedSpecialInvokes(Value leftValue, InvokeExpr exp, HashMap<Integer, MyVariable> nowlocalmap, String preloc) {
		boolean isPreferenceRelatedInv = false;
		SootMethod method = exp.getMethod();
		
		SootClass myclass = method.getDeclaringClass();
		String className = myclass.getName();
		String methodName = method.getName();
		boolean solved = false;
		switch(className){
		case "android.content.SharedPreferences" :{
			for (String name : getValuesStr) {
				if (name.equals(methodName)) {
					isPreferenceRelatedInv = true;
					if ("getAll".equals(name)) {
						break;
					}
					solved = true;
					Value param = exp.getArg(0);
					String interestname = null;
					if (param instanceof StringConstant) {
						interestname = ((StringConstant)param).value;
					} else if (param instanceof Ref || param instanceof Local) {
						MyExpression tempexp = Util.getVal(param, nowlocalmap, refMaps);
						if(null != tempexp && !tempexp.unknown) {
							Object resultvalue = tempexp.calculate();
							if (null != resultvalue) {
								interestname = resultvalue + "";
							}
							
						}
					}
					if (null != interestname) {	
						MyInterest myInterest = involvedInterest.get(interestname);
						if (myInterest == null) {
							myInterest = new MyPreference(interestname, name);
							involvedInterest.put(interestname, myInterest);
						} else if (myInterest.getResultType() == ResultType.DEFAULT){
							myInterest.setResultType(name);
						}
						if (null != leftValue) {
							MyExpression nowexp = myInterest.getMyInterestExpression();
							MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
							nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
							myInterest.addAffectedVariable(nowVal);
						}
					}
					break;
				}
			}
			break;
		}
		case "android.content.SharedPreferences$Editor": {
			for (String name : putValuesStr) {
				if (name.equals(methodName)) {
					isPreferenceRelatedInv = true;
					solved = true;
					Value param = exp.getArg(0);
					Value newvalue = exp.getArg(1);
					String interestname = null;
					if (param instanceof StringConstant) {
						interestname = ((StringConstant)param).value;
					} else if (param instanceof Ref || param instanceof Local){
						MyExpression tempexp = Util.getVal(param, nowlocalmap, refMaps);
						if(null != tempexp && !tempexp.unknown) {
							ExpressionValue resultvalue = tempexp.calculate(new HashMap<MyInterest,String>());
							if (null != resultvalue) {
								interestname = resultvalue.value + "";
							}
						}
					}
					if (null != interestname) {
						MyInterest myInterest = involvedInterest.get(interestname);
						if (myInterest == null) {
							myInterest = new MyPreference(interestname, name);
							involvedInterest.put(interestname, myInterest);
						} else if (myInterest.getResultType() == ResultType.DEFAULT){
							myInterest.setResultType(name);
						}
						MyExpression nowexp = Util.getVal(newvalue, nowlocalmap, refMaps);
						if (null != nowexp) {
							myInterest.setSelfexp(nowexp);
						}
						
					}
					break;
				}
			}
			break;
		}
		case "android.location.LocationManager": {
			if (methodName.equals("isProviderEnabled")) {
				solved = true;
				Value param = exp.getArg(0);
				String interestname = null;
				if (param instanceof StringConstant) {
					interestname = ((StringConstant)param).value;
				}
				if (interestname != null) {
					MyInterest myInterest = involvedInterest.get("ass_location_" + interestname);
					if (null != leftValue && null != myInterest) {
						MyExpression nowexp = myInterest.getMyInterestExpression();
						MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
						nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
						myInterest.addAffectedVariable(nowVal);
					}
				}
			} else if (methodName.equals("getProviders") || methodName.equals("getAllProviders")) {
				solved = true;
				if (null != leftValue) {
					MyExpression nowexp = new MyExpression(new MySPArrayContent("location"));
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
				}
			}
			break;
		} 
		case "android.net.wifi.WifiManager": {
			if (methodName.equals("isWifiEnabled")) {
				solved = true;
				MyInterest myInterest = involvedInterest.get("ass_wifi");
				if (null != leftValue) {
					MyExpression nowexp = myInterest.getMyInterestExpression();
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
					myInterest.addAffectedVariable(nowVal);
				}
			} else if (methodName.equals("setWifiEnabled")) {
				solved = true;
				MyInterest myInterest = involvedInterest.get("ass_wifi");
				Value newvalue = exp.getArg(0);
				MyExpression nowexp = Util.getVal(newvalue, nowlocalmap, refMaps);
				if (null != nowexp) {
					myInterest.setSelfexp(nowexp);
				}
			}
			break;
		}
		case "android.media.AudioManager": {
			if (methodName.equals("isMusicActive")) {
				solved = true;
				MyInterest myInterest = involvedInterest.get("ass_musicactive");
				if (null != leftValue) {
					MyExpression nowexp = myInterest.getMyInterestExpression();
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
					myInterest.addAffectedVariable(nowVal);
				}
			}
			break;
		}
		case "android.net.NetworkInfo": {
			if (methodName.equals("isConnected")) {
				solved = true;
				MyInterest myInterest = involvedInterest.get("ass_mobiledata");
				if (null != leftValue) {
					MyExpression nowexp = myInterest.getMyInterestExpression();
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
					myInterest.addAffectedVariable(nowVal);
				}
			}
			break;
		}
		case "java.lang.String": {
			if (methodName.equals("equals")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpression equalRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpression equalLeft = Util.getVal(equalLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.EQUAL, equalLeft, equalRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("hashCode")) {
				solved = true;
				Value hashLeftVal = exp.getUseBoxes().get(0).getValue();
				MyExpression hashLeft = Util.getVal(hashLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.HASH, hashLeft, null), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("contains")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpression containRight = Util.getVal(param, nowlocalmap, refMaps);
				Value containLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpression containLeft = Util.getVal(containLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.CONTAINS, containLeft, containRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("contentEquals")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpression equalRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpression equalLeft = Util.getVal(equalLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.EQUAL, equalLeft, equalRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("valueOf")) {
			} else if (methodName.equals("toUpperCase")) {
				solved = true;
				Value hashLeftVal = exp.getUseBoxes().get(0).getValue();
				MyExpression hashLeft = Util.getVal(hashLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.UPPERCASE, hashLeft, null), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("toLowerCase")) {
				solved = true;
				Value hashLeftVal = exp.getUseBoxes().get(0).getValue();
				MyExpression hashLeft = Util.getVal(hashLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.LOWERCASE, hashLeft, null), nowlocalmap, nowlocalmap, refMaps);
			}
			break;
		} 
		case "java.lang.Integer": {
			if (methodName.equals("parseInt")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpression praseString = Util.getVal(param, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, new MyExpression(OperationType.PRASEINT, praseString, null), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("valueOf")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpression valueofint = Util.getVal(param, nowlocalmap, refMaps);
				if (valueofint != null) {
					MyExpression newExp = new MyExpression(OperationType.VALUEOF, valueofint, null);
					newExp.resultType = ResultType.INT;
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(newExp, nowlocalmap, refMaps);
				}
			} else if (methodName.equals("intValue")) {
				solved = true;
				Value param = exp.getUseBoxes().get(0).getValue();
				MyExpression intvalue = Util.getVal(param, nowlocalmap, refMaps);
				if (intvalue != null) {
					MyExpression newExp = new MyExpression(OperationType.SAME, intvalue, null);
					newExp.resultType = ResultType.INT;
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(newExp, nowlocalmap, refMaps);
				}
			}
			break;
		}
		case "java.lang.StringBuilder": {
			if (methodName.equals("append")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpression appendRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyVariable appendLeftVal = Util.getParam(equalLeftVal, nowlocalmap, refMaps);
				appendLeftVal.setTrueExp(null, new MyExpression(OperationType.APPEND, appendLeftVal.getTrueExp(), appendRight), nowlocalmap, nowlocalmap, refMaps);
				if (null != leftValue) {
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(null, appendLeftVal.getTrueExp(), nowlocalmap, nowlocalmap, refMaps);
				}

			} else if (methodName.equals("toString")) {
				solved = true;
				Value toStringrightVal = exp.getUseBoxes().get(0).getValue();
				MyExpression toStringRight = Util.getVal(toStringrightVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, toStringRight, nowlocalmap, nowlocalmap, refMaps);
			}
			break;
		}
		case "java.lang.Object": {
			if (methodName.equals("clone")) {
				solved = true;
				MyVariable leftVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				Value cloneVal = exp.getUseBoxes().get(0).getValue();
				MyExpression rightExp = Util.getVal(cloneVal, nowlocalmap, refMaps);
				if (null != rightExp) {
					leftVal.setTrueExp(null, rightExp, nowlocalmap, nowlocalmap, refMaps);
				}
			}
			break;
		} 
		case "java.lang.Enum": {
			if (methodName.equals("valueOf")) {
				solved = true;
				Value enumclass = exp.getUseBoxes().get(0).getValue();
				String enumname = ((ClassConstant)enumclass).value.substring(1);
				enumname = enumname.substring(0, enumname.length() -1);
				enumname = enumname.replace('/', '$');
				Value target = exp.getUseBoxes().get(1).getValue();
				MyExpression targetexp = Util.getVal(target, nowlocalmap, refMaps);
				if (targetexp != null) {
					MyExpression newExp = new MyExpression(OperationType.VALUEOF, targetexp, null);
					newExp.content = enumname;
					newExp.resultType = ResultType.ENUM;
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(newExp, nowlocalmap, refMaps);
				}
			} else if (methodName.equals("ordinal")) {
				solved = true;
				Value target = exp.getUseBoxes().get(0).getValue();
				MyExpression targetexp = Util.getVal(target, nowlocalmap, refMaps);
				if (targetexp != null) {
					String enumname = ((RefType)target.getType()).getClassName().replace('.', '$');
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					MyExpression newExp = new MyExpression(OperationType.ORDINAL, targetexp, null);
					newExp.content = enumname;
					nowVal.setTrueExp(newExp, nowlocalmap, refMaps);
				}
			}
			break;
		} 
		case "java.util.List": {
			if (methodName.equals("contains")) {
				solved = true;
				Value target = exp.getUseBoxes().get(exp.getUseBoxes().size() - 1).getValue();
				MyExpression targetexp = Util.getVal(target, nowlocalmap, refMaps);
				String tag = null;
				if (null != targetexp && (tag = targetexp.getSPTag()) != null) {
					if (tag.equals("location")) {
						Value param = exp.getArg(0);
						String interestname = null;
						if (param instanceof StringConstant) {
							interestname = ((StringConstant)param).value;
						}
						if (interestname != null) {
							MyInterest myInterest = involvedInterest.get("ass_location_" + interestname);
							if (null != leftValue) {
								MyExpression nowexp = myInterest.getMyInterestExpression();
								MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
								nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
								myInterest.addAffectedVariable(nowVal);
							}
						}
					}
				}
			}
		}
		case "android.content.res.Resources":;
		case "android.content.Context": {
			if (methodName.equals("getString")) {
				solved = true;
				Value param1 = exp.getArg(0);
				MyExpression strexp = Util.getVal(param1, nowlocalmap, refMaps);
				if (null != strexp) {
					Object result = strexp.calculate();
					if (null != result) {
						long intresult = Long.parseLong(result + "");
						String truevalue = stridmap.get(intresult);
						if (null != truevalue) {
							MyExpression nowexp = new MyExpression(truevalue, ResultType.STRING);
							MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
							nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
						}
					}
				}
			}
			break;
		}
		}
		if (!solved) {
			if (null != leftValue) {
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(null, nowlocalmap, refMaps);
			}
		}
		
		if (Main.shouldAddNoBranchTargets && !isPreferenceRelatedInv) {
			Set<Value> allparamvalues = new HashSet<Value>();
			List<Value> values = exp.getArgs();
			if(null != values) {
				for (Value v : values) {
					allparamvalues.add(v);
				}
			}
			List<ValueBox> valueboxes = exp.getUseBoxes();
			if (null != valueboxes && !valueboxes.isEmpty()) {
				Value v = valueboxes.get(valueboxes.size() - 1).getValue();
				allparamvalues.add(v);
			}
			Set<MyExpression> exps = new HashSet<MyExpression>();
			for (Value v : allparamvalues) {
				MyExpression e = Util.getVal(v, nowlocalmap, refMaps);
				if (null != e && e.interestRelated) {
					exps.add(e);
				}
			}
			if (!exps.isEmpty()) {
				constraintSolver.addNoBranchConstraint(preloc, exps);
			}
		}
		
		return false;
	}
	public static Map<String, MyEnum> getEnumMap() {
		return enumMap;
	}
}