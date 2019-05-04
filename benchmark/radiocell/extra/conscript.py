try:
	driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")").click()
	time.sleep(0.1)
except NoSuchElementException:
	time.sleep(0.1)
return