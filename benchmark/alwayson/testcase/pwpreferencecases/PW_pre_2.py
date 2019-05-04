#coding=utf-8
import os
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
	'noReset' : True
	}

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
			swipe(driver, 0.5, 0.5, 0.5, 0.2)
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
	os.popen("adb shell am start -a android.intent.action.VIEW -d file:///mnt/sdcard/music/MoonFlow.mp3 -t audio/wav -f 1")
	os.popen("adb shell svc data diable")
	os.popen("adb shell service call bluetooth_manager 8")
	os.popen("adb shell svc wifi diable")
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.tomer.alwayson/com.tomer.alwayson.activities.PreferencesActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Enable\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Enable\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Automatic Rules\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Stop delay\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"1 Minute\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Charging rules\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"While not charging\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Battery rules\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Always\")").click()
	conscript(driver)
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Gestures & shortcuts\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Double tap action\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Turn the screen on\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Swipe up action\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Turn the screen on\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Swipe down action\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Off\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Volume keys action\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Turn the screen on\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Back button action\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Turn the screen on\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Allow camera shortcuts\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Allow camera shortcuts\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Allow \"OK Google\"\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Allow \"OK Google\"\")", "true")
	conscript(driver)
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Customize Watchface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Battery style\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Hidden\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Show AM/PM\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Show AM/PM\")", "true")
	conscript(driver)
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Screen orientation\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Landscape (Horizontal)\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Animation\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Fade out\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Battery save mode\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Battery save mode\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Raise to wake\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Raise to wake\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Notifications\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Notifications\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Notification content\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Notification content\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Start after lock\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Start after lock\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Move the widget automatically\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Move the widget automatically\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Auto night mode\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Auto night mode\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Proximity sensor (Pocket mode)\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Proximity sensor (Pocket mode)\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Doze mode\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Doze mode\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Greenify integration\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Greenify integration\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Disable volume keys\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Disable volume keys\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Show persistent notification\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Show persistent notification\")", "false")
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
	os.open("adb shell input keyevent 127")
	os.popen("adb shell svc data enable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi enable")
	os.popen("adb shell settings put secure location_providers_allowed gps, network")
	driver.quit()
