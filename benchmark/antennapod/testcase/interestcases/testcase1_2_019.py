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

	os.popen("adb shell am start -n de.danoeh.antennapod/de.danoeh.antennapod.activity.PreferenceActivity -a test")
	scrollToFindElement(driver, "new UiSelector().text(\"Network\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Automatic Download\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Automatic Download\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Automatic Download\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Episode Cleanup\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"1\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Download on mobile connection\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Download on mobile connection\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Mobile Updates\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Mobile Updates\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Download Report\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Download Report\")", "false")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Playback\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Enqueue at Front\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Enqueue at Front\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"User Interface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Select Theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Black (AMOLED ready)\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Set Subscription Order\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Sort alphabetically\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Set Subscription Counter\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Number of downloaded episodes\")").click()

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
	os.popen("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_019_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase019
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Open menu\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Subscriptions\")", "new UiSelector().className(\"android.widget.TextView\").instance(6)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"de.danoeh.antennapod:id/imgvCover\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"de.danoeh.antennapod:id/refresh_item\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememt(driver, "new UiSelector().resourceId(\"de.danoeh.antennapod:id/butSecondaryAction\").className(\"android.widget.ImageButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Enqueue\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"de.danoeh.antennapod:id/refresh_item\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	swipe(driver, 0.5, 0.2, 0.5, 0.8)
	element = getElememtBack(driver, "new UiSelector().text(\"Monstruo Sneak Preview\")", "new UiSelector().className(\"android.widget.TextView\").instance(10)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Monstruo\")", "new UiSelector().className(\"android.widget.TextView\").instance(2)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Monstruo\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"de.danoeh.antennapod:id/search_button\").className(\"android.widget.ImageView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Navigate up\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Monstruo\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"de.danoeh.antennapod:id/search_button\").className(\"android.widget.ImageView\")")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_019\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'de.danoeh.antennapod'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
	os.popen("adb shell svc data enable")
