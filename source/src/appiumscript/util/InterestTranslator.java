package appiumscript.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GUI.Main;
import data.InterestValue;
import data.PreferenceAdaptData;
import tools.Logger;

public class InterestTranslator {

	public static String generatetotalpreferences(String packagename, ArrayList<InterestValue> values, boolean hasconscript, String conscripthandler, Map<String, String> failureactivitymap) {
		String exitStr = getUIBackStr();
		
		StringBuilder sb = new StringBuilder();
		Map<String, ArrayList<InterestValue>> preferenceactivity2interestmap = new HashMap<String, ArrayList<InterestValue>>();
		for (InterestValue value : values) {
			if (null != value.activityname) {
				String key = value.activityname;
				if (null != value.activityextra) {
					key += ";" + value.activityextra;
				}
				ArrayList<InterestValue> list = preferenceactivity2interestmap.get(key);
				if (null == list) {
					list = new ArrayList<InterestValue>();
					preferenceactivity2interestmap.put(key, list);
				}
				list.add(value);
			}
		}
		
		for (String activityname_extra : preferenceactivity2interestmap.keySet()) {
			String startactivity = null;
			String activityname = null;
			if (activityname_extra.contains(";")) {
				int index = activityname_extra.indexOf(";");
				activityname = activityname_extra.substring(0, index);
				String activityextra = activityname_extra.substring(index + 1, activityname_extra.length());
				startactivity = "adb shell am start -n " + packagename + "/" + activityname + adjustforextras(activityextra);
			} else {
				activityname = activityname_extra;
				startactivity = "adb shell am start -n " + packagename + "/" + activityname + " -a test";
			}
			
			sb.append("	command(\""+ startactivity + "\")\r\n");
			sb.append("	time.sleep(1)\r\n");
			String activityadapt = failureactivitymap.get(activityname);
			if (null != activityadapt) {
				sb.append(activityadapt);
			}
			if (hasconscript) {
				sb.append(conscripthandler);
			}
			sb.append(InterestTranslator.generatepreferences(preferenceactivity2interestmap.get(activityname_extra), hasconscript, conscripthandler) + "\r\n");
			sb.append(exitStr);
			sb.append("	time.sleep(2)\r\n");
		}
		return sb.toString();
	}
	
	private static String adjustforextras(String origin) {
		origin = origin.replace("\"", "\\\"");
		return origin;
	}
	
	
	
	private static String generatepreferences(ArrayList<InterestValue> values, boolean hasconscript, String conscripthandler) {
		StringBuilder sb = new StringBuilder();
		values.sort(new Comparator<InterestValue>() {
			@Override
			public int compare(InterestValue o1, InterestValue o2) {
				return o1.index - o2.index;
			}
		});
		InterestValue beforevalue = null;
		for (InterestValue value : values) {
			if ("language".equalsIgnoreCase(value.name)) {
				continue;
			}
			String title = value.preferencesteps.get(value.preferencesteps.size() - 1);
			if ("language".equalsIgnoreCase(title)) {
				continue;
			}

			
			Logger.log("current preference, name:" + value.name + ", value:" + value.value);
			if (value.generaltype.equals("preference")) {
				
				if (null == beforevalue) {
					for (String target :value.preferencesteps) {
						sb.append("	scrollToClickElement(driver, " + getUITextStr(target, null) + ")\r\n");
						if (hasconscript && !sb.toString().endsWith(conscripthandler) && !value.innertype.equals("edit")) {
							sb.append(conscripthandler);
						}
					}
				} else {
					int max = value.preferencesteps.size()<beforevalue.preferencesteps.size()?value.preferencesteps.size():beforevalue.preferencesteps.size();
					int diffstep = 0;
					for (diffstep = 0; diffstep < max; diffstep++) {
						try {
							if (!value.preferencesteps.get(diffstep).equals(beforevalue.preferencesteps.get(diffstep))) {
								break;
							}
						} catch(Exception e) {
							System.out.println();
						}

					}
					for (int i = 0; i < beforevalue.preferencesteps.size() -diffstep - 1; i++) {
						sb.append("	time.sleep(1)\r\n");
						sb.append(getUIBackStr());
					}
					for (int i = diffstep; i < value.preferencesteps.size(); i++) {
						String textcontent = value.preferencesteps.get(i);
						if (null != textcontent) {
							sb.append("	scrollToClickElement(driver, " + getUITextStr(textcontent, null) + ")\r\n");
							if (hasconscript && !sb.toString().endsWith(conscripthandler)) {
								sb.append(conscripthandler);
							}
						}
					}
				}
				String selfdefinedcall = ScriptGenerationUtil.getAdaptPreferenceSettingCall(value.name, value.value);
				String currenttext = value.preferencesteps.get(value.preferencesteps.size() - 1);
				if (null != selfdefinedcall) {
					sb.append(selfdefinedcall);
				} else if ("checkbox".equals(value.innertype) || "switch".equals(value.innertype)) {
					if (hasconscript && !sb.toString().endsWith(conscripthandler)) {
						sb.append(conscripthandler);
					}
					String text = "	clickOnCheckable(driver, " + getUITextStr(currenttext, "text") + "," + getUIBooleanValue(value.value, "value") + ")\r\n";
					sb.append(text);
				} else if (value.innertype.equals("list")) {		
					if (null != value.value) {
						sb.append("	clickInList(driver, " + getUITextStr(value.value, null) + ")\r\n");
					} else {
						sb.append("	clickInList(driver, None)\r\n");
					}
					if (hasconscript) {
						sb.append(conscripthandler);
					}
				} else if (value.innertype.equals("edit")) {
					if (null != value.value) {
						String text = value.value;
						sb.append("	typeText(driver,\"" + text + "\")\r\n");
					}
					if (hasconscript) {
						sb.append(conscripthandler);
					}
				} else if (value.innertype.equals("seekbar")) {
					if (null != value.value) {
						double rate = 0.5;
						if (null != value.extradatas && !value.extradatas.isEmpty() && value.extradatas.containsKey("min") && value.extradatas.containsKey("max")) {
							double min = Double.parseDouble(value.extradatas.get("min").toString());
							double max = Double.parseDouble(value.extradatas.get("max").toString());
							double currentvaue = Double.parseDouble(value.value);
							rate = (currentvaue-min)/(max-min);
						}
						sb.append("	testingSeekBar(driver, " + getUITextStr(currenttext, "text") + ", " + getUIDoubleValue(rate, "value") + ")\r\n");
					}
					
				} else if (value.innertype.equals("multilist")) {
					sb.append("	clickInMultiList(driver, " + getUITextStr(value.value, null) + ")\r\n");
				}

				beforevalue = value;
			}
		}
		return sb.toString();
	}
	
	private static String getUIBackStr() {
		String exitStr = null;
		switch (Main.scriptForm) {
		case Main.APPIUM : {
			exitStr = "	driver.press_keycode(4)\r\n";
			break;
		}
		case Main.UIAUTOMATOR2 : {
			exitStr = "	driver.press(\'back\')\r\n";
			break;
		}
		}
		return exitStr;
	}
	
	private static String getUITextStr(String text, String textPrefix) {
		if (null != text) {
			text = text.replace("\\n", "");
		}
		String result = "";
		switch (Main.scriptForm) {
			case Main.APPIUM:{
				result = "\"new UiSelector().text(\\\"" + text + "\\\")\"";
				break;
			}
			case Main.UIAUTOMATOR2:{
				if (null == textPrefix) {
					result = "\'" + text + "\'";
				} else {
					result = textPrefix + " = \'" + text + "\'";
				}
				break;
			}
		}
		return result;
	}
	
	private static String getUIDoubleValue(double value, String valuePrefix) {
		String result = "";
		switch (Main.scriptForm) {
			case Main.APPIUM:{
				result = value + "";
				break;
			}
			case Main.UIAUTOMATOR2:{
				if (null == valuePrefix) {
					result = value + "";
				} else {
					result = valuePrefix + " = " + value ;
				}
				break;
			}
		}
		return result;
	}
	
	private static String getUIBooleanValue(String value, String valuePrefix) {
		String result = "";
		switch (Main.scriptForm) {
			case Main.APPIUM:{
				if (value.equals("1")) {
					value = "true";
				} else {
					value = "false";
				}
				result = "\"" + value + "\"";
				break;
			}
			case Main.UIAUTOMATOR2:{
				if (value.equals("1")) {
					value = "True";
				} else {
					value = "False";
				}
				if (null == valuePrefix) {
					result = value + "";
				} else {
					result = valuePrefix + " = " + value ;
				}
				break;
			}
		}
		return result;
	}
	
	
	
	private static StringBuilder systemservicestorestr = null;
	public static String generatesystemservicestr(ArrayList<InterestValue> values) {
		systemservicestorestr = new StringBuilder();
		ArrayList<InterestValue> services = new ArrayList<InterestValue>();
		for (InterestValue value : values) {
			if (value.generaltype.equals("systemservice")) {
				Logger.log("current systemservice, name: " + value.name + ", value: " + value.value);
				services.add(value);
			}
		}
		if (services.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Map<String, Boolean> locationlist = new HashMap<String, Boolean>();
		String locations = null;
		for (InterestValue service : services) {
			boolean bvalue = service.value.equals("1")?true:false;
			switch(service.name) {
			case "ass_musicactive": {
				if (bvalue) {
					sb.append("	os.popen(\"adb shell am start -a android.intent.action.VIEW -d file:
					systemservicestorestr.append("	os.open(\"adb shell input keyevent 127\")\r\n");
				}
				break;
			}
			case "ass_bluetooth": {
				if (bvalue) {
					sb.append("	os.popen(\"adb shell service call bluetooth_manager 6\")\r\n");

				} else {
					sb.append("	os.popen(\"adb shell service call bluetooth_manager 8\")\r\n");
				}
				systemservicestorestr.append("	os.popen(\"adb shell service call bluetooth_manager 6\")\r\n");
				break;
			}
			case "ass_mobiledata": {
				if (bvalue) {
					sb.append("	os.popen(\"adb shell svc data enable\")\r\n");

				} else {
					sb.append("	os.popen(\"adb shell svc data disable\")\r\n");
				}
				systemservicestorestr.append("	os.popen(\"adb shell svc data enable\")\r\n");
				break;
			}
			case "ass_wifi": {
				if (bvalue) {
					sb.append("	os.popen(\"adb shell svc wifi enable\")\r\n");

				} else {
					sb.append("	os.popen(\"adb shell svc wifi disable\")\r\n");
				}
				systemservicestorestr.append("	os.popen(\"adb shell svc wifi enable\")\r\n");
				break;
			}
			case "ass_location_network": {
				locationlist.put("network", bvalue);
				if (bvalue) {
					if (locations == null) {
						locations = "network";
					} else {
						locations += ",network";
					}

				}
				break;
			}
			case "ass_location_gps": {
				locationlist.put("gps", bvalue);
				if (bvalue) {
					if (locations == null) {
						locations = "gps";
					} else {
						locations += ",gps";
					}
				}
				break;
			}
			}
		}
		if (null != locations) {
			if (null != locationlist.get("gps")) {
				sb.append("	os.popen(\"adb shell settings put secure location_providers_allowed 'false'\")\r\n");
			}
			sb.append("	os.popen(\"adb shell settings put secure location_providers_allowed "+ locations +"\")\r\n");
			systemservicestorestr.append("	os.popen(\"adb shell settings put secure location_providers_allowed gps,network\")\r\n");
			systemservicestorestr.append("	os.popen(\"adb shell settings put secure location_providers_allowed 'true'\")\r\n");
		} else if (null == locations && !locationlist.isEmpty()) {
			sb.append("	os.popen(\"adb shell settings put secure location_providers_allowed 'false'\")\r\n");
			systemservicestorestr.append("	os.popen(\"adb shell settings put secure location_providers_allowed gps,network\")\r\n");
		}
		if (sb.length() > 0) {
			sb.append("	time.sleep(5)\r\n");
		}
		return sb.toString();
	}

	public static String getsystemservicerestorestr() {
		return systemservicestorestr.toString();
	}

}
