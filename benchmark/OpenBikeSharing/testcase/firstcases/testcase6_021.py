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
	'appPackage' : 'be.brunoparmentier.openbikesharing.app',
	'appActivity' : 'be.brunoparmentier.openbikesharing.app.activities.StationsListActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'be.brunoparmentier.openbikesharing.app/be.brunoparmentier.openbikesharing.app.JacocoInstrumentation',
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

# testcase021
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Refresh\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememt(driver, "new UiSelector().resourceId(\"be.brunoparmentier.openbikesharing.app:id/emptySlotsLogo\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"4\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"6\")", "new UiSelector().className(\"android.widget.TextView\").instance(16)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"be.brunoparmentier.openbikesharing.app:id/action_favorite\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/up\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Campo de Marte\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/home\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"1\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"12\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"12\")", "new UiSelector().className(\"android.widget.TextView\").instance(11)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"6_021\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'be.brunoparmentier.openbikesharing.app'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)