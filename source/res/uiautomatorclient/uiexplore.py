# coding=utf-8
import sys
import time
import threading
import socket
import os
import automation
def main(arg):
    receviedcmd = arg[1]
    if receviedcmd == 'stop':
        # automation.command("adb shell am broadcast -a com.example.pkg.END_EMMA --es name \"exploration\"")
        return
    else:
        result = 'success'
        cmds = []
        if receviedcmd.find('|'):
            cmds = receviedcmd.split('|')
        else:
            cmds.append(receviedcmd)
    for cmd in cmds:
        if cmd.startswith('start'):
            activityname = cmd.split('---')
            automation.startactivity(activityname[1])
        elif cmd.startswith('stop'):
            activityname = cmd.split('---')
            if len(activityname) == 3 :
                automation.stopactivitywithcoverage(activityname[1], activityname[2])
            else :
                automation.stopactivity(activityname[1])
        elif cmd == 'back':
            automation.back()
        elif cmd.startswith('touch'):
            text = cmd.split('---')
            booleanresult = automation.scrolltoclickontext(text[1])
            if not booleanresult:
                result = 'failure'
                break;
        else:
            print('error cmd')
    print(result + '\r')
    print('---\r')
    print(str(automation.getCurrentScreen()) + '\r')
    print('end' + '\r')
if __name__ == '__main__':
	main(sys.argv)