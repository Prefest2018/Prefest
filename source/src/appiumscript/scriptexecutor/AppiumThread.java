package appiumscript.scriptexecutor;

import java.io.IOException;

public class AppiumThread extends Thread{
	Process appiumP = null;
	@Override
	public void run() {
		if (null == appiumP || !appiumP.isAlive()) {
			ProcessBuilder pb = new ProcessBuilder("appium");
			try {
				appiumP = pb.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private boolean checkinuse() {
		ProcessBuilder pb = new ProcessBuilder("node", "C:\\Program Files (x86)\\Appium\\resources\\app\\node_modules\\Appium", "-p", "4273", "-a", "0.0.0.0");
		return true;
	}
}
