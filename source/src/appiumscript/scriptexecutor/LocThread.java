package appiumscript.scriptexecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import tools.ProcessExecutor;

public class LocThread extends Thread{
	private String loclogfileName = null;
	private StringBuilder locbuilder = new StringBuilder();
	private Process p = null;
	public static String ERROREXCEPTION = "errorException";
	public static String ERRORASSERT = "errorAssert";
	private boolean containsErrorLog = false;
	public LocThread() {
	}

	@Override
	public void run() {
		ProcessBuilder builder = ProcessExecutor.getPBInstance("adb", "logcat", "loc:V *:S");
		containsErrorLog = false;
		try {
			locbuilder = new StringBuilder();
	    	System.out.println("adb loc start:");
			p = builder.start();
	    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	String result = null;
	    	while(((result = p_stdout.readLine()) != null)) {
	    		if (!containsErrorLog && (result.contains(ERROREXCEPTION) || result.contains("ERRORASSERT"))) {
	    			containsErrorLog = true;
	    		}
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
			e.printStackTrace();
		}
	}
	
	public boolean locstop() {
		p.destroy();
		return !containsErrorLog;
	}
	
	public void setFile(String newfileName) {
		loclogfileName = newfileName;
	}
}
