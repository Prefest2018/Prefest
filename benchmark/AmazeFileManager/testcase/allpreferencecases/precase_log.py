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
	'appPackage' : 'com.amaze.filemanager',
	'appActivity' : 'com.amaze.filemanager.activities.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.amaze.filemanager/com.amaze.filemanager.JacocoInstrumentation',
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
def testingSeekBar(driver, str, value):
	try :
		if(not checkWindow(driver)) :
			element = seekForNearestSeekBar(driver, str)
		else :
			element = driver.find_element_by_class_name("android.widget.SeekBar")
		if (None != element):
			settingSeekBar(driver, element, value)
			driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
	except NoSuchElementException:
		time.sleep(1)

def seekForNearestSeekBar(driver, str):
	parents = driver.find_elements_by_class_name("android.widget.LinearLayout")
	for parent in parents:
		try :
			parent.find_element_by_android_uiautomator(str)
			lists = parent.find_elements_by_class_name("android.widget.LinearLayout")
			if len(lists) == 1 :
				innere = parent.find_element_by_class_name("android.widget.SeekBar")
				return innere
				break
		except NoSuchElementException:
			continue
def settingSeekBar(driver, element, value) :
	x = element.rect.get("x")
	y = element.rect.get("y")
	width = element.rect.get("width")
	height = element.rect.get("height")
	TouchAction(driver).press(None, x + 10, y + height/2).move_to(None, x + width * value,y + height/2).release().perform()
	y = value
def clickInMultiList(driver, str) :
	element = None
	if (str is None) :
		candidates = driver.find_elements_by_class_name("android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(driver):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToFindElement(driver, str)
	if element is not None :
		nowvalue = element.get_attribute("checked")
		if (nowvalue != "true") :
			element.click()
	if checkWindow(driver) :
		driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
def settingPref_theme(driver, value):
	try :
		if value == "0":
			element = getElememtBack(driver, "new UiSelector().text(\"Material Light\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "1":
			element = getElememtBack(driver, "new UiSelector().text(\"Material Dark\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "2":
			element = getElememtBack(driver, "new UiSelector().text(\"Daytime\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "3":
			element = getElememtBack(driver, "new UiSelector().text(\"Black (for OLED)\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
	except NoSuchElementException:
		time.sleep(1)
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.amaze.filemanager/com.amaze.filemanager.activities.PreferencesActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Color\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Colorize Icons\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Colorize Icons\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Colored Navigation bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Colored Navigation bar\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Quick accesses\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Quick access\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Quick access\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Recent files\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Recent files\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Images\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Images\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Videos\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Videos\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Audio\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Audio\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Documents\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Documents\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Apks\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Apks\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Use circular icons\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use circular icons\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Dividers\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Dividers\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Headers\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Headers\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Intellihide Toolbar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Intellihide Toolbar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show shortcuts\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show shortcuts\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show quick accesses\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show quick accesses\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Remember paths\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remember paths\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Thumbnails\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Thumbnails\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Hidden Files and Folders\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Hidden Files and Folders\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Last Modified Date and Time\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Last Modified Date and Time\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Sizes\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Sizes\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Back navigation\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Back navigation\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable marquee effect for file names\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable marquee effect for file names\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Fingerprint\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Fingerprint\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Custom paths\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Custom paths\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Open files as new task\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Open files as new task\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Permissions\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Permissions\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Root Explorer\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Root Explorer\")", "true")

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
