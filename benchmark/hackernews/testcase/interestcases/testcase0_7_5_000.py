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
	'appPackage' : 'io.dwak.holohackernews.app',
	'appActivity' : 'io.dwak.holohackernews.app.ui.storylist.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'io.dwak.holohackernews.app/io.dwak.holohackernews.app.JacocoInstrumentation',
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
def conscript(driver):
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Ok\")")
	except NoSuchElementException:
		time.sleep(0.1)
	else:
		element.click()
		os.popen("adb shell am start -n io.dwak.holohackernews.app/io.dwak.holohackernews.app.ui.settings.SettingsActivity -a test")
	return
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n io.dwak.holohackernews.app/io.dwak.holohackernews.app.ui.settings.SettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Show link first\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Show link first\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Open links in system browser\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Open links in system browser\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Night Mode\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Night Mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe back to list\")")
	conscript(driver)
	clickOnCheckable(driver, "new UiSelector().text(\"Swipe back to list\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Comment Text size\")")
	clickInList(driver, "new UiSelector().text(\"Small\")")
	conscript(driver)

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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_000_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
# testcase000
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"jsomers\")", "new UiSelector().className(\"android.widget.TextView\").instance(16)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"io.dwak.holohackernews.app:id/action_open_browser\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"io.dwak.holohackernews.app:id/action_2\").className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"12 hours ago\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"12 hours ago\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"16 eibrahim\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"How long have you been working on it? What marketing efforts have you done? Building the product is necessary but not sufficient...\")", "new UiSelector().className(\"android.widget.TextView\").instance(6)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"How can you think of calling quits without ever doing marketing?Stop developing and start selling.Also your price is too low.Advertise on Facebook. Target business professionals.\")", "new UiSelector().className(\"android.widget.TextView\").instance(15)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"How can you think of calling quits without ever doing marketing?Stop developing and start selling.Also your price is too low.Advertise on Facebook. Target business professionals.\")", "new UiSelector().className(\"android.widget.TextView\").instance(15)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"How can you think of calling quits without ever doing marketing?Stop developing and start selling.Also your price is too low.Advertise on Facebook. Target business professionals.\")", "new UiSelector().className(\"android.widget.TextView\").instance(15)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"+1\")", "new UiSelector().className(\"android.widget.TextView\").instance(6)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Show Link\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"OK\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Open\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"69 comments\")", "new UiSelector().className(\"android.widget.TextView\").instance(25)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Open links in system browser\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Comment Text size\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Large\")", "new UiSelector().className(\"android.widget.CheckedTextView\").instance(2)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_000\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'io.dwak.holohackernews.app'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
