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
	'appPackage' : 'ch.blinkenlights.android.vanilla',
	'appActivity' : 'ch.blinkenlights.android.vanilla.LibraryActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'ch.blinkenlights.android.vanilla/ch.blinkenlights.android.vanilla.JacocoInstrumentation',
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

	os.popen("adb shell am start -n ch.blinkenlights.android.vanilla/ch.blinkenlights.android.vanilla.PreferencesActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Cover art\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Load artwork from folder\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Load artwork from folder\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Load artwork from hidden folder\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Load artwork from hidden folder\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Load artwork from Android\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Load artwork from Android\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Load artwork from file tags\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Load artwork from file tags\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Library screen\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Default action\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Expand or play all\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Miscellaneous features\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Disable lockscreen\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Disable lockscreen\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Keep screen on\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Keep screen on\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Enable idle timeout\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Enable idle timeout\")", "false")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Notifications\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Notification action\")").click()
	nowparent = driver.find_elements_by_class_name("android.widget.ListView")
	candidates = nowparent[0].find_elements_by_class_name("android.widget.CheckedTextView")
	if(len(candidates)-1>=0):
		candidates[len(candidates)-1].click()
	else:
		driver.press_keycode(4)
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Playback screen\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Open on startup\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Open on startup\")", "false")

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_026_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase026
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"She\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Play\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"block\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"android.hardware.camera.autofocus.xml\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Expand\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Android\")", "new UiSelector().className(\"android.widget.TextView\").instance(11)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Play\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"ch.blinkenlights.android.vanilla:id/cover\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Albums\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"ch.blinkenlights.android.vanilla:id/dragger\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Me\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Blackbird Blackbird\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Me\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"ch.blinkenlights.android.vanilla:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"ch.blinkenlights.android.vanilla:id/cover\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"sdcard\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Expand\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"cpuctl\")", "new UiSelector().className(\"android.widget.TextView\").instance(10)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Expand\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_026\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'ch.blinkenlights.android.vanilla'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)