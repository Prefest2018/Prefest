try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Ok\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	element.click()
	os.popen("adb shell am start -n io.dwak.holohackernews.app/io.dwak.holohackernews.app.ui.settings.SettingsActivity -a test")
return