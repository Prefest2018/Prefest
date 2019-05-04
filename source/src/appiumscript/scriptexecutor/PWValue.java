package appiumscript.scriptexecutor;
import java.io.File;
import java.util.*;

import GUI.Main;
import data.InterestValue;
import data.TestCaseData;
import sootproject.analysedata.MyInterest;
import sootproject.preferenceAnalyse.PreferenceAnalyser;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.JsonHelper;
import tools.PWCounter;
import tools.ProcessExecutor;

public class PWValue {

	public int index = -1;
	public String state = "unstart";//unstart, processing, end
	public String preferencescriptfile = null;
	public String preferencescriptlogfile = null;
	public String preferencetestcaseinfofile = null;
	public String nowtagname = Main.PRESCRIPT;

	protected Map<String, String> scriptmap = null;
	protected LinkedList<String> taglist = null;
	public PWPlan plan = null;

	private ArrayList<InterestValue> allpreferences = null;

	public PWValue(PWPlan plan, int index, PWCounter counter, Map<String, String> scriptmap, Set<PreferenceTreeNode> allpreferencetree, LinkedList<String> taglist) {
		this.index = index;
		this.scriptmap = scriptmap;
		this.taglist = taglist;
		this.plan = plan;
		int maxmun = allpreferencetree.size() + 6;
		allpreferences = new ArrayList<InterestValue>();
		int i = 0;
		Boolean[] booleanlist = counter.getValues(maxmun, index);
		for (PreferenceTreeNode node : allpreferencetree) {
			if (booleanlist[i]) {

				InterestValue value = node.toInterestValueDefault();
				if (null != value) {
					allpreferences.add(value);
				}
			} else {

				InterestValue value = node.toInterestValueReverseDefault();
				if (null != value) {
					allpreferences.add(value);
				}			
			}
			i++;
		}
		for (MyInterest sysmteminterest : PreferenceAnalyser.allsystemservices.values()) {
			if (booleanlist[i]) {
				allpreferences.add(sysmteminterest.getInterestValue("true"));
			} else {
				allpreferences.add(sysmteminterest.getInterestValue("false"));
			}
			i++;
		}
		

		preferencescriptfile = Main.pwpreferencecases + File.separator + "PW_pre_" + index + ".py";
		preferencescriptlogfile = Main.pwpreferencecases + File.separator + "PW_pre_" + index + "_log.py";
		File tempfile = new File(preferencescriptfile);
		if (!tempfile.exists()) {
			ScriptExecutor.generatepreferencecaseforNonDefault(allpreferences, tempfile, false);
		}
		tempfile = new File(preferencescriptlogfile);
		if (!tempfile.exists()) {
			ScriptExecutor.generatepreferencecaseforNonDefault(allpreferences, tempfile, true);
		}
		this.preferencetestcaseinfofile = Main.pwpreferencecaseinfo + File.separator + "pwcaseinfo_" + index + ".json";
	}

	public PWValue(int index, String state, String preferencescriptfile, String preferencescriptlogfile, String preferencetestcaseinfofile, String nowtagname) {
		this.index = index;
		this.state = state;
		this.preferencescriptfile = preferencescriptfile;
		this.preferencescriptlogfile = preferencescriptlogfile;
		this.preferencetestcaseinfofile = preferencetestcaseinfofile;
		this.nowtagname = nowtagname;
	}
	
	public boolean execute() {

		Map<String, TestCaseData> testcaseinfomap = null;
		File tempfile = new File(preferencetestcaseinfofile);
		if (tempfile.exists()) {
			testcaseinfomap = JsonHelper.gettestcasesdataAdapt(preferencetestcaseinfofile, false);
		} else {
			testcaseinfomap = new HashMap<String, TestCaseData>();
		}
		
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		
		int startindex = 0;
		if (nowtagname.equals(Main.PRESCRIPT)) {

			TestCaseData predata = ScriptExecutor.scriptexecuteforpw(this, Main.PRESCRIPT, preferencescriptlogfile);
			testcaseinfomap.put(Main.PRESCRIPT, predata);
		} else {
			startindex = taglist.indexOf(nowtagname);
		}
		for (int i = startindex; i < taglist.size(); i++) {
			nowtagname = taglist.get(i);
			String nowscriptfile = scriptmap.get(nowtagname);
			TestCaseData nowdata = ScriptExecutor.scriptexecuteforpw(this, nowtagname, nowscriptfile);
			testcaseinfomap.put(nowtagname, nowdata);
			if (i % 3 == 0) {
				JsonHelper.savetestcasesdataAdapt(testcaseinfomap, preferencetestcaseinfofile);
				JsonHelper.setpwplanAdapt(this.plan, Main.pwpreferenceplanfile);
			}
		}
		this.state = "end";
		JsonHelper.savetestcasesdataAdapt(testcaseinfomap, preferencetestcaseinfofile);
		JsonHelper.setpwplanAdapt(this.plan, Main.pwpreferenceplanfile);
		return true;
	}
	
	
}