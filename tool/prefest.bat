echo @off
tasklist /FI "IMAGENAME eq Appium.exe" 2>NUL | find /I /N "Appium.exe">NUL
if "%ERRORLEVEL%"=="1" start "test" "C:\Program Files (x86)\Appium\Appium.exe"
start java -Xmx2048m -XX:MaxHeapSize=2048m -jar prefest.jar