try :
	time.sleep(0.5)
	if driver(text='Confirm your password').exists:
		driver(focused=True).set_text('qwer')
		driver.press('enter')
		time.sleep(1)
except Exception:
	pass