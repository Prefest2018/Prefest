# PREFEST

# Environment
* Window 10 OS;
* Java 64 1.8.0_191, with JAVA_HOME exported;
* python 2.7 with 'Appium-Python-Client' and 'uiautomator' installed;
* Android SDK, with ANDROID_HOME exported;
* Appium 1.10.0;
* Jadx;

# How to use PREFEST
* config file 'config.txt' and start Appium sever
* create a test project, provide the apk file in the 'app' sub folder, and the 'mcmc_all_history_testsuites.txt' test record from Stoat in the 'test case' sub folder
* start PREFEST with 'java -jar prefest.jar'
* set test project home, run 'stub' to stub the app with loggers(the stubbed app should be manually installed in the test avd), run 'firstExe' to run the test cases generated by Stoat, run 'Analyze' to perform the precise data-flow analysis from the logs
* run 'PREFEST(T)', 'PREFEST(N)', 'NonDefault' or 'Pairwise' to execute the corresponding tests for preferences

'config.txt' Example:
```
JAVA_HOME=C:\Program Files\Java\jdk1.8.0_191
ANDROID_HOME=C:\Program Files\AndroidStudio\sdk
JADX_HOME=C:\Program Files\jadx
PYTHON_HOME=C:\Python27
ANDROID_LIB=C:\Program Files\AndroidStudio\sdk\build-tools\27.0.2

default_projecthome=C:\Users\xxx\Documents\GoodWeather
avd_name=Nexus_S_API_19
reset_when_error=true
preference_explore=false
reset_for_each_run=false
```

test project Example:
```
GoodWeather
|
|-----app
|      |-----GoodWeather.apk (input)
|      |-----GoodWeather_stub.apk (stubbed apk file)
|
|-----testcase
|      |-----mcmc_all_history_testsuites.txt (input)
|      |-----firstcases (test cases generated from Stoat)
|      |-----interestcases (test cases generated in PREFEST(T))
|      |-----interestallcases (test cases generated in PREFEST(N))
|      |-----allpreferencecases (test prefix scripts of NonDefault)
|      |-----pwpreferencecases (test prefix scripts of Pairwise)
|
|-----error (error logs)
```