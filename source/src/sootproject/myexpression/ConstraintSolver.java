package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import data.InterestValue;
import data.Scene;
import data.TestCaseData;
import sootproject.analysedata.MyInterest;
import sootproject.preferenceAnalyse.PreferenceAnalyser;

public class ConstraintSolver {
	private Map<String, MyNoBranchConstraint> instancecache = new HashMap<String, MyNoBranchConstraint>();
	private LinkedList<MyConstraint> constraints = null;

	private Map<String, InterestValue> preinterests = null;
	public ConstraintSolver() {
		this.constraints = new LinkedList<MyConstraint>();

		preinterests = new HashMap<String, InterestValue>();
		MyConstraint.init();
	}

	public void addConstraint(MyConstraint newConstraint) {
		constraints.add(newConstraint);
	}
	public void addNoBranchConstraint(String loc, Set<MyExpression> exps) {
		MyNoBranchConstraint constraint = MyNoBranchConstraint.getInstance(loc, exps, instancecache);
		if (null != constraint) {
			constraints.add(constraint);
		}

	}

	public void analyse(String tagname) {
		
		for (MyConstraint cons : constraints) {
			Scene scene = null;
			if (!cons.unknown) {
				List<InterestValue> templist = new ArrayList<InterestValue>();
				templist.addAll(preinterests.values());
				if (cons instanceof MyNoBranchConstraint) {
					List<Scene> scenes = getscenes((MyNoBranchConstraint)cons, templist);
					Map<String, TestCaseData> datas = PreferenceAnalyser.getTestcasedata();
					TestCaseData nowdata = datas.get(tagname);
					if (null == nowdata.interestScenes) {
						nowdata.interestScenes = new ArrayList<Scene>();
					}
					nextone:for (Scene addscene : scenes) {
						for (Scene innerscene: nowdata.interestScenes) {
							if (innerscene.equals(addscene)) {
								continue nextone;
							}
						}
						nowdata.interestScenes.add(addscene);
					}
				} else {
					out:for (long tagetresult : cons.targetresults) {
						scene = caclucate(cons, tagetresult);
						if (scene != null) {
							scene.preinterests = templist;
							scene.body = cons.body;
							Map<String, TestCaseData> datas = PreferenceAnalyser.getTestcasedata();
							TestCaseData nowdata = datas.get(tagname);
							if (null == nowdata.interestScenes) {
								nowdata.interestScenes = new ArrayList<Scene>();
							}
							for (Scene innerscene: nowdata.interestScenes) {
								if (innerscene.equals(scene)) {
									continue out;
								}
							}
							nowdata.interestScenes.add(scene);
						}
					}
					Scene extrascene = caclucate(cons, cons.originresult);
					if (null != extrascene) {
						for (InterestValue prevalue: extrascene.interests) {
							preinterests.put(prevalue.name, prevalue);
						}
					}
				}

			}
			//z3 part
//			} else {
//				scene = calculateforZ3(cons);
//			}


		}
		
	}
	
	private Map<InterestValue, List<InterestValue>> interestlistchache = new HashMap<InterestValue, List<InterestValue>>();
	public List<Scene> getscenes(MyNoBranchConstraint constrain, List<InterestValue> preinterests) {
		List<Scene> scenes = new ArrayList<Scene>();
		Set<MyInterest> interests = constrain.involvedInterest;
		for (MyInterest interest : interests) {
			for (InterestValue interestvalue : interest.getallpossibleValues()) {
				List<InterestValue> list = interestlistchache.get(interestvalue);
				if (null == list) {
					list = new ArrayList<InterestValue>();
					list.add(interestvalue);
					interestlistchache.put(interestvalue, list);
				}
				Scene scene = new Scene(constrain.originloc, constrain.originloc, list, preinterests);
				scenes.add(scene);
			}
		}

		return scenes;
	}

	public Scene calculateforZ3(MyConstraint nowConstraint) {
		Solver solver = MyConstraint.ctx.mkSolver();
		if (nowConstraint.z3Expr instanceof BoolExpr) {
			if (nowConstraint.originresult == 1) {
				solver.add(MyConstraint.ctx.mkNot((BoolExpr)nowConstraint.z3Expr));
			} else {
				solver.add((BoolExpr)nowConstraint.z3Expr);
			}
			
			if (solver.check() == Status.SATISFIABLE) {
				Model model = solver.getModel();
				List<InterestValue> values = new ArrayList<InterestValue>();
				for (MyInterest interest : nowConstraint.involvedInterest) {
					Expr interestexpr = MyConstraint.interestMap.get(interest);
					String value = model.evaluate(interestexpr, false).toString();
					values.add(interest.getInterestValue(value));
				}
				Scene scene = new Scene(nowConstraint.originloc, nowConstraint.targetlocs.get(0), values, null);
				return scene;
			}
		}

		return null;
	}
	
	public Scene caclucate(MyConstraint nowConstraint, long targetresult) {
		Set<MyInterest> involvedInterests = nowConstraint.involvedInterest;
		Map<MyInterest, itData> interestDataMap = new HashMap<MyInterest, itData>();
		Map<MyInterest, String> interestValueMap = new HashMap<MyInterest, String>();
		LinkedList<MyInterest> involvedInterestslist = new LinkedList<MyInterest>(involvedInterests);

		for (MyInterest temp : involvedInterestslist) {
			itData newData = new itData(temp, temp.getPossibleValues());
			interestDataMap.put(temp, newData);
			interestValueMap.put(temp, newData.nowValue);
		}

		total:while (true) {
			HashMap<MyInterest, String> newmap = new HashMap<MyInterest, String>();
			for (MyInterest temp : interestValueMap.keySet()) {
				MyExpression selfexp = nowConstraint.interestselfexps.get(temp);
				if (null != selfexp) {
					Object newvalue = selfexp.calculate(interestValueMap).value;
					if (null != newvalue) {
						newmap.put(temp, newvalue + "");
						continue;
					}
				}
				newmap.put(temp, interestValueMap.get(temp));
			}
			
			ExpressionValue calcuResult = nowConstraint.myexpr.calculate(newmap);
			if (calcuResult.value != null) {
				long nowvalue = Long.parseLong(calcuResult.value.toString());
				boolean checkpoint = false;
				if (nowConstraint instanceof MySwitchConstraint && !nowConstraint.targetresults.contains(nowvalue) && nowConstraint.originresult != nowvalue) {
					nowvalue = -10000;
				}
				checkpoint = targetresult == nowvalue;
				if (checkpoint) {
					String targetloc = null;
					for (int i = 0; i < nowConstraint.targetresults.size(); i++) {
						if (nowConstraint.targetresults.get(i).equals(nowvalue)) {
							targetloc = nowConstraint.targetlocs.get(i);
							break;
						}
					}
					List<InterestValue> values = new ArrayList<InterestValue>();
					for (MyInterest nowinterest: interestValueMap.keySet()) {
						InterestValue tempvalue = nowinterest.getInterestValue(interestValueMap.get(nowinterest));
						values.add(tempvalue);
						if (null != tempvalue.dependency) {
							values.add(tempvalue.dependency);
						}
					}
					Scene scene = new Scene(nowConstraint.originloc, targetloc, values, null);
					return scene;
				}
			}
			int i = involvedInterestslist.size() - 1;
			boolean shouldLoop = false;
			do {
				itData nowData = interestDataMap.get(involvedInterestslist.get(i));
				shouldLoop = nowData.add();
				interestValueMap.put(nowData.interest, nowData.nowValue);
				i--;
				if ((i < 0) && shouldLoop) {
					break total;
				}
			} while (shouldLoop);
			
		}
		
		return null;
	}
	
//	private Scene caclucateWithBeforeConstraints(MyConstraint nowConstraint) {
//		LinkedList<MyConstraint> beforeconstraints = new LinkedList<MyConstraint>();
//		for (int i = 0; i < constraints.size(); i++) {
//			MyConstraint now = constraints.get(i);
//			if (!now.unknown) {
//				beforeconstraints.add(now);
//			}
//			if (now == nowConstraint) {
//				break;
//			}
//		}
//		Set<MyInterest> involvedInterests = new HashSet<MyInterest>();
//		for (MyConstraint con : beforeconstraints) {
//			involvedInterests.addAll(con.involvedInterest);
//		}
//		Map<MyInterest, itData> interestDataMap = new HashMap<MyInterest, itData>();
//		Map<MyInterest, String> interestValueMap = new HashMap<MyInterest, String>();
//		LinkedList<MyInterest> involvedInterestslist = new LinkedList<MyInterest>(involvedInterests);
//
//		for (MyInterest temp : involvedInterestslist) {
//			itData newData = new itData(temp, temp.getPossibleValues());
//			interestDataMap.put(temp, newData);
//			interestValueMap.put(temp, newData.nowValue);
//		}
//
//		total:while (true) {
//			boolean isok = true;
//			long finalnewvalue = -1;
//			for (MyConstraint con : beforeconstraints) {
//				HashMap<MyInterest, String> newmap = new HashMap<MyInterest, String>();
//				for (MyInterest temp : interestValueMap.keySet()) {
//					MyExpression selfexp = con.interestselfexps.get(temp);
//					if (null != selfexp) {
//						Object newvalue = selfexp.calculate(interestValueMap).value;
//						if (null != newvalue) {
//							newmap.put(temp, newvalue + "");
//							continue;
//						}
//					}
//					newmap.put(temp, interestValueMap.get(temp));
//				}
//				
//				ExpressionValue calcuResult = con.myexpr.calculate(newmap);
//				if (null != calcuResult.value) {
//					long nowvalue = Long.parseLong(calcuResult.value.toString());
//					if (con != nowConstraint) {
//						if (nowvalue != con.originresult) {
//							isok = false;
//							break;
//						}
//					} else {
//						finalnewvalue = nowvalue;
//						if (nowvalue == con.originresult) {
//							isok = false;
//							break;
//						}
//					}	
//				}
//			}
//			
//			if (isok) {
//				String targetloc = null;
//				for (int i = 0; i < nowConstraint.targetresults.size(); i++) {
//					if (nowConstraint.targetresults.get(i).equals(finalnewvalue)) {
//						targetloc = nowConstraint.targetlocs.get(i);
//						break;
//					}
//				}
//				List<InterestValue> values = new ArrayList<InterestValue>();
//				List<InterestValue> prevalues = new ArrayList<InterestValue>();
//				for (MyInterest nowinterest: interestValueMap.keySet()) {
//					if (nowConstraint.involvedInterest.contains(nowinterest)) {
//						values.add(nowinterest.getInterestValue(interestValueMap.get(nowinterest)));
//					} else {
//						prevalues.add(nowinterest.getInterestValue(interestValueMap.get(nowinterest)));
//					}
//					
//				}
//				Scene scene = new Scene(nowConstraint.originloc, targetloc, values, prevalues);
//				return scene;
//			}
//			int i = involvedInterestslist.size() - 1;
//			boolean shouldLoop = false;
//			do {
//				itData nowData = interestDataMap.get(involvedInterestslist.get(i));
//				shouldLoop = nowData.add();
//				interestValueMap.put(nowData.interest, nowData.nowValue);
//				i--;
//				if ((i < 0) && shouldLoop) {
//					break total;
//				}
//			} while (shouldLoop);
//			
//		}
//		
//		return null;
//	}
	
	private class itData {
		MyInterest interest = null;
		String nowValue = null;
		List<String> allvalues = null;
		int allNum = -1;
		int nowNum = -1;
		public itData(MyInterest interest, List<String> allvalues) {
			this.interest = interest;
			this.allvalues = allvalues;
			this.allNum = allvalues.size() - 1;
			this.nowNum = 0;
			this.nowValue = allvalues.get(0);
		}
		
		public boolean add() {
			this.nowNum++;
			if (nowNum > allNum) {
				nowNum = 0;
				nowValue = allvalues.get(nowNum);
				return true;
			} else {
				nowValue = allvalues.get(nowNum);
				return false;
			}
		}
	}

	
}
