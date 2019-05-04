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
	'appPackage' : 'org.wikipedia',
	'appActivity' : 'org.wikipedia.main.MainActivity',
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
	os.popen("adb shell svc data diable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi diable")
	os.popen("adb shell settings put secure location_providers_allowed 'false'")
	os.popen("adb shell settings put secure location_providers_allowed network,gps")
	time.sleep(5)
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.wikipedia/org.wikipedia.settings.DeveloperSettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"useRestbase_setManually\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"useRestbase_setManually\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"useRestbase\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"useRestbase\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"mediaWikiBaseUriSupportsLangCode\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"mediaWikiBaseUriSupportsLangCode\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Retrofit Log Level (restart required)\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"NONE\")").click()
	scrollToFindElement(driver, "new UiSelector().text(\"suppressNotificationPolling\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"suppressNotificationPolling\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"showDeveloperSettings\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"showDeveloperSettings\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"memoryLeakTest\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"memoryLeakTest\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"crashedBeforeActivityCreated\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"crashedBeforeActivityCreated\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"initialOnboardingEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"initialOnboardingEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"descriptionEditTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"descriptionEditTutorialEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"tocTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"tocTutorialEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"selectTextTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"selectTextTutorialEnabled\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"shareTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"shareTutorialEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"multilingualSearchTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"multilingualSearchTutorialEnabled\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"readingListTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"readingListTutorialEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"readingListSyncReminder\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"readingListSyncReminder\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"readingListLoginReminder\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"readingListLoginReminder\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"zeroTutorialEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"zeroTutorialEnabled\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"feedCustomizeOnboardingCardEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"feedCustomizeOnboardingCardEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"feedReadingListsSyncOnboardingCardEnabled\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"feedReadingListsSyncOnboardingCardEnabled\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"showReadingListsSyncPrompt\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"showReadingListsSyncPrompt\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"readingListsFirstTimeSync\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"readingListsFirstTimeSync\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"showRemoveChineseVariantPrompt\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"showRemoveChineseVariantPrompt\")", "false")

	driver.press_keycode(4)
	time.sleep(2)
	os.popen("adb shell am start -n org.wikipedia/org.wikipedia.settings.SettingsActivity")
	scrollToFindElement(driver, "new UiSelector().text(\"Show link previews\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show link previews\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Reading list syncing\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Reading list syncing\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Download only over Wi-Fi\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Download only over Wi-Fi\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Show images\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Show images\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Prefer offline content\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Prefer offline content\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Send usage reports\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Send usage reports\")", "false")
	scrollToFindElement(driver, "new UiSelector().text(\"Send crash reports\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Send crash reports\")", "true")
	scrollToFindElement(driver, "new UiSelector().text(\"Warn if leaving Wikipedia Zero\")").click()
	clickoncheckable(driver, "new UiSelector().text(\"Warn if leaving Wikipedia Zero\")", "true")

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
	os.popen("adb shell svc data enable")
	os.popen("adb shell service call bluetooth_manager 6")
	os.popen("adb shell svc wifi enable")
	os.popen("adb shell settings put secure location_providers_allowed gps, network")
	driver.quit()
