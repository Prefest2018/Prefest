package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GUI.Main;
import data.LocalValMap;
import data.RefValMap;
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
//	private Map<SootMethod, MyMethodDeclaration> methodMaps = null;
	private ConstraintSolver constraintSolver = null;
	private Map<String, MyInterest> involvedInterest = null;
	private RefValMap refMaps = null;
	private String tagname = null;
	private CMD currentCMD = null;

	private static String[] getValuesStr = {"getInt", "getBoolean", "getFloat", "getLong", "getString", "getStringSet", "getAll"};
	private static String[] putValuesStr = {"putInt", "putBoolean", "putFloat", "putLong", "putString"};
//	private ReturnStatement resutSta = null;
	public ExpressionTranslator(Map<String, MyInterest> involvedInterest, String tagname, Map<Long, String> stridmap) {
//		this.methodMaps = methodMaps;
		this.involvedInterest = involvedInterest;
		for (MyInterest i : this.involvedInterest.values()) {
			i.restoreExp();
		}
		this.refMaps = new RefValMap();
		constraintSolver= new ConstraintSolver();
		
		this.tagname = tagname;
		this.stridmap = stridmap;
	}
	public void analysePreference() {
		constraintSolver.analyse(tagname);
	}
	
	public boolean dealWithStmts(LinkedList<CMD> cmds, int logidNum, int branchNum) {
		boolean result = true;
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println();
//			System.out.println();
		for (CMD cmd : cmds) {
//				System.out.println();
//				System.out.println();
//				System.out.println();
//				System.out.println();
//				System.out.println();
			currentCMD = cmd;
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

	private boolean dealWithIfStatement(IfStmt sta, Unit nextUnit, Body body, LocalValMap localMap) {
		Value val = sta.getCondition();
		MyExpressionInterface conditionExp = null;
		MyConstraint constraint = null;
		if (val instanceof Expr) {
			conditionExp = MyExpression.getMyExpression((Expr)val, localMap, refMaps);
		} else if (val instanceof Local || val instanceof Ref) {
			MyVariable var = Util.getParam(val, localMap, refMaps);
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

	private boolean dealWithSwitchStatement(LookupSwitchStmt sta, Unit nextUnit, Body body, LocalValMap localMap) {
		Value val = sta.getKey();
		MyExpressionInterface conditionExp = null;
		MyConstraint constraint = null;
		if (val instanceof Expr) {
			conditionExp = MyExpression.getMyExpression((Expr)val, localMap, refMaps);
		} else if (val instanceof Local || val instanceof Ref) {
			MyVariable var = Util.getParam(val, localMap, refMaps);
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
	
	public boolean dealWithReturnStatement(ReturnStmt sta, JAssignStmt originInvoke, LocalValMap nowlocalmap, LocalValMap nextlocalmap) {
		Value left = originInvoke.getLeftOp();
		Value right = sta.getOp();
		return dealWithAssignment(left, right, nextlocalmap, nowlocalmap);
	}
	
	private boolean dealWithAssignment(AbstractDefinitionStmt assign, LocalValMap nowlocalmap) {
		Value left = assign.getLeftOp();
		Value right = assign.getRightOp();
		return dealWithAssignment(left, right, nowlocalmap, nowlocalmap);
	}
	
	private boolean dealWithAssignment(Value left, Value right, LocalValMap leftlocalmap, LocalValMap rightlocalmap) {
		MyVariable leftVal = Util.getParam(left, leftlocalmap, refMaps);
		MyVariable rightVal = null;
		MyExpressionInterface nowExpression = null;
		if (right instanceof Expr) {
			nowExpression = MyExpression.getMyExpression((Expr)right, rightlocalmap, refMaps);
		} else {
			if ((right instanceof Local) || (right instanceof Ref)) {
				rightVal = Util.getParam(right, rightlocalmap, refMaps);
				nowExpression = rightVal.getTrueExp();
			} else if (right instanceof Constant) {
				nowExpression = Util.getConstant((Constant)right);
			} else {
				
			}
		}
		leftVal.setTrueExp(nowExpression, leftlocalmap, rightlocalmap, refMaps);


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
//				String enumName = nowfield.getName().replace("$SwitchMap$", "");
//				MyEnum myenum = enumMap.get(enumName);
//					ExpressionValue value = rightVal.getTrueExp().calculate(new HashMap<MyInterest,String>());
//						myenum.switchMap = (MyArrayContent)value.value;
		}
	}
	
	private boolean dealWithMethodInvoke(InvokeExpr exp, MyMethodDeclaration myMethod, LocalValMap nowlocalmap, LocalValMap nextlocalmap) {
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
	private boolean dealWithLoggedSpecialInvokes(Value leftValue, InvokeExpr exp, LocalValMap nowlocalmap) {
		SootMethod method = exp.getMethod();
		SootClass myclass = method.getDeclaringClass();
		SootClass superclass = myclass.getSuperclass();
		String methodName = method.getName();
		if (superclass.getName().equals("java.lang.Enum")) {
			if (methodName.equals("<init>")) {
				Value target = exp.getUseBoxes().get(exp.getUseBoxes().size() - 1).getValue();
				MyVariable variable = Util.getParam(target, nowlocalmap, refMaps);
				MyEnumInstance myenuminstance = null;
				if (null == variable.trueExp || ! (variable.trueExp instanceof MyEnumInstance)) {
					variable.trueExp = MyExpressionTree.createEnumInstance();
				}
				myenuminstance = (MyEnumInstance)variable.trueExp;
				if ((exp.getArg(0) instanceof StringConstant) && (exp.getArg(1) instanceof IntConstant)) {
					myenuminstance.name = ((StringConstant)exp.getArg(0)).value;
					myenuminstance.index = ((IntConstant)exp.getArg(1)).value;		
					
				} else {
					MyExpressionInterface nameexp = Util.getVal(exp.getArg(0), nowlocalmap, refMaps);
					if (null != nameexp) {
						Object result = nameexp.calculate();
						if (null != result) {
							myenuminstance.name = result.toString();
						}
					}
					MyExpressionInterface indexexp = Util.getVal(exp.getArg(1), nowlocalmap, refMaps);
					if (null != indexexp) {
						Object result = indexexp.calculate();
						if (null != result) {
							myenuminstance.index = ((Long)result).intValue();
						}
					}
				}
			}
		}
		return true;
	}
	public static Set<String> testPreferenceNames = new HashSet<String>();
	
	private boolean dealWithUnLoggedSpecialInvokes(Value leftValue, InvokeExpr exp, LocalValMap nowlocalmap, String preloc) {
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
					if ("getAll".equals(name)) {
						System.out.println("getAll");
						break;
					}
					isPreferenceRelatedInv = true;
					solved = true;
					Value param = exp.getArg(0);
					String interestname = null;
					if (param instanceof StringConstant) {
						interestname = ((StringConstant)param).value;
					} else if (param instanceof Ref || param instanceof Local) {
						MyExpressionInterface tempexp = Util.getVal(param, nowlocalmap, refMaps);
						if(null != tempexp && !tempexp.interestRelated) {
							Object resultvalue = tempexp.calculate();
							if (null != resultvalue) {
								interestname = resultvalue + "";
							}
							
						}
					}
					if (null != interestname) {
						if ("getAll".equals(name)) {
							Map<Object, MyExpressionTree> preferencearray = new HashMap<Object, MyExpressionTree>();
							for (String key : involvedInterest.keySet()) {
								preferencearray.put(key, MyExpressionTree.createLeaf(involvedInterest.get(key).getSelfexp()));
							}
							MyExpressionTree allinterests = refMaps.getOrCreateSPTree(new MySPArrayContent("preferencearray", preferencearray));
							MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
							nowVal.setTrueExp(allinterests, nowlocalmap, refMaps);
						} else {
							if (!testPreferenceNames.contains(interestname)) {
								System.out.println(interestname);
//									System.out.println();
								testPreferenceNames.add(interestname);
							}
   							MyInterest myInterest = involvedInterest.get(interestname);
							if (myInterest == null) {
								myInterest = new MyPreference(interestname, name);
								involvedInterest.put(interestname, myInterest);
							} else if (myInterest.getResultType() == ResultType.DEFAULT){
								myInterest.setResultType(name);
								
							}
							if (null != leftValue) {
								MyExpressionInterface nowexp = myInterest.getMyInterestExpression();
								MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
								nowVal.setTrueExp(nowexp, nowlocalmap, refMaps);
								myInterest.addAffectedVariable(nowVal);
							}
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
						MyExpressionInterface tempexp = Util.getVal(param, nowlocalmap, refMaps);
						if(null != tempexp && !tempexp.interestRelated) {
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
						MyExpressionInterface nowexp = Util.getVal(newvalue, nowlocalmap, refMaps);
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
					MyExpressionTree nowtree = refMaps.getOrCreateSPTree(new MySPArrayContent("location"));
//					MyExpression nowexp = new MyExpression(new MySPArrayContent("location"));
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(nowtree, nowlocalmap, refMaps);
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
				MyExpressionInterface nowexp = Util.getVal(newvalue, nowlocalmap, refMaps);
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
				MyExpressionInterface equalRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpressionInterface equalLeft = Util.getVal(equalLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.EQUAL, equalLeft, equalRight), nowlocalmap, nowlocalmap, refMaps);
				//left = new MyExpression();
			} else if (methodName.equals("hashCode")) {
				solved = true;
				Value hashLeftVal = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface hashLeft = Util.getVal(hashLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.HASH, hashLeft, null), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("contains")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpressionInterface containRight = Util.getVal(param, nowlocalmap, refMaps);
				Value containLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpressionInterface containLeft = Util.getVal(containLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.CONTAINS, containLeft, containRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("contentEquals")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpressionInterface equalRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpressionInterface equalLeft = Util.getVal(equalLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.EQUAL, equalLeft, equalRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("startsWith")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpressionInterface equalRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpressionInterface equalLeft = Util.getVal(equalLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.STARTWITH, equalLeft, equalRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("endsWith")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpressionInterface equalRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyExpressionInterface equalLeft = Util.getVal(equalLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.ENDWITH, equalLeft, equalRight), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("valueOf")) {
			} else if (methodName.equals("toUpperCase")) {
				solved = true;
				Value hashLeftVal = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface hashLeft = Util.getVal(hashLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.UPPERCASE, hashLeft, null), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("toLowerCase")) {
				solved = true;
				Value hashLeftVal = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface hashLeft = Util.getVal(hashLeftVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.LOWERCASE, hashLeft, null), nowlocalmap, nowlocalmap, refMaps);
			}
			break;
		} 
		case "java.lang.Integer": {
			if (methodName.equals("parseInt")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpressionInterface praseString = Util.getVal(param, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.PRASEINT, praseString, null), nowlocalmap, nowlocalmap, refMaps);
			} else if (methodName.equals("valueOf")) {
				solved = true;
				Value param = exp.getArg(0);
				MyExpressionInterface valueofint = Util.getVal(param, nowlocalmap, refMaps);
				if (valueofint != null) {
					MyExpression newExp = new MyExpression(OperationType.VALUEOF, valueofint, null);
					newExp.resultType = ResultType.INT;
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(newExp, nowlocalmap, refMaps);
				}
			} else if (methodName.equals("intValue")) {
				solved = true;
				Value param = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface intvalue = Util.getVal(param, nowlocalmap, refMaps);
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
				MyExpressionInterface appendRight = Util.getVal(param, nowlocalmap, refMaps);
				Value equalLeftVal = exp.getUseBoxes().get(1).getValue();
				MyVariable appendLeftVal = Util.getParam(equalLeftVal, nowlocalmap, refMaps);
				appendLeftVal.setTrueExp(new MyExpression(OperationType.APPEND, appendLeftVal.getTrueExp(), appendRight), nowlocalmap, nowlocalmap, refMaps);
				if (null != leftValue) {
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					nowVal.setTrueExp(appendLeftVal.getTrueExp(), nowlocalmap, nowlocalmap, refMaps);
				}

			} else if (methodName.equals("toString")) {
				solved = true;
				Value toStringrightVal = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface toStringRight = Util.getVal(toStringrightVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(toStringRight, nowlocalmap, nowlocalmap, refMaps);
			}
			break;
		}
		case "java.lang.Object": {
			if (methodName.equals("clone")) {
				solved = true;
				MyVariable leftVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				Value cloneVal = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface rightExp = Util.getVal(cloneVal, nowlocalmap, refMaps);
				if (null != rightExp) {
					leftVal.setTrueExp(rightExp, nowlocalmap, nowlocalmap, refMaps);
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
				MyExpressionInterface targetexp = Util.getVal(target, nowlocalmap, refMaps);
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
				MyExpressionInterface targetexp = Util.getVal(target, nowlocalmap, refMaps);
				if (targetexp != null) {
					String enumname = ((RefType)target.getType()).getClassName().replace('.', '$');
					MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
					MyExpression newExp = new MyExpression(OperationType.ORDINAL, targetexp, null);
					newExp.content = enumname;
					nowVal.setTrueExp(newExp, nowlocalmap, refMaps);
				}
			} else if (methodName.equals("name")) {
				solved = true;
				Value enumVal = exp.getUseBoxes().get(0).getValue();
				MyExpressionInterface nameVal = Util.getVal(enumVal, nowlocalmap, refMaps);
				MyVariable nowVal = Util.getParam(leftValue, nowlocalmap, refMaps);
				nowVal.setTrueExp(new MyExpression(OperationType.NAME, nameVal, null), nowlocalmap, nowlocalmap, refMaps);
			}
			break;
		} 
		case "java.util.List": {
			if (methodName.equals("contains")) {
				solved = true;
				Value target = exp.getUseBoxes().get(exp.getUseBoxes().size() - 1).getValue();
				MyExpressionInterface targetexp = Util.getVal(target, nowlocalmap, refMaps);
				if (null == targetexp || !(targetexp instanceof MyExpressionTree)) {
					break;
				}
				String tag = ((MyExpressionTree)targetexp).name;
				if (null != tag && tag.equals("location")) {
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
			} else if (methodName.equals("add")) {
				solved = true;
				Value arrayvalue = exp.getUseBoxes().get(exp.getUseBoxes().size() - 1).getValue();
				MyVariable arrayval = Util.getParam(arrayvalue, nowlocalmap, refMaps);
				if (exp.getArgs().size() == 2) {
					Value indexvalue = exp.getArg(0);
					Value contentvalue = exp.getArg(1);
					MyExpressionInterface indexExp = Util.getVal(indexvalue, nowlocalmap, refMaps);
					MyExpressionInterface contentExp = Util.getVal(contentvalue, nowlocalmap, refMaps);
					((MyExpressionTree)arrayval.getTrueExp()).setChild(indexExp, contentExp);
				}
			} else if (methodName.equals("get")) {
				solved = true;
				Value arrayvalue = exp.getUseBoxes().get(exp.getUseBoxes().size() - 1).getValue();
				MyVariable arrayval = Util.getParam(arrayvalue, nowlocalmap, refMaps);
				if (exp.getArgs().size() == 1) {
					Value indexvalue = exp.getArg(0);
					MyExpressionInterface indexExp = Util.getVal(indexvalue, nowlocalmap, refMaps);
					MyExpressionTree result = null;
					try {
						result = ((MyExpressionTree)arrayval.getTrueExp()).getChild(indexExp, false);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
//						System.out.println();
					}
					arrayval.setTrueExp(result, nowlocalmap, refMaps);
				}
			} else if (methodName.equals("size")) {
				
			}
			break;
		}
		case "android.content.res.Resources":
		case "android.content.Context": {
			if (methodName.equals("getString")) {
				solved = true;
				Value param1 = exp.getArg(0);
				MyExpressionInterface strexp = Util.getVal(param1, nowlocalmap, refMaps);
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
//				solved = true;
////				ThisRef thisref = method.getThisref();
////					List<ValueBox> values = exp.getUseBoxes();
////					dealWithAssignment(thisref, values.get(values.size() - 1).getValue(), nextlocalmap, nowlocalmap);
//				System.out.println();
//			break;
		default : {
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
			Set<MyExpressionInterface> exps = new HashSet<MyExpressionInterface>();
			for (Value v : allparamvalues) {
				MyExpressionInterface e = Util.getVal(v, nowlocalmap, refMaps);
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
