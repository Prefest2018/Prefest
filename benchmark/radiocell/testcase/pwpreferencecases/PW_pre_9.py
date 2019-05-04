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
	'appPackage' : 'org.openbmap',
	'appActivity' : 'org.openbmap.activities.StartscreenActivity',
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
			swipe(driver, 0.5, 0.55, 0.5, 0.2)
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
					parent.click()
				break
		except NoSuchElementException:
			continue
def conscript(driver):
	try:
		driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
		time.sleep(0.1)
	except NoSuchElementException:
		time.sleep(0.1)
	return
# preference setting and exit
try :
	os.popen("adb shell am start -a android.intent.action.VIEW -d file:///mnt/sdcard/music/MoonFlow.mp3 -t audio/wav -f 1")
	os.popen("adb shell svc data enable")
	os.popen("adb shell service call bluetooth_manager 8")
	os.popen("adb shell svc wifi diable")
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.openbmap/org.openbmap.activities.AdvancedSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Ignore low battery\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Ignore low battery\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Cleanup old sessions\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Cleanup old sessions\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"On export, create a GPX file locally on your device\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"On export, create a GPX file locally on your device\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"GPX verbosity\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Waypoints only\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Wireless scan mode\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Full power mode\")").click()
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Anonymise SSID in upload files\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Anonymise SSID in upload files\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Skip upload\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Skip upload\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Keep uploaded files\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Keep uploaded files\")", "false")
	conscript(driver)

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n org.openbmap/org.openbmap.activities.SettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Anonymous upload\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Anonymous upload\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Save Cells\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Save Cells\")", "true")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Save WiFis\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Save WiFis\")", "false")
	conscript(driver)
	scrollToFindElement(driver, "new UiSelector().text(\"Keep screen on\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Keep screen on\")", "false")
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
