try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	element.click()
try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Confirm\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	element.click()
try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().description(\"Open menu\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	os.popen("adb shell am start -n de.danoeh.antennapod/de.danoeh.antennapod.activity.PreferenceActivity -a test")
	scrollToClickElement(driver, "new UiSelector().text(\"User Interface\")")
return