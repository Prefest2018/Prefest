package tools;

import java.io.InputStream;
import java.net.URL;

import GUI.Main;
public abstract class PathHelper {
	private static boolean debug = Main.debug;
	private static PathHelper instance = debug?new DebugPathHelper():new PublishPathHelper();
	public static String getJavaHome() {
		String javahome = System.getenv("JAVA_HOME");
		return javahome;
	}
	public static String getAndroidSDKHome() {
		String androidhome = System.getenv("ANDROID_HOME");
		return androidhome;
	}

	public static String projectpath = System.getProperty("user.dir");
	public static String getSootPath() {
		return instance.getSootPathImp();

	}
	public static String getDebugKeyPath() {
		return instance.getDebugKeyPathImp();
	}
	
	public static URL getFXMLURL() {
		return instance.getFXMLURLImp();
	}
	
	public static InputStream getPICTStream(int num) {
		return instance.getPICTStreamImp(num);
	}
	
	public static String getConfigFilePath() {
		return instance.getConfigFileImp();
	}
	
	public static String getUIAutomatorClientPath() {
		return instance.getUIAutomatorClientPathImp();
	}
	
	protected abstract String getSootPathImp();
	protected abstract String getDebugKeyPathImp();
	protected abstract URL getFXMLURLImp();
	protected abstract InputStream getPICTStreamImp(int num);
	protected abstract String getConfigFileImp();
	protected abstract String getUIAutomatorClientPathImp();
}
