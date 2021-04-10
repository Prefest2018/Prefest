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

def clickInList(driver, str) :
	element = None
	if (str is None) :
		candidates = driver(className="android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(driver):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToFindElement(driver, str)
	if element is not None :
		element.click()
	else :
		if checkWindow(driver) :
			driver.press('back')

def clickInMultiList(driver, str) :
	element = None
	if (str is None) :
		candidates = driver(className="android.widget.CheckedTextView")
		if len(candidates) >= 1 and checkWindow(d):
			element = candidates[len(candidates)-1]
	else :
		element = scrollToClickElement(driver, str)
	if element is not None :
		nowvalue = element.get_attribute("checked")
		if (nowvalue != "true") :
			element.click()
	if checkWindow(driver) :
		driver(text='OK').click()

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
# preference setting and exit
try :
	starttime = time.time()
	commond("adb shell am start -n com.forrestguice.suntimeswidget/com.forrestguice.suntimeswidget.SuntimesSettingsActivity -a test")
	time.sleep(1)
	scrollToClickElement(driver, 'General Settings')
	scrollToClickElement(driver, 'Time Format')
	clickInList(driver, 'System format')
	scrollToClickElement(driver, 'Units of length')
	clickInList(driver, 'Metric')
	scrollToClickElement(driver, 'Use Elevation')
	clickOnCheckable(driver, text = 'Use Elevation',value = True)
	scrollToClickElement(driver, 'Object Shadow')
	typeText(driver,"1.8288")
	scrollToClickElement(driver, 'Data Source Plugins')
	clickOnCheckable(driver, text = 'Data Source Plugins',value = False)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Language Settings')
	scrollToClickElement(driver, 'Mode')
	clickInList(driver, 'System')
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Places')
	scrollToClickElement(driver, 'GPS time limit')
	clickInList(driver, '1 minute')
	scrollToClickElement(driver, 'GPS recent max age')
	clickInList(driver, '5 minutes')
	scrollToClickElement(driver, 'Passive Location')
	clickOnCheckable(driver, text = 'Passive Location',value = False)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'User Interface')
	scrollToClickElement(driver, 'Appearance')
	clickInList(driver, 'Dark Theme')
	scrollToClickElement(driver, 'Astronomical Twilight')
	clickOnCheckable(driver, text = 'Astronomical Twilight',value = True)
	scrollToClickElement(driver, 'Nautical Twilight')
	clickOnCheckable(driver, text = 'Nautical Twilight',value = True)
	scrollToClickElement(driver, 'Blue Hour')
	clickOnCheckable(driver, text = 'Blue Hour',value = False)
	scrollToClickElement(driver, 'Civil Twilight')
	clickOnCheckable(driver, text = 'Civil Twilight',value = True)
	scrollToClickElement(driver, 'Actual Time')
	clickOnCheckable(driver, text = 'Actual Time',value = True)
	scrollToClickElement(driver, 'Solar Noon')
	clickOnCheckable(driver, text = 'Solar Noon',value = True)
	scrollToClickElement(driver, 'Golden Hour')
	clickOnCheckable(driver, text = 'Golden Hour',value = True)
	scrollToClickElement(driver, 'Show Data Source')
	clickOnCheckable(driver, text = 'Show Data Source',value = True)
	scrollToClickElement(driver, 'Show Light Map')
	clickOnCheckable(driver, text = 'Show Light Map',value = True)
	scrollToClickElement(driver, 'Show Moon')
	clickOnCheckable(driver, text = 'Show Moon',value = True)
	scrollToClickElement(driver, 'Show Solstice / Equinox')
	clickOnCheckable(driver, text = 'Show Solstice / Equinox',value = True)
	scrollToClickElement(driver, 'Solstice Tracking')
	clickInList(driver, 'Upcoming Event')
	scrollToClickElement(driver, 'Show Warnings')
	clickOnCheckable(driver, text = 'Show Warnings',value = True)
	scrollToClickElement(driver, 'Verbose TalkBack')
	clickOnCheckable(driver, text = 'Verbose TalkBack',value = False)
	scrollToClickElement(driver, 'Show Weeks')
	clickOnCheckable(driver, text = 'Show Weeks',value = False)
	scrollToClickElement(driver, 'Show Hours')
	clickOnCheckable(driver, text = 'Show Hours',value = True)
	scrollToClickElement(driver, 'Show Seconds')
	clickOnCheckable(driver, text = 'Show Seconds',value = False)
	scrollToClickElement(driver, 'Show Time (with dates)')
	clickOnCheckable(driver, text = 'Show Time (with dates)',value = True)
	scrollToClickElement(driver, 'On Clock Tap')
	clickInList(driver, 'Show upcoming event')
	scrollToClickElement(driver, 'On Date Tap')
	clickInList(driver, 'Swap cards (today/tomorrow)')
	scrollToClickElement(driver, 'On Date Long Press')
	clickInList(driver, 'Open calendar')
	scrollToClickElement(driver, 'On Note Tap')
	clickInList(driver, 'Show next note')
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Alarms')
	scrollToClickElement(driver, 'Show all ringtones')
	clickOnCheckable(driver, text = 'Show all ringtones',value = False)
	scrollToClickElement(driver, 'Launcher Icon')
	clickOnCheckable(driver, text = 'Launcher Icon',value = True)

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
