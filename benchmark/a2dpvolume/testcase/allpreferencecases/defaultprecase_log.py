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
	os.popen("adb shell am start -n a2dp.Vol/a2dp.Vol.Preferences -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Start at Boot?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Start at Boot?\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Pop-ups?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Show Pop-ups?\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Notification Icon?\")")
	clickInList(driver, "new UiSelector().text(\"The service will start as a foreground task with an icon on the notification bar.\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Use Local File Storage?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Use Local File Storage?\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Respond to Car Mode?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Respond to Car Mode?\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Respond to Home Dock?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Respond to Home Dock?\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Respond to Audio Jack?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Respond to Audio Jack?\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Respond to Power Connection?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Respond to Power Connection?\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable Reading Text Messages?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Enable Reading Text Messages?\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable reading notification messages\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Enable reading notification messages\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide volume pop-up\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Hide volume pop-up\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"GPS Listener Timeout\")")
	clickInList(driver, "new UiSelector().text(\"20 seconds\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"GPS Max Inaccuracy\")")
	clickInList(driver, "new UiSelector().text(\"2m (6ft)\")")
	conscript(driver)
	scrollToClickElement(driver, "new UiSelector().text(\"Use Passive Locations?\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Use Passive Locations?\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Network Locations\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Use Network Locations\")", "true")

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
