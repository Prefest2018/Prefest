package tools.tagselector;

import java.util.ArrayList;
import java.util.Map;

import GUI.Main;
import data.TestCaseData;

public class EffectTagSelector {
	public static EffectTagSelector getInstance() {
		EffectTagSelector selector = null;
		switch(Main.tagSortStrategy) {
		case DEFAULT: {
			selector = new EffectTagSelector();
			break;
		}
		case MOSTLOGIC: {
			selector = new MostLogicSelector();
			break;
		}
		case RANDOM : {
			selector = new RandomSelector();
			break;
		}
		default : {
			selector = new EffectTagSelector();
		}
		}
		return selector;
	}
	
	
	public ArrayList<String> sort(Map<String, TestCaseData> datas) {
		ArrayList<String> sortedTagList = new ArrayList<String>();
		sortedTagList.addAll(datas.keySet());
		for (int i = 0; i < sortedTagList.size(); i++) {
			for (int j = i; j < sortedTagList.size(); j++) {
				String tagi = sortedTagList.get(i);
				String tagj = sortedTagList.get(j);
				TestCaseData data1 = datas.get(tagi);
				TestCaseData data2 = datas.get(tagj);
				if (shouldExchange(data1, data2)) {
					sortedTagList.set(i, tagj);
					sortedTagList.set(j, tagi);
				}
			}
		}
		return sortedTagList;
	}
	
	protected boolean shouldExchange(TestCaseData data1, TestCaseData data2) {
		int numi = data1.interestScenes==null?0:data1.interestScenes.size();
		int numj = data2.interestScenes==null?0:data2.interestScenes.size();
		return numi > numj;
	}
}
