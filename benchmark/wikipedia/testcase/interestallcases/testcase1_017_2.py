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
				end_y=int(height * endyper), duration=2000)
	except WebDriverException:
		time.sleep(1)
	driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),
				end_y=int(height * endyper), duration=2000)
	return

def scrollToFindElement(driver, str) :
	for i in range(0, 5, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str)
		except NoSuchElementException:
			swipe(driver, 0.5, 0.6, 0.5, 0.2)
		else:
			return element
	return

def clickoncheckable(driver, str, value = "true") :
	parents = driver.find_elements_by_class_name("android.widget.LinearLayout")
	for parent in parents:
		try :
			parent.find_element_by_android_uiautomator(str)
			lists = parent.find_elements_by_class_name("android.widget.LinearLayout")
			if (len(lists) == 1) :
				innere = parent.find_element_by_android_uiautomator("new UiSelector().checkable(true)")
				nowvalue = innere.get_attribute("checked")
				if (nowvalue != value) :
					innere.click()
				break
		except NoSuchElementException:
			continue

# preference setting and exit
try :
	os.popen("adb shell svc data diable")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n org.wikipedia/org.wikipedia.settings.DeveloperSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"useRestbase_setManually\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"useRestbase_setManually\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"mediaWikiBaseUriSupportsLangCode\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"mediaWikiBaseUriSupportsLangCode\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"suppressNotificationPolling\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"suppressNotificationPolling\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"memoryLeakTest\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"memoryLeakTest\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"tocTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"tocTutorialEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"readingListLoginReminder\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"readingListLoginReminder\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"readingListsFirstTimeSync\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"readingListsFirstTimeSync\")", "false")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n org.wikipedia/org.wikipedia.settings.SettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Show link previews\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show link previews\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Download only over Wi-Fi\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Download only over Wi-Fi\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show images\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show images\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Prefer offline content\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Prefer offline content\")", "true")

	driver.press_keycode(4)
	time.sleep(2)

except Exception, e:
	print 'FAIL'
	print 'str(e):\t\t', str(e)
	print 'repr(e):\t', repr(e)
	print traceback.format_exc()
finally :
	endtime = time.time()
	print 'consumed time:', str(endtime - starttime), 's'
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_017_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase017
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/voice_search_button\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Got it\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/view_static_card_icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"Table of Contents\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/page_toolbar_button_search\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememtBack(driver, "new UiSelector().text(\"русский\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/recent_searches_delete_button\").className(\"android.widget.ImageView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/search_src_text\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Search");
	element = getElememtBack(driver, "new UiSelector().text(\"more\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Add language\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/menu_search_language\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/search_src_text\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Search for a language");
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/action_mode_close_button\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.wikipedia:id/menu_search_language\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	driver.press_keycode(4)
	element = getElememtBack(driver, "new UiSelector().text(\"Russian\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_017\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'org.wikipedia'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
	os.popen("adb shell svc data enable")
