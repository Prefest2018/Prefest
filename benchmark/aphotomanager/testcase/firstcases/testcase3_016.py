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
	'appPackage' : 'de.k3b.android.androFotoFinder',
	'appActivity' : 'de.k3b.android.androFotoFinder.FotoGalleryActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'de.k3b.android.androFotoFinder/de.k3b.android.androFotoFinder.JacocoInstrumentation',
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

# testcase016
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememtBack(driver, "new UiSelector().text(\"/storage/sdcard/Pictures/Wikipedia/Michael Mosman District Judge.jpg\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	driver.press_keycode(82)
	element = getElememtBack(driver, "new UiSelector().text(\"Recortar\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"/Places\")", "new UiSelector().className(\"android.widget.TextView\").instance(3)")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Settings\")", "new UiSelector().className(\"android.widget.TextView\").instance(5)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Settings\")", "new UiSelector().className(\"android.widget.TextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Slideshow interval in millisecs\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Cancelar\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Locatie voor voorbeeldweergaven\")", "new UiSelector().className(\"android.widget.TextView\").instance(14)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"キャンセル\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"الفاصل الزمني بين عرض الشرائح بالجزء من الثانية\")", "new UiSelector().className(\"android.widget.TextView\").instance(8)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("/storage/sdcard/DECIMAL/.thumbCache");
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("/storage/sdcard/DECIMAL/.thumbCache");
	element = getElememtBack(driver, "new UiSelector().text(\"موافق\")", "new UiSelector().className(\"android.widget.Button\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Fotografii\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Breite/Höhe für große Photos\")", "new UiSelector().className(\"android.widget.TextView\").instance(12)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("2000");
	element = getElememt(driver, "new UiSelector().resourceId(\"android:id/edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("testrage/sdcard/copy/log");
	element = getElememtBack(driver, "new UiSelector().text(\"Annulla\")", "new UiSelector().className(\"android.widget.Button\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Fotoğraflar\")", "new UiSelector().className(\"android.widget.TextView\").instance(7)")
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"3_016\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'de.k3b.android.androFotoFinder'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)