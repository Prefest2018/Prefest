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
	'appPackage' : 'com.tomer.alwayson',
	'appActivity' : 'com.tomer.alwayson.activities.PreferencesActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.tomer.alwayson/com.tomer.alwayson.JacocoInstrumentation',
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
				end_y=int(height * endyper), duration=1000)
	except WebDriverException:
		time.sleep(1)
	driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),
				end_y=int(height * endyper), duration=1000)
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
		else :
			return element
	for i in range(0, 4, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str)
			elements = driver.find_elements_by_android_uiautomator(str)
			if (len(elements) > 1):
				for temp in elements:
					if temp.get_attribute("enabled") == "true":
						element = temp
						break
		except NoSuchElementException:
			swipe(driver, 0.5, 0.2, 0.5, 0.55)
		else :
			return element
	return

def scrollToClickElement(driver, str) :
	element = scrollToFindElement(driver, str)
	if element is None :
		return
	else :
		element.click()

def clickInList(driver, str) :
	element = None
	if (str is None) :
		candidates = driver.find_elements_by_class_name("android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(driver):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToFindElement(driver, str)
	if element is not None :
		element.click()
	else :
		if checkWindow(driver) :
			driver.press_keycode(4)

def clickOnCheckable(driver, str, value = "true") :
	parents = driver.find_elements_by_class_name("android.widget.LinearLayout")
	for parent in parents:
		try :
			parent.find_element_by_android_uiautomator(str)
			lists = parent.find_elements_by_class_name("android.widget.LinearLayout")
			if len(lists) == 1 :
				innere = parent.find_element_by_android_uiautomator("new UiSelector().checkable(true)")
				nowvalue = innere.get_attribute("checked")
				if (nowvalue != value) :
					innere.click()
				break
		except NoSuchElementException:
			continue

def typeText(driver, value) :
	element = getElememt(driver, "new UiSelector().className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys(value)
	enterelement = getElememt(driver, "new UiSelector().text(\"OK\")")
	if (enterelement is None) :
		if checkWindow(driver):
			driver.press_keycode(4)
	else :
		enterelement.click()
def checkWindow(driver) :
	dsize = driver.get_window_size()
	nsize = driver.find_element_by_class_name("android.widget.FrameLayout").size
	if dsize['height'] > nsize['height']:
		return True
	else :
		return False
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
	os.popen("adb shell am start -n com.tomer.alwayson/com.tomer.alwayson.activities.PreferencesActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Enable\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic Rules\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Stop delay\")")
	clickInList(driver, "new UiSelector().text(\"Disabled\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Charging rules\")")
	clickInList(driver, "new UiSelector().text(\"Both, while charging and while discharging\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Battery rules\")")
	clickInList(driver, "new UiSelector().text(\"Always\")")
	conscript(driver)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Gestures & shortcuts\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Double tap action\")")
	clickInList(driver, "new UiSelector().text(\"Turn the screen on\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe up action\")")
	clickInList(driver, "new UiSelector().text(\"Off\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe down action\")")
	clickInList(driver, "new UiSelector().text(\"Off\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Volume keys action\")")
	clickInList(driver, "new UiSelector().text(\"Off\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Back button action\")")
	clickInList(driver, "new UiSelector().text(\"Off\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Allow camera shortcuts\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Allow camera shortcuts\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Allow \"OK Google\"\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Allow \"OK Google\"\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Customize Watchface\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Battery style\")")
	clickInList(driver, "new UiSelector().text(\"Hidden\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Show AM/PM\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Show AM/PM\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Screen orientation\")")
	clickInList(driver, "new UiSelector().text(\"Portrait (Vertical)\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Animation\")")
	clickInList(driver, "new UiSelector().text(\"No animation\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Battery save mode\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Battery save mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Raise to wake\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Raise to wake\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Notifications\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Notifications\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Notification content\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Notification content\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Start after lock\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Start after lock\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Move the widget automatically\")")
	clickInList(driver, "new UiSelector().text(\"Move the widget automatically\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Auto night mode\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Auto night mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Proximity sensor (Pocket mode)\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Proximity sensor (Pocket mode)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Doze mode\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Doze mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Greenify integration\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Greenify integration\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Disable volume keys\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Disable volume keys\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show persistent notification\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Show persistent notification\")", "true")

	driver.press_keycode(4)
	time.sleep(2)
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"preference_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
