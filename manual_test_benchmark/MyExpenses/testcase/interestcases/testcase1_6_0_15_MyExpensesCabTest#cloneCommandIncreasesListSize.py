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
		candidates = driver.find_elements_by_class_name("android.widget.CheckedTextView")
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
		time.sleep(0.5)
		if driver(text='Confirm your password').exists:
			driver(focused=True).set_text('qwer')
			driver.press('enter')
			time.sleep(1)
	except Exception:
		pass
# preference setting and exit
try :
	stop(driver)
	starttime = time.time()
	commond("adb shell am start -n org.totschnig.myexpenses/org.totschnig.myexpenses.activity.MyPreferenceActivity -a test")
	time.sleep(1)
	conscript(driver)
	scrollToClickElement(driver, 'Theme')
	conscript(driver)
	clickInList(driver, 'Dark')
	conscript(driver)
	scrollToClickElement(driver, 'Date')
	conscript(driver)
	scrollToClickElement(driver, 'Allow to set exact transaction time.')
	conscript(driver)
	clickOnCheckable(driver, text = 'Allow to set exact transaction time.',value = True)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Upon app start, scroll transaction list to current date')
	conscript(driver)
	clickOnCheckable(driver, text = 'Upon app start, scroll transaction list to current date',value = False)
	scrollToClickElement(driver, 'Enable fast scroll for account list. May lead to incorrect display on some devices')
	conscript(driver)
	clickOnCheckable(driver, text = 'Enable fast scroll for account list. May lead to incorrect display on some devices',value = False)
	scrollToClickElement(driver, 'Cloned transactions have current date')
	conscript(driver)
	clickOnCheckable(driver, text = 'Cloned transactions have current date',value = False)
	scrollToClickElement(driver, 'Write local changes immediately to backend')
	conscript(driver)
	clickOnCheckable(driver, text = 'Write local changes immediately to backend',value = True)
	scrollToClickElement(driver, 'Protection')
	conscript(driver)
	scrollToClickElement(driver, 'Device lock screen')
	conscript(driver)
	clickOnCheckable(driver, text = 'Device lock screen',value = True)
	scrollToClickElement(driver, 'My templates')
	conscript(driver)
	clickOnCheckable(driver, text = 'My templates',value = False)
	scrollToClickElement(driver, 'Data entry from widgets and shortcuts')
	conscript(driver)
	clickOnCheckable(driver, text = 'Data entry from widgets and shortcuts',value = False)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Crash reports')
	conscript(driver)
	scrollToClickElement(driver, 'Allow automatic sending of crash reports')
	conscript(driver)
	clickOnCheckable(driver, text = 'Allow automatic sending of crash reports',value = True)
	time.sleep(1)
	driver.press('back')
	scrollToClickElement(driver, 'Number format')
	conscript(driver)
	typeText(driver,"")
	conscript(driver)
	scrollToClickElement(driver, 'Debug')
	conscript(driver)
	scrollToClickElement(driver, 'Write debugging information into system log')
	conscript(driver)
	clickOnCheckable(driver, text = 'Write debugging information into system log',value = True)

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
	commond("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"0_15_MyExpensesCabTest#cloneCommandIncreasesListSize_pre\"")
	jacocotime = time.time()
	print('jacoco time:', str(jacocotime - endtime), 's')
	stop(driver)
