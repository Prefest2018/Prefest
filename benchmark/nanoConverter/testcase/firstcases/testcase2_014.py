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
	'appPackage' : 'com.nanoconverter.zlab',
	'appActivity' : 'com.nanoconverter.zlab.NanoConverter',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.nanoconverter.zlab/com.nanoconverter.zlab.JacocoInstrumentation',
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

# testcase014
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"PLN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromPLN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"RUB\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toRUB\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("2.49");
	element = getElememtBack(driver, "new UiSelector().text(\"EUR\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toEUR\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.00");
	element = getElememtBack(driver, "new UiSelector().text(\"PLN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromPLN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"BGN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toBGN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"LVL\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toLVL\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Rates\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseMDLrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("16.7800");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseBYRrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.0000");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseGBPrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("88.7303");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseCHFrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("9.3132");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseCHFrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.9660");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseRUBrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("68.1958");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseJPYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.0803");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseEURrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("79.3853");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseJPYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1.6531");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseBYRrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("273.2517");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseGBPrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.7644");
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"2_014\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.nanoconverter.zlab'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
