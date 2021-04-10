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
	'appPackage' : 'de.danoeh.antennapod',
	'appActivity' : 'de.danoeh.antennapod.activity.SplashActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'de.danoeh.antennapod/de.danoeh.antennapod.JacocoInstrumentation',
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
def settingPref_prefAutoUpdateIntervall(driver, value):
	try :
		element = getElememtBack(driver, "new UiSelector().text(\"Set Interval\")", "new UiSelector()")
		TouchAction(driver).tap(element).perform()
		if value == "1":
			element = getElememtBack(driver, "new UiSelector().text(\"1 hour\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
	except NoSuchElementException:
		time.sleep(1)
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n de.danoeh.antennapod/de.danoeh.antennapod.activity.PreferenceActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Network\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic Download\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Automatic Download\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Automatic Download\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Episode Cache\")")
	clickInList(driver, "new UiSelector().text(\"100\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Episode Cleanup\")")
	clickInList(driver, "new UiSelector().text(\"7\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Download when not charging\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Download when not charging\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Download on mobile connection\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Download on mobile connection\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable Wi-Fi filter\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable Wi-Fi filter\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Mobile Updates\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Mobile Updates\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Download Report\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Download Report\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Playback\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Headphones Disconnect\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Headphones Disconnect\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Headphones Reconnect\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Headphones Reconnect\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Bluetooth Reconnect\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Bluetooth Reconnect\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Pause for Interruptions\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Pause for Interruptions\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Resume after Call\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Resume after Call\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Upon exiting video\")")
	clickInList(driver, "new UiSelector().text(\"Picture-in-picture mode\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Forward Button Skips\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Forward Button Skips\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Previous button restarts\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Previous button restarts\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Enqueue Downloaded\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enqueue Downloaded\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enqueue at Front\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enqueue at Front\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Continuous Playback\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Continuous Playback\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Smart Mark as Played\")")
	clickInList(driver, "new UiSelector().text(\"300\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep Skipped Episodes\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep Skipped Episodes\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Media player\")")
	clickInList(driver, "new UiSelector().text(\"ExoPlayer\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Chromecast support\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Chromecast support\")", "true")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Storage\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Image Cache Size\")")
	clickInList(driver, "new UiSelector().text(\"500 MiB\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Auto Delete\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Auto Delete\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Keep Favorite Episodes\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Keep Favorite Episodes\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"User Interface\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Select Theme\")")
	clickInList(driver, "new UiSelector().text(\"Dark\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Set Subscription Order\")")
	clickInList(driver, "new UiSelector().text(\"Sort alphabetically\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Set Subscription Counter\")")
	clickInList(driver, "new UiSelector().text(\"Number of new episodes\")")
	scrollToClickElement(driver, "new UiSelector().text(\"High Notification priority\")")
	clickOnCheckable(driver, "new UiSelector().text(\"High Notification priority\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Persistent Playback Controls\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Persistent Playback Controls\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Set Lockscreen Background\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Set Lockscreen Background\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Back Button Behavior\")")
	clickInList(driver, "new UiSelector().text(\"Double tap to exit\")")

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
