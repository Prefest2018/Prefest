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
	'appPackage' : 'com.nanoconverter.zlab',
	'appActivity' : 'com.nanoconverter.zlab.NanoConverter',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.nanoconverter.zlab/com.nanoconverter.zlab.JacocoInstrumentation',
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
	else :
		if checkWindow(driver) :
			driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")")
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.nanoconverter.zlab/com.nanoconverter.zlab.Preferences -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Updates\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Rates sources\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Reverse\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Reverse\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Default currency\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Currency list\")")
	scrollToClickElement(driver, "new UiSelector().text(\"USD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"USD\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"EUR\")")
	clickOnCheckable(driver, "new UiSelector().text(\"EUR\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"CHF\")")
	clickOnCheckable(driver, "new UiSelector().text(\"CHF\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"GBP\")")
	clickOnCheckable(driver, "new UiSelector().text(\"GBP\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"JPY\")")
	clickOnCheckable(driver, "new UiSelector().text(\"JPY\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"UAH\")")
	clickOnCheckable(driver, "new UiSelector().text(\"UAH\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"RUB\")")
	clickOnCheckable(driver, "new UiSelector().text(\"RUB\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"MDL\")")
	clickOnCheckable(driver, "new UiSelector().text(\"MDL\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"BYR\")")
	clickOnCheckable(driver, "new UiSelector().text(\"BYR\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"PLN\")")
	clickOnCheckable(driver, "new UiSelector().text(\"PLN\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"LTL\")")
	clickOnCheckable(driver, "new UiSelector().text(\"LTL\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"LVL\")")
	clickOnCheckable(driver, "new UiSelector().text(\"LVL\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"AZN\")")
	clickOnCheckable(driver, "new UiSelector().text(\"AZN\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"AUD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"AUD\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"AMD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"AMD\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"BGN\")")
	clickOnCheckable(driver, "new UiSelector().text(\"BGN\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"BRL\")")
	clickOnCheckable(driver, "new UiSelector().text(\"BRL\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"HUF\")")
	clickOnCheckable(driver, "new UiSelector().text(\"HUF\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"DKK\")")
	clickOnCheckable(driver, "new UiSelector().text(\"DKK\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"INR\")")
	clickOnCheckable(driver, "new UiSelector().text(\"INR\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"KZT\")")
	clickOnCheckable(driver, "new UiSelector().text(\"KZT\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"CAD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"CAD\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"KGS\")")
	clickOnCheckable(driver, "new UiSelector().text(\"KGS\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"CNY\")")
	clickOnCheckable(driver, "new UiSelector().text(\"CNY\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"NOK\")")
	clickOnCheckable(driver, "new UiSelector().text(\"NOK\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"RON\")")
	clickOnCheckable(driver, "new UiSelector().text(\"RON\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"XDR\")")
	clickOnCheckable(driver, "new UiSelector().text(\"XDR\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"SGD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"SGD\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"TJS\")")
	clickOnCheckable(driver, "new UiSelector().text(\"TJS\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"TRY\")")
	clickOnCheckable(driver, "new UiSelector().text(\"TRY\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"TMT\")")
	clickOnCheckable(driver, "new UiSelector().text(\"TMT\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"UZS\")")
	clickOnCheckable(driver, "new UiSelector().text(\"UZS\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"CZK\")")
	clickOnCheckable(driver, "new UiSelector().text(\"CZK\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"SEK\")")
	clickOnCheckable(driver, "new UiSelector().text(\"SEK\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"ZAR\")")
	clickOnCheckable(driver, "new UiSelector().text(\"ZAR\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"KRW\")")
	clickOnCheckable(driver, "new UiSelector().text(\"KRW\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"FOO\")")
	clickOnCheckable(driver, "new UiSelector().text(\"FOO\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Background\")")
	clickInList(driver, None)

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
