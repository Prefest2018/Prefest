package espresso;

import java.util.HashMap;
import java.util.Map;

public class EspressoScriptName {
	public String activityName;
	public String methodName;
	public String activityPackage;
	public String activityShortName;
	public String shortName;
	public String longName;
	private static Map<String, String> shortName2LongNameMap = new HashMap<String, String>();
	public static String getLongName(String shortName) {
		return shortName2LongNameMap.get(shortName);
	}
	public EspressoScriptName(String activityName, String methodName) {
		this.activityName = activityName;
		this.methodName = methodName;
		this.activityShortName = activityName.substring(activityName.lastIndexOf('.') + 1);
		this.activityPackage = this.activityName.replace(activityShortName, "");
		this.shortName = this.activityShortName + "#" + this.methodName;
		this.longName = this.activityName + "#" + this.methodName;
		shortName2LongNameMap.put(this.shortName, this.longName);
	}
	
	public String toString() {
		return this.shortName;
	}
	
}
