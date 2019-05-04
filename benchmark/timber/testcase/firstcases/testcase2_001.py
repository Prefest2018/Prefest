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
	'appPackage' : 'com.naman14.timber',
	'appActivity' : 'com.naman14.timber.activities.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.naman14.timber/com.naman14.timber.JacocoInstrumentation',
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

# testcase001
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	driver.press_keycode(82)
	element = getElememtBack(driver, "new UiSelector().text(\"Sort by\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(82)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(82)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(82)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(82)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.naman14.timber:id/albumArt\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"intermission\")", "new UiSelector().className(\"android.widget.TextView\").instance(18)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.naman14.timber:id/action_search\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.naman14.timber:id/action_search\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.naman14.timber:id/search_src_text\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Search Library");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.naman14.timber:id/search_src_text\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("test");
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_001\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.naman14.timber'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)