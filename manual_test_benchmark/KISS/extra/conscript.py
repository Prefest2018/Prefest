try :
	if driver(text='OK').exists:
		driver(text='OK').click()
except Exception:
	pass