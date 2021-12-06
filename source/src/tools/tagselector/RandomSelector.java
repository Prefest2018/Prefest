package tools.tagselector;

import java.io.File;

import data.TestCaseData;

public class RandomSelector extends EffectTagSelector{
	protected boolean shouldExchange(TestCaseData data1, TestCaseData data2) {
		return Math.random() > 0.5;
	}
}
