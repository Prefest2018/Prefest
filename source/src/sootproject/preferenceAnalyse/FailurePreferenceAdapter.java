package sootproject.preferenceAnalyse;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GUI.Main;
import appiumscript.util.InterestTranslator;
import appiumscript.util.ScriptGenerationUtil;
import data.PreferenceAdaptData;
import sootproject.resourceLoader.PreferenceTreeNode;
import sootproject.analysedata.MyInterest;
import sootproject.analysedata.MyPreference;
import tools.JsonHelper;

public class FailurePreferenceAdapter {
	private static Map<String, PreferenceAdaptData> failurepreferencemap = null;

	public static Map<String, MyInterest> dealWithFailurePreference(Map<String, MyInterest> interests) {
		File failurefolder = new File(Main.failurepreferences);
		if (!failurefolder.exists()) {
			failurefolder.mkdirs();
		}
		for (String key : interests.keySet()) {
			MyInterest interest = interests.get(key);
			if (!(interest instanceof MyPreference) || !((MyPreference)interest).requireAdapt) {
				continue;
			}
			MyPreference preference = (MyPreference)interest;
			PreferenceTreeNode preferencenode = preference.getPreferencenode();
			File preferencefile = new File(failurefolder, preferencenode.key + ".json");
			if (!preferencefile.exists()) {
				PreferenceAdaptData exampledata = PreferenceAdaptData.getExampleInstance(preferencenode.key);
				JsonHelper.setPreferenceAdaptData(preferencefile.getAbsolutePath(), exampledata);
			}
		}

		failurepreferencemap = getFailurePreferences();
		if (!failurepreferencemap.isEmpty()) {
			for (String key : failurepreferencemap.keySet()) {
				MyPreference preference = (MyPreference)interests.get(key);
				preference.updateWithAdapt(failurepreferencemap.get(key));
			}
		}

		return interests;
	}
	
	public static Map<String, PreferenceAdaptData> getFailurePreferences() {
		if (null != failurepreferencemap) {
			return failurepreferencemap;
		}
		failurepreferencemap = new HashMap<String, PreferenceAdaptData>();
		File failurefolder = new File(Main.failurepreferences);
		for (File preferencefile : failurefolder.listFiles()) {
			if (!preferencefile.getName().endsWith(".json")) {
				continue;
			}
			PreferenceAdaptData adaptdata = JsonHelper.getPreferenceAdaptData(preferencefile.getAbsolutePath());
			if (adaptdata.adapted) {
				failurepreferencemap.put(adaptdata.key, adaptdata);
			}
		}
		return failurepreferencemap;
	}
	
}
