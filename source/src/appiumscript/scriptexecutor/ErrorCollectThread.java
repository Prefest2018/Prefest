package appiumscript.scriptexecutor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.locks.ReentrantLock;

import GUI.Main;
import tools.ProcessExecutor;

public class ErrorCollectThread extends Thread{
	private boolean shouldlogerror = false;
	private File errorlogFile = null;
	private StringBuilder errorlogBuilder = new StringBuilder();
	private Process p = null;
	private boolean hasbug = false;
	private ReentrantLock lock = new ReentrantLock();
	public ErrorCollectThread() {
	}

	@Override
	public void run() {
		ProcessBuilder builder = ProcessExecutor.getPBInstance("adb", "logcat", "AndroidRuntime:E CrashAnrDetector:D ActivityManager:E SQLiteDatabase:E WindowManager:E ActivityThread:E Parcel:E *:F *:S");
//		ProcessBuilder builder = new ProcessBuilder("adb", "logcat", "AndroidRuntime:E *:S");

		try {
	    	System.out.println("adb error log start:");
			p = builder.start();
	    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    	String result = null;
	    	while(((result = p_stdout.readLine()) != null)) {
	    		lock.lock();
	    		if (result.contains("AndroidRuntime")) {
	    			hasbug = true;
	    		}
		    	BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errorlogFile, true), "UTF-8"));
		    	bw.write(result + "\n");
		    	bw.close();
		    	lock.unlock();
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	System.out.println("adb error log end:");
		
	}
	
	public void addIndex(String indexInfo) {
		lock.lock();
    	BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(errorlogFile, true), "UTF-8"));
	    	bw.write(indexInfo + "\n");
	    	bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lock.unlock();
	}
	
	public void setErrorFile(String errorlogfile) {
		this.errorlogFile = new File(errorlogfile);
		if (!errorlogFile.exists()) {
			try {
				errorlogFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public boolean errorlogstop() {
		p.destroy();
		boolean result = hasbug;
		hasbug = false;
		return result;
	}
}
