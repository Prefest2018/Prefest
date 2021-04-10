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
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n com.ds.avare/com.ds.avare.PrefActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Storage and Downloads\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Charts Download Server\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Chart Download Cycle\")")
	clickInList(driver, None)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"GPS\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Short GPS Update Period\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Short GPS Update Period\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Warn GPS Disabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Warn GPS Disabled\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use NMEA Altitude\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use NMEA Altitude\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"GPS Position Source\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"AHRS Roll Reverse\")")
	clickOnCheckable(driver, "new UiSelector().text(\"AHRS Roll Reverse\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Weather\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Data Expiry\")")
	clickInList(driver, "new UiSelector().text(\"360\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use ADSB Weather\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use ADSB Weather\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"AIRMET/SIGMET Type\")")
	clickInList(driver, "new UiSelector().text(\"ALL\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Translate Weather\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Translate Weather\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Label for ADSB METARs\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Label for ADSB METARs\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Winds Aloft Up To Altitude\")")
	clickInList(driver, "new UiSelector().text(\"39\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Application State\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Leave Running\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Leave Running\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Unit\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Use Dynamic Fields\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Dynamic Fields\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Blink Screen\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Blink Screen\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Tips\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Tips\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Instrumentation\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Edge Tape\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Edge Tape\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Ring Style\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Ring Colors\")")
	clickInList(driver, "new UiSelector().text(\"Default\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Timer Ring Size\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Fuel Timer Interval\")")
	clickInList(driver, "new UiSelector().text(\"30\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show CDI/VDI\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show CDI/VDI\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"ETA/ETE Bearing\")")
	clickOnCheckable(driver, "new UiSelector().text(\"ETA/ETE Bearing\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Early Passage\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Early Passage\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Early Pass Timer Value\")")
	clickInList(driver, "new UiSelector().text(\"30\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Display\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Track Up\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track Up\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Track Up - Plates\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track Up - Plates\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Pilot Cam\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Pilot Cam\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep Screen On\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep Screen On\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen Orientation\")")
	clickInList(driver, "new UiSelector().text(\"Portrait\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Night Mode (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Night Mode (requires restart)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Obstacles\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Obstacles\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Filter ADSB Traffic\")")
	clickInList(driver, "new UiSelector().text(\"100000\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show ADSB Callsigns\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show ADSB Callsigns\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show CAP Grids\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show CAP Grids\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Runway Extensions\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Runway Extensions\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Auto Show Airport Diagram\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Auto Show Airport Diagram\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Destination Course Line\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Destination Course Line\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show All Bases\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show All Bases\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Location Display Icon (requires restart)\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Near Runway Minimum Length\")")
	clickInList(driver, "new UiSelector().text(\"6000\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Select Layer Transparency\")")
	clickInList(driver, "new UiSelector().text(\"Opaque\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Stadiums\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Stadiums\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Default Chart Supplement\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Default Chart Supplement\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Flight Tracking\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Track History\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track History\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic Track Post\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Use Rubber Banding\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Rubber Banding\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Pilot and Aircraft\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Aircraft Color - First\")")
	clickInList(driver, "new UiSelector().text(\"Amber\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Aircraft Color - Second\")")
	clickInList(driver, "new UiSelector().text(\"Amber\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"UI Configuration\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Tab Bar Content (requires restart)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Near\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Near\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"PFD\")")
	clickOnCheckable(driver, "new UiSelector().text(\"PFD\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"3D\")")
	clickOnCheckable(driver, "new UiSelector().text(\"3D\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"List\")")
	clickOnCheckable(driver, "new UiSelector().text(\"List\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"FAA\")")
	clickOnCheckable(driver, "new UiSelector().text(\"FAA\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Tools\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Tools\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"W&B\")")
	clickOnCheckable(driver, "new UiSelector().text(\"W&B\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 1 in Map (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 1 in Map (requires restart)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 1 in Plate (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 1 in Plate (requires restart)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 2 in Plate (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 2 in Plate (requires restart)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 3 in Plate (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 3 in Plate (requires restart)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Flight Plan Controls\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Flight Plan Controls\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Background\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Background\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Status Area on Plates\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Status Area on Plates\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Status Area on 3D\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Status Area on 3D\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use System Font (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use System Font (requires restart)\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Adjust Font Size (requires restart)\")")
	clickInList(driver, "new UiSelector().text(\"Normal\")")

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
