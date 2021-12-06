package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import appiumscript.scripttranslator.TestOperation;
import sootproject.analysedata.MyInterest;
import sootproject.myexpression.ResultType;
import sootproject.resourceLoader.PreferenceTreeNode;

public class PreferenceAdaptData {
	public String key = null;
	public ResultType type = ResultType.DEFAULT;
	public boolean adapted = false;
	public ArrayList<TestOperation> presteps = null;
	public ArrayList<TestOperation> consteps = null;
	public Map<String, ArrayList<TestOperation>> valuestepmap = null;

	public static PreferenceAdaptData getExampleInstance(String key) {
		PreferenceAdaptData exinstance = new PreferenceAdaptData();
		exinstance.key = key;
		exinstance.adapted = false;
		exinstance.type = ResultType.STRING;
		
		exinstance.presteps = new ArrayList<TestOperation>();
		TestOperation preop = new TestOperation();
		preop.operationType = "click";
		preop.className = "android.widget.TextView";
		preop.resourceId = "a2dp.Vol:id/enableTTSBox";
		preop.instance = 8;
		exinstance.presteps.add(preop);
		
		exinstance.consteps = new ArrayList<TestOperation>();
		TestOperation conop = new TestOperation();
		conop.operationType = "click";
		conop.text = "OK";
		exinstance.consteps.add(conop);
		
		exinstance.valuestepmap = new HashMap<String, ArrayList<TestOperation>>();
		TestOperation valueop1 = new TestOperation();
		valueop1.operationType = "click";
		valueop1.text = "value1";
		ArrayList<TestOperation> list1 = new ArrayList<TestOperation>();
		list1.add(valueop1);
		exinstance.valuestepmap.put("value1", list1);
		TestOperation valueop2 = new TestOperation();
		valueop2.operationType = "scroll";
		valueop2.fromx = 0.5;
		valueop2.tox = 0.5;
		valueop2.fromy = 0.2;
		valueop2.toy = 0.8;
		ArrayList<TestOperation> list2 = new ArrayList<TestOperation>();
		list2.add(valueop2);
		exinstance.valuestepmap.put("value2", list2);
		
		return exinstance;
	}

}
