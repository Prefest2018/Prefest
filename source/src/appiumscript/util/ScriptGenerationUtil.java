package appiumscript.util;

import GUI.Main;

public class ScriptGenerationUtil {

	public static String getPrefixFunctions_General(boolean shouldjacoco, boolean hasconscript, String conscript) {
		StringBuilder result = new StringBuilder();
		result.append("#coding=utf-8\r\n" + 
				"import os\r\n" + 
				"import subprocess\r\n" +
				"import time\r\n" + 
				"import traceback\r\n" + 
				"from appium import webdriver\r\n" + 
				"from appium.webdriver.common.touch_action import TouchAction\r\n" + 
				"from selenium.common.exceptions import NoSuchElementException, WebDriverException\r\n" + 
				"desired_caps = {\r\n" + 
				"	'platformName' : 'Android',\r\n" + 
				"	'deviceName' : 'Android Emulator',\r\n" + 
				"	'platformVersion' : '4.4',\r\n" + 
				"	'appPackage' : '" + Main.packagename + "',\r\n" + 
				"	'appActivity' : '" + Main.luanchactivityname + "',\r\n" + 
				"	'resetKeyboard' : True,\r\n");
		if (shouldjacoco) {
			result.append("	'androidCoverage' : '" + Main.packagename + "/" + Main.packagename + ".JacocoInstrumentation',\r\n");
		}
		result.append(
				"	'noReset' : True\r\n" + 
				"	}\r\n" + 
				"\r\n" + 
				"def command(cmd, timeout=5):\r\n" + 
				"	p = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=True)\r\n" + 
				"	time.sleep(timeout)\r\n" + 
				"	p.terminate()\r\n" + 
				"	return\r\n" + 
				"def getElememt(driver, str) :\r\n" + 
				"	for i in range(0, 5, 1):\r\n" + 
				"		try:\r\n" + 
				"			element = driver.find_element_by_android_uiautomator(str)\r\n" + 
				"		except NoSuchElementException:\r\n" + 
				"			time.sleep(1)\r\n" + 
				"		else:\r\n" + 
				"			return element\r\n" + 
				"	os.popen(\"adb shell input tap 50 50\")\r\n" + 
				"	element = driver.find_element_by_android_uiautomator(str)\r\n" + 
				"	return element\r\n" + 
				"\r\n" + 
				"def getElememtBack(driver, str1, str2) :\r\n" + 
				"	for i in range(0, 2, 1):\r\n" + 
				"		try:\r\n" + 
				"			element = driver.find_element_by_android_uiautomator(str1)\r\n" + 
				"		except NoSuchElementException:\r\n" + 
				"			time.sleep(1)\r\n" + 
				"		else:\r\n" + 
				"			return element\r\n" + 
				"	for i in range(0, 5, 1):\r\n" + 
				"		try:\r\n" + 
				"			element = driver.find_element_by_android_uiautomator(str2)\r\n" + 
				"		except NoSuchElementException:\r\n" + 
				"			time.sleep(1)\r\n" + 
				"		else:\r\n" + 
				"			return element\r\n" + 
				"	os.popen(\"adb shell input tap 50 50\")\r\n" + 
				"	element = driver.find_element_by_android_uiautomator(str2)\r\n" + 
				"	return element\r\n" + 
				"def swipe(driver, startxper, startyper, endxper, endyper) :\r\n" + 
				"	size = driver.get_window_size()\r\n" + 
				"	width = size[\"width\"]\r\n" + 
				"	height = size[\"height\"]\r\n" + 
				"	try:\r\n" + 
				"		driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),\r\n" + 
				"				end_y=int(height * endyper), duration=1000)\r\n" + 
				"	except WebDriverException:\r\n" + 
				"		time.sleep(1)\r\n" + 
				"	driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),\r\n" + 
				"				end_y=int(height * endyper), duration=1000)\r\n" + 
				"	return\r\n" + 
				"\r\n" + 
				"def scrollToFindElement(driver, str) :\r\n" + 
				"	for i in range(0, 5, 1):\r\n" + 
				"		try:\r\n" + 
				"			element = driver.find_element_by_android_uiautomator(str)\r\n" + 
				"			elements = driver.find_elements_by_android_uiautomator(str)\r\n" + 
				"			if (len(elements) > 1) :\r\n" + 
				"				for temp in elements :\r\n" + 
				"					if temp.get_attribute(\"enabled\") == \"true\" :\r\n" + 
				"						element = temp\r\n" + 
				"						break\r\n" + 
				"		except NoSuchElementException:\r\n" + 
				"			swipe(driver, 0.5, 0.55, 0.5, 0.2)\r\n" + 
				"		else :\r\n" + 
				"			return element\r\n" + 
				"	for i in range(0, 4, 1):\r\n" + 
				"		try:\r\n" + 
				"			element = driver.find_element_by_android_uiautomator(str)\r\n" + 
				"			elements = driver.find_elements_by_android_uiautomator(str)\r\n" + 
				"			if (len(elements) > 1):\r\n" + 
				"				for temp in elements:\r\n" + 
				"					if temp.get_attribute(\"enabled\") == \"true\":\r\n" + 
				"						element = temp\r\n" + 
				"						break\r\n" + 
				"		except NoSuchElementException:\r\n" + 
				"			swipe(driver, 0.5, 0.2, 0.5, 0.55)\r\n" + 
				"		else :\r\n" + 
				"			return element\r\n" + 
				"	return\r\n" + 
				"\r\n" + 
				"def scrollToClickElement(driver, str) :\r\n" + 
				"	element = scrollToFindElement(driver, str)\r\n" + 
				"	if element is None :\r\n" + 
				"		return\r\n" + 
				"	else :\r\n" + 
				"		element.click()\r\n" + 
				"\r\n" + 
				"def clickInList(driver, str) :\r\n" + 
				"	element = None\r\n" + 
				"	if (str is None) :\r\n" + 
				"		candidates = driver.find_elements_by_class_name(\"android.widget.CheckedTextView\")\r\n" + 
				"		if len(candidates) >= 1 and checkWindow(driver):\r\n" + 
				"			element = candidates[len(candidates)-1]\r\n" + 
				"	else :\r\n" + 
				"		element = scrollToFindElement(driver, str)\r\n" + 
				"	if element is not None :\r\n" + 
				"		element.click()\r\n" + 
				"	else :\r\n" + 
				"		if checkWindow(driver) :\r\n" + 
				"			driver.press_keycode(4)\r\n" + 
				"\r\n" + 
				"def clickOnCheckable(driver, str, value = \"true\") :\r\n" + 
				"	parents = driver.find_elements_by_class_name(\"android.widget.LinearLayout\")\r\n" + 
				"	for parent in parents:\r\n" + 
				"		try :\r\n" + 
				"			parent.find_element_by_android_uiautomator(str)\r\n" + 
				"			lists = parent.find_elements_by_class_name(\"android.widget.LinearLayout\")\r\n" + 
				"			if len(lists) == 1 :\r\n" + 
				"				innere = parent.find_element_by_android_uiautomator(\"new UiSelector().checkable(true)\")\r\n" + 
				"				nowvalue = innere.get_attribute(\"checked\")\r\n" + 
				"				if (nowvalue != value) :\r\n" + 
				"					innere.click()\r\n" + 
				"				break\r\n" + 
				"		except NoSuchElementException:\r\n" + 
				"			continue\r\n" + 
				"\r\n" + 
				"def typeText(driver, value) :\r\n" + 
				"	element = getElememt(driver, \"new UiSelector().className(\\\"android.widget.EditText\\\")\")\r\n" + 
				"	element.clear()\r\n" + 
				"	element.send_keys(value)\r\n" + 
				"	enterelement = getElememt(driver, \"new UiSelector().text(\\\"OK\\\")\")\r\n" + 
				"	if (enterelement is None) :\r\n" + 
				"		if checkWindow(driver):\r\n" + 
				"			driver.press_keycode(4)\r\n" + 
				"	else :\r\n" + 
				"		enterelement.click()" +
				"\r\n" + 
				"def checkWindow(driver) :\r\n" + 
				"	dsize = driver.get_window_size()\r\n" + 
				"	nsize = driver.find_element_by_class_name(\"android.widget.FrameLayout\").size\r\n" + 
				"	if dsize['height'] > nsize['height']:\r\n" + 
				"		return True\r\n" + 
				"	else :\r\n" + 
				"		return False"
				+ "\r\n");
		if (hasconscript) {
			result.append(conscript);
		}
		return result.toString();
	}
	
	private static String getTestcase_General(String title, String testopstrs, String systemsettingscripts, String systemrestorescripts, boolean ispreferencesetting, boolean shouldjacoco) {
		StringBuilder sb = new StringBuilder();
		if (!ispreferencesetting) {
			sb.append("\r\n# testcase" + title + "\r\n");
		} else {
			sb.append("# preference setting and exit\r\n");
		}
		sb.append("try :\r\n");
		if (null != systemsettingscripts && !"".equals(systemsettingscripts)) {
			sb.append(systemsettingscripts);
		}
		sb.append("	starttime = time.time()\r\n" +
				"	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)\r\n");
		sb.append(testopstrs);
		sb.append("except Exception, e:\r\n" + 
				"	print 'FAIL'\r\n" + 
				"	print 'str(e):\\t\\t', str(e)\r\n" + 
				"	print 'repr(e):\\t', repr(e)\r\n" + 
				"	print traceback.format_exc()\r\n" + 
				"else:\r\n" + 
				"	print 'OK'\r\n" + 
				"finally:\r\n" + 
				"	cpackage = driver.current_package\r\n" +
				"	endtime = time.time()\r\n" + 
				"	print 'consumed time:', str(endtime - starttime), 's'\r\n");
		if (shouldjacoco) {
			sb.append(
				"	command(\"adb shell am broadcast -a com.example.pkg.END_EMMA --es name \\\"" + title + "\\\"\")\r\n" + 
				"	jacocotime = time.time()\r\n" + 
				"	print 'jacoco time:', str(jacocotime - endtime), 's'\r\n");
		}
		if (null != systemrestorescripts && !"".equals(systemrestorescripts)) {
			sb.append(systemrestorescripts);
		}
		sb.append("	driver.quit()\r\n");
		if (!ispreferencesetting) {
			sb.append(
				"	if (cpackage != '" + Main.packagename + "'):\r\n" + 
				"		cpackage = \"adb shell am force-stop \" + cpackage\r\n" + 
				"		os.popen(cpackage)");
		}

		return sb.toString();
	}
	
	public static String getTestcase_FirstExe(String title, String testopstrs) {
		return getTestcase_General(title, testopstrs, null, null, false, true);
	}
	
	public static String getPreferenceSetting_General(String title, String testopstrs, String systemsettingscripts, String systemrestorescripts, boolean shouldjacoco) {
		return getTestcase_General(title, testopstrs, systemsettingscripts, systemrestorescripts, true, shouldjacoco);
	}
	
}
