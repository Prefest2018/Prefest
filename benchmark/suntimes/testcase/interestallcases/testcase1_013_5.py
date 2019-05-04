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
	'appPackage' : 'com.forrestguice.suntimeswidget',
	'appActivity' : 'com.forrestguice.suntimeswidget.SuntimesActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.JacocoInstrumentation',
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
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	os.popen("adb shell settings put secure location_providers_allowed network,gps")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.SuntimesSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"General Settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Time Format\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"System format\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show Seconds\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Seconds\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Time (with dates)\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Time (with dates)\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Language Settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Mode\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"User Defined\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"User Interface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Theme:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Dark Theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show Data Source\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Data Source\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Blue Hour\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Blue Hour\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Golden Hour\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Golden Hour\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Light Map\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Light Map\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Moon\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Moon\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Warnings\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Warnings\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Weeks\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Weeks\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Hours\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Hours\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Verbose TalkBack\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Verbose TalkBack\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Solstice / Equinox\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Solstice / Equinox\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Solstice Tracking\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Closest Event\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"On Clock Tap:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Set Alarm\")").click()

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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_013_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase013
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.forrestguice.suntimeswidget:id/action_location_add\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Prescott, AZ\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Prescott, AZ\")", "new UiSelector().className(\"android.widget.CheckedTextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Update\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"AM\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"sunset\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/icon1\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"sunrise\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/icon1\").className(\"android.widget.ImageView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"sunset\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_013\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.forrestguice.suntimeswidget'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
	os.popen("adb shell settings put secure location_providers_allowed gps, network")