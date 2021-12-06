package tools.tagselector;

import java.io.File;

import data.TestCaseData;

public class MostLogicSelector extends EffectTagSelector{

	protected boolean shouldExchange(TestCaseData data1, TestCaseData data2) {
		File locfile1 = new File(data1.firstlocpath);
		File locfile2 = new File(data2.firstlocpath);
		return locfile1.length() < locfile2.length();
	}
}
