package appiumscript.scriptexecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import GUI.Main;
import data.CoverData;
import data.ITScene;
import data.PlanState;
import data.Scene;
import data.TestCaseData;
import tools.JsonHelper;

public class InterestPlan {
	public int currentit = -1;
	public PlanState state = PlanState.DEFAULT;//default, analyzing, executing, end
	public Map<Integer, String> allTestcasedatas = null;
	public Map<Integer, Double> allAnalysistimes = null;
	public Set<ITScene> allScenes = null;
	public boolean hasTestedSettings = false;
	public Map<String, TestCaseData> currentDatas = null;
	public ArrayList<String> currentTagList = null;
	public InterestPlan() {
	}
	public InterestPlanMode mode = InterestPlanMode.APPIUM;
	

	public InterestPlan(String firstExefilepath) {
		allTestcasedatas = new HashMap<Integer, String>();
		allAnalysistimes = new HashMap<Integer, Double>();
		currentit = 0;
		allTestcasedatas.put(currentit,  firstExefilepath);
		allAnalysistimes.put(currentit, 0.0);
		currentDatas = JsonHelper.gettestcasesdataAdapt(firstExefilepath, true);
		updateScenes(currentDatas, 0);
		state = PlanState.EXECUTING;
	}
	
	public void execute() {
		total: while (true) {
			switch (state) {
			case EXECUTING :{
				String currentfilename = allTestcasedatas.get(currentit);
				currentDatas = JsonHelper.gettestcasesdataAdapt(currentfilename, true);
				if (null == currentDatas) {
					currentDatas = new HashMap<String, TestCaseData>();
				}
				Set<ITScene> currentScenes = new HashSet<ITScene>();
				for (ITScene scene : allScenes) {
					if (scene.discoverit == currentit && !scene.covered) {
						currentScenes.add(scene);
					}
				}
				currentTagList = new ArrayList<String>();
				for (String key : currentDatas.keySet()) {
					currentTagList.add(key);
				}
				currentTagList.sort(new Comparator<String>() {
					@Override
					public int compare(String o1, String o2) {
						TestCaseData case1 = currentDatas.get(o1);
						TestCaseData case2 = currentDatas.get(o2);
						if (case1.firstexecutionsuccess && !case2.firstexecutionsuccess) {
							return -1;
						} else if (case2.firstexecutionsuccess && !case1.firstexecutionsuccess) {
							return 1;
						} else {
							File locfile1 = new File(case1.firstlocpath);
							File locfile2 = new File(case2.firstlocpath);
							if (locfile1.length() > locfile2.length()) {
								return -1;
							} else if (locfile1.length() == locfile2.length()){
								return 0;
							} else {
								return 1;
							}
						}
					}
				});
				if (!currentScenes.isEmpty()) {
					String newtestcasedatafilepath = Main.getInterestTestCaseDataFilePath(currentit);
					ScriptExecutor.scriptexecuteforPREFEST_T(currentTagList, currentScenes, currentDatas, newtestcasedatafilepath, currentit, mode);
					currentit++;
					allTestcasedatas.put(currentit, newtestcasedatafilepath);
					state = PlanState.ANALYSING;
				} else {
					state = PlanState.END;
				}
				store(currentDatas, currentfilename);
				break;
			}
			case ANALYSING : {
				String currentfilename = allTestcasedatas.get(currentit);
				currentDatas = JsonHelper.gettestcasesdataAdapt(currentfilename, false);
				double consumedTime = Main.analysisIt(currentfilename, currentDatas, currentit);
				currentDatas = JsonHelper.gettestcasesdataAdapt(currentfilename, true);
				allAnalysistimes.put(currentit, consumedTime);
				updateScenes(currentDatas, currentit);
				state = PlanState.EXECUTING;
				store(currentDatas, currentfilename);
				break;
			}
			case END : break total;
			default : break total;
		}	
		}
	}
	
	private void updateScenes(Map<String, TestCaseData> datas, int dicoverit) {
		Map<ITScene, Map<Object, Scene>> newfoundscenemap = null;
		if (null == allScenes) {
			allScenes = new HashSet<ITScene>();
		}
		newfoundscenemap = new HashMap<ITScene,  Map<Object, Scene>>();
		for (TestCaseData data: datas.values()) {
			if (null != data.interestScenes) {
				for (Scene scene : data.interestScenes) {
					Map<Object, Scene> tags = null;
					if (newfoundscenemap.containsKey(scene)) {
						tags = newfoundscenemap.get(scene);
					} else {
						tags = new HashMap<Object, Scene>();
						newfoundscenemap.put(new ITScene(scene, dicoverit), tags);
					}
					tags.put(data.tagname, scene);
				}
			}
		}
		for (ITScene itscene : newfoundscenemap.keySet()) {
			itscene.discovertag = newfoundscenemap.get(itscene);
		}

		CoverData coverdata = JsonHelper.getInterestAndFirstCoverData();
		for (ITScene newscene : newfoundscenemap.keySet()) {
			if (coverdata.coveredTags.contains(newscene.changebranchids)) {
				if (newscene.isnobranchScene() && currentit == 0) {
					newscene.covered = false;
				} else {
					newscene.covered = true;
				}
			}
		}

		duplicatecheck : for (ITScene newscene : newfoundscenemap.keySet()) {
			for (ITScene anotherscene : allScenes) {
					if (newscene.changebranchids.equals(anotherscene.branchids) || newscene.equalsIgnoreSuffix(anotherscene, '_')) {
					continue duplicatecheck;
				}
			}
			allScenes.add(newscene);
		}
	}

	private void store(Map<String, TestCaseData> datas, String datafilename) {
		if (null != datas && null != datafilename) {
			JsonHelper.savetestcasesdataAdapt(datas, datafilename);
		}
		if (this.state == PlanState.ANALYSING) {
			JsonHelper.setinterestplanAdapt(this, Main.getInterestPlanBakFilePath(this.currentit));
		}
		JsonHelper.setinterestplanAdapt(this, Main.interestplanfile);
		
	}

//		store(datas, datafilename);
//		
//	
//		store(datas, datafilename);
//		updateScenes(datas, datafilename);
	
	public void setPlanMode(InterestPlanMode mode) {
		this.mode = mode;
	}
}
