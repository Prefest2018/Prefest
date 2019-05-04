#coding=utf-8
import os
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
	'noReset' : True
	}

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
					parent.click()
				break
		except NoSuchElementException:
			continue
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n de.k3b.android.androFotoFinder/de.k3b.android.androFotoFinder.SettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Date Picker: use decades\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Date Picker: use decades\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Use Mapsforge offline map\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Use Mapsforge offline map\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Write changes to\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"jpg file only\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"Long Xmp Sidecar File Name?\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Long Xmp Sidecar File Name?\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Rename private *.jpg to *.jpg-p\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Rename private *.jpg to *.jpg-p\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Clear multi-selection after action\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Clear multi-selection after action\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat SQL\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat SQL\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat gridview/listview\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat gridview/listview\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat geo map\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat geo map\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat memory consumption\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat memory consumption\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat JPG\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat JPG\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat jpg/xmp metadata read/write\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat jpg/xmp metadata read/write\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat libs\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat libs\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"LogCat misc.\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"LogCat misc.\")", "true")

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
	driver.quit()
