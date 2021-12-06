package GUI;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import tools.PathHelper;

public class TestEntry {
	private static String[] skipList = {"AmazonKindle", "Gmail", "Instagram", "NOOK", "GPBooks", "Waze", "YouTube"};
	
	public static void main(String[] arg) {
    	File sootstublog = new File("sootstublog.log");
    	if (!sootstublog.exists()) {
    		try {
				sootstublog.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	BufferedWriter bw = null;
    	try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sootstublog)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	File configFile = new File(PathHelper.getConfigFilePath());
//    	Map<String, String> newenvs = FXGUI.readConfigFile(configFile);
//			FXGUI.setEnv(newenvs);
//			// TODO Auto-generated catch block
//			e.printStackTrace();
    	File folder = new File("C:\\Users\\yifeiLu\\Documents\\Git_Space\\PREFEST\\projecthome_in");
    	total:for (File file : folder.listFiles()) {
    		System.out.println(file.getName());
    		for (String name: skipList) {
    			if (name.equals(file.getName())) {
    				continue total;
    			}
    		}
    		File newfile = new File(file, "app");
    		boolean shouldsoot = true;
    		for (File stubfile: newfile.listFiles()) {
    			if (stubfile.getName().endsWith("_stub.apk")) {
    				shouldsoot = false;
    				break;
    			}
    		}
    		if (shouldsoot) {
    			Main main = new Main();
    			main.updateHome(file.getAbsolutePath());
					main.stub();
					break total;
//					e.printStackTrace();
//						bw.write(file.getName() + " soot stub fails!!!");
//						bw.newLine();
//						bw.write(e.getMessage());
//						bw.newLine();
//						e1.printStackTrace();
//
    		}
    	}

//		Main main = new Main();
//        main.exploreForAdapter();
//        main.PREFEST_T();

	}
}
