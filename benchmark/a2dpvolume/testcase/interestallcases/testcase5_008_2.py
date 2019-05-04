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
	'appPackage' : 'a2dp.Vol',
	'appActivity' : 'a2dp.Vol.main',
	'resetKeyboard' : True,
	'androidCoverage' : 'a2dp.Vol/a2dp.Vol.JacocoInstrumentation',
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

def conscript(driver):
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")")
	except NoSuchElementException:
		time.sleep(0.1)
	else:
		element.click()
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Activate\")")
	except NoSuchElementException:
		time.sleep(0.1)
	else:
		element.click()
		return
	try:
		driver.find_element_by_android_uiautomator("new UiSelector().text(\"Notification access\")")
		driver.find_element_by_android_uiautomator("new UiSelector().checkable(true)").click()
		driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
		driver.press_keycode(4)
		time.sleep(0.1)
	except NoSuchElementException:
		time.sleep(0.1)
	return

# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n a2dp.Vol/a2dp.Vol.Preferences")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Pop-ups?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Show Pop-ups?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Use Local File Storage?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Use Local File Storage?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Car Mode?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Car Mode?\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Home Dock?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Home Dock?\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Audio Jack?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Audio Jack?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Power Connection?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Power Connection?\")", "true")
	conscript(driver)

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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_008_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase008
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"Stop Service\")", "new UiSelector().className(\"android.widget.Button\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Stored Location\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"testr Connection\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Delete\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"testo Jack\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Edit\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editApp\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12standroid.settings");
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editApp\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12dp.Vol");
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editApp\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("a2dp.Vol");
	element = getElememtBack(driver, "new UiSelector().text(\"Add to dictionary\")", "new UiSelector().className(\"android.widget.TextView\")")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_008\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'a2dp.Vol'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
