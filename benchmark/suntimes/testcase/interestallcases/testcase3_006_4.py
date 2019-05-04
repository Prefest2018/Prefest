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
	'appPackage' : 'com.forrestguice.suntimeswidget',
	'appActivity' : 'com.forrestguice.suntimeswidget.SuntimesActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.JacocoInstrumentation',
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

	os.popen("adb shell am start -n com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.SuntimesSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"General Settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Time Format\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"System format\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show Seconds\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Seconds\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Time (with dates)\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Time (with dates)\")", "true")
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"Language Settings\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Mode\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"System\")").click()
	driver.press_keycode(4)
	scrollToFindElement(driver, "new UiSelector().text(\"User Interface\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Theme:\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Light Theme\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Show Blue Hour\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Blue Hour\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Golden Hour\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Golden Hour\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Moon\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Moon\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Warnings\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Warnings\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Weeks\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Weeks\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show Hours\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show Hours\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Verbose TalkBack\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Verbose TalkBack\")", "false")

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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_006_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()

# testcase006
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"Feb\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2018\")", "new UiSelector().className(\"android.widget.Button\").instance(5)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"11\")", "new UiSelector().className(\"android.widget.Button\").instance(2)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Dec\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"09\")", "new UiSelector().className(\"android.widget.Button\").instance(2)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Oct\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Sep\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2016\")", "new UiSelector().className(\"android.widget.Button\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Feb\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2022\")", "new UiSelector().className(\"android.widget.Button\").instance(5)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2017\")", "new UiSelector().className(\"android.widget.Button\").instance(5)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2018\")", "new UiSelector().className(\"android.widget.Button\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/numberpicker_input\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("Aug");
	element = getElememtBack(driver, "new UiSelector().text(\"2016\")", "new UiSelector().className(\"android.widget.Button\").instance(5)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2022\")", "new UiSelector().className(\"android.widget.Button\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Sep\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"2020\")", "new UiSelector().className(\"android.widget.Button\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"15\")", "new UiSelector().className(\"android.widget.Button\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Mar\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_006\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.forrestguice.suntimeswidget'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
