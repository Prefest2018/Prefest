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
	'appPackage' : 'io.dwak.holohackernews.app',
	'appActivity' : 'io.dwak.holohackernews.app.ui.storylist.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'io.dwak.holohackernews.app/io.dwak.holohackernews.app.JacocoInstrumentation',
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

# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)

	os.popen("adb shell am start -n io.dwak.holohackernews.app/io.dwak.holohackernews.app.ui.settings.SettingsActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Show link first\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show link first\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Open links in system browser\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Open links in system browser\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Night Mode\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Night Mode\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Swipe back to list\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Swipe back to list\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Comment Text size\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Large\")").click()

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_021_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase021
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\" | devonzuegel.com | \")", "new UiSelector().className(\"android.widget.TextView\").instance(11)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2 pavlov\")", "new UiSelector().className(\"android.widget.TextView\").instance(13)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"The note about the Brunswick Centre is interesting. This odd space-age utopian building was in much worse shape until a £20M renovation in 2006, with The Guardian characterizing it as one of the “most miserable places in London”: https://www.google.co.uk/amp/s/amp.theguardian.com/artanddes...In that article, the architect mentions he originally wanted it built out of brick in the style of Alvar Aalto, but they had to settle for painted concrete. In 1970 even the paint job was cut from the budget and wasn’t done until the renovation, so for 36 years the Brunswick was a brutalist bunker.Im glad they fixed it. It’s an interesting landmark of British modernism’s fling with low-key sci-fi architecture — and the grocery store is good, I go there all the time.\")", "new UiSelector().className(\"android.widget.TextView\").instance(17)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"26 minutes ago\")", "new UiSelector().className(\"android.widget.TextView\").instance(15)")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
	element = getElememt(driver, "new UiSelector().resourceId(\"io.dwak.holohackernews.app:id/action_1\").className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Show Link\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_021\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'io.dwak.holohackernews.app'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)