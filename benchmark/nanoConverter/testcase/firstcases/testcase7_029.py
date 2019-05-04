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

# testcase029
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueFrom\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12");
	element = getElememtBack(driver, "new UiSelector().text(\"AUD\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toAUD\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"RUB\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toRUB\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("201.38");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("47.82");
	element = getElememtBack(driver, "new UiSelector().text(\"AZN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toAZN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1757.91");
	element = getElememtBack(driver, "new UiSelector().text(\"AZN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toAZN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"USD\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromUSD\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("4.66");
	element = getElememtBack(driver, "new UiSelector().text(\"AUD\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toAUD\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"MDL\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromMDL\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"CHF\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/toCHF\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"UAH\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromUAH\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("1758.41");
	element = getElememtBack(driver, "new UiSelector().text(\"PLN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromPLN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"AZN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromAZN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"LVL\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromLVL\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"AZN\")", "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/fromAZN\").className(\"android.widget.RadioButton\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.nanoconverter.zlab:id/ValueResult\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("201.38");
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"7_029\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.nanoconverter.zlab'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)
