package appiumscript.scriptexecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class LocThread extends Thread{
	private String loclogfileName = null;
	private StringBuilder locbuilder = new StringBuilder();
	private Process p = null;
	public LocThread() {
	}

	@Override
	public void run() {
		ProcessBuilder builder = new ProcessBuilder("adb", "logcat", "loc:V *:S");

		try {
			locbuilder = new StringBuilder();
	    	System.out.println("adb loc start:");
			p = builder.start();
	    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	String result = null;
	    	while(((result = p_stdout.readLine()) != null)) {
	    		locbuilder.append(result+"\n");
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("adb loc end:");
    	try {
        	File outputfile = new File(loclogfileName);
			outputfile.createNewFile();
	    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputfile), "UTF-8"));
	    	bw.write(locbuilder.toString());
	    	bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void locstop() {
		p.destroy();
	}
	
	public void setFile(String newfileName) {
		loclogfileName = newfileName;
	}
}
