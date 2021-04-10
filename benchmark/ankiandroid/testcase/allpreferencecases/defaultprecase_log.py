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
	'appPackage' : 'com.ichi2.anki',
	'appActivity' : 'com.ichi2.anki.IntentHandler',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.ichi2.anki/com.ichi2.anki.JacocoInstrumentation',
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
	os.popen("adb shell am start -n com.ichi2.anki/com.ichi2.anki.FilteredDeckOptions -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Reschedule\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Reschedule\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Define custom steps\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Define custom steps\")", "true")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n com.ichi2.anki/com.ichi2.anki.Preferences -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Advanced\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Max number of backups\")")
	testingSeekBar(driver, "new UiSelector().text(\"Max number of backups\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Safe display mode\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Safe display mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Type answer into the card\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Type answer into the card\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Fix for Hebrew vowels\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Fix for Hebrew vowels\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable AnkiDroid API\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable AnkiDroid API\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Text to speech\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Text to speech\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Lookup dictionary\")")
	clickInList(driver, "new UiSelector().text(\"None\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Chess notation support\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Chess notation support\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"eReader\")")
	clickOnCheckable(driver, "new UiSelector().text(\"eReader\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Double scrolling\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Double scrolling\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"HTML / Javascript Debugging\")")
	clickOnCheckable(driver, "new UiSelector().text(\"HTML / Javascript Debugging\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Advanced statistics\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable advanced statistics\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable advanced statistics\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Compute first n days, simulate remainder\")")
	testingSeekBar(driver, "new UiSelector().text(\"Compute first n days, simulate remainder\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Precision of computation\")")
	testingSeekBar(driver, "new UiSelector().text(\"Precision of computation\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Number of iterations of the simulation\")")
	testingSeekBar(driver, "new UiSelector().text(\"Number of iterations of the simulation\")", 0.5)
	time.sleep(1)
	driver.press_keycode(4)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Appearance\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Day theme\")")
	clickInList(driver, "new UiSelector().text(\"Light\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Night theme\")")
	clickInList(driver, "new UiSelector().text(\"Black\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Default font applicability\")")
	clickInList(driver, "new UiSelector().text(\"When no font specified on flashcards\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Card browser font scaling\")")
	testingSeekBar(driver, "new UiSelector().text(\"Card browser font scaling\")", 0.5)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Reviewing\")")
	scrollToClickElement(driver, "new UiSelector().text(\"App bar buttons\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Undo\")")
	clickInList(driver, "new UiSelector().text(\"Always show\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Scheduling\")")
	clickInList(driver, "new UiSelector().text(\"Always show\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Mark note\")")
	clickInList(driver, "new UiSelector().text(\"Always show\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Edit note\")")
	clickInList(driver, "new UiSelector().text(\"Show if room\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Add note\")")
	clickInList(driver, "new UiSelector().text(\"Disabled\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Replay audio\")")
	clickInList(driver, "new UiSelector().text(\"Show if room\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Set TTS language\")")
	clickInList(driver, "new UiSelector().text(\"Menu only\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Options\")")
	clickInList(driver, "new UiSelector().text(\"Menu only\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Bury\")")
	clickInList(driver, "new UiSelector().text(\"Menu only\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Suspend\")")
	clickInList(driver, "new UiSelector().text(\"Menu only\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Delete note\")")
	clickInList(driver, "new UiSelector().text(\"Menu only\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide whiteboard\")")
	clickInList(driver, "new UiSelector().text(\"Always show\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Clear whiteboard\")")
	clickInList(driver, "new UiSelector().text(\"Show if room\")")
	time.sleep(1)
	driver.press_keycode(4)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Advanced\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Custom sync server\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Use custom sync server\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use custom sync server\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"AnkiDroid\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fetch media on sync\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Fetch media on sync\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic synchronization\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Automatic synchronization\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Deck for new cards\")")
	clickInList(driver, "new UiSelector().text(\"Use current deck\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Share feature usage\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Share feature usage\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Error reporting mode\")")
	clickInList(driver, "new UiSelector().text(\"Ask me\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Notify when\")")
	clickInList(driver, "new UiSelector().text(\"Pending messages available\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Vibrate\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Vibrate\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Blink light\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Blink light\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Gestures\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable gestures\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable gestures\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe sensitivity\")")
	testingSeekBar(driver, "new UiSelector().text(\"Swipe sensitivity\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe up\")")
	clickInList(driver, "new UiSelector().text(\"No action\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe down\")")
	clickInList(driver, "new UiSelector().text(\"No action\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe left\")")
	clickInList(driver, "new UiSelector().text(\"Answer recommended (green)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe right\")")
	clickInList(driver, "new UiSelector().text(\"Answer button 1\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Double touch\")")
	clickInList(driver, "new UiSelector().text(\"No action\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Touch top\")")
	clickInList(driver, "new UiSelector().text(\"Show answer\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Touch bottom\")")
	clickInList(driver, "new UiSelector().text(\"Show answer\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Touch left\")")
	clickInList(driver, "new UiSelector().text(\"Show answer\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Touch right\")")
	clickInList(driver, "new UiSelector().text(\"Show answer\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Reviewing\")")
	scrollToClickElement(driver, "new UiSelector().text(\"New card position\")")
	clickInList(driver, "new UiSelector().text(\"Mix new cards and reviews\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Start of next day\")")
	testingSeekBar(driver, "new UiSelector().text(\"Start of next day\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Keep screen on\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep screen on\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Fullscreen mode\")")
	clickInList(driver, "new UiSelector().text(\"Off\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Center align\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Center align\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show button time\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show button time\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Card zoom\")")
	testingSeekBar(driver, "new UiSelector().text(\"Card zoom\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Image zoom\")")
	testingSeekBar(driver, "new UiSelector().text(\"Image zoom\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Answer button size\")")
	testingSeekBar(driver, "new UiSelector().text(\"Answer button size\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Answer buttons position\")")
	clickInList(driver, "new UiSelector().text(\"Bottom\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show remaining\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show remaining\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show ETA\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show ETA\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Stroke width\")")
	testingSeekBar(driver, "new UiSelector().text(\"Stroke width\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Black strokes\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Black strokes\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic display answer\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Automatic display answer\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Time to show answer\")")
	testingSeekBar(driver, "new UiSelector().text(\"Time to show answer\")", 0.5)
	scrollToClickElement(driver, "new UiSelector().text(\"Time to show next question\")")
	testingSeekBar(driver, "new UiSelector().text(\"Time to show next question\")", 0.5)

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n com.ichi2.anki/com.ichi2.anki.DeckOptions -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"New cards\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Bury related new cards\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Bury related new cards\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Reviews\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Bury related reviews\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Bury related reviews\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"General\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show answer timer\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show answer timer\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatically play audio\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Automatically play audio\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Replay question\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Replay question\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Reminders\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable reminder notifications for decks in this deck group\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable reminder notifications for decks in this deck group\")", "false")

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
