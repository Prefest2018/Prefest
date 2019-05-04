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
	clickoncheckable(driver, "new UiSelector().text(\"Show Pop-ups?\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Use Local File Storage?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Use Local File Storage?\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Car Mode?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Car Mode?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Home Dock?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Home Dock?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Audio Jack?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Audio Jack?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Respond to Power Connection?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Respond to Power Connection?\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Enable Reading Text Messages?\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Enable Reading Text Messages?\")", "true")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"4_020_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase020
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"Stop Service\")", "new UiSelector().className(\"android.widget.Button\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"testr Connection\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Edit\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editDesc2\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Home Dock");
	element = getElememtBack(driver, "new UiSelector().text(\"Disconnect WIFI when bluetooth connects?\")", "new UiSelector().resourceId(\"a2dp.Vol:id/checkwifi\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/home\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Silence notifications while connected?\")", "new UiSelector().resourceId(\"a2dp.Vol:id/silentBox\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editDesc2\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Power Connection");
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editDesc2\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("testo Jack");
	element = getElememtBack(driver, "new UiSelector().text(\"Capture location on disconnect?\")", "new UiSelector().resourceId(\"a2dp.Vol:id/checkCaptLoc\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Silence notifications while connected?\")", "new UiSelector().resourceId(\"a2dp.Vol:id/silentBox\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editDesc2\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Home Dock");
	element = getElememtBack(driver, "new UiSelector().text(\"Automatically remember last used media volume on disconnect\")", "new UiSelector().resourceId(\"a2dp.Vol:id/autoVolcheckBox\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editDesc2\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Power Connection");
	element = getElememtBack(driver, "new UiSelector().text(\"Set media volume on connect?\")", "new UiSelector().resourceId(\"a2dp.Vol:id/checkSetVol\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editBtConnect\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1test");
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/iconradio0\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editBtConnect\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12stt");
	element = getElememtBack(driver, "new UiSelector().text(\"Set media volume on connect?\")", "new UiSelector().resourceId(\"a2dp.Vol:id/checkSetVol\").className(\"android.widget.CheckBox\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"a2dp.Vol:id/editBtConnect\").className(\"android.widget.EditText\")")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"4_020\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'a2dp.Vol'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
