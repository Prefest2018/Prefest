package appiumscript.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import GUI.Main;
import appiumscript.scripttranslator.TestOperation;
import data.PreferenceAdaptData;

public class ScriptGenerationUtil {
	private static Map<String, PreferenceAdaptData> adaptDatas = null;
//	private static Map<String, String> adaptSettingMethods = null;
	private static String allAdaptMethods = null;

	public static String getPrefixFunctions_General(boolean shouldjacoco, boolean hasconscript, String conscript) {
		StringBuilder result = new StringBuilder();
		switch (Main.scriptForm) {
		case Main.APPIUM : {
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
					"	'platformVersion' : '" + Main.AVDVersion + "',\r\n" + 
					"	'appPackage' : '" + Main.packagename + "',\r\n" + 
					"	'appActivity' : '" + Main.luanchactivityname + "',\r\n" + 
					"	'resetKeyboard' : True,\r\n");
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
					"		driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),\r\n" + 
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
					"	for i in range(0, 5, 1):\r\n" + 
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
					"	try:\r\n" +
					"		element = getElememt(driver, \"new UiSelector().className(\\\"android.widget.EditText\\\")\")\r\n" + 
					"		element.clear()\r\n" + 
					"		element.send_keys(value)\r\n" + 
					"		enterelement = getElememt(driver, \"new UiSelector().text(\\\"OK\\\")\")\r\n" + 
					"		if (enterelement is None) :\r\n" + 
					"			if checkWindow(driver):\r\n" + 
					"				driver.press_keycode(4)\r\n" + 
					"		else :\r\n" + 
					"			enterelement.click()\r\n" +
					"	except NoSuchElementException:\r\n" +
					"		pass" +
					"\r\n" + 
					"def checkWindow(driver) :\r\n" + 
					"	dsize = driver.get_window_size()\r\n" + 
					"	nsize = driver.find_element_by_class_name(\"android.widget.FrameLayout\").size\r\n" + 
					"	if dsize['height'] > nsize['height']:\r\n" + 
					"		return True\r\n" + 
					"	else :\r\n" + 
					"		return False"
					+ "\r\n"
					+ "def testingSeekBar(driver, str, value):\r\n" + 
					"	try :\r\n" + 
					"		if(not checkWindow(driver)) :\r\n" + 
					"			element = seekForNearestSeekBar(driver, str)\r\n" + 
					"		else :\r\n" + 
					"			element = driver.find_element_by_class_name(\"android.widget.SeekBar\")\r\n" + 
					"		if (None != element):\r\n" + 
					"			settingSeekBar(driver, element, value)\r\n" + 
					"			driver.find_element_by_android_uiautomator(\"new UiSelector().text(\\\"OK\\\")\").click()\r\n" + 
					"	except NoSuchElementException:\r\n" + 
					"		time.sleep(1)\r\n" + 
					"\r\n" + 
					"def seekForNearestSeekBar(driver, str):\r\n" + 
					"	parents = driver.find_elements_by_class_name(\"android.widget.LinearLayout\")\r\n" + 
					"	for parent in parents:\r\n" + 
					"		try :\r\n" + 
					"			parent.find_element_by_android_uiautomator(str)\r\n" + 
					"			lists = parent.find_elements_by_class_name(\"android.widget.LinearLayout\")\r\n" + 
					"			if len(lists) == 1 :\r\n" + 
					"				innere = parent.find_element_by_class_name(\"android.widget.SeekBar\")\r\n" + 
					"				return innere\r\n" + 
					"				break\r\n" + 
					"		except NoSuchElementException:\r\n" + 
					"			continue\r\n" + 
					"def settingSeekBar(driver, element, value) :\r\n" + 
					"	x = element.rect.get(\"x\")\r\n" + 
					"	y = element.rect.get(\"y\")\r\n" + 
					"	width = element.rect.get(\"width\")\r\n" + 
					"	height = element.rect.get(\"height\")\r\n" + 
					"	TouchAction(driver).press(None, x + 10, y + height/2).move_to(None, x + width * value,y + height/2).release().perform()\r\n" + 
					"	y = value\r\n" +
					"def clickInMultiList(driver, str) :\r\n" + 
					"	element = None\r\n" + 
					"	if (str is None) :\r\n" + 
					"		candidates = driver.find_elements_by_class_name(\"android.widget.CheckedTextView\")\r\n" + 
					"		if len(candidates) >= 1 and checkWindow(driver):\r\n" + 
					"			element = candidates[len(candidates)-1]\r\n" + 
					"	else :\r\n" + 
					"		element = scrollToFindElement(driver, str)\r\n" + 
					"	if element is not None :\r\n" + 
					"		nowvalue = element.get_attribute(\"checked\")\r\n" + 
					"		if (nowvalue != \"true\") :\r\n" + 
					"			element.click()\r\n" + 
					"	if checkWindow(driver) :\r\n" + 
					"		driver.find_element_by_android_uiautomator(\"new UiSelector().text(\\\"OK\\\")\").click()\r\n");
			break;
		}
		case Main.UIAUTOMATOR2:{
			result.append("import os\r\n" + 
					"import subprocess\r\n" + 
					"import time\r\n" + 
					"import uiautomator2 as u2\r\n" + 
					"import traceback\r\n" + 
					"\r\n" + 
					"driver = u2.connect(\"emulator-5554\")\r\n" + 
					"driver.settings['wait_timeout'] = 5.0\r\n" + 
					"dinfo = driver.device_info\r\n" + 
					"dwidth = dinfo['display']['width']\r\n" + 
					"dheight = dinfo['display']['height']\r\n" + 
					"\r\n" + 
					"def stop(driver):\r\n" + 
					"	driver.app_stop_all()\r\n" + 
					"\r\n" + 
					"def command(cmd, timeout=3):\r\n" + 
					"	p = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=True)\r\n" + 
					"	time.sleep(timeout)\r\n" + 
					"	p.terminate()\r\n" + 
					"	return\r\n" + 
					"\r\n" + 
					"def startactivity(activityname, acitivityextra='-a test') :\r\n" + 
					"	command('adb shell am start -n' + activityname + ' ' + acitivityextra)\r\n" + 
					"	return\r\n" + 
					"\r\n" + 
					"def back() :\r\n" + 
					"	driver.press('back')\r\n" + 
					"\r\n" + 
					"def checkWindow(driver) :\r\n" + 
					"	currentWindow = driver(className='android.widget.FrameLayout', instance = 0).info['bounds']\r\n" + 
					"	if currentWindow['bottom'] == dheight and currentWindow['right'] == dwidth:\r\n" + 
					"		return False\r\n" + 
					"	else :\r\n" + 
					"		return True\r\n" + 
					"\r\n" + 
					"def scrollToFindElement(driver, text, innerClassName=\"android.widget.LinearLayout\") :\r\n" + 
					"	try:\r\n" + 
					"		element = driver(text=text)\r\n" + 
					"		if element.exists:\r\n" + 
					"			return element\r\n" + 
					"	except Exception:\r\n" + 
					"		pass\r\n" + 
					"	currentH = driver.dump_hierarchy()\r\n" + 
					"	for i in range(0,10) :\r\n" + 
					"		try :\r\n" + 
					"			driver.drag(dwidth/2, dheight * 0.7, dwidth/2, dheight * 0.2, 0.3)\r\n" + 
					"			time.sleep(0.2)\r\n" + 
					"			tempH = driver.dump_hierarchy()\r\n" + 
					"			if currentH == tempH:\r\n" + 
					"				break\r\n" + 
					"			else :\r\n" + 
					"				currentH = tempH\r\n" + 
					"			element = driver(text=text)\r\n" + 
					"			if (element.exists):\r\n" + 
					"				return element\r\n" + 
					"		except Exception:\r\n" + 
					"			pass\r\n" + 
					"	for i in range(0,10) :\r\n" + 
					"		try :\r\n" + 
					"			driver.drag(dwidth/2, dheight * 0.2, dwidth/2, dheight * 0.7, 0.3)\r\n" + 
					"			time.sleep(0.2)\r\n" + 
					"			tempH = driver.dump_hierarchy()\r\n" + 
					"			if currentH == tempH:\r\n" + 
					"				break\r\n" + 
					"			else :\r\n" + 
					"				currentH = tempH\r\n" + 
					"			element = driver(text=text)\r\n" + 
					"			if (element.exists):\r\n" + 
					"				return element\r\n" + 
					"		except Exception as e:\r\n" + 
					"			print(e)\r\n" + 
					"	return None\r\n" + 
					"\r\n" + 
					"def scrollToClickElement(driver, str) :\r\n" + 
					"	element = scrollToFindElement(driver, str)\r\n" + 
					"	if element is None :\r\n" + 
					"		return\r\n" + 
					"	else :\r\n" + 
					"		element.click()\r\n" + 
					"\r\n" + 
					"def testingSeekBar(driver, text = None, resourceId = None, className = None, instance = 0, value = None):\r\n" + 
					"	try :\r\n" + 
					"		if(not checkWindow(driver)) :\r\n" + 
					"			element = seekForNearestSeekBar(driver, text = text, resourceId = resourceId, className = className, instance = instance)\r\n" + 
					"		else :\r\n" + 
					"			element = driver(className=\"android.widget.SeekBar\")\r\n" + 
					"		if (None != element):\r\n" + 
					"			settingSeekBar(driver, element, value)\r\n" + 
					"			driver(text=\"OK\").click()\r\n" + 
					"	except Exception:\r\n" + 
					"		time.sleep(1)\r\n" + 
					"\r\n" + 
					"def seekForNearestSeekBar(driver, text = None, resourceId = None, className = None, instance = 0):\r\n" + 
					"	parents = driver(className=\"android.widget.LinearLayout\")\r\n" + 
					"	for parent in parents:\r\n" + 
					"		try :\r\n" + 
					"			if parent.child(text = text).exists:\r\n" + 
					"				lists = parent.child(className=\"android.widget.LinearLayout\")\r\n" + 
					"			if len(lists) == 1 :\r\n" + 
					"				innere = parent.child(className=\"android.widget.SeekBar\")\r\n" + 
					"				return innere\r\n" + 
					"				break\r\n" + 
					"		except Exception:\r\n" + 
					"			continue\r\n" + 
					"def settingSeekBar(driver, element, value) :\r\n" + 
					"	left = element.info['bounds']['left']\r\n" + 
					"	right = element.info['bounds']['right']\r\n" + 
					"	width = right - left\r\n" + 
					"	height = (element.info['bounds']['bottom']+element.info['bounds']['top'])/2\r\n" + 
					"	driver.touch.down(left + 10, height)\r\n" + 
					"	time.sleep(.5)\r\n" + 
					"	driver.touch.move(left + width * value, height)\r\n" + 
					"	driver.touch.up()\r\n" + 
					"\r\n" + 
					"def clickInList(driver, str) :\r\n" + 
					"	element = None\r\n" + 
					"	if (str is None) :\r\n" + 
					"		candidates = driver(className=\"android.widget.CheckedTextView\")\r\n" + 
					"		if len(candidates) >= 1 and checkWindow(driver):\r\n" + 
					"			element = candidates[len(candidates)-1]\r\n" + 
					"	else :\r\n" + 
					"		element = scrollToFindElement(driver, str)\r\n" + 
					"	if element is not None :\r\n" + 
					"		element.click()\r\n" + 
					"	else :\r\n" + 
					"		if checkWindow(driver) :\r\n" + 
					"			driver.press('back')\r\n" + 
					"\r\n" + 
					"def clickInMultiList(driver, str) :\r\n" + 
					"	element = None\r\n" + 
					"	if (str is None) :\r\n" + 
					"		candidates = driver(className=\"android.widget.CheckedTextView\")\r\n" + 
					"		if len(candidates) >= 1 and checkWindow(d):\r\n" + 
					"			element = candidates[len(candidates)-1]\r\n" + 
					"	else :\r\n" + 
					"		element = scrollToClickElement(driver, str)\r\n" + 
					"	if element is not None :\r\n" + 
					"		nowvalue = element.get_attribute(\"checked\")\r\n" + 
					"		if (nowvalue != \"true\") :\r\n" + 
					"			element.click()\r\n" + 
					"	if checkWindow(driver) :\r\n" + 
					"		driver(text='OK').click()\r\n" + 
					"\r\n" + 
					"def clickOnCheckable(driver, text = None, resourceId = None, className = None, instance = 0, value = True) :\r\n" + 
					"	parents = driver(className=\"android.widget.LinearLayout\")\r\n" + 
					"	for parent in parents:\r\n" + 
					"		try :\r\n" + 
					"			if parent.child(text = text).exists:\r\n" + 
					"				lists = parent.child(className=\"android.widget.LinearLayout\")\r\n" + 
					"				if len(lists) == 1:\r\n" + 
					"					innere = parent.child(checkable=\"true\")\r\n" + 
					"					if innere.info['checked'] != value:\r\n" + 
					"						innere.click()\r\n" + 
					"					break\r\n" + 
					"		except Exception:\r\n" + 
					"			continue\r\n" + 
					"\r\n" + 
					"def typeText(driver, value) :\r\n" + 
					"	try :\r\n" + 
					"		element = driver(className='android.widget.EditText')\r\n" + 
					"		element.clear_text()\r\n" + 
					"		element.set_text(value)\r\n" + 
					"		enterelement = driver(text='OK')\r\n" + 
					"		if (enterelement is None) :\r\n" + 
					"			if checkWindow(driver):\r\n" + 
					"				driver.press('back')\r\n" + 
					"		else :\r\n" + 
					"			enterelement.click()\r\n" + 
					"	except Exception:\r\n" + 
					"		pass\r\n");
			break;
		}
		}
		
		if (null != allAdaptMethods) {
			result.append(allAdaptMethods);
		}
		if (hasconscript) {
			result.append(conscript);
		}
		return result.toString();
	}
	
	private static String getTestcase_General(String title, String testopstrs, String systemsettingscripts, String systemrestorescripts, boolean ispreferencesetting, boolean shouldjacoco) {
		StringBuilder sb = new StringBuilder();
		String startStr = "";
		String getPackageStr = "";
		String stopStr = "";
		switch(Main.scriptForm) {
		case Main.APPIUM : {
			startStr = "	starttime = time.time()\r\n" +
					"	driver = webdriver.Remote('http:
			getPackageStr = "	cpackage = driver.current_package\r\n";
			stopStr = "	driver.quit()\r\n";
			break;
		}
		case Main.UIAUTOMATOR2 : {
			startStr = "	starttime = time.time()\r\n";
			getPackageStr = "	cpackage = driver.info[\'currentPackageName\']\r\n";
			stopStr = "	stop(driver)\r\n";
			break;
		}
		}
		
		
		if (!ispreferencesetting) {
			sb.append("\r\n# testcase" + title + "\r\n");
		} else {
			sb.append("# preference setting and exit\r\n");
		}
		sb.append("try :\r\n");
		if (null != systemsettingscripts && !"".equals(systemsettingscripts)) {
			sb.append(systemsettingscripts);
		}
		sb.append(startStr);
		sb.append(testopstrs);
		sb.append("except Exception as e:\r\n" + 
				"	print('FAIL')\r\n" + 
				"	print('str(e):\\t\\t', str(e))\r\n" + 
				"	print('repr(e):\\t', repr(e))\r\n" + 
				"	print(traceback.format_exc())\r\n" + 
				"else:\r\n" + 
				"	print('OK')\r\n" + 
				"finally:\r\n" + 
				getPackageStr + 
				"	endtime = time.time()\r\n" + 
				"	print 'consumed time:', str(endtime - starttime), 's'\r\n");
		if (shouldjacoco) {
			sb.append(
				"	command(\"adb shell am broadcast -a com.example.pkg.END_EMMA -f 16777216 --es name \\\"" + title + "\\\"\")\r\n" + 
				"	jacocotime = time.time()\r\n" + 
				"	print 'jacoco time:', str(jacocotime - endtime), 's'\r\n");
		}
		if (null != systemrestorescripts && !"".equals(systemrestorescripts)) {
			sb.append(systemrestorescripts);
		}
		sb.append(stopStr);
		if (!ispreferencesetting) {
			sb.append(
				"	if (cpackage != '" + Main.packagename + "'):\r\n" + 
				"		cpackage = \"adb shell am force-stop \" + cpackage\r\n" + 
				"		os.popen(cpackage)");
		}

		return sb.toString();
	}
	
	public static String getTestcase_FirstExe(String title, String testopstrs, String systemrestorescripts) {
		return getTestcase_General(title, testopstrs, null, systemrestorescripts, false, true);
	}
	
	public static String getPreferenceSetting_General(String title, String testopstrs, String systemsettingscripts, boolean shouldjacoco) {
		return getTestcase_General(title, testopstrs, systemsettingscripts, null, true, shouldjacoco);
	}
	
	private static String getNormalMethodName(String key) {
		return Main.SETTINGMETHODTAG + key.replace('.', '_');
	}
	
	public static void initAdaptDatas(Map<String, PreferenceAdaptData> inAdaptDatas) {
		adaptDatas = inAdaptDatas;
		StringBuilder sb = new StringBuilder();
		if (null == adaptDatas || adaptDatas.isEmpty()) {
			return;
		}
		for (String key : adaptDatas.keySet()) {
			PreferenceAdaptData adaptData = adaptDatas.get(key);
			String methodstr = "def " + getNormalMethodName(key) + "(driver, value):\r\n";
			methodstr += "	try :\r\n";
			if (null != adaptData.presteps && !adaptData.presteps.isEmpty()) {
				for (TestOperation op : adaptData.presteps) {
					methodstr += op.getTestLine(2) + "\r\n";
				}
			}
			int i = 0;
			for (String value : adaptData.valuestepmap.keySet()) {
				ArrayList<TestOperation> oparray = adaptData.valuestepmap.get(value);
				if (i == 0) {
					methodstr += "		if value == \"" + value + "\":\r\n";
				} else {
					methodstr += "		elif value == \"" + value + "\":\r\n";
				}
				for (TestOperation op : oparray) {
					methodstr += op.getTestLine(3) + "\r\n";
				}
				i++;
			}
			if (null != adaptData.consteps && !adaptData.consteps.isEmpty()) {
				for (TestOperation op : adaptData.consteps) {
					methodstr += op.getTestLine(2) + "\r\n";
				}
			}
			methodstr += "	except Exception:\r\n";
			methodstr += "		time.sleep(1)\r\n";
			sb.append(methodstr);

		}
		allAdaptMethods = sb.toString();
	}
	
	public static String getAdaptPreferenceSettingCall(String key, String value) {
		String callstr = null;
		if (adaptDatas.containsKey(key)) {
			callstr = "	" + getNormalMethodName(key) + "(driver, \"" + value + "\")\r\n";
		}
		return callstr;
	}
	
}
