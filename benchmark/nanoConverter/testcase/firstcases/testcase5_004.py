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

# testcase004
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"BGN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromBGN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Rates\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseMDLrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("2.4427");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseCHFrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("70.6275");
	element = getElememtBack(driver, "new UiSelector().text(\"Update now!\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseRONrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.0587");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseTRYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("6.1661");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseKGSrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1.0299");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseCNYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("6.8687");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseKGSrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1.0299");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseRONrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.0587");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseRONrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.0587");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseKGSrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("69.3878");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/InverseStateButton\").className(\"android.widget.ToggleButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseCNYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.1013");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseTJSrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("7.1779");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseRONrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.0000");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseTMTrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("19.3233");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseCNYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("0.1013");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/CourseTRYrate\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("11.0886");
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"5_004\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.nanoconverter.zlab'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)