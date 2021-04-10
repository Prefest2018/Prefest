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
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n fr.neamar.kiss/fr.neamar.kiss.SettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"History settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"History mode\")")
	clickInList(driver, "new UiSelector().text(\"Accessed recently first\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Freeze history\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Freeze history\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show incoming calls in history\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show incoming calls in history\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show newly installed apps in history\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show newly installed apps in history\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Favorites settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show favorites above search bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show favorites above search bar\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Exclude favorites from history\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Exclude favorites from history\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"User interface\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Theme interface\")")
	clickInList(driver, "new UiSelector().text(\"Transparent theme\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Display notification icons in black\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Display notification icons in black\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Transparent search bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Transparent search bar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Transparent favorites bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Transparent favorites bar\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Bigger search bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Bigger search bar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Rounded list corners\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Rounded list corners\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Rounded bar corners\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Rounded bar corners\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"User experience\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Display keyboard on start\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Display keyboard on start\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Minimalistic UI\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Minimalistic UI\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show history on touch\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show history on touch\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide favorites bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide favorites bar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide navbar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide navbar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide status bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide status bar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide the KISS circle\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide the KISS circle\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Force portrait mode\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Force portrait mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show app tags\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show app tags\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide icons\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide icons\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Live wallpaper interactions\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Send touch events to wallpaper\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Send touch events to wallpaper\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Emulate drag event for wallpaper\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Emulate drag event for wallpaper\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Scroll wallpaper\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Scroll wallpaper\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Center wallpaper\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Center wallpaper\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Side stick wallpaper\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Side stick wallpaper\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Customize tags menu\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Add tags in menu\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Add tags in menu\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show untagged action\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show untagged action\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Close tags menu after selection\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Close tags menu after selection\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Providers selection\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Contacts\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Contacts\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Device settings\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Device settings\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Shortcuts\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Shortcuts\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Web search\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Web search\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Advanced settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Root mode\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Root mode\")", "false")

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
