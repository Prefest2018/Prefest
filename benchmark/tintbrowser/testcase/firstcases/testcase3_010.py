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
	'appPackage' : 'org.tint',
	'appActivity' : 'org.tint.ui.activities.TintBrowserActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.tint/org.tint.JacocoInstrumentation',
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

# testcase010
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/BtnAddTab\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Tap to enter an URL or a search.\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/BookmarkRow.Thumbnail\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/MenuButton\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Decline\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Find on page\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12tp://12st//tests//en.m.wikipedia.org/wiki/Main_Page");
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"http://12sts//en.m.wikipedia.org/wiki/Main_Page\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("http://12sts//en.m.wikipedia.org/wiki/Main_Page");
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("test://12sts//en.m.wikipedia.org/wiki/Main_Page");
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("http://tests//en.m.wikipedia.org/wiki/Main_Page");
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("http://testtesthttps//en.m.wikipedia.org/wiki/Main_Page#/search");
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12st://tests//en.m.wikipedia.org/wiki/Main_Page");
	element = getElememt(driver, "new UiSelector().resourceId(\"org.tint:id/UrlBarUrlEdit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12st://testtesthttps//en.m.wikipedia.org/wiki/Main_Page#/search");
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_010\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'org.tint'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)