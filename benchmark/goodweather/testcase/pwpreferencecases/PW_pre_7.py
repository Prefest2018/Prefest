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
	'appPackage' : 'org.asdtm.goodweather',
	'appActivity' : 'org.asdtm.goodweather.MainActivity',
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
			swipe(driver, 0.5, 0.6, 0.5, 0.2)
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
# preference setting and exit
try :
	os.popen("adb shell svc data diable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi diable")
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	os.popen("adb shell settings put secure location_providers_allowed gps")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.asdtm.goodweather/org.asdtm.goodweather.SettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"General settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Temperature\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"°F\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Hide weather description\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Hide weather description\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Dark\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Enable Notifications\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Enable Notifications\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Refresh Interval\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Every hour\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Vibrate\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Vibrate\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Widget settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Update location\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Update location\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Use geolocation for displaying\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Use geolocation for displaying\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Widget theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Dark\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Update Period\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Every hour\")").click()

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
	os.popen("adb shell svc data enable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi enable")
	os.popen("adb shell settings put secure location_providers_allowed gps, network")
	driver.quit()
