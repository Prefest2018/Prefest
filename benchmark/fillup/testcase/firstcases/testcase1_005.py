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
	'appPackage' : 'com.github.wdkapps.fillup',
	'appActivity' : 'com.github.wdkapps.fillup.StartupActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.github.wdkapps.fillup/com.github.wdkapps.fillup.JacocoInstrumentation',
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

# testcase005
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/textVehicleName\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/textVehicleName\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1test");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/textVehicleTankSize\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12.0");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/textVehicleTankSize\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/textVehicleTankSize\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1");
	element = getElememtBack(driver, "new UiSelector().text(\"OK\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/buttonVehicleDelete\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"No\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Log\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/buttonVehicleEdit\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"OK\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Add Fuel\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/editTextDate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("3/13/2018 11:20 AM");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/editTextPrice\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12.00");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/editTextNotes\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12sttstest");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.github.wdkapps.fillup:id/editTextCost\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("112");
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_005\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.github.wdkapps.fillup'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)