package appiumscript.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.InterestValue;
import tools.Logger;

public class InterestTranslator {
	public static String generatetotalpreferences(String packagename, ArrayList<InterestValue> values, boolean hasconscript, String conscripthandler) {
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
			if (activityname_extra.contains(";")) {
				int index = activityname_extra.indexOf(";");
				String activityname = activityname_extra.substring(0, index);
				String activityextra = activityname_extra.substring(index + 1, activityname_extra.length());
				startactivity = "adb shell am start -n " + packagename + "/" + activityname + adjustforextras(activityextra);
			} else {
				startactivity = "adb shell am start -n " + packagename + "/" + activityname_extra + " -a test";
			}
			
			sb.append("	os.popen(\""+ startactivity + "\")\r\n");
			sb.append(InterestTranslator.generatepreferences(preferenceactivity2interestmap.get(activityname_extra), hasconscript, conscripthandler) + "\r\n");
			sb.append("	driver.press_keycode(4)\r\n");
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
						sb.append("	scrollToClickElement(driver, \"new UiSelector().text(\\\"" + target + "\\\")\")\r\n");
					}
				} else {
					int max = value.preferencesteps.size()<beforevalue.preferencesteps.size()?value.preferencesteps.size():beforevalue.preferencesteps.size();
					int diffstep = 0;
					for (diffstep = 0; diffstep < max; diffstep++) {
						if (!value.preferencesteps.get(diffstep).equals(beforevalue.preferencesteps.get(diffstep))) {
							break;
						}
					}
					for (int i = 0; i < beforevalue.preferencesteps.size() -diffstep - 1; i++) {
						sb.append("	time.sleep(1)\r\n");
						sb.append("	driver.press_keycode(4)\r\n");
					}
					for (int i = diffstep; i < value.preferencesteps.size(); i++) {
						sb.append("	scrollToClickElement(driver, \"new UiSelector().text(\\\"" + value.preferencesteps.get(i) + "\\\")\")\r\n");
					}
				}
				if ("checkbox".equals(value.innertype) || "switch".equals(value.innertype)) {
					if (hasconscript) {
						sb.append(conscripthandler);
					}
					String text = "	clickOnCheckable(driver, \"new UiSelector().text(\\\"" + value.preferencesteps.get(value.preferencesteps.size() - 1) + "\\\")\", \"";
					if ("1".equals(value.value)) {
						text = text + "true";
					} else {
						text = text + "false";
					}
					text = text + "\")\r\n";
					sb.append(text);
				} else if (value.innertype.equals("list")) {		
					if (null != value.value) {
						sb.append("	clickInList(driver, \"new UiSelector().text(\\\"" + value.value + "\\\")\")\r\n");
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
				}

				beforevalue = value;
			}
		}
		return sb.toString();
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
					sb.append("	os.popen(\"adb shell am start -a android.intent.action.VIEW -d file:///mnt/sdcard/music/MoonFlow.mp3 -t audio/wav -f 1\")\r\n");
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
