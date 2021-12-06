import os
import subprocess
import time
from uiautomator import Device

d = Device("emulator-5554")

def command(cmd, timeout=5):
    p = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=True)
    time.sleep(timeout)
    p.terminate()
    return

def startactivity(activityname, acitivityextra='-a test') :
    command('adb shell am start -n' + activityname + ' ' + acitivityextra)
    return

def stopactivity(activityname) :
    command('adb shell am force-stop ' + activityname)
    return

def stopactivitywithcoverage(activityname, ecname) :
    command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"" + ecname + "\"")
    command('adb shell am force-stop ' + activityname)
    return

def getCurrentScreen() :
    xml = d.dump()
    return xml.encode('utf-8')

def back() :
    d.press('back')

def scrolltoclickontext(text) :
    try:
        if d(className="android.widget.ListView").exists :
            d(className="android.widget.ListView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.LinearLayout", clickable=True).\
                click()
        elif d(className="android.support.v7.widget.RecyclerView").exists : 
            d(className="android.support.v7.widget.RecyclerView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.LinearLayout", clickable=True).\
                click()
        elif d(className="androidx.recyclerview.widget.RecyclerView").exists : 
            d(className="androidx.recyclerview.widget.RecyclerView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.LinearLayout", clickable=True).\
                click()
        elif d(className="android.widget.ScrollView").exists : 
            d(className="android.widget.ScrollView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.TextView", clickable=True).\
                click()
        return True
    except Exception as e:
        print(e)
        return False
            # d.screen.on()
# d(text="Night mode").click()