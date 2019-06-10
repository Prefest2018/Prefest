package uiautomationexploration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import GUI.Main;
import data.InterestValue;
import tools.Logger;
import tools.PathHelper;
import tools.ProcessExecutor;
 
public class ExplorerServer {
    private PreferenceExplorer explorer = null;
    private Adapter adapter = null;
    public ExplorerServer(Adapter adapter) {
    	PreferenceExplorer explorer = new PreferenceExplorer(adapter);
        this.explorer = explorer;
        this.adapter = adapter;
    }
    
    public void start() {

        System.out.println("in handling..");
        Date startTime = new Date();
        String commond = null;
    	while(true) {
    		commond = explorer.givecommond();
        	System.out.println(commond);
        	if (commond.equals("stop")) {
        		break;
        	}
        	Process p = null;
			ProcessBuilder builder = ProcessExecutor.getPBInstance("python", "uiexplore.py", commond);
			try {
				builder.directory(new File(PathHelper.getUIAutomatorClientPath()));
				p = builder.start();
		    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    	String contentline = null;
	            StringBuilder successsb = new StringBuilder();
	            StringBuilder contentsb = new StringBuilder();
	            int index = 0;
	            while((contentline = p_stdout.readLine())!= null) {
	            	if (contentline.equals("end")) {
	            		break;
	            	}
	            	if (contentline.equals("---")) {
	            		index++;
	            		continue;
	            	}
	            	switch (index) {
	            	case 0: successsb.append(contentline);break;
	            	case 1: contentsb.append(contentline);break;
	            	}
	            	
	            }
	            explorer.updatestate(contentsb.toString(), successsb.toString().equals("success")?true:false);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	 Date endTime = new Date();
    	 long milltimes = endTime.getTime() - startTime.getTime();
    	 Logger.setTempLogFile(Main.interestplan, true);
    	 String timelog = "This exploration took time: " + milltimes/1000 + "s";
    	 System.out.println(timelog);
    	 Logger.log(timelog);
    	 for (int i = 0; i < ExploreState.numcount; i++) {
 			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + "coverage" + i + ".ec", Main.testadpatercoverage + File.separator  + "adapter" + i + ".ec");
    	 }
    	 
    }
    
    
    public void exploreForAdapter() {
    	if (!this.adapter.explored) { 
    		if (!checkOKInterestValue()) {
    	    	start();
    		} else {
    			this.adapter.explored = true;
    		}
	    	explorer.saveAdater();
		}
	    explorer.adaptData();
    }
    
    private boolean checkOKInterestValue() {

    	boolean ok = true;
    	for (InterestValue value : this.adapter.preferencelist.values()) {
    		if ("preference".equals(value.generaltype) && !value.isadapted) {
    			ok = false;
    			break;
    		}
    	}
    	return ok;
    }
 
 
}
 