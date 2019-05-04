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
	'appPackage' : 'com.ichi2.anki',
	'appActivity' : 'com.ichi2.anki.IntentHandler',
	'resetKeyboard' : True,
	'androidCoverage' : 'com.ichi2.anki/com.ichi2.anki.JacocoInstrumentation',
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

# testcase018
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	element = getElememt(driver, "new UiSelector().className(\"android.widget.ImageButton\").description(\"Navigate up\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Card browser\")", "new UiSelector().className(\"android.widget.CheckedTextView\").instance(1)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_add_card_from_card_browser\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_save\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/id_note_editText\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_save\").className(\"android.widget.TextView\")")
	TouchAction(driver).tap(element).perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Cards: Cloze\")", "new UiSelector().className(\"android.widget.TextView\").instance(9)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/back_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("testoze:Text}}<br>{{Extra}}");
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/front_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("testoze:Text}}");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/front_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("{{cloze:Text}}");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/styling_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("testd { font-family: arial font-size: 20px text-align: center color: black background-color: white}.cloze {font-weight: boldcolor: blue}");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/styling_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys(".card { font-family: arial font-size: 20px text-align: center color: black background-color: white}.cloze {font-weight: boldcolor: blue}");
	element = getElememtBack(driver, "new UiSelector().text(\"Cloze\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/back_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("{{FrontSide}}<hr id=answer>{{Back}}");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/back_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("{{cloze:Text}}<br>{{Extra}}");
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/action_confirm\").className(\"android.widget.TextView\")")
	TouchAction(driver).long_press(element).release().perform()
	element = getElememtBack(driver, "new UiSelector().text(\"Cloze\")", "new UiSelector().className(\"android.widget.TextView\").instance(4)")
	TouchAction(driver).tap(element).perform()
	element = getElememt(driver, "new UiSelector().resourceId(\"com.ichi2.anki:id/styling_edit\").className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys("12std { font-family: arial font-size: 20px text-align: center color: black background-color: white}.cloze {font-weight: boldcolor: blue}");
	swipe(driver, 0.5, 0.8, 0.5, 0.2)
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"1_018\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
	if (cpackage != 'com.ichi2.anki'):
		cpackage = "adb shell am force-stop " + cpackage
		os.popen(cpackage)