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
	'appPackage' : 'com.ichi2.anki',
	'appActivity' : 'com.ichi2.anki.IntentHandler',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.ichi2.anki/com.ichi2.anki.JacocoInstrumentation',
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
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.ichi2.anki/com.ichi2.anki.Preferences -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Advanced\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Type answer into the card\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Type answer into the card\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Text to speech\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Text to speech\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lookup dictionary\")")
	clickInList(driver, "new UiSelector().text(\"None\")")
	scrollToClickElement(driver, "new UiSelector().text(\"HTML / Javascript Debugging\")")
	clickOnCheckable(driver, "new UiSelector().text(\"HTML / Javascript Debugging\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"AnkiDroid\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Share feature usage\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Share feature usage\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Notify when\")")
	clickInList(driver, "new UiSelector().text(\"Never notify\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Reviewing\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep screen on\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep screen on\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Fullscreen mode\")")
	clickInList(driver, "new UiSelector().text(\"Off\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Answer buttons position\")")
	clickInList(driver, "new UiSelector().text(\"Bottom\")")

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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_026_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
# testcase026
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"Default\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"0\")", "new UiSelector().className(\"android.widget.TextView\").instance(6)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"0\")", "new UiSelector().className(\"android.widget.TextView\").instance(6)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"This is a special deck for studying outside of the normal schedule. Cards will be automatically returned to their original decks after you review them. Deleting this deck from the deck list will return all remaining cards to their original deck.\")", "new UiSelector().className(\"android.widget.TextView\").instance(13)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_rebuild\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_empty\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_rebuild\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Custom study session\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"0\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"This is a special deck for studying outside of the normal schedule. Cards will be automatically returned to their original decks after you review them. Deleting this deck from the deck list will return all remaining cards to their original deck.\")", "new UiSelector().className(\"android.widget.TextView\").instance(13)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_empty\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_empty\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\")")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_026\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.ichi2.anki'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
