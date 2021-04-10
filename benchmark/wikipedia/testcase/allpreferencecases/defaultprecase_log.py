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
	'appPackage' : 'org.wikipedia',
	'appActivity' : 'org.wikipedia.main.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.wikipedia/org.wikipedia.JacocoInstrumentation',
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
def settingPref_colorTheme(driver, value):
	try :
		if value == "0":
			element = getElememtBack(driver, "new UiSelector().text(\"Light\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "1":
			element = getElememtBack(driver, "new UiSelector().text(\"Dark\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		elif value == "2":
			element = getElememtBack(driver, "new UiSelector().text(\"Black\")", "new UiSelector()")
			TouchAction(driver).tap(element).perform()
		driver.press_keycode(4)
	except NoSuchElementException:
		time.sleep(1)
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.wikipedia/org.wikipedia.settings.DeveloperSettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"useRestbase_setManually\")")
	clickOnCheckable(driver, "new UiSelector().text(\"useRestbase_setManually\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"useRestbase\")")
	clickOnCheckable(driver, "new UiSelector().text(\"useRestbase\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"mediaWikiBaseUriSupportsLangCode\")")
	clickOnCheckable(driver, "new UiSelector().text(\"mediaWikiBaseUriSupportsLangCode\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Retrofit Log Level (restart required)\")")
	clickInList(driver, "new UiSelector().text(\"HEADERS\")")
	scrollToClickElement(driver, "new UiSelector().text(\"suppressNotificationPolling\")")
	clickOnCheckable(driver, "new UiSelector().text(\"suppressNotificationPolling\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"showDeveloperSettings\")")
	clickOnCheckable(driver, "new UiSelector().text(\"showDeveloperSettings\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"memoryLeakTest\")")
	clickOnCheckable(driver, "new UiSelector().text(\"memoryLeakTest\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"crashedBeforeActivityCreated\")")
	clickOnCheckable(driver, "new UiSelector().text(\"crashedBeforeActivityCreated\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"initialOnboardingEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"initialOnboardingEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"descriptionEditTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"descriptionEditTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"tocTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"tocTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"selectTextTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"selectTextTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"shareTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"shareTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"multilingualSearchTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"multilingualSearchTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"readingListTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"readingListTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"readingListSyncReminder\")")
	clickOnCheckable(driver, "new UiSelector().text(\"readingListSyncReminder\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"readingListLoginReminder\")")
	clickOnCheckable(driver, "new UiSelector().text(\"readingListLoginReminder\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"zeroTutorialEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"zeroTutorialEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"feedCustomizeOnboardingCardEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"feedCustomizeOnboardingCardEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"feedReadingListsSyncOnboardingCardEnabled\")")
	clickOnCheckable(driver, "new UiSelector().text(\"feedReadingListsSyncOnboardingCardEnabled\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"showReadingListsSyncPrompt\")")
	clickOnCheckable(driver, "new UiSelector().text(\"showReadingListsSyncPrompt\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"readingListsFirstTimeSync\")")
	clickOnCheckable(driver, "new UiSelector().text(\"readingListsFirstTimeSync\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"showRemoveChineseVariantPrompt\")")
	clickOnCheckable(driver, "new UiSelector().text(\"showRemoveChineseVariantPrompt\")", "false")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n org.wikipedia/org.wikipedia.settings.SettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Show link previews\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show link previews\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Reading list syncing\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Reading list syncing\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Download only over Wi-Fi\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Download only over Wi-Fi\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show images\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show images\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Prefer offline content\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Prefer offline content\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Send usage reports\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Send usage reports\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Send crash reports\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Send crash reports\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Warn if leaving Wikipedia Zero\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Warn if leaving Wikipedia Zero\")", "true")

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
