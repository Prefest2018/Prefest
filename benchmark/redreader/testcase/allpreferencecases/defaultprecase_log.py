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
	'appPackage' : 'org.quantumbadger.redreader',
	'appActivity' : 'org.quantumbadger.redreader.activities.MainActivity',
	'resetKeyboard' : True,
	'androidCoverage' : 'org.quantumbadger.redreader/org.quantumbadger.redreader.JacocoInstrumentation',
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
				end_y=int(height * endyper), duration=1000)
	except WebDriverException:
		time.sleep(1)
		driver.swipe(start_x=int(width * startxper), start_y=int(height * startyper), end_x=int(width * endxper),
				end_y=int(height * endyper), duration=1000)
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
		else :
			return element
	for i in range(0, 4, 1):
		try:
			element = driver.find_element_by_android_uiautomator(str)
			elements = driver.find_elements_by_android_uiautomator(str)
			if (len(elements) > 1):
				for temp in elements:
					if temp.get_attribute("enabled") == "true":
						element = temp
						break
		except NoSuchElementException:
			swipe(driver, 0.5, 0.2, 0.5, 0.55)
		else :
			return element
	return

def scrollToClickElement(driver, str) :
	element = scrollToFindElement(driver, str)
	if element is None :
		return
	else :
		element.click()

def clickInList(driver, str) :
	element = None
	if (str is None) :
		candidates = driver.find_elements_by_class_name("android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(driver):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToFindElement(driver, str)
	if element is not None :
		element.click()
	else :
		if checkWindow(driver) :
			driver.press_keycode(4)

def clickOnCheckable(driver, str, value = "true") :
	parents = driver.find_elements_by_class_name("android.widget.LinearLayout")
	for parent in parents:
		try :
			parent.find_element_by_android_uiautomator(str)
			lists = parent.find_elements_by_class_name("android.widget.LinearLayout")
			if len(lists) == 1 :
				innere = parent.find_element_by_android_uiautomator("new UiSelector().checkable(true)")
				nowvalue = innere.get_attribute("checked")
				if (nowvalue != value) :
					innere.click()
				break
		except NoSuchElementException:
			continue

def typeText(driver, value) :
	element = getElememt(driver, "new UiSelector().className(\"android.widget.EditText\")")
	element.clear()
	element.send_keys(value)
	enterelement = getElememt(driver, "new UiSelector().text(\"OK\")")
	if (enterelement is None) :
		if checkWindow(driver):
			driver.press_keycode(4)
	else :
		enterelement.click()
def checkWindow(driver) :
	dsize = driver.get_window_size()
	nsize = driver.find_element_by_class_name("android.widget.FrameLayout").size
	if dsize['height'] > nsize['height']:
		return True
	else :
		return False
def testingSeekBar(driver, str, value):
	try :
		if(not checkWindow(driver)) :
			element = seekForNearestSeekBar(driver, str)
		else :
			element = driver.find_element_by_class_name("android.widget.SeekBar")
		if (None != element):
			settingSeekBar(driver, element, value)
			driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
	except NoSuchElementException:
		time.sleep(1)

def seekForNearestSeekBar(driver, str):
	parents = driver.find_elements_by_class_name("android.widget.LinearLayout")
	for parent in parents:
		try :
			parent.find_element_by_android_uiautomator(str)
			lists = parent.find_elements_by_class_name("android.widget.LinearLayout")
			if len(lists) == 1 :
				innere = parent.find_element_by_class_name("android.widget.SeekBar")
				return innere
				break
		except NoSuchElementException:
			continue
def settingSeekBar(driver, element, value) :
	x = element.rect.get("x")
	y = element.rect.get("y")
	width = element.rect.get("width")
	height = element.rect.get("height")
	TouchAction(driver).press(None, x + 10, y + height/2).move_to(None, x + width * value,y + height/2).release().perform()
	y = value
def clickInMultiList(driver, str) :
	element = None
	if (str is None) :
		candidates = driver.find_elements_by_class_name("android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(driver):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToFindElement(driver, str)
	if element is not None :
		nowvalue = element.get_attribute("checked")
		if (nowvalue != "true") :
			element.click()
	if checkWindow(driver) :
		driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
# preference setting and exit
try :
	starttime = time.time()
	driver = webdriver.Remote('http://localhost:4723/wd/hub', desired_caps)
	os.popen("adb shell am start -n org.quantumbadger.redreader/org.quantumbadger.redreader.settings.SettingsActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"Appearance\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Left-handed mode\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Left-handed mode\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Theme\")")
	clickInList(driver, "new UiSelector().text(\"Red\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Navbar Colour\")")
	clickInList(driver, "new UiSelector().text(\"Black\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide Android status bar\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide Android status bar\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Posts Font Scale\")")
	clickInList(driver, "new UiSelector().text(\"Normal\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Comments Font Scale\")")
	clickInList(driver, "new UiSelector().text(\"Normal\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Inbox Font Scale\")")
	clickInList(driver, "new UiSelector().text(\"Normal\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show floating toolbar over image\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show floating toolbar over image\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show thumbnails\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show thumbnails\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Thumbnails on Wi-Fi only\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Thumbnails on Wi-Fi only\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show NSFW thumbnails\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show NSFW thumbnails\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Tablet Mode (two pane)\")")
	clickInList(driver, "new UiSelector().text(\"Auto-detect\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show floating toolbar over comments\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show floating toolbar over comments\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Comment Header\")")
	clickInMultiList(driver, "new UiSelector().text(\"Reddit Gold\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Link Buttons\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Link Buttons\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Make link text clickable\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Make link text clickable\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show indent lines\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show indent lines\")", "false")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Behaviour\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Skip to front page\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Skip to front page\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Internal Browser\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Internal Browser\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Use Custom Tabs\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Use Custom Tabs\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Enable video playback controls\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Enable video playback controls\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Image Viewer\")")
	clickInList(driver, "new UiSelector().text(\"Internal viewer (OpenGL)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"GIF Viewer\")")
	clickInList(driver, "new UiSelector().text(\"Internal viewer (Movie, Android 4.0+)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Video Viewer\")")
	clickInList(driver, "new UiSelector().text(\"Internal VideoView (recommended)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Album Viewer\")")
	clickInList(driver, "new UiSelector().text(\"Internal viewer (list)\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Gallery Swipe Length\")")
	clickInList(driver, "new UiSelector().text(\"150 dp\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Notifications\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Notifications\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Screen Orientation\")")
	clickInList(driver, "new UiSelector().text(\"Auto\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Swipe down to refresh\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Swipe down to refresh\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Default Post Sort\")")
	clickInList(driver, "new UiSelector().text(\"Hot\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Default Comment Sort\")")
	clickInList(driver, "new UiSelector().text(\"Best\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Pinned Subreddit Sort\")")
	clickInList(driver, "new UiSelector().text(\"By Name\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Blocked Subreddit Sort\")")
	clickInList(driver, "new UiSelector().text(\"By Name\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fling Post Left Action\")")
	clickInList(driver, "new UiSelector().text(\"Downvote\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fling Post Right Action\")")
	clickInList(driver, "new UiSelector().text(\"Upvote\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Self-Post Tap Action\")")
	clickInList(driver, "new UiSelector().text(\"Toggle Collapse\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Comment Tap Action\")")
	clickInList(driver, "new UiSelector().text(\"Toggle Collapse\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Comment Long Click Action\")")
	clickInList(driver, "new UiSelector().text(\"Action Menu\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fling Comment Left Action\")")
	clickInList(driver, "new UiSelector().text(\"Downvote\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Fling Comment Right Action\")")
	clickInList(driver, "new UiSelector().text(\"Upvote\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Show NSFW content\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show NSFW content\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide read posts\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide read posts\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Restrict post count\")")
	clickInList(driver, "new UiSelector().text(\"All\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Share as permalink\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Share as permalink\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Include title/description when sharing\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Include title/description when sharing\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Include text when sharing comment\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Include text when sharing comment\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Bezel toolbar swipe zone size\")")
	clickInList(driver, "new UiSelector().text(\"10 dp\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Cache\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Refresh posts if cache older than\")")
	clickInList(driver, "new UiSelector().text(\"1 hour\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Precache images\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Precache images\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"On Wi-Fi only\")")
	clickOnCheckable(driver, "new UiSelector().text(\"On Wi-Fi only\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Precache comments\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Precache comments\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"On Wi-Fi only\")")
	clickOnCheckable(driver, "new UiSelector().text(\"On Wi-Fi only\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Delete cached listings after\")")
	clickInList(driver, "new UiSelector().text(\"1 week\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Delete cached thumbnails after\")")
	clickInList(driver, "new UiSelector().text(\"1 week\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Delete cached images after\")")
	clickInList(driver, "new UiSelector().text(\"1 day\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Delete cached flags (read, upvoted, etc.) after\")")
	clickInList(driver, "new UiSelector().text(\"1 week\")")
	time.sleep(1)
	driver.press_keycode(4)
	scrollToClickElement(driver, "new UiSelector().text(\"Menus\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Main Menu User Items\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Main Menu Shortcuts\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Hide username in main menu\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Hide username in main menu\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show blocked subreddits in main menu\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show blocked subreddits in main menu\")", "false")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Multireddits\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Multireddits\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Show Subscribed Subreddits\")")
	clickOnCheckable(driver, "new UiSelector().text(\"Show Subscribed Subreddits\")", "true")
	scrollToClickElement(driver, "new UiSelector().text(\"Post Context Menu Items\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Post Toolbar Items\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Options Menu Items\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Link Context Menu Items\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")
	scrollToClickElement(driver, "new UiSelector().text(\"Subreddit Context Menu Items\")")
	clickInMultiList(driver, "new UiSelector().text(\"null\")")

	driver.press_keycode(4)
	time.sleep(2)
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
	command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"preference_pre\"")
	jacocotime = time.time()
	print 'jacoco time:', str(jacocotime - endtime), 's'
	driver.quit()
