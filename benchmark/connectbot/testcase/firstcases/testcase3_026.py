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
	'appPackage' : 'org.connectbot',
	'appActivity' : 'org.connectbot.HostListActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.connectbot/org.connectbot.JacocoInstrumentation',
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

# testcase026
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Delete host\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Cancel\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Connecting to 12est:23 v\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $[F/system/bin/sh: [F: not found127|u0_a55@generic_x86:/ $ [F\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Navigate up\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $u0_a55@generic_x86:/ $[F/system/bin/sh: [F: not found127|u0_a55@generic_x86:/ $ [5~\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"u0_a55@generic_x86:/ $ 12stt/system/bin/sh: 12stt: not found127|u0_a55@generic_x86:/ $127|u0_a55@generic_x86:/ $ 12stt\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"org.connectbot:id/button_right\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememtBack(driver, "new UiSelector().text(\"testt\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Connecting to 12:12 via telnet\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"End\")", "new UiSelector().className(\"android.widget.Button\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Connecting to 12est:23 v\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Ctrl\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	driver.press_keycode(4)
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/icon\").className(\"android.widget.ImageView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Delete host\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_026\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'org.connectbot'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)