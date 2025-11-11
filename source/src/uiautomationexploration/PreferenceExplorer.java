package uiautomationexploration;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import GUI.Main;
import data.InterestValue;
import sootproject.resourceLoader.PreferenceTreeNode;
import tools.JsonHelper;

/**
 * 偏好设置探索器
 * 负责管理UI自动化探索的状态和流程，生成探索命令并更新探索状态
 */
public class PreferenceExplorer {
	// 待查看的偏好设置Activity栈
	private Stack<String> tobeviewedpreferenceactivities = null;
	// 待查看的偏好设置树结构映射（文件名 -> 节点列表）
	private Map<String, List<PreferenceTreeNode>> tobeviewedpreferenceforests = null;
	// 探索状态栈
	private Stack<ExploreState> explorestates = null;
	
//	private boolean findnewpages = false;
	// 适配器对象，包含偏好设置相关信息
	private Adapter adapter = null;
	
//		this.adapter = new Adapter();
//		inittoviewlist();
	
	// 是否应该停止探索的标志
	public boolean shouldstop = false;
	
	/**
	 * 构造函数
	 * 
	 * @param adapter 适配器对象，包含偏好设置相关信息
	 */
	public PreferenceExplorer(Adapter adapter) {
		this.adapter = adapter;
		inittoviewlist();
	}

	/**
	 * 初始化待查看列表
	 * 将适配器中的Activity和偏好设置树结构添加到待查看列表中
	 */
	private void inittoviewlist() {
		// 初始化Activity栈
		tobeviewedpreferenceactivities = new Stack<String>();
		// 将所有可能的Activity添加到栈中
		for (String activityname : this.adapter.possibleactivities) {
			tobeviewedpreferenceactivities.push(activityname);
		}
		// 初始化偏好设置树结构映射
		tobeviewedpreferenceforests = new HashMap<String, List<PreferenceTreeNode>>(this.adapter.xmlcontentlist);
		// 初始化探索状态栈
		explorestates = new Stack<ExploreState>();
		// 初始化探索状态
		ExploreState.init(tobeviewedpreferenceforests, explorestates, adapter);
	}
	
	
//		return false;
	
	/**
	 * 生成下一个探索命令
	 * 
	 * @return 探索命令字符串，如果探索完成则返回"stop"
	 */
	public String givecommond() {
		while(true) {
			// 如果探索状态栈为空，尝试获取新状态
			if (explorestates.isEmpty()) {
				boolean newstatesuccess = getNewState();
				// 如果无法获取新状态，标记探索完成并返回停止命令
				if (!newstatesuccess) {
					adapter.explored = true;
					return "stop";
				}
			}
			// 获取栈顶的探索状态并生成命令
			ExploreState state = explorestates.peek();
			String commond = state.getCommond();
			return commond;
		}
	}
	
	/**
	 * 获取新的探索状态
	 * 优先从Activity列表获取，如果Activity列表为空则从偏好设置树获取
	 * 
	 * @return 如果成功获取新状态返回true，否则返回false
	 */
	private boolean getNewState() {
		// 如果还有待查看的Activity，创建屏幕定位状态
		if (!tobeviewedpreferenceactivities.isEmpty()) {
			String activity = tobeviewedpreferenceactivities.pop();
			ScreenLocatingState newstate = ScreenLocatingState.getScreenLocatingState(activity);
			explorestates.push(newstate);
			return true;
		} else  {
			// 如果Activity列表为空，从偏好设置树中获取节点探索状态
			while (!tobeviewedpreferenceforests.isEmpty()) {
				String nowkey = null;
				// 获取第一个键
				for (String key : tobeviewedpreferenceforests.keySet()) {
					nowkey = key;
					break;
				}
				// 移除该键并创建节点探索状态
				tobeviewedpreferenceforests.remove(nowkey);
				NodeExploreState newstate = NodeExploreState.getNodeExploreState(nowkey, true);
				if (null != newstate) {
					explorestates.push(newstate);
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * 更新探索状态
	 * 
	 * @param uicontent 当前UI内容的XML字符串
	 * @param success 上次操作是否成功
	 */
	public void updatestate(String uicontent, boolean success) {
		// 如果探索状态栈不为空，更新栈顶状态
		if (!explorestates.isEmpty()) {
			ExploreState state = explorestates.peek();
			state.updatestate(uicontent, success);
		}
	}
	
	/**
	 * 保存适配器到文件
	 * 标记适配器为已探索状态并保存
	 */
	public void saveAdater() {
		adapter.explored = true;
		JsonHelper.saveadapter(adapter, Main.testadapter);
	}

//		JSONObject jsonall = JsonHelper.getJsonObject(testcasedata.getAbsolutePath());
//
//		Map<String, InterestValue> adaptInterests = adapter.preferencelist;
//		JSONArray interestmap = (JSONArray)jsonall.get("interestmap");
//			JSONObject interestvalue = (JSONObject)interestmap.get(i);
//			String name = (String)interestvalue.get("name");
//			InterestValue value = adaptInterests.get(name);
//				continue;
//				interestvalue.put("activityextra", value.activityextra);
//				interestvalue.put("activityname", value.activityname);
//				JSONArray preferencesteps = new JSONArray();
//				preferencesteps.addAll(value.preferencesteps);
//				interestvalue.put("preferencesteps", preferencesteps);
//		JsonHelper.saveJsonFile(jsonall, newtestcasedata.getAbsolutePath());


	
}
