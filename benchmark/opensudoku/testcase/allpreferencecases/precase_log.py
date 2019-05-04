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
	'appPackage' : 'org.moire.opensudoku',
	'appActivity' : 'org.moire.opensudoku.gui.FolderListActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.moire.opensudoku/org.moire.opensudoku.JacocoInstrumentation',
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
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.moire.opensudoku/org.moire.opensudoku.gui.GameSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Highlight wrong values\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Highlight wrong values\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Highlight completed numbers\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Highlight completed numbers\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Highlight similar cells\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Highlight similar cells\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Bidirectional selection\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Bidirectional selection\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Number Totals\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Number Totals\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Fill in notes\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Fill in notes\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Popup\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Popup\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Single number\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Single number\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Numpad\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Numpad\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"More settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Move right on press\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Move right on press\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Show time\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show time\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"First launch hints\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"First launch hints\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"High Contrast\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"More settings\").instance(1)").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Highlight cell on touch\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Highlight cell on touch\")", "false")

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
	driver.quit()