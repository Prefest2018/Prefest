try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"OK\")")
except NoSuchElementException:
	time.sleep(0.1)
else:
	element.click()
return