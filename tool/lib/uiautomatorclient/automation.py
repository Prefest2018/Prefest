"""
UI自动化操作模块
提供Android应用的UI自动化操作功能，包括启动/停止Activity、获取屏幕内容、点击等
"""
import os
import subprocess
import time
from uiautomator import device as d

def command(cmd, timeout=5):
    """
    执行shell命令并等待指定时间后终止
    
    Args:
        cmd: 要执行的shell命令字符串
        timeout: 等待时间（秒），默认5秒
    """
    p = subprocess.Popen(cmd, stderr=subprocess.STDOUT, stdout=subprocess.PIPE, shell=True)
    time.sleep(timeout)
    p.terminate()
    return

def startactivity(activityname, acitivityextra='-a test') :
    """
    启动指定的Android Activity
    
    Args:
        activityname: Activity的完整名称（包名/类名）
        acitivityextra: Activity启动的额外参数，默认为'-a test'
    """
    command('adb shell am start -n' + activityname + ' ' + acitivityextra)
    return

def stopactivity(activityname) :
    """
    强制停止指定的Android应用
    
    Args:
        activityname: 应用的包名
    """
    command('adb shell am force-stop ' + activityname)
    return

def stopactivitywithcoverage(activityname, ecname) :
    """
    停止Activity并收集代码覆盖率数据
    
    Args:
        activityname: 应用的包名
        ecname: 覆盖率数据文件的名称
    """
    # 发送广播以结束EMMA覆盖率收集
    command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"" + ecname + "\"")
    # 强制停止应用
    command('adb shell am force-stop ' + activityname)
    return

def getCurrentScreen() :
    """
    获取当前屏幕的UI层次结构XML内容
    
    Returns:
        当前屏幕的XML内容（UTF-8编码的字符串）
    """
    xml = d.dump()
    return xml.encode('utf-8')

def back() :
    """
    执行返回操作（模拟按下返回键）
    """
    d.press.back()

def scrolltoclickontext(text) :
    """
    在可滚动视图中查找并点击包含指定文本的控件
    
    Args:
        text: 要查找并点击的文本内容
        
    Returns:
        bool: 如果成功找到并点击返回True，否则返回False
    """
    try:
        # 尝试在ListView中查找并点击
        if d(className="android.widget.ListView").exists :
            d(className="android.widget.ListView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.LinearLayout", clickable=True).\
                click()
        # 尝试在support库的RecyclerView中查找并点击
        elif d(className="android.support.v7.widget.RecyclerView").exists : 
            d(className="android.support.v7.widget.RecyclerView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.LinearLayout", clickable=True).\
                click()
        # 尝试在androidx的RecyclerView中查找并点击
        elif d(className="androidx.recyclerview.widget.RecyclerView").exists : 
            d(className="androidx.recyclerview.widget.RecyclerView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.LinearLayout", clickable=True).\
                click()
        # 尝试在ScrollView中查找并点击
        elif d(className="android.widget.ScrollView").exists : 
            d(className="android.widget.ScrollView").\
                child_by_text(text, allow_scroll_search=True, className="android.widget.TextView", clickable=True).\
                click()
        return True
    except Exception as e:
        # 如果发生异常，打印错误信息并返回False
        print(e)
        return False
            # d.screen.on()
# d(text="Night mode").click()