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
	'appPackage' : 'org.thoughtcrime.securesms',
	'appActivity' : 'org.thoughtcrime.securesms.ConversationListActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.thoughtcrime.securesms/org.thoughtcrime.securesms.JacocoInstrumentation',
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
def settingPref_pref_call_ringtone(driver, value):
	try :
		if value == "null":
			element = getElememtBack(driver, "new UiSelector().text(\"None\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "content://settings/system/notification_sound":
			element = getElememtBack(driver, "new UiSelector().text(\"Default notification sound\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "file://HeartBeat":
			element = getElememtBack(driver, "new UiSelector().text(\"Heartbeat\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		element = getElememtBack(driver, "new UiSelector().text(\"OK\")", "new UiSelector()")
		TouchAction(driver).tap(element).perform()
	except NoSuchElementException:
		time.sleep(1)
def settingPref_pref_key_ringtone(driver, value):
	try :
		if value == "null":
			element = getElememtBack(driver, "new UiSelector().text(\"None\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "content://settings/system/notification_sound":
			element = getElememtBack(driver, "new UiSelector().text(\"Default notification sound\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		element = getElememtBack(driver, "new UiSelector().text(\"OK\")", "new UiSelector()")
		TouchAction(driver).tap(element).perform()
	except NoSuchElementException:
		time.sleep(1)
def conscript(driver):
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Enable local backups?\")")
		element = driver.find_element_by_android_uiautomator("new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/confirmation_check\")")
		element.click()
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Enable backups\")")
		element.click()
	except NoSuchElementException:
		time.sleep(0.1)
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Delete backups?\")")
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Delete backups\")")
		element.click()
	except NoSuchElementException:
		time.sleep(0.1)
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Cancel\")")
		element.click()
		time.sleep(1)
	except NoSuchElementException:
		time.sleep(0.1)
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.thoughtcrime.securesms/org.thoughtcrime.securesms.ApplicationPreferencesActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Privacy\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen lock\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Screen lock\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable passphrase screen lock\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Enable passphrase screen lock\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Inactivity timeout passphrase\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Inactivity timeout passphrase\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen security\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Screen security\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Incognito keyboard\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Incognito keyboard\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Read receipts\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Read receipts\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Typing indicators\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Typing indicators\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Display indicators\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Display indicators\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Chats and media\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use system emoji\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Use system emoji\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enter key sends\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Enter key sends\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Delete old messages\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Delete old messages\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Chat backups\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Chat backups\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Notifications\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Notifications\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Notifications\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"In-chat sounds\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"In-chat sounds\")", "true")

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
