#coding=utf-8
import os
import subprocess
import time
import traceback
from appium import webdriver
from appium.webdriver.common.touch_action import TouchAction
from selenium.common.exceptions import NoSuchElementException, WebDriverException
desired_caps = {
	'platformName' : 'Android',
	'deviceName' : 'Android Emulator',
	'platformVersion' : '4.4',
	'appPackage' : 'org.connectbot',
	'appActivity' : 'org.connectbot.HostListActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.connectbot/org.connectbot.JacocoInstrumentation',
	'noReset' : True
	}

def command(cmd, timeout=5):
	p = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=True)
	time.sleep(timeout)
	p.terminate()
	return
def getElememt(driver, str) :
	for i in range(0, 5, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str)
		except NoSuchElementException:
			time.sleep(1)
		else:
			return element
	os.popen("adb shell input tap 50 50")
	element = driver.find_element_by_android_uiautomator(str)
	return element

def getElememtBack(driver, str1, str2) :
	for i in range(0, 2, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str1)
		except NoSuchElementException:
			time.sleep(1)
		else:
			return element
	for i in range(0, 5, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str2)
		except NoSuchElementException:
			time.sleep(1)
		else:
			return element
	os.popen("adb shell input tap 50 50")
	element = driver.find_element_by_android_uiautomator(str2)
	return element
def swipe(driver, startxper, startyper, endxper, endyper) :
	size = driver.get_window_size()
	width = size["width"]
	height = size["height"]
	try:
		driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),
				end_y=int(height * endyper), duration=2000)
	except WebDriverException:
		time.sleep(1)
	driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),
				end_y=int(height * endyper), duration=2000)
	return

def scrollToFindElement(driver, str) :
	for i in range(0, 5, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str)
			elements = driver.find_elements_by_android_uiautomator(str)
			if (len(elements) > 1) :
				for temp in elements :
					if temp.get_attribute("enabled") == "true" :
						element = temp
						break
		except NoSuchElementException:
			swipe(driver, 0.5, 0.55, 0.5, 0.2)
		else:
			return element
	return

def clickoncheckable(driver, str, value = "true") :
	parents = driver.find_elements_by_class_name("android.widget.LinearLayout")
	for parent in parents:
		try :
			parent.find_element_by_android_uiautomator(str)
			lists = parent.find_elements_by_class_name("android.widget.LinearLayout")
			if (len(lists) == 1) :
				innere = parent.find_element_by_android_uiautomator("new UiSelector().checkable(true)")
				nowvalue = innere.get_attribute("checked")
				if (nowvalue != value) :
					innere.click()
				break
		except NoSuchElementException:
			continue

# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n org.connectbot/org.connectbot.SettingsActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Keep Wi-Fi active\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Keep Wi-Fi active\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Scrollback size\")").click()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("")
	getElememt(driver, "new UiSelector().text(\"OK\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Autohide title bar\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Autohide title bar\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Keep screen awake\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Keep screen awake\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Special keys always visible\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Special keys always visible\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Audible bell\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Audible bell\")", "false")

	driver.press_keycode(4)
	time.sleep(2)

except Exception, e:
	print 'FAIL'
	print 'str(e):\t\t', str(e)
	print 'repr(e):\t', repr(e)
	print traceback.format_exc()
finally :
	endtime = time.time()
	print 'consumed time:', str(endtime - starttime), 's'
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_002_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase002
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	driver.press_keycode(82)
	element = getElememtBack(driver, "new UiSelector().text(\"Settings\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
	element = getElememtBack(driver, "new UiSelector().text(\"Backup pubkeys\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Navigate up\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Never connected\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $[F\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $[F/system/bin/sh: [F: not found127|u0_a55@generic_x86:/ $ [F\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"PgUp\")", "new UiSelector().className(\"android.widget.Button\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"12:12\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $ 12stt/system/bin/sh: 12stt: not found127|u0_a55@generic_x86:/ $127|u0_a55@generic_x86:/ $\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Ctrl\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"test\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.connectbot:id/button_down\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Connecting to 12est:23 v\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Connecting to 12:12 via telnet\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Home\")", "new UiSelector().className(\"android.widget.Button\").instance(2)")
	TouchAction(driver).tap(element).perform()
except Exception, e:
	print 'FAIL'
	print 'str(e):\t\t', str(e)
	print 'repr(e):\t', repr(e)
	print traceback.format_exc()
else:
	print 'OK'
finally:
	cpackage = driver.current_package
	endtime = time.time()
	print 'consumed time:', str(endtime - starttime), 's'
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_002\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'org.connectbot'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)