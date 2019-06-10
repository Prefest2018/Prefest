package tools;

import sun.awt.OSInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class DebugPathHelper extends PathHelper {
	@Override
	protected String getSootPathImp() {
		return projectpath + "/lib/sootclasses-trunk-jar-with-dependencies.jar";
	}
	@Override
	protected String getDebugKeyPathImp() {
		return projectpath + "/res/debug.keystore";
	}
	@Override
	protected String getJadxPathImp() {
		return projectpath + "/lib/jadx";
	}
	@Override
	protected URL getFXMLURLImp() {
		File fxmlfile = new File(projectpath + "/res/PREFEST.fxml");
		URL url = null;
		try {
			url = fxmlfile.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	@Override
	protected InputStream getPICTStreamImp(int num) {
		File pictfile = new File(projectpath + "/res/pairwise/pict" + num + ".txt");
		InputStream stream = null;
		try {
			stream =  new FileInputStream(pictfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return stream;
	}
	@Override
	protected String getConfigFileImp() {
		if (OSInfo.getOSType() == OSInfo.OSType.MACOSX || OSInfo.getOSType() == OSInfo.OSType.LINUX) {
			return projectpath + "/res/config-mac.txt";
		} else if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) {
			return projectpath + "/res/config-win.txt";
		} else {
			throw new RuntimeException("Unsupported operation system");
		}
	}
	@Override
	protected String getUIAutomatorClientPathImp() {
		return projectpath + "/res/uiautomatorclient";
	}
}
