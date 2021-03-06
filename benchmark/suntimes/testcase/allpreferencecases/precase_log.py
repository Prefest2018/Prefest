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
	'appPackage' : 'com.forrestguice.suntimeswidget',
	'appActivity' : 'com.forrestguice.suntimeswidget.SuntimesActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.JacocoInstrumentation',
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
	os.popen("adb shell am start -n com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.SuntimesSettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"General Settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Time Format\")")
	clickInList(driver, "new UiSelector().text(\"24-hour\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Seconds\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Seconds\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Time (with dates)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Time (with dates)\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Language Settings\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Mode\")")
	clickInList(driver, "new UiSelector().text(\"User Defined\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"User Interface\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Theme:\")")
	clickInList(driver, "new UiSelector().text(\"Light Theme\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Data Source\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Data Source\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Blue Hour\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Blue Hour\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Golden Hour\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Golden Hour\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Light Map\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Light Map\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Moon\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Moon\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Warnings\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Warnings\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Weeks\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Weeks\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Hours\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Hours\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Verbose TalkBack\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Verbose TalkBack\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Solstice / Equinox\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Solstice / Equinox\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Solstice Tracking\")")
	clickInList(driver, "new UiSelector().text(\"Closest Event\")")
	scrollToClickElement(driver, "new UiSelector().text(\"On Clock Tap:\")")
	clickInList(driver, "new UiSelector().text(\"Do nothing\")")
	scrollToClickElement(driver, "new UiSelector().text(\"On Date Tap:\")")
	clickInList(driver, "new UiSelector().text(\"Show calendar app\")")
	scrollToClickElement(driver, "new UiSelector().text(\"On Note Tap:\")")
	clickInList(driver, "new UiSelector().text(\"Set alarm for note\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Places\")")
	scrollToClickElement(driver, "new UiSelector().text(\"GPS time limit\")")
	clickInList(driver, "new UiSelector().text(\"15 seconds\")")
	scrollToClickElement(driver, "new UiSelector().text(\"GPS recent max age\")")
	clickInList(driver, "new UiSelector().text(\"one month (672 hours)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Passive Location\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Passive Location\")", "true")

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
