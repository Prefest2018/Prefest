try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Enable local backups?\")")
	element = driver.find_element_by_android_uiautomator("new UiSelector().resourceId(\"org.thoughtcrime.securesms:id/confirmation_check\")")
	element.click()
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Enable backups\")")
	element.click()
except NoSuchElementException:
	time.sleep(0.1)
try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Delete backups?\")")
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Delete backups\")")
	element.click()
except NoSuchElementException:
	time.sleep(0.1)
try:
	element = driver.find_element_by_android_uiautomator("new UiSelector().text(\"Cancel\")")
	element.click()
	time.sleep(1)
except NoSuchElementException:
	time.sleep(0.1)
