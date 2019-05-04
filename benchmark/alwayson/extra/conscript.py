try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	element.click()
try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Activate\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	element.click()
	return
try:
	driver.find_element_by_android_uiautomator("new UiSelector().text(\"Notification access\")")
	driver.find_element_by_android_uiautomator("new UiSelector().checkable(true)").click()
	driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
	driver.press_keycode(4)
	time.sleep(0.1)
except NoSuchElementException:
	time.sleep(0.1)
return