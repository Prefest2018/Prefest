try :
	if driver(text='OK').exists:
		driver(text='OK').click()
except Exception:
	pass
try :
	if driver(text='Queue').exists and driver(text='Settings').exists:
		driver(text='Settings').click()
		driver(text='User Interface').click()
except Exception:
	pass