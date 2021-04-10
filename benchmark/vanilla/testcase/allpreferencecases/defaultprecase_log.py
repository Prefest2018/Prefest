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
	'appPackage' : 'ch.blinkenlights.android.vanilla',
	'appActivity' : 'ch.blinkenlights.android.vanilla.LibraryActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'ch.blinkenlights.android.vanilla/ch.blinkenlights.android.vanilla.JacocoInstrumentation',
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
	os.popen("adb shell am start -n ch.blinkenlights.android.vanilla/ch.blinkenlights.android.vanilla.PreferencesActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Audio\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Volume during notification\")")
	testingSeekBar(driver, "new UiSelector().text(\"Volume during notification\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Headset/Bluetooth controls\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Headset/Bluetooth controls\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Headset control beep\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Headset control beep\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"External output only\")")
	clickOnCheckable(driver, "new UiSelector().text(\"External output only\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Pause when unplugged\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Pause when unplugged\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Cover art\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Load artwork from folder\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Load artwork from folder\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Load artwork from hidden folder\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Load artwork from hidden folder\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Load artwork from Android\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Load artwork from Android\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Load artwork from file tags\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Load artwork from file tags\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show artwork on lockscreen\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show artwork on lockscreen\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Library screen\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Default action\")")
	clickInList(driver, "new UiSelector().text(\"Expand or play all\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Default playlist action\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Scroll to track title\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Scroll to track title\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Scroll to track title in queue\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Scroll to track title in queue\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Jump to enqueued song on \'Play\'\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Jump to enqueued song on \'Play\'\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Miscellaneous features\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Disable lockscreen\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Disable lockscreen\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep screen on\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep screen on\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable idle timeout\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable idle timeout\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Double tap widget\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Double tap widget\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Scrobble Droid API\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Scrobble Droid API\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Emulate stock broadcasts\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Emulate stock broadcasts\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable readahead\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable readahead\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Notifications\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Notification visibility\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Notification action\")")
	clickInList(driver, None)
	scrollToClickElement(driver, "new UiSelector().text(\"Very verbose notification\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Very verbose notification\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Playback screen\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Open on startup\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Open on startup\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Display mode\")")
	clickInList(driver, "new UiSelector().text(\"Info fixed at top\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe up action\")")
	clickInList(driver, "new UiSelector().text(\"Do nothing\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe down action\")")
	clickInList(driver, "new UiSelector().text(\"Do nothing\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Cover tap action\")")
	clickInList(driver, "new UiSelector().text(\"Toggle controls\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Cover long press action\")")
	clickInList(driver, "new UiSelector().text(\"Play/Pause\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Playlists\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic playlist creation\")")
	testingSeekBar(driver, "new UiSelector().text(\"Automatic playlist creation\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Playlist synchronization\")")
	clickInList(driver, None)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Accelerometer shake\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable accelerometer shake\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable accelerometer shake\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Shake action\")")
	clickInList(driver, "new UiSelector().text(\"Next track\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Shake force threshold\")")
	testingSeekBar(driver, "new UiSelector().text(\"Shake force threshold\")", 0.5)

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
