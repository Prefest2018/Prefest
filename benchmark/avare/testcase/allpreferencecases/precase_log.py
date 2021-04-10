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
	clickInList(driver, "new UiSelector().text(\"Kitepilot.net\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Chart Download Cycle\")")
	clickInList(driver, "new UiSelector().text(\"Next - Warning! Downloads may fail indefinitely\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"GPS\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Short GPS Update Period\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Short GPS Update Period\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Warn GPS Disabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Warn GPS Disabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Use NMEA Altitude\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use NMEA Altitude\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"GPS Position Source\")")
	clickInList(driver, "new UiSelector().text(\"Internal Only\")")
	scrollToClickElement(driver, "new UiSelector().text(\"AHRS Roll Reverse\")")
	clickOnCheckable(driver, "new UiSelector().text(\"AHRS Roll Reverse\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Weather\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Data Expiry\")")
	clickInList(driver, "new UiSelector().text(\"1440\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use ADSB Weather\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use ADSB Weather\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"AIRMET/SIGMET Type\")")
	clickInList(driver, "new UiSelector().text(\"CONVECTIVE OUTLOOK\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Translate Weather\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Translate Weather\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Label for ADSB METARs\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Label for ADSB METARs\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Winds Aloft Up To Altitude\")")
	clickInList(driver, "new UiSelector().text(\"12\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Application State\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Leave Running\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Leave Running\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Unit\")")
	clickInList(driver, "new UiSelector().text(\"Statute Miles\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Dynamic Fields\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Dynamic Fields\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Blink Screen\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Blink Screen\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Tips\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Tips\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Instrumentation\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Edge Tape\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Edge Tape\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Ring Style\")")
	clickInList(driver, "new UiSelector().text(\"Dynamic\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Distance Ring Colors\")")
	clickInList(driver, "new UiSelector().text(\"Red\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Timer Ring Size\")")
	clickInList(driver, "new UiSelector().text(\"2 Minutes\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fuel Timer Interval\")")
	clickInList(driver, "new UiSelector().text(\"45\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show CDI/VDI\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show CDI/VDI\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"ETA/ETE Bearing\")")
	clickOnCheckable(driver, "new UiSelector().text(\"ETA/ETE Bearing\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Early Passage\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Early Passage\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Early Pass Timer Value\")")
	clickInList(driver, "new UiSelector().text(\"60\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Display\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Track Up\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track Up\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Track Up - Plates\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track Up - Plates\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Pilot Cam\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Pilot Cam\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep Screen On\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep Screen On\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen Orientation\")")
	clickInList(driver, "new UiSelector().text(\"Reverse Portrait\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Night Mode (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Night Mode (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Obstacles\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Obstacles\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Filter ADSB Traffic\")")
	clickInList(driver, "new UiSelector().text(\"5000\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show ADSB Callsigns\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show ADSB Callsigns\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show CAP Grids\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show CAP Grids\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Runway Extensions\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Runway Extensions\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Auto Show Airport Diagram\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Auto Show Airport Diagram\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Destination Course Line\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Destination Course Line\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show All Bases\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show All Bases\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Location Display Icon (requires restart)\")")
	clickInList(driver, "new UiSelector().text(\"Helicopter\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Near Runway Minimum Length\")")
	clickInList(driver, "new UiSelector().text(\"4000\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Select Layer Transparency\")")
	clickInList(driver, "new UiSelector().text(\"50 Percent\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Stadiums\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Stadiums\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Default Chart Supplement\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Default Chart Supplement\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Flight Tracking\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Track History\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Track History\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic Track Post\")")
	clickInList(driver, "new UiSelector().text(\"Send email\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Rubber Banding\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Rubber Banding\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Pilot and Aircraft\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Aircraft Color - First\")")
	clickInList(driver, "new UiSelector().text(\"Blue\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Aircraft Color - Second\")")
	clickInList(driver, "new UiSelector().text(\"Blue\")")
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
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 1 in Plate (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 1 in Plate (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 2 in Plate (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 2 in Plate (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Remove Button 3 in Plate (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Remove Button 3 in Plate (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Flight Plan Controls\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Flight Plan Controls\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Background\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Background\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Status Area on Plates\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Status Area on Plates\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Status Area on 3D\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Status Area on 3D\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Use System Font (requires restart)\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use System Font (requires restart)\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Adjust Font Size (requires restart)\")")
	clickInList(driver, "new UiSelector().text(\"Huge\")")

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
