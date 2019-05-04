package sootproject.myexpression;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FPExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.SeqExpr;

import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MySystemService;
import tools.Logger;
import soot.Body;
import soot.Unit;

public abstract class MyConstraint {
	protected MyExpression myexpr = null;
	protected boolean unknown = false;
	protected Expr z3Expr = null;
	protected Set<MyInterest> involvedInterest = null;
	protected static Map<MyInterest, Expr> interestMap = null;
	protected static Context ctx = null;
	protected long originresult = -1;
	protected String originloc = null;
	protected List<Long> targetresults = null;
	protected List<String> targetlocs = null;
	protected Unit unit = null;
	protected Body body = null;
	protected Map<MyInterest, MyExpression> interestselfexps = null;
	public MyConstraint(MyExpression myexp, Unit unit, Body body) {
		this.unit = unit;
		this.body = body;
		if (myexp.unknown) {
			//z3Expr = getZ3Expr(myexp);
		} else {
			this.myexpr = myexp;
		}
		this.unknown = myexp.unknown;
		this.involvedInterest = getInvolvedInterest(myexp);
		this.interestselfexps = new HashMap<MyInterest, MyExpression>();
		for (MyInterest nowinterest: this.involvedInterest) {
			interestselfexps.put(nowinterest, nowinterest.getSelfexp());
		}
	}
	
	public MyConstraint(Unit unit, Body body) {
		this.unit = unit;
		this.body = body;
	}
	
	public boolean isSystemEventReleated() {
		for (MyInterest interest : involvedInterest) {
			if (interest instanceof MySystemService) {
				return true;
			}
		}
		return false;
	}

	
	public static void init() {
		interestMap = new HashMap<MyInterest, Expr>();
//		ctx = new Context();
	}
	
	protected static Expr getZ3Expr(MyExpression myexp) {
		if (myexp == null) {
			System.out.println("error : myexp is null!");
			Logger.log("error : myexp is null!");
		}
		Expr trueExp = null;
		switch (myexp.getType()) {
		case EXPRESSION : {
			Expr expr1 = myexp.param1 != null?getZ3Expr(myexp.param1):null;
			Expr expr2 = myexp.param2 != null?getZ3Expr(myexp.param2):null;
			trueExp = opertaion(expr1, expr2, myexp.operator);
			break;
		}
		case STRING : {
			trueExp = ctx.MkString(myexp.content + "");
			break;
		}
		case INT : {
			trueExp = ctx.mkInt(myexp.content + "");
			break;
		}
//		case LONG : {
//			trueExp = ctx.mkInt(myexp.content + "");
//			break;
//		}
		case FLOAT : {
			trueExp = ctx.mkFP(Double.parseDouble(myexp.content + "") , ctx.mkFPSort64());
			break;
		}
		case INTEREST : {
			MyInterest nowInterest = (MyInterest)myexp.content;
			trueExp = interestMap.get(nowInterest);
			if (null == trueExp) {
				String constName = nowInterest.getClass().getName() + "-" + nowInterest.getName();
				switch (nowInterest.getResultType()) {
				case STRING: {
					trueExp = ctx.mkConst(constName, ctx.mkStringSort());
					break;
				}
				case INT: {
					trueExp = ctx.mkConst(constName, ctx.mkIntSort());
					break;
				}
				case FLOAT: {
					trueExp = ctx.mkConst(constName, ctx.mkFPSort64());
					break;
				}
				default : {
					break;
				}
				}
				interestMap.put(nowInterest, trueExp);
			}

		}
		
		default : break;
		}

		return trueExp;
	}
	
	//for z3
	protected static Expr opertaion(Expr expr1, Expr expr2, OperationType optype) {
		if (null == expr1 && null == expr2) {
			return null;
		}
		if (null == expr1 || null == expr2) {
			if (optype != OperationType.AND && optype != OperationType.LENGTH && optype != OperationType.OR && optype != OperationType.NEG && optype != OperationType.APPEND) {
				return null;
			}
		}
		
		Expr resultExp = null;
		if (expr1 instanceof FPExpr && expr2 instanceof ArithExpr) {
			if (expr2 instanceof IntNum) {
				expr2 = ctx.mkFP(Double.valueOf(((IntNum)expr2).getInt()), ctx.mkFPSort64());
			} else {
				return null;
			}
		} else if (expr2 instanceof FPExpr && expr1 instanceof ArithExpr) {
			if (expr1 instanceof IntNum) {
				expr1 = ctx.mkFP(Double.valueOf(((IntNum)expr1).getInt()), ctx.mkFPSort64());
			} else {
				return null;
			}
		}
		boolean hasFP = false;
		if (expr1 instanceof FPExpr || expr2 instanceof FPExpr) {
			hasFP = true;
		}
		switch (optype) {
		case ADD : {
			if (hasFP) {
				resultExp = ctx.mkFPAdd(ctx.mkFPRoundNearestTiesToEven(), (FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkAdd((ArithExpr)expr1, (ArithExpr)expr2);
			}

			break;
		}
		case SUB : {
			if (hasFP) {
				resultExp = ctx.mkFPSub(ctx.mkFPRoundNearestTiesToEven(), (FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkSub((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case MUL : {
			if (hasFP) {
				resultExp = ctx.mkFPMul(ctx.mkFPRoundNearestTiesToEven(), (FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkMul((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case DIV : {
			if (hasFP) {
				resultExp = ctx.mkFPDiv(ctx.mkFPRoundNearestTiesToEven(), (FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkDiv((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case MOD : {
			if (hasFP) {
				resultExp = ctx.mkFPRem((FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkRem((IntExpr)expr1, (IntExpr)expr2);
			}
		}
		case EQUAL : {
			if (expr1.isBool() && expr2.isIntNum()) {
				int num = ((IntNum)expr2).getInt();
				if (num == 0) {
					expr2 = ctx.mkBool(false);
				} else {
					expr2 = ctx.mkBool(true);
				}
			} else if (expr1.isIntNum() && expr2.isBool()) {
				int num = ((IntNum)expr1).getInt();
				if (num == 0) {
					expr1 = ctx.mkBool(false);
				} else {
					expr1 = ctx.mkBool(true);
				}
			}
			if (hasFP) {
				resultExp = ctx.mkFPEq((FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkEq(expr1, expr2);
			}
			break;
		}
		case NAEQ : {
			if (expr1.isBool() && expr2.isIntNum()) {
				int num = ((IntNum)expr2).getInt();
				if (num == 0) {
					expr2 = ctx.mkBool(false);
				} else {
					expr2 = ctx.mkBool(true);
				}
			} else if (expr1.isIntNum() && expr2.isBool()) {
				int num = ((IntNum)expr1).getInt();
				if (num == 0) {
					expr1 = ctx.mkBool(false);
				} else {
					expr1 = ctx.mkBool(true);
				}
			}
			resultExp = ctx.mkNot(ctx.mkEq(expr1, expr2));
			break;
		}
		case NEG : {
			if (expr1 instanceof BoolExpr) {
				resultExp = ctx.mkNot((BoolExpr)expr1);
			} else if (expr1 instanceof ArithExpr){
				resultExp = ctx.mkMul(ctx.mkInt(0), (ArithExpr)expr1);
			}

			break;
		}
		case LE : {
			if (hasFP) {
				resultExp = ctx.mkFPLEq((FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkLe((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case LT : {
			if (hasFP) {
				resultExp = ctx.mkFPLt((FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkLt((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case GE : {
			if (hasFP) {
				resultExp = ctx.mkFPGEq((FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkGe((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case GT : {
			if (hasFP) {
				resultExp = ctx.mkFPGt((FPExpr)expr1, (FPExpr)expr2);
			} else {
				resultExp = ctx.mkGt((ArithExpr)expr1, (ArithExpr)expr2);
			}
			break;
		}
		case LENGTH : {
			resultExp = ctx.MkLength((SeqExpr)expr1);
			break;
		}
		case STARTWITH : {
			resultExp = ctx.MkPrefixOf((SeqExpr)expr2, (SeqExpr)expr1);
			break;
		}
		case ENDWITH : {
			resultExp = ctx.MkSuffixOf((SeqExpr)expr2, (SeqExpr)expr1);
			break;
		}
		case AND : {
			if (null == expr1) {
				resultExp = expr2;
			} else if (null == expr2) {
				resultExp = expr1;
			} else {
				resultExp = ctx.mkAnd((BoolExpr)expr1, (BoolExpr)expr2);
			}
			break;
		}
		case OR : {
			if (null == expr1) {
				resultExp = expr2;
			} else if (null == expr2) {
				resultExp = expr1;
			} else {
				resultExp = ctx.mkOr((BoolExpr)expr1, (BoolExpr)expr2);
			}
			break;
		}
		case APPEND : {
			if (null == expr1) {
				resultExp = expr2;
			} else if (null == expr2) {
				resultExp = expr1;
			} else {
				if (expr1 instanceof SeqExpr && expr2 instanceof SeqExpr) {
					resultExp = ctx.MkConcat((SeqExpr)expr1, (SeqExpr)expr2);
				}
			}
		}
		default:
			break;
		}
		return resultExp;
	}
	
	public static Set<MyInterest> getInvolvedInterest(MyExpression exp) {
		Set<MyInterest> involvedInterests = new HashSet<MyInterest>();
		Set<MyExpression> allexps = new HashSet<MyExpression>();
		getInvolvedInterestIt(involvedInterests, exp, allexps);
		return involvedInterests;
	}
	
	private static void getInvolvedInterestIt(Set<MyInterest> involvedInterests, MyExpression exp, Set<MyExpression> allexps) {
		if (!allexps.contains(exp)) {
			allexps.add(exp);
		} else {
			return;
		}
		switch (exp.getType()) {
		case EXPRESSION: {
			if (null != exp.param1) {
				getInvolvedInterestIt(involvedInterests, exp.param1, allexps);
			}
			if (null != exp.param2) {
				getInvolvedInterestIt(involvedInterests, exp.param2, allexps);
			}
			break;
		}
		case INTEREST: {
			involvedInterests.add((MyInterest)exp.content);
			break;
		}
		case ARRAY: {
			MyArrayContent arraycontent = (MyArrayContent)exp.content;
			for (Object key : arraycontent.getContentList().keySet()) {
				MyExpression innerexp = arraycontent.contentlist.get(key);
				if (null != innerexp && innerexp.type != ResultType.INSTANCE && innerexp.type != ResultType.ARRAY) {
					getInvolvedInterestIt(involvedInterests, arraycontent.contentlist.get(key), allexps);
				}
			}
			break;
		}
		case INSTANCE: {
			MyInstanceContent instancecontent = (MyInstanceContent)exp.content;
			for (String key : instancecontent.getfieldmap().keySet()) {
				MyExpression innerexp = instancecontent.getfieldmap().get(key);
				if (null != innerexp && innerexp.type != ResultType.INSTANCE && innerexp.type != ResultType.ARRAY) {
					getInvolvedInterestIt(involvedInterests, innerexp, allexps);
				}
			}
			break;
		}
		default : {
			break;
		}
		}
	}


	public Set<MyInterest> getInvolvedInterest() {
		return involvedInterest;
	}


	public long getOriginResult() {
		return originresult;
	}
}
