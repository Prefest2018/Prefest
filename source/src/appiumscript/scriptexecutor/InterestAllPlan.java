package appiumscript.scriptexecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import GUI.Main;
import data.CoverData;
import data.InterestValue;
import data.Scene;
import data.TestCaseData;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.JsonHelper;
import tools.Logger;
import tools.PWCounter;
import tools.ProcessExecutor;
import tools.TagnameComparator;

public class InterestAllPlan {
	public Map<String, String> scriptmap = null;
	public String nowtagname = null;
	public LinkedList<String> taglist = null;
	public InterestAllPlan(Map<String, TestCaseData> datas, Map<String, List<PreferenceTreeNode>> preferencetrees) {
		scriptmap = new HashMap<String, String>();
		taglist = new LinkedList<String>();
		PWCounter pwcounter = new PWCounter();
		for (String tagname : datas.keySet()) {
			Map<String, String> newscriptnames = ScriptExecutor.generateinterestcaseforPREFEST_N(datas.get(tagname), pwcounter, preferencetrees);
			if (null != newscriptnames && !newscriptnames.isEmpty()) {
				for (String newtagname : newscriptnames.keySet()) {
					scriptmap.put(newtagname, newscriptnames.get(newtagname));
					taglist.add(newtagname);
				}
			}
			

		}
		taglist.sort(new TagnameComparator<String>());
		nowtagname = taglist.get(0);
	}
	
	public InterestAllPlan(Map<String, String> scriptmap, String nowtagname) {
		this.scriptmap = scriptmap;
		this.nowtagname = nowtagname;
		this.taglist = new LinkedList<String>(scriptmap.keySet());
		this.taglist.sort(new TagnameComparator<String>());
	}
	

	public void execute() {
		Logger.setTempLogFile(Main.interestallcasesexeresultfile, true);
		Map<String, TestCaseData> testcaseinfomap = null;
		File tempfile = new File(Main.interestallcaseinfofile);
		if (tempfile.exists()) {
			testcaseinfomap = JsonHelper.gettestcasesdataAdapt(Main.interestallcaseinfofile, false);
		} else {
			testcaseinfomap = new HashMap<String, TestCaseData>();
		}
		ProcessExecutor.processnolog("adb", "shell", "rm", "/mnt/sdcard/coverage/*.ec");
		
		int index = taglist.indexOf(nowtagname);
		CoverData coverdata = JsonHelper.getCoverData(Main.interestallcoveragedata);
		for (int i = index; i < taglist.size(); i++) {
			nowtagname = taglist.get(i);
			String nowscriptfile = scriptmap.get(nowtagname);
			TestCaseData nowdata = ScriptExecutor.scriptexecuteforPREFEST_N(nowtagname, nowscriptfile, coverdata);
			testcaseinfomap.put(nowtagname, nowdata);
			if (i % 3 == 0 || i == taglist.size() - 1) {
				JsonHelper.savetestcasesdataAdapt(testcaseinfomap, Main.interestallcaseinfofile);
				JsonHelper.setinterestallplanAdapt(this, Main.interestallplanfile);
				coverdata.save(Main.interestallcoveragedata);
			}


		}
	}
}
