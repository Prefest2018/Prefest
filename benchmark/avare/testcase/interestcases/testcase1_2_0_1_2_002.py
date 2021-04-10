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
	'appPackage' : 'com.ds.avare',
	'appActivity' : 'com.ds.avare.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.ds.avare/com.ds.avare.JacocoInstrumentation',
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
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.ds.avare/com.ds.avare.PrefActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"GPS\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Short GPS Update Period\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Short GPS Update Period\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Warn GPS Disabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Warn GPS Disabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"External WiFi Port\")")
	typeText(driver,"")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Weather\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use ADSB Weather\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use ADSB Weather\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Application State\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Unit\")")
	clickInList(driver, "new UiSelector().text(\"Statute Miles\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Dynamic Fields\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Dynamic Fields\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Tips\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Tips\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Instrumentation\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Edge Tape\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Edge Tape\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show CDI/VDI\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show CDI/VDI\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Display\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep Screen On\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep Screen On\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen Orientation\")")
	clickInList(driver, "new UiSelector().text(\"Landscape\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Night Mode (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Night Mode (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Obstacles\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Obstacles\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show CAP Grids\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show CAP Grids\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Runway Extensions\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Runway Extensions\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Location Display Icon (requires restart)\")")
	clickInList(driver, "new UiSelector().text(\"Helicopter\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Stadiums\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Stadiums\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Default Chart Supplement\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Default Chart Supplement\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Flight Tracking\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Track History\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track History\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Rubber Banding\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Rubber Banding\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"UI Configuration\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Tab Bar Content (requires restart)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Near\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Near\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"PFD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"PFD\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"3D\")")
	clickOnCheckable(driver, "new UiSelector().text(\"3D\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"List\")")
	clickOnCheckable(driver, "new UiSelector().text(\"List\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"FAA\")")
	clickOnCheckable(driver, "new UiSelector().text(\"FAA\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Tools\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Tools\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"W&B\")")
	clickOnCheckable(driver, "new UiSelector().text(\"W&B\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 1 in Map (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 1 in Map (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Flight Plan Controls\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Flight Plan Controls\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Background\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Background\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Status Area on Plates\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Status Area on Plates\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use System Font (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use System Font (requires restart)\")", "true")

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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"0_1_2_002_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
# testcase2_002
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"Menu\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Tracks Off\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Preferences\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"Plate\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Timer\")", "new UiSelector().className(\"android.widget.Button\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"AP\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ds.avare:id/plates_button_center\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Map\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"CSup\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Location ID\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"12AZ\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"12AZ\")", "new UiSelector().className(\"android.widget.CheckedTextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
	element = getElememtBack(driver, "new UiSelector().text(\"DT: 0,Elev: 1840.0,Surf: GRAVEL,Ptrn: Right,ALS: No,ILS: No,VGSI: No\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Find\")", "new UiSelector().className(\"android.widget.TextView\").instance(15)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Map\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ds.avare:id/location_button_center\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ds.avare:id/location_button_center\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).long_press(element).release().perform()
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_002\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.ds.avare'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
	os.popen("adb shell settings put secure location_providers_allowed gps,network")
	os.popen("adb shell settings put secure location_providers_allowed gps,network")
