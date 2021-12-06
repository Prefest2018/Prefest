package tools;

import exception.EspressoTagnameNotFoundException;

public class GeneralUtils {
	public static String removeSuffixInEspressoTagname(Object tagname) {
		String newTagname = tagname.toString();
		String[] temps = newTagname.split("_");
		int startNum = 0;
		for (String t : temps) {
			try {
				Integer.parseInt(t);
			} catch (Exception e) {
				startNum = newTagname.indexOf(t);
				break;
			}
		}
		newTagname = newTagname.substring(startNum);
		
		
		if (newTagname.contains("#")) {
			return newTagname;
		}
		throw EspressoTagnameNotFoundException.getException();
	}
	
	public static String getTagnameInPreTurn(String newTagname) {
		if (!newTagname.contains("_")) {
			return newTagname;
		}
		int _num = 0;
		String result = null;
		for (int i = 0;i < newTagname.length(); i++) {
			char c = newTagname.charAt(i);
			if (c == '_') {
				_num++;
			}
			if (_num == 2) {
				result = newTagname.substring(i + 1);
				break;
			}
		}
		return result;
	}
	
	
}
