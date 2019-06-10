package appiumscript.scriptexecutor;

import java.io.IOException;

import tools.ProcessExecutor;

public class AppiumThread extends Thread{
	Process appiumP = null;
	@Override
	public void run() {
		if (null == appiumP || !appiumP.isAlive()) {
			ProcessExecutor.processnolognoprint("appium");
		}
	}
	
	
	private boolean checkinuse() {
		ProcessBuilder pb = new ProcessBuilder("node", "C:\\Program Files (x86)\\Appium\\resources\\app\\node_modules\\Appium", "-p", "4273", "-a", "0.0.0.0");
		return true;
	}
}
