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
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n org.thoughtcrime.securesms/org.thoughtcrime.securesms.ApplicationPreferencesActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Privacy\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Screen lock\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Screen lock\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Inactivity timeout passphrase\")").click()
	conscript(driver)
	clickoncheckable(driver, "new UiSelector().text(\"Inactivity timeout passphrase\")", "true")

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_008_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase008
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"133\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/toolbar_icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageView\").description(\"More options\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/sms_failed_indicator\").className(\"android.widget.ImageView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"R322\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/fab\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/search_view\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Enter name or number");
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Navigate up\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"4 min\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Your version of Signal has expired!\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_008\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'org.thoughtcrime.securesms'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)