package sootproject.soot;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import GUI.Main;
import sootproject.data.LogBranchNode;
import sootproject.data.MyBranch;
import sootproject.data.MyIfStatement;
import sootproject.data.MyMethodDeclaration;
import sootproject.data.MyNode;
import sootproject.data.MyReturn;
import sootproject.data.MySwitchStatement;
import sootproject.data.MyTryCatch;
import tools.JsonHelper;
import tools.Logger;
import soot.Body;
import soot.BodyTransformer;
import soot.Hierarchy;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.Expr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.StringConstant;
import soot.jimple.ThisRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JGotoStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.util.Chain;

public class PreferenceAnalyseTransformer extends BodyTransformer{
	public List<MyNode> allNodesList = null;
	public Map<SootMethod, MyMethodDeclaration> methodMap = null;
	public Map<Integer, MyNode> logMap = null;
	public Map<Unit, MyNode> nodeMap = null;
	public String packagename = null;
	public Set<String> preferenceactivitynames = null;
	public Map<String, String> preferencesupermap = null;
	public Map<String, Integer> preferencexmlmap = null;
	private static String[] superpreferenceclassnames = new String[]{"ListPreference", "CheckBoxPreference", "SwitchPreference", "SwitchPreferenceCompat", "PreferenceCategory", "EditTextPreference", "IntPreference", "RingtonePreference", "DialogPreference", "Preference"};
	private Map<SootMethod, Set<SootMethod>> invokelinks = null;
	private Map<InvokeExpr, SootMethod> addresourcemap = null;
	private Map<InvokeExpr, SootMethod> loadHeadermap = null;
	private Map<SootClass, SootMethod> newfragmentinvokemap = null;
	private Set<String> coveredclasses = null;
	private Map<SootClass, Map<String, Set<SootMethod>>> supermethodmap = null;
	private Map<MyMethodDeclaration, Set<SootMethod>> tempoverridemap = null;
	public Map<SootMethod, Map<Integer, MyMethodDeclaration>> overridemap = null;
	public Hashtable<SootMethod, Set<String>> skipmethodmap = null;
	private boolean inskipmethod = false;
	private SootMethod currentskipmethod = null;
	public PreferenceAnalyseTransformer(String packagename) {
		super();
		allNodesList = new ArrayList<MyNode>();
		methodMap = new HashMap<SootMethod, MyMethodDeclaration>();
		logMap = new HashMap<Integer, MyNode>();
		nodeMap = new HashMap<Unit, MyNode>();
		preferencesupermap = new HashMap<String, String>();
		preferencexmlmap = new HashMap<String, Integer>();
		coveredclasses = new HashSet<String>();
		preferenceactivitynames = new HashSet<String>();
		invokelinks = new HashMap<SootMethod, Set<SootMethod>>();
		addresourcemap = new HashMap<InvokeExpr, SootMethod>();
		loadHeadermap =  new HashMap<InvokeExpr, SootMethod>();
		newfragmentinvokemap = new HashMap<SootClass, SootMethod>();
		supermethodmap = new HashMap<SootClass, Map<String, Set<SootMethod>>>();
		tempoverridemap = new HashMap<MyMethodDeclaration, Set<SootMethod>>();
		this.packagename = packagename;
		this.skipmethodmap = new Hashtable<SootMethod, Set<String>>();
	}
	public void analyzeoverride() {
		overridemap = new HashMap<SootMethod, Map<Integer, MyMethodDeclaration>>();
		for (MyMethodDeclaration mymethod : tempoverridemap.keySet()) {
			Set<SootMethod> methods = tempoverridemap.get(mymethod);
			for (SootMethod method : methods) {
				Map<Integer, MyMethodDeclaration> mymethodmap = overridemap.get(method);
				if (null == mymethodmap) {
					mymethodmap = new HashMap<Integer, MyMethodDeclaration>();
					overridemap.put(method, mymethodmap);
				}
				mymethodmap.put(mymethod.getLogIndex(), mymethod);
			}
		}
	}
	
	@Override
	protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
		
		SootMethod method = body.getMethod();
		SootClass nowclass = method.getDeclaringClass();
		String nowclassname = nowclass.getName();
		if (!coveredclasses.contains(nowclassname)) {
			coveredclasses.add(nowclassname);
			String superclassname = nowclass.getSuperclass().getName();
			
			if (nowclass.getPackageName().contains(packagename) && !nowclass.isAbstract()) {
				if (superclassname.contains("PreferenceActivity") || (nowclassname.contains("Preference") || nowclassname.contains("Setting")) && nowclassname.contains("Activity") && !nowclassname.contains("$") && !nowclassname.contains("_")) {
					preferenceactivitynames.add(nowclassname);
				}
			}
			SootClass superclass = nowclass.getSuperclass();
			String nowshortname = nowclass.getShortName();
			total: while (null != superclass && !superclass.getShortName().equals("Object")) {
				String shortname = superclass.getShortName();
				for (int i = 0; i < superpreferenceclassnames.length; i++) {
					if (superpreferenceclassnames[i].equalsIgnoreCase(shortname)) {
						preferencesupermap.put(nowshortname, superpreferenceclassnames[i]);
						break total;
					}
				}
				superclass = superclass.getSuperclass();
			}
		}
		if (!nowclass.getPackageName().contains(packagename)) {
			return;
		}


		String methodname = method.getName();
		MyMethodDeclaration myMethod = new MyMethodDeclaration(method, body);
		allNodesList.add(myMethod);
		methodMap.put(method, myMethod);
		PatchingChain<Unit> units = body.getUnits();
		Iterator<Unit> it = units.iterator();
		List<Unit> skipList = new ArrayList<Unit>();

		if (!methodname.equals("<clinit>") && !methodname.equals("<init>")) {
			Map<String, Set<SootMethod>> supermethods = supermethodmap.get(nowclass);
			if (null == supermethods) {
				supermethods = new HashMap<String, Set<SootMethod>>();
				supermethodmap.put(nowclass, supermethods);
				for (SootClass interfaces : nowclass.getInterfaces()) {
					for (SootMethod m : interfaces.getMethods()) {
						String sig = m.getSubSignature();
						Set<SootMethod> ms = supermethods.get(sig);
						if (null == ms) {
							ms = new HashSet<SootMethod>();
							supermethods.put(sig, ms);
						}
						ms.add(m);
					}
				}
				SootClass superclass = nowclass.getSuperclass();
				while (null != superclass && !superclass.getShortName().equals("Object")) {
					for (SootMethod m : superclass.getMethods()) {
						String sig = m.getSubSignature();
						Set<SootMethod> ms = supermethods.get(sig);
						if (null == ms) {
							ms = new HashSet<SootMethod>();
							supermethods.put(sig, ms);
						}
						ms.add(m);
					}
					for (SootClass interfaces : superclass.getInterfaces()) {
						for (SootMethod m : interfaces.getMethods()) {
							String sig = m.getSubSignature();
							Set<SootMethod> ms = supermethods.get(sig);
							if (null == ms) {
								ms = new HashSet<SootMethod>();
								supermethods.put(sig, ms);
							}
							ms.add(m);
						}
					}
					superclass = superclass.getSuperclass();
				}
			}
			Set<SootMethod> overridemethods = supermethods.get(method.getSubSignature());
			if (null != overridemethods) {
				tempoverridemap.put(myMethod, overridemethods);
			}
		}
		
		inskipmethod = false;
		for (String tep : Main.skipstaticmethods) {
			if (tep.equals(methodname)) {
				inskipmethod = true;
				currentskipmethod = method;
				if (!skipmethodmap.containsKey(method)) {
					skipmethodmap.put(method, new HashSet<String>());
				}
				break;
			}
		}

		boolean methodLog = true;
		while(it.hasNext()) {
			Unit unit = it.next();
			if (unit instanceof JInvokeStmt) {
				JInvokeStmt invoke = ((JInvokeStmt)unit);
				if (methodLog) {
					if (specialLogFind(myMethod, invoke)) {
						methodLog = false;
					}
				}
				dealwithinvokeexp(invoke.getInvokeExpr(), method);

			} else if (unit instanceof JAssignStmt) {
				Value rightval = ((JAssignStmt)unit).getRightOp();
				if (rightval instanceof JNewExpr) {
					JNewExpr expr = (JNewExpr)rightval;
					SootClass sootclass = expr.getBaseType().getSootClass();
					if (isPreferenceFragment(sootclass)) {
						newfragmentinvokemap.put(sootclass, method);
					}
				} else if (rightval instanceof InvokeExpr){
					dealwithinvokeexp((InvokeExpr)rightval, method);
				}
			} else if (unit instanceof JIfStmt) {
				JIfStmt ifStmt = (JIfStmt)unit;
				MyIfStatement myif = new MyIfStatement(ifStmt, myMethod);
				allNodesList.add(myif);
				Unit nextUnit = it.next();
				if (!(nextUnit instanceof JInvokeStmt) || !specialLogFind(myif, (JInvokeStmt)nextUnit)) {
					Logger.log("error: no log after 'if'!");
					System.out.println("error: no log after 'if'!");
				}
				
				
				UnitBox box = ifStmt.getTargetBox();
				if (box.isBranchTarget()) {
					Unit jumpUnit = box.getUnit();
					if (!(jumpUnit instanceof JInvokeStmt) || !specialLogFind(myif, (JInvokeStmt)jumpUnit)) {
						Logger.log("error: no log in the jump of 'if'!");
						System.out.println("error: no log in the jump of 'if'!");
					}
				}
				continue;
			} else if (unit instanceof JLookupSwitchStmt) {
				JLookupSwitchStmt switchStmt = (JLookupSwitchStmt)unit;
				MySwitchStatement myswitch = new MySwitchStatement(switchStmt, myMethod);
				allNodesList.add(myswitch);
				List<Unit> targetUnits = switchStmt.getTargets();

				for (Unit targetUnit : targetUnits) {
					if ((targetUnit instanceof JInvokeStmt) && (specialLogFind(myswitch, (JInvokeStmt)targetUnit))) {
						skipList.add(targetUnit);
					} else {
						System.out.println("error: no log for branchs of 'swtich'!");
					}
				}
				Unit defaultUnit = switchStmt.getDefaultTarget();
				if(null != defaultUnit && defaultUnit instanceof JGotoStmt) {
					Unit defaulttargetunit = ((JGotoStmt)defaultUnit).getTarget();
					if ((defaulttargetunit instanceof JInvokeStmt) && (specialLogFind(myswitch, (JInvokeStmt)defaulttargetunit))) {
						skipList.add(defaulttargetunit);
					} else {
						System.out.println("error: no log for the default branch of 'swtich'!");
					}
				}
			} else if ((unit instanceof JReturnStmt) || (unit instanceof JReturnVoidStmt)) {
				MyReturn myreturn = new MyReturn(unit);
				Unit lastUnit = units.getPredOf(unit);
				if (lastUnit instanceof JInvokeStmt && specialLogFind(myreturn, (JInvokeStmt)lastUnit)) {
					myMethod.addReturn(myreturn);
				} else {
					System.out.println("error: no log for 'return' !");
				}
			} else if (unit instanceof JIdentityStmt && ((JIdentityStmt)unit).getRightOp() instanceof ParameterRef) {
				myMethod.addParams((ParameterRef)((JIdentityStmt)unit).getRightOp());
			} else if (unit instanceof JIdentityStmt && ((JIdentityStmt)unit).getRightOp() instanceof ThisRef) {
				myMethod.setThisref((ThisRef)((JIdentityStmt)unit).getRightOp());
			}
			
		}
		Chain<Trap> traps = body.getTraps();
		Iterator<Trap> trapit = traps.snapshotIterator();
		while (trapit.hasNext()) {
			Trap nowTrap = trapit.next();
			MyTryCatch mytrap = new MyTryCatch(nowTrap);
			UnitBox box = nowTrap.getHandlerUnitBox();
			Unit targetUnit = units.getSuccOf(box.getUnit());
			if (targetUnit instanceof JInvokeStmt && specialLogFind(mytrap, (JInvokeStmt)targetUnit)) {
				myMethod.addTrap(mytrap);
			} else {
				System.out.println("error: error log for 'trap'!");
			}
		}
	}

	private boolean specialLogFind(final MyNode myNode, final JInvokeStmt invoke) {
		InvokeExpr expr = invoke.getInvokeExpr();
		SootMethod method = expr.getMethod();
		String exprName = method.getName();
		SootClass sclass = method.getDeclaringClass();
		String className = sclass.getName();
		if (expr instanceof JStaticInvokeExpr) {
			if (exprName.equals("v") && className.equals("android.util.Log")) {
				List<Value> values = expr.getArgs();
				if (values.size() != 2) {
					return false;
				}
				Value loc = values.get(0);
				if (!(loc instanceof StringConstant) || !(((StringConstant)loc).value.equals("loc"))) {
					return false;
				}
				String logstr = ((StringConstant)values.get(1)).value;
				int logid = -1;
				int branchid = -1;
				if (!logstr.contains("-")) {
					logid = Integer.parseInt(logstr);
				} else {
					String[] temp = logstr.split("-");
					try {
						logid = Integer.parseInt(temp[0]);
						branchid = Integer.parseInt(temp[1]);
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}
				if (-1 != logid && inskipmethod) {
					Set<String> list = skipmethodmap.get(currentskipmethod);
					if (null == list) {
						list = new HashSet<String>();
						skipmethodmap.put(currentskipmethod, list);
					}
					list.add(logstr);
				}

				if (myNode instanceof MyMethodDeclaration) {
					if (myNode.getLogIndex() < 0) {
						myNode.setLogIndex(logid);
						logMapAdd(logid, myNode);
					}
					return true;
				} else if (myNode instanceof MyIfStatement) {
					if (myNode.getLogIndex() < 0) {
						myNode.setLogIndex(logid);
						logMapAdd(logid, myNode);
					} else if (myNode.getLogIndex() != logid) {
					}
					LogBranchNode branchNode = new LogBranchNode(logid, branchid, invoke);
					((MyIfStatement)myNode).updateBranch(logid, branchid, branchNode);
					return true;
				} else if (myNode instanceof MySwitchStatement) {
					if (myNode.getLogIndex() < 0) {
						myNode.setLogIndex(logid);
						logMapAdd(logid, myNode);
					} else if (myNode.getLogIndex() != logid) {
					}
					LogBranchNode branchNode = new LogBranchNode(logid, branchid, invoke);
					((MySwitchStatement)myNode).updateBranch(logid, branchid, branchNode);
					return true;
				} else if (myNode instanceof MyReturn) {
					((MyReturn)myNode).setId(logid, branchid);
					return true;
				} else if (myNode instanceof MyTryCatch) {
					((MyTryCatch)myNode).setId(logid, branchid);
					return true;
				}
			}
		}
		return false;
	}
	
	private void logMapAdd(int logid, MyNode myNode) {
		if (null != logMap.get(logid)) {
			Logger.log("error: several nodes share the same logid!");
			System.out.println("error: several nodes share the same logid!");
		}
		if (!(myNode instanceof MyMethodDeclaration)) {
			nodeMap.put((Unit)myNode.getInnerNode(), myNode);
		} else {
			logMap.put(logid, myNode);
		}
	}

	private void dealwithinvokeexp(InvokeExpr exp, SootMethod nowmethod) {
		SootMethod invokemethod = exp.getMethod();
		if (exp.getArgCount() + 1 == exp.getUseBoxes().size()) {
			Value caller = exp.getUseBoxes().get(exp.getUseBoxes().size() -1).getValue();
			if (caller.getType() instanceof RefType) {
				SootClass callerclass = ((RefType)caller.getType()).getSootClass();
				if (!callerclass.equals(invokemethod.getDeclaringClass())) {
					try {
						invokemethod = callerclass.getMethod(invokemethod.getSubSignature());
					} catch (java.lang.RuntimeException e) {
					}
				}
			}
		}
		Set<SootMethod> callfrommethods = invokelinks.get(invokemethod);
		if (null == callfrommethods) {
			callfrommethods = new HashSet<SootMethod>();
			invokelinks.put(invokemethod, callfrommethods);
		}
		callfrommethods.add(nowmethod);
		String invokemethodname = exp.getMethod().getName();
		if (invokemethodname.equals("addPreferencesFromResource") || invokemethodname.equals("setPreferencesFromResource")) {
			Value value = exp.getArg(0);
			if (value instanceof IntConstant) {
				preferencexmlmap.put(nowmethod.getDeclaringClass().getName(), ((IntConstant) value).value);
				addresourcemap.put(exp, nowmethod);
			}
		} else if (invokemethodname.equals("loadHeadersFromResource")) {
			Value value = exp.getArg(0);
			if (value instanceof IntConstant) {
				loadHeadermap.put(exp, nowmethod);
			}
		}
	}
	
	private Map<SootClass, Boolean> isfragmentcache = new HashMap<SootClass, Boolean>();
	private boolean isPreferenceFragment(SootClass inputclass) {
		if (null != isfragmentcache.get(inputclass)) {
			return isfragmentcache.get(inputclass);
		} else {
			SootClass nowclass = inputclass;
			while (null != nowclass) {
				String nowclassname = nowclass.getName();
				if (nowclassname.equals("android.preference.PreferenceFragment")|| nowclassname.equals("android.support.v7.preference.PreferenceFragmentCompat")) {
					isfragmentcache.put(inputclass, true);
					return true;
				}
				if (nowclass.hasSuperclass()) {
					nowclass = nowclass.getSuperclass();
				} else {
					break;
				}
			}
		}
		isfragmentcache.put(inputclass, false);
		return false;
	}
	
	private Map<SootClass, FragmentORActivity> isfragmentoractivitycache = new HashMap<SootClass, FragmentORActivity>();
	private FragmentORActivity isPreferenceFragmentOrPreferenceActivity(SootClass inputclass) {
		if (null != isfragmentoractivitycache.get(inputclass)) {
			return isfragmentoractivitycache.get(inputclass);
		} else {
			SootClass nowclass = inputclass;
			while (null != nowclass) {
				String nowclassname = nowclass.getShortName();
				if (nowclassname.equals("PreferenceFragment") || nowclassname.equals("PreferenceFragmentCompat") || nowclassname.equals("Fragment")) {
					isfragmentoractivitycache.put(inputclass, FragmentORActivity.FRAGMENT);
					return FragmentORActivity.FRAGMENT;
				} else if (nowclassname.equals("Activity")) {
					isfragmentoractivitycache.put(inputclass, FragmentORActivity.ACTIVITY);
					return FragmentORActivity.ACTIVITY;
				}
				if (nowclass.hasSuperclass()) {
					nowclass = nowclass.getSuperclass();
				} else {
					break;
				}
			}
		}
		isfragmentoractivitycache.put(inputclass, FragmentORActivity.NEITHER);
		return FragmentORActivity.NEITHER;
	}
	
	public Map<Integer, String> preference2activitymap = null;
	public void initpreferences2activity() {
		preference2activitymap = new HashMap<Integer, String>();
		HashMap<InvokeExpr, SootMethod> allinvokes = new HashMap<InvokeExpr, SootMethod>(addresourcemap);
		allinvokes.putAll(loadHeadermap);
		total:for (InvokeExpr expr : allinvokes.keySet()) {
			SootMethod firstmethod = allinvokes.get(expr);
			Stack<SootMethod> methodstack = new Stack<SootMethod>();
			Value value = expr.getArg(0);
			int xmlid = ((IntConstant)value).value;
			methodstack.push(firstmethod);
			while (!methodstack.isEmpty()) {
				SootMethod currentmethod = methodstack.pop();
				SootClass currentclass = currentmethod.getDeclaringClass();
				FragmentORActivity result = isPreferenceFragmentOrPreferenceActivity(currentclass);
				if (result == FragmentORActivity.ACTIVITY) {
					preference2activitymap.put(xmlid,  currentclass.getName());
					preferenceactivitynames.add(currentclass.getName());
					continue total;
				} else if (result == FragmentORActivity.FRAGMENT) {
					Stack<SootMethod> innermethodstack = new Stack<SootMethod>();
					SootMethod newcurrentmethod = newfragmentinvokemap.get(currentclass);
					if (null != newcurrentmethod) {
						innermethodstack.push(newcurrentmethod);
					}
					while (!innermethodstack.isEmpty()) {
						SootMethod innernowmethod = innermethodstack.pop();
						SootClass newcurrentclass = innernowmethod.getDeclaringClass();
						FragmentORActivity newresult = isPreferenceFragmentOrPreferenceActivity(newcurrentclass);
						if (newresult == FragmentORActivity.ACTIVITY) {
							preference2activitymap.put(xmlid,  newcurrentclass.getName());
							preferenceactivitynames.add(newcurrentclass.getName());
							break;
						} else {
							Set<SootMethod> invokemethods = invokelinks.get(innernowmethod);
							if (null != invokemethods) {
								for (SootMethod method : invokemethods) {
									innermethodstack.push(method);
								}
							}
						}
					}
					continue total;
				} else {
					Set<SootMethod> invokemethods = invokelinks.get(currentmethod);
					if (null != invokemethods) {
						for (SootMethod method : invokemethods) {
							methodstack.push(method);
						}
					}
				}
			}
		}
	}
	
	public void resultSystemout() {
		for (MyMethodDeclaration method : methodMap.values()) {
			System.out.println(((SootMethod)method.getInnerNode()).getDeclaration() + "---------------------   id:" + method.getLogIndex());
			Set<MyNode> childs = method.getChildNodes();
			for (MyNode child : childs) {
				if (child instanceof MyBranch) {
					Collection<LogBranchNode> branches = ((MyBranch)child).getBranches();
					if (!branches.isEmpty()) {
						for (LogBranchNode branch : branches) {
							System.out.println(branch.innerIndex + "----" + branch.innerNode.toString());
						}
					}
				}
			}
		}
	}
}
