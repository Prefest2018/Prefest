package sootproject.analysedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import sootproject.data.LogBranchNode;
import sootproject.data.MyIfStatement;
import sootproject.data.MyMethodDeclaration;
import sootproject.data.MyNode;
import sootproject.data.MySwitchStatement;
import sootproject.data.MyTryCatch;
import sootproject.exception.WrongThreadException;
import sootproject.myexpression.CMD;
import sootproject.myexpression.CMDType;
import sootproject.myexpression.ExpressionTranslator;
import sootproject.myexpression.MyConstraint;
import sootproject.myexpression.MyExpression;
import sootproject.myexpression.MyVariable;
import tools.Logger;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.StringConstant;
import soot.jimple.SwitchStmt;
import soot.jimple.internal.AbstractDefinitionStmt;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JInterfaceInvokeExpr;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JStaticInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.jimple.internal.JimpleLocal;
import soot.tagkit.Host;

public class TrailState {
	private static Map<Integer, MyNode> logMaps = null;
	private static Map<SootMethod, MyMethodDeclaration> methodMaps = null;
	private static Map<Unit, MyNode> nodeMaps = null;
	private static Map<SootMethod, Map<Integer, MyMethodDeclaration>> overrideMaps = null;
	private ExpressionTranslator translator = null;
	private String tagname = null;
	public List<ThreadState> threads = null;
	private int threadNum = 0;
	public static boolean debug = false;

	
	public TrailState(Map<String, MyInterest> interestMap, String tagname, Map<Long, String> stridmap) {
		translator = new ExpressionTranslator(interestMap, tagname, stridmap);
		threads = new LinkedList<ThreadState>();
		this.tagname = tagname;
	}
	
	public void analysePreference() {
		translator.analysePreference();
	}
	
	public void next(int logid, int branchid) {
		if (debug) {
			System.out.println("\n\n current logid: " + logid +  "      branchNum: "+ branchid);
			Logger.log("\n\n current logid: " + logid +  "      branchNum: "+ branchid);
		}
		boolean hasDealt = false;
		ThreadState dealthread = null;
		for (int i = threads.size() - 1; i >= 0; i--) {
			ThreadState thread = threads.get(i);
			if (thread.iterator(logid, branchid)) {
				dealthread = thread;
				hasDealt = true;

				break;
			}
		}
		if (null != dealthread) {
			if (dealthread.stack.isEmpty()) {
				threads.remove(dealthread);
			} else {
				threads.remove(dealthread);
				threads.add(dealthread);
			}
		}
		
		if (!hasDealt) {
			ThreadState newThread = new ThreadState(threadNum++);
			threads.add(newThread);
			if (!newThread.iterator(logid, branchid)) {
				if (debug) {
					System.out.println("error: the logid cannot be resloved in new threads: " + logid + "-" + branchid);
					Logger.log("error: the logid cannot be resloved in new threads: " + logid + "-" + branchid);
				}
				threads.remove(newThread);
			} else {

				if (threads.size() > 300) {
					threads.remove(0);
				}
			}
		}
	}
	
	public void stop() {
		
	}

	public static void setLogMaps(Map<Integer, MyNode> logMaps, Map<Unit, MyNode> nodeMaps, Map<SootMethod, MyMethodDeclaration> methodMaps, Map<SootMethod, Map<Integer, MyMethodDeclaration>> overrideMaps, Map<SootMethod, Set<String>> skipmethodMaps) {
		TrailState.logMaps = logMaps;
		TrailState.methodMaps = methodMaps;
		TrailState.nodeMaps = nodeMaps;
		TrailState.overrideMaps = overrideMaps;
		for (SootMethod m : skipmethodMaps.keySet()) {
			TrailState.methodMaps.remove(m);
			TrailState.overrideMaps.remove(m);
		}
	}
	
	
	public class ThreadState {
		protected Stack<BlockState> stack = null;
		protected int threadNum = -1;
		protected LinkedList<CMD> cacheCmd = null;
		public ThreadState(int threadNum) {
			stack = new Stack<BlockState>();
			this.threadNum = threadNum;
			this.cacheCmd = new LinkedList<CMD>();
		}
		
		private void tryCatch(int logidNum, int branchNum) {
			int i = 0;
			boolean catchException = false;
			int stacksize = stack.size();
			for (i = stacksize - 1; i >= 0; i--) {
				BlockState block = stack.get(i);
				catchException = block.startFromtryCatch(logidNum, branchNum);
				if(catchException) {
					break;
				}
			}
			if (catchException) {
				for (int j = i + 1; j < stacksize; j++) {
					stack.pop();
				}
			}
		}
		public boolean iterator(int logidNum, int branchNum) {
			tryCatch(logidNum, branchNum);
			boolean shouldContinue = true;

			try {
				while (shouldContinue) {
					if (stack.isEmpty()) {
						if (branchNum >= 0) {

							throw WrongThreadException.getWrongThreadException();
						} else {
							stack.push(new BlockState(logidNum, translator, this));
						}
					}
					BlockState nowState = stack.peek();
					shouldContinue = nowState.next(logidNum, branchNum);
				}
			} catch (WrongThreadException e) {
				return false;
			}
			return true;
		}
	}
	
	public class BlockState{
		protected int logidNum = -1;
		protected String currentLoc = "-1";
		protected MyMethodDeclaration node = null;
		protected Unit currentUnit = null;
		protected Unit nextUnit = null;
		protected PatchingChain<Unit> unitChain = null;
		protected ExpressionTranslator translator = null;
		protected List possibleValue = null;
		protected int chosenValue = -1;
		protected Body body = null;
		protected Stack<BlockState> stack = null;
		protected ThreadState threadState = null;
		protected boolean returncheck = false;
		protected LinkedList<CMD> cacheCmd = null;
		protected HashMap<Integer, MyVariable> nowLocalMap = null;
		public BlockState(int logidNum, ExpressionTranslator translator, ThreadState threadState) {
			this.logidNum = logidNum;
			this.translator = translator;
			this.threadState = threadState;
			this.stack = threadState.stack;
			this.node = (MyMethodDeclaration)logMaps.get(logidNum);
			this.cacheCmd = threadState.cacheCmd;
			this.nowLocalMap = new HashMap<Integer, MyVariable>();
			if ((this.node == null) || !(this.node instanceof MyMethodDeclaration)) {
				System.out.println("error: BlockState<Init>: MethodDeclaration for MyNode is not found, logidNum: " + logidNum);
				Logger.log("error: BlockState<Init>: MethodDeclaration for MyNode is not found, logidNum: " + logidNum);
			}
			body = ((MyMethodDeclaration)this.node).getBody();
			if (debug) {
				Logger.log(body.toString());
			}
			unitChain = body.getUnits();
		}
		
		public boolean startFromtryCatch(int logidNum, int branchNum) {
			MyTryCatch trap = node.getTrap(logidNum, branchNum);
			if (trap != null) {
				Trap nowtrap = (Trap)trap.getInnerNode();
				this.currentUnit = nowtrap.getBeginUnit();
				return true;
			} else {
				return false;
			}
		}

		public boolean next(int logidNum, int branchNum) throws WrongThreadException{
			
			Iterator<Unit> it = null;
			if (null == currentUnit) {
				it = unitChain.iterator();
			} else {
				it = unitChain.iterator(currentUnit);
			}
			while (it.hasNext()) {
				Unit nowUnit = it.next();
				if (nowUnit instanceof IfStmt) {
					MyIfStatement ifNode = (MyIfStatement)nodeMaps.get(nowUnit);
					Unit nextUnit = ifNode.getBranchUnit(logidNum, branchNum);
					if (null != nextUnit) {
						cacheCmd.add(new CMD(nowUnit, nextUnit, CMDType.IF, body, nowLocalMap, null));
						currentUnit = nextUnit;
						return true;
					} else {

						currentUnit = nowUnit;
						throw WrongThreadException.getWrongThreadException();
					}
					
					

				} else if (nowUnit instanceof LookupSwitchStmt) {
					MySwitchStatement switchNode = (MySwitchStatement)nodeMaps.get(nowUnit);
					Unit nextUnit = switchNode.getBranchUnit(logidNum, branchNum);
					if (null != nextUnit) {
						cacheCmd.add(new CMD(nowUnit, nextUnit, CMDType.SWITCH, body, nowLocalMap, null));
						currentUnit = nextUnit;
						return true;
					} else {
						currentUnit = nowUnit;
						throw WrongThreadException.getWrongThreadException();
					}
				} else if (nowUnit instanceof InvokeStmt) {
					InvokeExpr invokeexp = ((InvokeStmt)nowUnit).getInvokeExpr();
					SootMethod nowMethod = invokeexp.getMethod();
					MyMethodDeclaration myMethod = getMyMethod(invokeexp, logidNum);
					Result result = locConfirm((InvokeStmt)nowUnit, logidNum, branchNum);
					if (result == Result.TRUE) {
						translator.dealWithStmts(cacheCmd);
						cacheCmd.clear();
						if (returncheck) {
							continue;
						} else {
							currentUnit =  it.next();
							return false;
						}
					} else if (result == Result.FALSE) {
						if (myMethod == null) {
							cacheCmd.add(new CMD(null, ((InvokeStmt)nowUnit).getInvokeExpr(), CMDType.SPINVOKE, body, nowLocalMap, null).append(currentLoc));
							continue;
						}else if (myMethod.getLogIndex() == logidNum && -1 == branchNum) {
							currentUnit = nowUnit;
							BlockState newState = new BlockState(logidNum, translator, threadState);
							stack.push(newState);
							cacheCmd.add(new CMD(((InvokeStmt)nowUnit).getInvokeExpr(), null, CMDType.INVOKE, body, nowLocalMap, newState.nowLocalMap).append(myMethod));
							this.nextUnit = it.next();
							return true;
						} else {

							currentUnit = nowUnit;
							throw WrongThreadException.getWrongThreadException();
						}
					} else if (result == Result.FAILURE){
						currentUnit = nowUnit;
						throw WrongThreadException.getWrongThreadException();
					}

				} else if (nowUnit instanceof GotoStmt) {
					UnitBox target = ((GotoStmt)nowUnit).getTargetBox();
					currentUnit = target.getUnit();
					it = unitChain.iterator(currentUnit);
					continue;
				} else if (nowUnit instanceof ReturnVoidStmt) {
					if (returncheck) {
						stack.pop();
						if (!stack.isEmpty()) {
							BlockState nowState = stack.peek();
							nowState.currentUnit = nowState.nextUnit;
						}
						return false;
					} else {
						System.out.println("error: 'ReturnVoidStmt' returns when there is no return check!");
					}

				} else if (nowUnit instanceof ReturnStmt) {
					if (returncheck) {
						stack.pop();
						if (!stack.isEmpty()) {
							BlockState nowState = stack.peek();
							if (nowState.currentUnit instanceof JAssignStmt) {
								translator.dealWithReturnStatement((ReturnStmt)nowUnit, (JAssignStmt)nowState.currentUnit, nowLocalMap, nowState.nowLocalMap);
							}
							nowState.currentUnit = nowState.nextUnit;
						}
						return false;
					} else {
						System.out.println("error: 'ReturnStmt' returns when there is no return check!");
					}
				} else if (nowUnit instanceof JAssignStmt || nowUnit instanceof JIdentityStmt) {
					AbstractDefinitionStmt assign = (AbstractDefinitionStmt)nowUnit;
					Value right = assign.getRightOp();
					if (right instanceof InvokeExpr) {
						MyMethodDeclaration myMethod = getMyMethod(((InvokeExpr)right), logidNum);
						if (myMethod == null) {
							cacheCmd.add(new CMD(assign.getLeftOp(), (InvokeExpr)right, CMDType.SPINVOKE, body, nowLocalMap, null).append(currentLoc));
							continue;
						} else {
							if (myMethod.getLogIndex() == logidNum && -1 == branchNum) {
								currentUnit = nowUnit;
								BlockState newState = new BlockState(logidNum, translator, threadState);
								stack.push(newState);
								cacheCmd.add(new CMD((InvokeExpr)right, assign.getLeftOp(), CMDType.INVOKE, body, nowLocalMap, newState.nowLocalMap).append(myMethod));
								this.nextUnit = it.next();

							} else {
								currentUnit = nowUnit;
								throw WrongThreadException.getWrongThreadException();
							}
							return true;
						}
					} else {
						cacheCmd.add(new CMD(assign, null, CMDType.ASSIGN, body, nowLocalMap, null));
					}

				} else {
				}
			}

			return false;
		}

		private MyMethodDeclaration getMyMethod(InvokeExpr invokeexpr, int lognum) {
			SootMethod method = invokeexpr.getMethod();
			MyMethodDeclaration result = methodMaps.get(method);
			if ((result == null || result.getLogIndex() != lognum) && (invokeexpr instanceof JVirtualInvokeExpr || invokeexpr instanceof JInterfaceInvokeExpr)) {
				Map<Integer, MyMethodDeclaration> map = overrideMaps.get(method);
				if (null != map) {
					MyMethodDeclaration newresult = map.get(lognum);
					if (null != newresult) {
						return newresult;
					}
				}
			}
			return result;
		}
		
		private Result locConfirm(final InvokeStmt invoke, int logid, int branchid) {
			InvokeExpr expr = invoke.getInvokeExpr();
			SootMethod method = expr.getMethod();
			String exprName = method.getName();
			SootClass sclass = method.getDeclaringClass();
			String className = sclass.getName();
			if (expr instanceof JStaticInvokeExpr) {
				if (exprName.equals("v") && className.equals("android.util.Log")) {
					List<Value> values = expr.getArgs();
					if (values.size() != 2) {
						return Result.FALSE;
					}
					Value loc = values.get(0);
					if (!(loc instanceof StringConstant) || !(((StringConstant)loc).value.equals("loc"))) {
						return Result.FALSE;
					}
					String logstr = ((StringConstant)values.get(1)).value;
					int nowlogid = -1;
					int nowbranchid = -1;
					if (!logstr.contains("-")) {
						nowlogid = Integer.parseInt(logstr);
					} else {
						String[] temp = logstr.split("-");
						nowlogid = Integer.parseInt(temp[0]);
						nowbranchid = Integer.parseInt(temp[1]);
					}
					if ((nowlogid == logid) && (nowbranchid == branchid)) {
						if(null != this.node.getReturn(nowlogid, nowbranchid)) {
							returncheck = true;
						}
						currentLoc = logstr;
						return Result.TRUE;
					} else {
						return Result.FAILURE;
					}
					
				}
			}
			return Result.FALSE;
		}
		
	}
	
	



}
