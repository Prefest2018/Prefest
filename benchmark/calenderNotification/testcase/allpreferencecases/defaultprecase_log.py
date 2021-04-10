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
	'appPackage' : 'com.github.quarck.calnotify',
	'appActivity' : 'com.github.quarck.calnotify.ui.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.github.quarck.calnotify/com.github.quarck.calnotify.JacocoInstrumentation',
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
	os.popen("adb shell am start -n com.github.quarck.calnotify/com.github.quarck.calnotify.ui.SettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Behavior\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Manual calendar rescan\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Manual calendar rescan\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Handle e-mail only events\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Handle e-mail only events\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Handle events with no reminders\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Handle events with no reminders\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Ignore Declined events\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Ignore Declined events\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Ignore Cancelled events\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Ignore Cancelled events\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Ignore all-day events\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Ignore all-day events\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Ignore expired events\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Ignore expired events\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable mute\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable mute\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Misc settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"First day of the week\")")
	clickInList(driver, "new UiSelector().text(\"Sunday\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use setAlarmClock for reminders\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use setAlarmClock for reminders\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use setAlarmClock for events\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use setAlarmClock for events\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Forward to PebbleAPI\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Forward to PebbleAPI\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Forward reminders to PebbleAPI\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Forward reminders to PebbleAPI\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Event text in the title\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Event text in the title\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"PebbleAPI - only #alarm-s\")")
	clickOnCheckable(driver, "new UiSelector().text(\"PebbleAPI - only #alarm-s\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Notification Settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Alarm volume\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Alarm volume\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Vibration\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Vibration\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Vibration pattern\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Notification LED\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Notification LED\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Bundle notifications\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Bundle notifications\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Allow notification swipe\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Allow notification swipe\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Snooze on swipe\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Snooze on swipe\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Heads-up Notification\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Heads-up Notification\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Wake screen\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Wake screen\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Event color in the notification\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Event color in the notification\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Tap on title opens calendar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Tap on title opens calendar\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Collapse everything on exceeding\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Collapse everything on exceeding\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show event description\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show event description\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Append empty action\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Append empty action\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Quiet hours settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable quiet time\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable quiet time\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Mute primary notifications\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mute primary notifications\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Mute LED\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mute LED\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Reminders\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable reminders\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable reminders\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Custom ringtone\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Custom ringtone\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Custom vibration\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Custom vibration\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Vibration\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Vibration\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Vibration pattern\")")
	clickInList(driver, None)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Snooze settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"View after edit\")")
	clickOnCheckable(driver, "new UiSelector().text(\"View after edit\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide description\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide description\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Always use external editor\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Always use external editor\")", "false")

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
