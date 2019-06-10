package tools;

import sun.awt.OSInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

public class PublishPathHelper extends PathHelper {

	@Override
	protected String getSootPathImp() {
		return projectpath + File.separator + "lib" + File.separator + "sootclasses-trunk-jar-with-dependencies.jar";
	}

	@Override
	protected String getDebugKeyPathImp() {
		return projectpath + File.separator + "lib" + File.separator + "debug.keystore";
	}

	@Override
	protected String getJadxPathImp() {
		return projectpath + File.separator + "lib" + File.separator + "jadx";
	}

	@Override
	protected URL getFXMLURLImp() {
		return getClass().getResource("/PREFEST.fxml");
	}

	@Override
	protected InputStream getPICTStreamImp(int num) {
		File pictfile = new File(projectpath + File.separator + "lib" + File.separator + "pairwise" + File.separator + "pict" + num + ".txt");
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
			return projectpath + File.separator + "config-mac.txt";
		} else if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) {
			return projectpath + File.separator + "config-win.txt";
		} else {
			throw new RuntimeException("Unsupported operation system");
		}
	}

	@Override
	protected String getUIAutomatorClientPathImp() {
		return projectpath + File.separator + "lib" + File.separator + "uiautomatorclient";
	}


}
