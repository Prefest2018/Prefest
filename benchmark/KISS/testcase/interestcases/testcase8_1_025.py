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
	'appPackage' : 'fr.neamar.kiss',
	'appActivity' : 'fr.neamar.kiss.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'fr.neamar.kiss/fr.neamar.kiss.JacocoInstrumentation',
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
		except NoSuchElementException:
			swipe(driver, 0.5, 0.6, 0.5, 0.2)
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

	os.popen("adb shell am start -n fr.neamar.kiss/fr.neamar.kiss.SettingsActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"History settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Max number of results in search and history\")").click()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1")
	getElememt(driver, "new UiSelector().text(\"OK\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"History mode\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Accessed frequently recently\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Favorites settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Exclude favorites from history\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Exclude favorites from history\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"User interface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Theme interface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Light theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Display notification icons in black\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Display notification icons in black\")", "false")

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_025_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase025
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"fr.neamar.kiss:id/searchEditText\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Search apps, contacts, .");
	element = getElememt(driver, "new UiSelector().resourceId(\"fr.neamar.kiss:id/launcherButton\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(82)
	element = getElememtBack(driver, "new UiSelector().text(\"KISS settings\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"User experience\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"User experience\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"Reset your history, exclude apps, .\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Show newly installed apps in history\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Excluded apps\")", "new UiSelector().className(\"android.widget.TextView\").instance(10)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Show incoming calls in history\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_025\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'fr.neamar.kiss'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)