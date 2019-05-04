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
	'appPackage' : 'io.github.hidroh.materialistic',
	'appActivity' : 'io.github.hidroh.materialistic.LauncherActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'io.github.hidroh.materialistic/io.github.hidroh.materialistic.JacocoInstrumentation',
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
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n io.github.hidroh.materialistic/io.github.hidroh.materialistic.SettingsActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Display\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Auto day-night mode\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Auto day-night mode\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Use external browser\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Use external browser\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Text size\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Extra small\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"List\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Auto mark as viewed\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Auto mark as viewed\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Swipe left to\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Save\")").click()

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"6_011_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase011
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(13)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(21)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Refresh\")", "new UiSelector().className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(23)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(29)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"io.github.hidroh.materialistic:id/button_more\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(17)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(18)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(15)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(18)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(19)")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(29)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"io.github.hidroh.materialistic:id/button_more\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"...\")", "new UiSelector().className(\"android.widget.TextView\").instance(27)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"io.github.hidroh.materialistic:id/button_more\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).long_press(element).release().perform()
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"6_011\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'io.github.hidroh.materialistic'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)