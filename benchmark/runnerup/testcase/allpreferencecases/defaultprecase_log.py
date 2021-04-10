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
	'appPackage' : 'org.runnerup',
	'appActivity' : 'org.runnerup.view.MainLayout',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.runnerup/org.runnerup.JacocoInstrumentation',
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
	os.popen("adb shell am start -n org.runnerup/org.runnerup.view.SettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Unit preference\")")
	clickInList(driver, "new UiSelector().text(\"Kilometers\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Sensors\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Autostart GPS\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Autostart GPS\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Headset key start/stop\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Headset key start/stop\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lock activity buttons\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lock activity buttons\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Step sensor\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Step sensor\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Temperature sensor\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Temperature sensor\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Pressure sensor\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Pressure sensor\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Adjust barometer altitude to the GPS elevation\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Adjust barometer altitude to the GPS elevation\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Recording\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Autolap\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Autolap\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Autopause\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Autopause\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Activity countdown\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Activity countdown\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Adjust GPS altitude to geoid (EGM96)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Adjust GPS altitude to geoid (EGM96)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Current speed from GPS points\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Current speed from GPS points\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Log extended GPS accuracy data\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Log extended GPS accuracy data\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Graph\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Smooth pace graph\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Smooth pace graph\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Workout\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Convert rest on Interval tab\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Convert rest on Interval tab\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Convert rest on Advanced tab\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Convert rest on Advanced tab\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Autolap during intervals\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Autolap during intervals\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Add countdown after step that ends with user press\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Add countdown after step that ends with user press\")", "true")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n org.runnerup/org.runnerup.view.AudioCueSettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Time triggered audio cue\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Time triggered audio cue\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance triggered audio cue\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Distance triggered audio cue\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"End of lap audio cue\")")
	clickOnCheckable(driver, "new UiSelector().text(\"End of lap audio cue\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap start\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap start\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Mute music during audio cues\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mute music during audio cues\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"HRM connection triggered audio cue\")")
	clickOnCheckable(driver, "new UiSelector().text(\"HRM connection triggered audio cue\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Include unit in audio cue\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Include unit in audio cue\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Total distance\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Total distance\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Total time\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Total time\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Total speed\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Total speed\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Total pace\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Total pace\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Total heart rate\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Total heart rate\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Total heart rate zone\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Total heart rate zone\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Interval distance\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Interval distance\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Interval time\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Interval time\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Interval speed\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Interval speed\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Interval pace\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Interval pace\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Interval heart rate\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Interval heart rate\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Interval heart rate zone\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Interval heart rate zone\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap distance\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap distance\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap time\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap time\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap speed\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap speed\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap pace\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap pace\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap heart rate\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap heart rate\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lap heart rate zone\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Lap heart rate zone\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Current pace\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Current pace\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Current speed\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Current speed\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Current heart rate\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Current heart rate\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Current heart rate zone\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Current heart rate zone\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Current cadence\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Current cadence\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Target coaching\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Target coaching\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Skip event audio cues\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Skip event audio cues\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Audio cue language\")")
	clickInList(driver, "new UiSelector().text(\"Deutsch\")")

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