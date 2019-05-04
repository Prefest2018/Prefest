package appiumscript.scriptexecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import GUI.Main;

public class AVDThread extends Thread{
	private boolean hasavd = false;
	private static File sdktoolfolder = null;
	static {
		Map<String, String> envmap = System.getenv();
		sdktoolfolder = new File(envmap.get("ANDROID_HOME") + File.separator + "tools");
	}
	
	@Override
	public void run() {
		while(true) {
			try {
				ProcessBuilder pb = new ProcessBuilder("adb", "devices");
				Process p = pb.start();
		    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    	String result = null;
		    	hasavd = false;
				while(((result = p_stdout.readLine()) != null)) {
					if (result.contains("emulator-")) {
						hasavd = true;
					}
				}
				if (!hasavd) {
					pb = new ProcessBuilder("emulator", "-writable-system", "-avd", Main.avdname);

					pb.directory(sdktoolfolder);
					p = pb.start();
					p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while(((result = p_stdout.readLine()) != null)) {
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    	
		}
	}
	
}
