package data;

import java.io.File;
import java.util.Set;

import tools.JsonHelper;
//This is comment by Huangruishen
public class CoverData {
	public Set<String> coveredTags = null;
	public int currentit = -1;
	
	private boolean shouldsave = true;
	public void update(String locfile) {
		int originNum = coveredTags.size();
		JsonHelper.readloclogs(coveredTags, new File(locfile));
		int currentNum = coveredTags.size();
		if (originNum != currentNum) {
			shouldsave = true;
		}
	}
	
	public void save(String filename) {
		if (shouldsave) {
			JsonHelper.setCoverData(filename, this);
			shouldsave = false;
		}
	}
	
	public void merge(CoverData anotherOne) {
		coveredTags.addAll(anotherOne.coveredTags);
	}
}
