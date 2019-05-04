package tools;

import java.util.Comparator;

import GUI.Main;
import data.TestCaseData;

public class TagnameComparator<T> implements Comparator<T>{

	@Override
	public int compare(T o1, T o2) {
		String o1str = null;
		String o2str = null;
		if (o1 instanceof TestCaseData && o2 instanceof TestCaseData) {
			o1str = ((TestCaseData)o1).tagname;
			o2str = ((TestCaseData)o2).tagname;
		} else if (o1 instanceof String && o2 instanceof String) {
			o1str = (String)o1;
			o2str = (String)o2;
		}
		
		if (Main.PRESCRIPT.equals(o1str)) {
			return -1;
		} else if (Main.PRESCRIPT.equals(o2str)) {
			return 1;
		} else {
			if (!o1str.contains("_") || !o2str.contains("_")) {
				return o1str.compareTo(o2str);
			}
			String[] o1tags = o1str.split("_");
			String[] o2tags = o2str.split("_");
			int o11 = Integer.parseInt(o1tags[0]);
			int o21 = Integer.parseInt(o2tags[0]);

			if (o11 < o21) {
				return -1;
			} else if (o11 > o21) {
				return 1;
			} else {
				int o12 = Integer.parseInt(o1tags[1]);
				int o22 = Integer.parseInt(o2tags[1]);
				if (o12 < o22) {
					return -1;
				} else if (o12 > o22){
					return 1;
				} else {
					if (o1tags.length > 2 && o2tags.length > 2) {
						int o13 = Integer.parseInt(o1tags[2]);
						int o23 = Integer.parseInt(o2tags[2]);
						if (o13 < o23) {
							return -1;
						} else {
							return 1;
						}
					} else {
						return -1;
					}
				}
			}
		}
	}
}
