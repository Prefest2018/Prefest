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
	'appPackage' : 'paulscode.android.mupen64plusae',
	'appActivity' : 'paulscode.android.mupen64plusae.SplashActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.JacocoInstrumentation',
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
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.DisplayPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Rendered resolution\")")
	clickInList(driver, "new UiSelector().text(\"1440 x 1080\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Zoom\")")
	testingSeekBar(driver, "new UiSelector().text(\"Zoom\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Screen scaling\")")
	clickInList(driver, "new UiSelector().text(\"Stretch\nFill screen, no black bars (adds distortion)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen orientation\")")
	clickInList(driver, "new UiSelector().text(\"Portrait\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Immersive mode\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Immersive mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Action bar opacity\")")
	testingSeekBar(driver, "new UiSelector().text(\"Action bar opacity\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Framerate\")")
	clickInList(driver, "new UiSelector().text(\"Bottom left\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fix upside down screen\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Fix upside down screen\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"GLideN64 Threaded video\")")
	clickOnCheckable(driver, "new UiSelector().text(\"GLideN64 Threaded video\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Flicker reduction\")")
	clickInList(driver, "new UiSelector().text(\"O-1 profile\")")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.InputPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Accessibility\")")
	clickInList(driver, "new UiSelector().text(\"Standard mode\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show in-game menu\")")
	clickInList(driver, "new UiSelector().text(\"When slide gesture is used\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use high priority thread\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use high priority thread\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Mappable volume keys\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mappable volume keys\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Mappable back key\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mappable back key\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Mappable menu key\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mappable menu key\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Raphnet adapter raw USB access\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Raphnet adapter raw USB access\")", "true")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.DefaultsPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic player mapping\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Automatic player mapping\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"All emulated controllers are always attached\")")
	clickOnCheckable(driver, "new UiSelector().text(\"All emulated controllers are always attached\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Share controller\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Share controller\")", "true")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.TouchscreenPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Button scale\")")
	testingSeekBar(driver, "new UiSelector().text(\"Button scale\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Button opacity\")")
	testingSeekBar(driver, "new UiSelector().text(\"Button opacity\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Haptic feedback\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Haptic feedback\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Animate joystick\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Animate joystick\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Button style\")")
	clickInList(driver, "new UiSelector().text(\"Outlines\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Button auto-hold\")")
	clickInList(driver, "new UiSelector().text(\"On long-press\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Relative touchscreen joystick\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Relative touchscreen joystick\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide buttons when not used\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide buttons when not used\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Number of seconds before hiding touchscreen buttons\")")
	testingSeekBar(driver, "new UiSelector().text(\"Number of seconds before hiding touchscreen buttons\")", 0.5)

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.AudioPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Audio plugin\")")
	clickInList(driver, "new UiSelector().text(\"None (audio disabled)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Time stretch audio\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Time stretch audio\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Audio buffer number\")")
	testingSeekBar(driver, "new UiSelector().text(\"Audio buffer number\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Sampling Rate\")")
	clickInList(driver, "new UiSelector().text(\"Best for hardware\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Sampling Type\")")
	clickInList(driver, "new UiSelector().text(\"SoundTouch\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Floating Point Samples\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Floating Point Samples\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Synchronize audio and video\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Synchronize audio and video\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Swap audio channels\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Swap audio channels\")", "true")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.LibraryPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Recently played\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Recently played\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Cache recent games\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Cache recent games\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Sort\")")
	clickInList(driver, "new UiSelector().text(\"File name\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Full names\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Full names\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Cover art scale\")")
	testingSeekBar(driver, "new UiSelector().text(\"Cover art scale\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Game country filter\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Unknown\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Unknown\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"USA\")")
	clickOnCheckable(driver, "new UiSelector().text(\"USA\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Japan\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Japan\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Europe\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Europe\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Australia\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Australia\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Korea\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Korea\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Germany\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Germany\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"France\")")
	clickOnCheckable(driver, "new UiSelector().text(\"France\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Italy\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Italy\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Spain\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Spain\")", "false")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n paulscode.android.mupen64plusae/paulscode.android.mupen64plusae.persistent.DataPrefsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Max auto saves per game\")")
	testingSeekBar(driver, "new UiSelector().text(\"Max auto saves per game\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Use flat game data folder structure\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use flat game data folder structure\")", "true")

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
