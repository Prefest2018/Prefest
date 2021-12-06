package sootproject.soot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import GUI.Main;
import soot.Body;
import soot.BodyTransformer;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JIfStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JLookupSwitchStmt;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JThrowStmt;
import soot.tagkit.Tag;
import soot.util.Chain;

public class StubTransformer extends MyOwnTransformer{
	private static boolean notaddanything = false;
	private static int i = 0;
	private static int branchi = 0;
	private static Lock lock = new ReentrantLock();
	public StubTransformer(String packagename, Set<String> extrapackagenames) {
		this.packagename = packagename;
		this.extrapackagenames = extrapackagenames;
	}
	

	@Override
	protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
//		final PatchingChain<Unit> units = b.getUnits();
		
		final UnitPatchingChain units = b.getUnits();

		SootMethod method = b.getMethod();
		String methodname = method.getName();
		String currentPackageName = method.getDeclaringClass().getPackageName();
		if (!checkInTargetPackage(currentPackageName)) {
			return;
		}
		for (String tep : Main.skipstaticmethods) {
			if (tep.equals(methodname)) {
				return;
			}
		}
		Unit now = null;
		Unit pre = null;
		Unit methodMain = null;
		Iterator<Unit> iter = units.snapshotIterator();
		boolean methodLog = true;
		int methodid = -1;
		while(iter.hasNext()){
			now = iter.next();
			if ((now != null) && methodLog) {
				if (!(now instanceof JIdentityStmt) && ((pre == null) || (pre instanceof JIdentityStmt))) {
					methodMain = now;
					methodLog = false;
					lock.lock();
					methodid = i++;
					lock.unlock();
				}
			}
			if (now instanceof JIfStmt) {
				JIfStmt ifStmt = (JIfStmt)now;


				lock.lock();
				int tempi = i++;
				InvokeStmt invokeStmt = getNewLogStmt(tempi + "-" + branchi++);
				lock.unlock();
				units.insertAfter(invokeStmt, ifStmt);
				lock.lock();
				insertAndRecovertBox(units, ifStmt.getTargetBox(), tempi + "-" + branchi++);
				lock.unlock();
			} else if (now instanceof JLookupSwitchStmt) {
				JLookupSwitchStmt switchStmt = (JLookupSwitchStmt)now;
				List<UnitBox> boxes = switchStmt.getUnitBoxes();
				List<Unit> switchBranches = new ArrayList<Unit>();
				lock.lock();
				int nowI = i;
				i++;
				lock.unlock();
				for (UnitBox box : boxes) {
					Unit unit = box.getUnit();
					if (switchBranches.contains(unit)) {
						continue;
					}
					lock.lock();
					Unit invokeUnit = insertAndRecovertBox(units, box, nowI + "-" + branchi++);
					lock.unlock();
					switchBranches.add(invokeUnit);
				}
			} else if ((now instanceof JReturnStmt) || (now instanceof ReturnVoidStmt)){
				//TODO
				lock.lock();
				insertBeforeReturn(units, pre, now, methodid + "-" + branchi++);
				lock.unlock();
			}

			pre = now;
		}
		if (methodMain != null) {
			InvokeStmt invokeStmt = getNewLogStmt(methodid + "");
			units.insertBefore(invokeStmt, methodMain);
		} else {
			System.out.println("error: methods miss the logs!");
		}
		Chain<Trap> traps = b.getTraps();
		Iterator<Trap> trapit = traps.snapshotIterator();
		while (trapit.hasNext()) {
			Trap nowTrap = trapit.next();
			UnitBox box = nowTrap.getHandlerUnitBox();
			lock.lock();
			insertAfterTrap(units, box, methodid + "-" + branchi++);
			lock.unlock();
		}

	}
	private Unit insertAfterTrap(PatchingChain<Unit> units, UnitBox targetUnitBox, String logstr) {
		Unit targetUnit = targetUnitBox.getUnit();
		Unit checkUnit = units.getSuccOf(targetUnit);
		boolean shouldSkip = false;
		if ((checkUnit != null) && (checkUnit instanceof JInvokeStmt)) {
			InvokeExpr expr = ((JInvokeStmt)checkUnit).getInvokeExpr();
			SootMethod method = expr.getMethod();
			String exprName = method.getName();
			SootClass sclass = method.getDeclaringClass();
			String className = sclass.getName();
			if (exprName.equals("v") && className.equals("android.util.Log")) {
				List<Value> values = expr.getArgs();
				if (values.size() == 2) {
					Value loc = values.get(0);
					if ((loc instanceof StringConstant) &&((StringConstant)loc).value.equals("loc")) {
						shouldSkip = true;
					}
				}
			}
		}
		Unit invokeStmt = null;
		if (!shouldSkip) {
			invokeStmt = getNewLogStmt(logstr);
			if (!notaddanything) {
				units.insertAfter(invokeStmt, targetUnit);
			}
		} else {
			invokeStmt = checkUnit;
		}
		return invokeStmt;
	}
	
	private Unit insertBeforeReturn(PatchingChain<Unit> units, Unit preUnit, Unit targetUnit, String logstr) {
		boolean shouldSkip = false;
		if ((preUnit != null) && (preUnit instanceof JInvokeStmt)) {
			InvokeExpr expr = ((JInvokeStmt)preUnit).getInvokeExpr();
			SootMethod method = expr.getMethod();
			String exprName = method.getName();
			SootClass sclass = method.getDeclaringClass();
			String className = sclass.getName();
			if (exprName.equals("v") && className.equals("android.util.Log")) {
				List<Value> values = expr.getArgs();
				if (values.size() == 2) {
					Value loc = values.get(0);
					if ((loc instanceof StringConstant) &&((StringConstant)loc).value.equals("loc")) {
						shouldSkip = true;
					}
				}
			}
		}
		Unit invokeStmt = null;
		if (!shouldSkip) {
			invokeStmt = getNewLogStmt(logstr);
			if (!notaddanything) {
				units.insertBefore(invokeStmt, targetUnit);
			}
		} else {
			invokeStmt = preUnit;
		}
		return invokeStmt;
	}
	
	private Unit insertAndRecovertBox(PatchingChain<Unit> units, UnitBox targetUnitBox, String logstr) {
		Unit targetUnit = targetUnitBox.getUnit();
		boolean shouldSkip = false;
		if (targetUnit instanceof JInvokeStmt) {
			InvokeExpr expr = ((JInvokeStmt)targetUnit).getInvokeExpr();
			SootMethod method = expr.getMethod();
			String exprName = method.getName();
			SootClass sclass = method.getDeclaringClass();
			String className = sclass.getName();
			if (exprName.equals("v") && className.equals("android.util.Log")) {
				List<Value> values = expr.getArgs();
				if (values.size() == 2) {
					Value loc = values.get(0);
					if ((loc instanceof StringConstant) &&((StringConstant)loc).value.equals("loc")) {
						shouldSkip = true;
					}
				}
			}
		}
		Unit invokeStmt = null;
		if (!shouldSkip) {
			invokeStmt = getNewLogStmt(logstr);
			if (!notaddanything) {
				units.insertBefore(invokeStmt, targetUnit);
			}
		} else {
			invokeStmt = targetUnit;
		}
		return invokeStmt;
	}
	
	private InvokeStmt getNewLogStmt(String locContent) {
        SootClass logClass=Scene.v().getSootClass("android.util.Log");
        SootMethod sootMethod=logClass.getMethod("int v(java.lang.String,java.lang.String)");
        StaticInvokeExpr staticInvokeExpr=Jimple.v().newStaticInvokeExpr(sootMethod.makeRef(),StringConstant.v("loc"),StringConstant.v(locContent));
        InvokeStmt invokeStmt=Jimple.v().newInvokeStmt(staticInvokeExpr);
        return invokeStmt;
	}

}
