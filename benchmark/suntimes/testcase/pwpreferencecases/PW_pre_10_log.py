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
	'appPackage' : 'com.forrestguice.suntimeswidget',
	'appActivity' : 'com.forrestguice.suntimeswidget.SuntimesActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.JacocoInstrumentation',
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
	os.popen("adb shell am start -a android.intent.action.VIEW -d file:///mnt/sdcard/music/MoonFlow.mp3 -t audio/wav -f 1")
	os.popen("adb shell svc data enable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi enable")
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	os.popen("adb shell settings put secure location_providers_allowed network")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.SuntimesSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"General Settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Time Format\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"24-hour\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show Seconds\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Seconds\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Time (with dates)\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Time (with dates)\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Language Settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Mode\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"User Defined\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"User Interface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Theme:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Dark Theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show Data Source\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Data Source\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Blue Hour\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Blue Hour\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Golden Hour\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Golden Hour\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Light Map\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Light Map\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Moon\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Moon\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Warnings\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Warnings\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Weeks\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Weeks\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Hours\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Hours\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Verbose TalkBack\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Verbose TalkBack\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Solstice / Equinox\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Solstice / Equinox\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Solstice Tracking\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Upcoming Event\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"On Clock Tap:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Set Alarm\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"On Date Tap:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show calendar app\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"On Note Tap:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show next note\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Places\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"GPS time limit\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"15 seconds\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"GPS recent max age\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"one month (672 hours)\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Passive Location\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Passive Location\")", "false")

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"preference_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	os.open("adb shell input keyevent 127")
	os.popen("adb shell svc data enable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi enable")
	os.popen("adb shell settings put secure location_providers_allowed gps, network")
	driver.quit()
