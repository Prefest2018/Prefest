# coding=utf-8
"""
UI自动化探索脚本的主入口文件
用于接收命令并执行相应的UI自动化操作
"""
import sys
import time
import threading
import socket
import os
import automation

def main(arg):
    """
    主函数：解析并执行UI自动化命令
    
    Args:
        arg: 命令行参数列表，arg[1]包含要执行的命令字符串
    """
    # 获取接收到的命令
    receviedcmd = arg[1]
    # 如果命令是'stop'，则直接返回
    if receviedcmd == 'stop':
        # automation.command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"exploration\"")
        return
    else:
        # 初始化执行结果为成功
        result = 'success'
        # 命令列表，用于存储解析后的多个命令
        cmds = []
        # 如果命令中包含'|'分隔符，则按'|'分割成多个命令
        if receviedcmd.find('|'):
            cmds = receviedcmd.split('|')
        else:
            # 否则将单个命令添加到列表中
            cmds.append(receviedcmd)
    # 遍历并执行每个命令
    for cmd in cmds:
        # 如果命令以'start'开头，启动指定的Activity
        if cmd.startswith('start'):
            activityname = cmd.split('---')
            automation.startactivity(activityname[1])
        # 如果命令以'stop'开头，停止指定的Activity
        elif cmd.startswith('stop'):
            activityname = cmd.split('---')
            # 如果包含3个部分，说明需要同时停止并收集覆盖率数据
            if len(activityname) == 3 :
                automation.stopactivitywithcoverage(activityname[1], activityname[2])
            else :
                # 否则只停止Activity
                automation.stopactivity(activityname[1])
        # 如果命令是'back'，执行返回操作
        elif cmd == 'back':
            automation.back()
        # 如果命令以'touch'开头，执行点击文本操作
        elif cmd.startswith('touch'):
            text = cmd.split('---')
            # 尝试滚动并点击指定文本，返回是否成功
            booleanresult = automation.scrolltoclickontext(text[1])
            # 如果操作失败，设置结果为失败并跳出循环
            if not booleanresult:
                result = 'failure'
                break;
        else:
            # 未知命令，打印错误信息
            print 'error cmd'
    # 输出执行结果
    print(result + '\r')
    # 输出分隔符
    print('---\r')
    # 输出当前屏幕的XML内容
    print(automation.getCurrentScreen() + '\r')
    # 输出结束标记
    print('end' + '\r')
if __name__ == '__main__':
	main(sys.argv)