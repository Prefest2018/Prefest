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
	'appPackage' : 'org.thoughtcrime.securesms',
	'appActivity' : 'org.thoughtcrime.securesms.ConversationListActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.thoughtcrime.securesms/org.thoughtcrime.securesms.JacocoInstrumentation',
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
			elements = driver.find_elements_by_android_uiautomator(str)
			if (len(elements) > 1) :
				for temp in elements :
					if temp.get_attribute("enabled") == "true" :
						element = temp
						break
		except NoSuchElementException:
			swipe(driver, 0.5, 0.55, 0.5, 0.2)
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

def conscript(driver):
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Enable local backups?\")")
		element = driver.find_element_by_android_uiautomator("new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/confirmation_check\")")
		element.click()
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Enable backups\")")
		element.click()
	except NoSuchElementException:
		time.sleep(0.1)
	try:
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Delete backups?\")")
		element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Delete backups\")")
		element.click()
	except NoSuchElementException:
		time.sleep(0.1)

# preference setting and exit
try :
	os.popen("adb shell svc data diable")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n org.thoughtcrime.securesms/org.thoughtcrime.securesms.ApplicationPreferencesActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Privacy\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Screen lock\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Screen lock\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Read receipts\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Read receipts\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Typing indicators\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Typing indicators\")", "false")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Chats and media\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Use system emoji\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Use system emoji\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Conversation length limit\")").click()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("-2147483648")
	getElememt(driver, "new UiSelector().text(\"OK\")").click()
	conscript(driver)

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_017_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase017
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/search_action\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/search_action\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/search_action\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"R322\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/quick_camera_toggle\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/camera_capture_button\").className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/scribble_send_button\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Insecure SMS\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/scribble_draw_button\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/scribble_send_button\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
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
	if (cpackage != 'org.thoughtcrime.securesms'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
	os.popen("adb shell svc data enable")
