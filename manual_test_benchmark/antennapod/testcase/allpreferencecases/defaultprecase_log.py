import os
import subprocess
import time
import uiautomator2 as u2
import traceback

driver = u2.connect("emulator-5554")
driver.settings['wait_timeout'] = 5.0
dinfo = driver.device_info
dwidth = dinfo['display']['width']
dheight = dinfo['display']['height']

def stop(driver):
	driver.app_stop_all()

def commond(cmd, timeout=3):
	p = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=True)
	time.sleep(timeout)
	p.terminate()
	return

def startactivity(activityname, acitivityextra='-a test') :
	commond('adb shell am start -n' + activityname + ' ' + acitivityextra)
	return

def back() :
	driver.press('back')

def checkWindow(driver) :
	currentWindow = driver(className='android.widget.FrameLayout', instance = 0).info['bounds']
	if currentWindow['bottom'] == dheight and currentWindow['right'] == dwidth:
		return False
	else :
		return True

def scrollToFindElement(driver, text, innerClassName="android.widget.LinearLayout") :
	try:
		element = driver(text=text)
		if element.exists:
			return element
	except Exception:
		pass
	currentH = driver.dump_hierarchy()
	for i in range(0,10) :
		try :
			driver.drag(dwidth/2, dheight * 0.8, dwidth/2, dheight * 0.2, 0.3)
			time.sleep(0.2)
			tempH = driver.dump_hierarchy()
			if currentH == tempH:
				break
			else :
				currentH = tempH
			element = driver(text=text)
			if (element.exists):
				return element
		except Exception:
			pass
	for i in range(0,10) :
		try :
			driver.drag(dwidth/2, dheight * 0.2, dwidth/2, dheight * 0.8, 0.3)
			time.sleep(0.2)
			tempH = driver.dump_hierarchy()
			if currentH == tempH:
				break
			else :
				currentH = tempH
			element = driver(text=text)
			if (element.exists):
				return element
		except Exception as e:
			print(e)
	return None

def scrollToClickElement(driver, str) :
	element = scrollToFindElement(driver, str)
	if element is None :
		return
	else :
		element.click()

def testingSeekBar(driver, text = None, resourceId = None, className = None, instance = 0, value = None):
	try :
		if(not checkWindow(driver)) :
			element = seekForNearestSeekBar(driver, text = text, resourceId = resourceId, className = className, instance = instance)
		else :
			element = driver(className="android.widget.SeekBar")
		if (None != element):
			settingSeekBar(driver, element, value)
			driver(text="OK").click()
	except Exception:
		time.sleep(1)

def seekForNearestSeekBar(driver, text = None, resourceId = None, className = None, instance = 0):
	parents = driver(className="android.widget.LinearLayout")
	for parent in parents:
		try :
			if parent.child(text = text).exists:
				lists = parent.child(className="android.widget.LinearLayout")
			if len(lists) == 1 :
				innere = parent.child(className="android.widget.SeekBar")
				return innere
				break
		except Exception:
			continue
def settingSeekBar(driver, element, value) :
	left = element.info['bounds']['left']
	right = element.info['bounds']['right']
	width = right - left
	height = (element.info['bounds']['bottom']+element.info['bounds']['top'])/2
	driver.touch.down(left + 10, height)
	time.sleep(.5)
	driver.touch.move(left + width * value, height)
	driver.touch.up()

def clickInList(d, str) :
	element = None
	if (str is None) :
		candidates = d(className="android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(driver):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToFindElement(driver, str, "android.widget.CheckedTextView")
	if element is not None :
		element.click()
	else :
		if checkWindow(driver) :
			driver.press('back')

def clickInMultiList(d, str) :
	element = None
	if (str is None) :
		candidates = d(className="android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(d):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToClickElement(d, str, "android.widget.CheckedTextView")
	if element is not None :
		nowvalue = element.get_attribute("checked")
		if (nowvalue != "true") :
			element.click()
	if checkWindow(d) :
		d(text='OK').click()

def clickOnCheckable(driver, text = None, resourceId = None, className = None, instance = 0, value = True) :
	parents = driver(className="android.widget.LinearLayout")
	for parent in parents:
		try :
			if parent.child(text = text).exists:
				lists = parent.child(className="android.widget.LinearLayout")
				if len(lists) == 1:
					innere = parent.child(checkable="true")
					if innere.info['checked'] != value:
						innere.click()
					break
		except Exception:
			continue

def typeText(driver, value) :
	try :
		element = driver(className='android.widget.EditText')
		element.clear_text()
		element.set_text(value)
		enterelement = driver(text='OK')
		if (enterelement is None) :
			if checkWindow(driver):
				driver.press('back')
		else :
			enterelement.click()
	except Exception:
		pass
def conscript(driver):
	try :
		if driver(text='OK').exists:
			driver(text='OK').click()
	except Exception:
		pass
	try :
		if driver(text='Queue').exists and driver(text='Settings').exists:
			driver(text='Settings').click()
			driver(text='User Interface').click()
	except Exception:
		pass
# preference setting and exit
try :
	starttime = time.time()
	commond("adb shell am start -n de.danoeh.antennapod/de.danoeh.antennapod.activity.PreferenceActivity -a test")
	time.sleep(1)
	conscript(driver)
	scrollToClickElement(driver, 'Network')
	conscript(driver)
	scrollToClickElement(driver, 'Mobile Updates')
	conscript(driver)
	clickOnCheckable(driver, text = 'Mobile Updates',value = False)
	scrollToClickElement(driver, 'Show Download Report')
	conscript(driver)
	clickOnCheckable(driver, text = 'Show Download Report',value = True)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Playback')
	conscript(driver)
	scrollToClickElement(driver, 'Headphones Disconnect')
	conscript(driver)
	clickOnCheckable(driver, text = 'Headphones Disconnect',value = True)
	scrollToClickElement(driver, 'Headphones Reconnect')
	conscript(driver)
	clickOnCheckable(driver, text = 'Headphones Reconnect',value = True)
	scrollToClickElement(driver, 'Bluetooth Reconnect')
	conscript(driver)
	clickOnCheckable(driver, text = 'Bluetooth Reconnect',value = False)
	scrollToClickElement(driver, 'Pause for Interruptions')
	conscript(driver)
	clickOnCheckable(driver, text = 'Pause for Interruptions',value = False)
	scrollToClickElement(driver, 'Resume after Call')
	conscript(driver)
	clickOnCheckable(driver, text = 'Resume after Call',value = True)
	scrollToClickElement(driver, 'Upon exiting video')
	conscript(driver)
	clickInList(driver, 'Stop playback')
	conscript(driver)
	scrollToClickElement(driver, 'Forward Button Skips')
	conscript(driver)
	clickOnCheckable(driver, text = 'Forward Button Skips',value = False)
	scrollToClickElement(driver, 'Previous button restarts')
	conscript(driver)
	clickOnCheckable(driver, text = 'Previous button restarts',value = False)
	scrollToClickElement(driver, 'Enqueue Downloaded')
	conscript(driver)
	clickOnCheckable(driver, text = 'Enqueue Downloaded',value = True)
	scrollToClickElement(driver, 'Enqueue at Front')
	conscript(driver)
	clickOnCheckable(driver, text = 'Enqueue at Front',value = False)
	scrollToClickElement(driver, 'Continuous Playback')
	conscript(driver)
	clickOnCheckable(driver, text = 'Continuous Playback',value = True)
	scrollToClickElement(driver, 'Smart Mark as Played')
	conscript(driver)
	clickInList(driver, '30')
	conscript(driver)
	scrollToClickElement(driver, 'Keep Skipped Episodes')
	conscript(driver)
	clickOnCheckable(driver, text = 'Keep Skipped Episodes',value = True)
	scrollToClickElement(driver, 'Media player')
	conscript(driver)
	clickInList(driver, 'Sonic Media Player')
	conscript(driver)
	scrollToClickElement(driver, 'Chromecast support')
	conscript(driver)
	clickOnCheckable(driver, text = 'Chromecast support',value = False)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Storage')
	conscript(driver)
	scrollToClickElement(driver, 'Image Cache Size')
	conscript(driver)
	clickInList(driver, '100 MiB')
	conscript(driver)
	scrollToClickElement(driver, 'Auto Delete')
	conscript(driver)
	clickOnCheckable(driver, text = 'Auto Delete',value = False)
	scrollToClickElement(driver, 'Keep Favorite Episodes')
	conscript(driver)
	clickOnCheckable(driver, text = 'Keep Favorite Episodes',value = True)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'User Interface')
	conscript(driver)
	scrollToClickElement(driver, 'Select Theme')
	conscript(driver)
	clickInList(driver, None)
	conscript(driver)
	scrollToClickElement(driver, 'Set Subscription Order')
	conscript(driver)
	clickInList(driver, None)
	conscript(driver)
	scrollToClickElement(driver, 'Set Subscription Counter')
	conscript(driver)
	clickInList(driver, None)
	conscript(driver)
	scrollToClickElement(driver, 'High Notification priority')
	conscript(driver)
	clickOnCheckable(driver, text = 'High Notification priority',value = False)
	scrollToClickElement(driver, 'Persistent Playback Controls')
	conscript(driver)
	clickOnCheckable(driver, text = 'Persistent Playback Controls',value = True)
	scrollToClickElement(driver, 'Set Lockscreen Background')
	conscript(driver)
	clickOnCheckable(driver, text = 'Set Lockscreen Background',value = True)
	scrollToClickElement(driver, 'Back Button Behavior')
	conscript(driver)
	clickInList(driver, 'Default')
	conscript(driver)

	driver.press('back')
	time.sleep(2)
except Exception as e:
	print('FAIL')
	print('str(e):\t\t', str(e))
	print('repr(e):\t', repr(e))
	print(traceback.format_exc())
else:
	print('OK')
finally:
	cpackage = driver.info['currentPackageName']
	endtime = time.time()
	print('consumed time:', str(endtime - starttime), 's')
	commond("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"preference_pre\"")
	jacocotime = time.time()
	print('jacoco time:', str(jacocotime - endtime), 's')
	stop(driver)
