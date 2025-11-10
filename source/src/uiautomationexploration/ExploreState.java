package uiautomationexploration;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import GUI.Main;
import data.InterestValue;
import sootproject.resourceLoader.PreferenceTreeNode;

/**
 * 探索状态抽象类
 * 定义了UI自动化探索的基本状态和行为
 */
public abstract class ExploreState {
	// 列表类型的UI控件类名集合
	private static Set<String> listclasses = new HashSet<String>();
	// 标题控件的ID集合
	private static Set<String> titleids = new HashSet<String>();
	// 标题控件的类名集合
	private static Set<String> titleclasses = new HashSet<String>();
	// 覆盖率文件计数器
	public static int numcount = 0;
	// 静态初始化块：初始化UI控件类型集合
	static {
		listclasses.add("android.widget.ListView");
		listclasses.add("android.support.v7.widget.RecyclerView");
		listclasses.add("androidx.recyclerview.widget.RecyclerView");
		listclasses.add("android.widget.ScrollView");
		titleids.add("android:id/title");
		titleclasses.add("android.widget.TextView");
	}
	
	
	// 标题映射（键 -> 标题集合）
	private static Map<Object, Set<String>> titlemap = null;
	// 待探索的所有节点映射（文件名 -> 节点集合）
	protected static Map<String, Set<PreferenceTreeNode>> tobeexploredallnodes = null;
	// 所有节点映射（文件名 -> 节点集合）
	protected static Map<String, Set<PreferenceTreeNode>> allnodes = null;
	// 当前步骤栈
	protected Stack<String> currentsteps = null;
	// 当前Activity名称
	protected String currentactivity = null;
	// 状态类型
	protected StateType statetype = StateType.DEFAULT;
	// 适配器对象
	protected static Adapter adapter = null;
//	protected static Set<PreferenceTreeNode> allnodes = null;
	// 探索状态栈
	protected static Stack<ExploreState> explorestates = null;
	
	/**
	 * 初始化探索状态
	 * 
	 * @param tobeviewedpreferenceforests 待查看的偏好设置树结构映射
	 * @param explorestates 探索状态栈
	 * @param adapter 适配器对象
	 */
	public static void init(Map<String, List<PreferenceTreeNode>> tobeviewedpreferenceforests, Stack<ExploreState> explorestates, Adapter adapter) {
		ExploreState.explorestates = explorestates;
		ExploreState.adapter = adapter;
//		ExploreState.allnodes = new HashSet<PreferenceTreeNode>();
		titlemap = new HashMap<Object, Set<String>>();
		tobeexploredallnodes = new HashMap<String, Set<PreferenceTreeNode>>();
		allnodes = new HashMap<String, Set<PreferenceTreeNode>>();
		numcount = 0;
		// 遍历所有偏好设置树结构
		for (String key : tobeviewedpreferenceforests.keySet()) {
			List<PreferenceTreeNode> nodes = tobeviewedpreferenceforests.get(key);
			Stack<PreferenceTreeNode> nodestack = new Stack<PreferenceTreeNode>();
			Set<String> titles = new HashSet<String>();
			Set<PreferenceTreeNode> allnode = new HashSet<PreferenceTreeNode>();
			Set<PreferenceTreeNode> tobeexplorednodes = new HashSet<PreferenceTreeNode>();
			// 将所有节点添加到栈中
			nodestack.addAll(nodes);
			// 遍历所有节点
			while(!nodestack.isEmpty()) {
				PreferenceTreeNode node = nodestack.pop();
				allnode.add(node);
				// 如果节点需要探索或是标题节点，添加到待探索集合
				if (node.shouldexplore || node.isheader) {
					tobeexplorednodes.add(node);
				}
				// 如果是偏好设置屏幕类型，将其子节点也添加到栈中
				if ("preferencescreen".equals(node.preferencetype)) {
					if (null != node.childnodes) {
						nodes.addAll(node.childnodes);
					}
				}
				// 如果节点有标题，添加到标题集合
				if (null != node.title) {
					titles.add(node.title);
				}
			}
			// 如果有待探索的节点，添加到映射中
			if (!tobeexplorednodes.isEmpty()) {
				tobeexploredallnodes.put(key, tobeexplorednodes);
			}
			titlemap.put(key, titles);
			allnodes.put(key, allnode);
		}
	}
	
//		List<PreferenceTreeNode> nodes = nownode.getChildnodes();
//		Set<String> titles = new HashSet<String>();
//					tobeexplorednodes.add(node);
//					addTitlesAndTobeexplored(titlemap, node, tobeexplorednodes);
//					titles.add(node.title);
//				titlemap.put(nownode, titles);
	/**
	 * 构造函数
	 * 初始化当前步骤栈
	 */
	public ExploreState() {
		currentsteps = new Stack<String>();
	}
	
	/**
	 * 获取探索命令（抽象方法）
	 * 
	 * @return 探索命令字符串
	 */
	public abstract String getCommond();
	
	/**
	 * 重置状态（抽象方法）
	 */
	public abstract void reset();
	
	/**
	 * 更新状态（抽象方法）
	 * 
	 * @param uicontent 当前UI内容的XML字符串
	 * @param success 上次操作是否成功
	 */
	public abstract void updatestate(String uicontent, boolean success);
	
	/**
	 * 读取并解析UI自动化输出的XML内容
	 * 适配uiautomator2的最新XML版本格式
	 * 
	 * @param uicontent UI内容的XML字符串
	 * @return 解析后的XML根元素，如果解析失败返回null
	 */
	protected static Element readXML(String uicontent) {
		SAXReader reader = new SAXReader();
		Document document = null;
		Element root = null;
		// 适配uiautomator2的最新XML版本格式
		// 查找"<hierarchy"标签的起始位置
		int tindex = uicontent.indexOf("<hierarchy");
		if (tindex > 0) {
			// 如果字符串以单引号结尾，移除末尾的单引号
			if (uicontent.endsWith("'")) {
				uicontent = uicontent.substring(tindex, uicontent.length() - 1);
			} else {
				// 否则从"<hierarchy"开始截取
				uicontent = uicontent.substring(tindex);
			}
		} else {
			System.out.println();
		}
		try {
			// 读取XML内容
			document = reader.read(new ByteArrayInputStream(uicontent.getBytes("UTF-8")));
			root = document.getRootElement();
		} catch (DocumentException e) {
			e.printStackTrace();
			System.out.println("warning:XML file reading fails! ");
		} catch (UnsupportedEncodingException e) {
			System.out.println("warning:XML file reading fails! ");
			e.printStackTrace();
		}
		return root;
	}
	
	/**
	 * 根据UI内容确定当前位置对应的偏好设置文件名或节点
	 * 通过比较屏幕标题与已知偏好设置的标题来匹配
	 * 
	 * @param inite UI的XML根元素
	 * @return 匹配的偏好设置文件名或节点，如果匹配度不够（少于3个标题匹配）返回null
	 */
	protected static Object getPosition(Element inite) {
		Set<String> screentitles = new HashSet<String>();
		// 从UI自动化输出中提取所有标题
		screentitles = (Set<String>)getAllTitlesFromUiAutomation(false, screentitles, inite);
		
		int maxnum = 0;
		Object maxkey = null;
		// 遍历所有已知的偏好设置，找到匹配度最高的
		for (Object key : titlemap.keySet()) {
			Set<String> keytitles = titlemap.get(key);
			int nownum = 0;
			// 统计匹配的标题数量
			for (String nowtitle : keytitles) {
				if (screentitles.contains(nowtitle)) {
					nownum++;
				}
			}
			// 更新最大匹配数和对应的键
			if (nownum > maxnum) {
				maxnum = nownum;
				maxkey = key;
			}
		}
		// 如果匹配的标题数大于等于3，返回对应的键
		if (null != maxkey && maxnum >=3) {
			return maxkey;
		}
		return null;
	}
	
	/**
	 * 从UI自动化输出中获取所有标题
	 * 先尝试检查ID，如果失败则尝试不检查ID
	 * 
	 * @param shouldadd 是否将标题添加到集合中
	 * @param tobetestedtitles 待测试的标题集合
	 * @param inite UI的XML根元素
	 * @return 包含所有标题的集合
	 */
	public static <T extends Collection<String>> T getAllTitlesFromUiAutomation(boolean shouldadd, T tobetestedtitles, Element inite) {
		// 先尝试检查ID
		T templist = getAllTitlesFromUiAutomationInner(shouldadd, tobetestedtitles, inite, true);
		// 如果结果为空，尝试不检查ID
		if (templist.isEmpty()) {
			templist = getAllTitlesFromUiAutomationInner(shouldadd, tobetestedtitles, inite, false);
		}
		return templist;
	}
	
	/**
	 * 从UI自动化输出中递归获取所有标题（内部方法）
	 * 
	 * @param shouldadd 是否将标题添加到集合中
	 * @param tobetestedtitles 待测试的标题集合
	 * @param inite 当前XML元素
	 * @param checkid 是否检查ID
	 * @return 包含所有标题的集合
	 */
	protected static <T extends Collection<String>> T getAllTitlesFromUiAutomationInner(boolean shouldadd, T tobetestedtitles, Element inite, boolean checkid) {
		String classname = inite.attributeValue("class");
		String id = inite.attributeValue("resource-id");
		String title = inite.attributeValue("text");
		// 如果是列表类型的控件，递归处理其子元素
		if (listclasses.contains(classname)) {
			for (Element child : (List<Element>)inite.elements()) {
				getAllTitlesFromUiAutomationInner(true, tobetestedtitles, child, checkid);
			}
		} else {
			// 检查是否包含标题ID
			boolean containsid = titleids.contains(id);
			// 如果满足标题条件（ID匹配或不需要检查ID，且类名匹配，且有文本），添加标题
			if ((containsid || !checkid) && titleclasses.contains(classname) && null != title) {
				if (shouldadd) {
					tobetestedtitles.add(title);
				}
			} else {
				// 否则递归处理子元素
				for (Element child : (List<Element>)inite.elements()) {
					getAllTitlesFromUiAutomationInner(shouldadd, tobetestedtitles, child, checkid);
				}
			}
		}
		return tobetestedtitles;
	}
	
	/**
	 * 更新适配器中的兴趣值
	 * 将探索到的步骤信息更新到对应的偏好设置节点
	 * 
	 * @param filename 偏好设置文件名
	 */
	protected static void updateAdapter(String filename) {
		String activityname = null;
		ArrayList<String> steps = new ArrayList<String>();
		// 收集所有探索状态中的步骤
		for (int i = 0; i < explorestates.size(); i++) {
			ExploreState nowstate = explorestates.get(i);
			// 第一个状态的Activity名称作为目标Activity
			if (i == 0) {
				activityname = nowstate.currentactivity;
			}
			// 收集当前状态的所有步骤
			if (!nowstate.currentsteps.isEmpty()) {
				for (int j = 0; j < nowstate.currentsteps.size(); j++) {
					steps.add(nowstate.currentsteps.get(j));
				}
			}
		}
		
		// 更新该文件对应的所有节点
		Set<PreferenceTreeNode> nodes = allnodes.get(filename);
		for (PreferenceTreeNode node : nodes) {
			InterestValue value = adapter.preferencelist.get(node.key);
			// 如果兴趣值存在且未适配，更新适配信息
			if (null != value && !value.isadapted) {
				value.isadapted = true;
				value.activityname = activityname;
				value.preferencesteps.clear();
				value.preferencesteps.addAll(steps);
				value.preferencesteps.addAll(node.getTitleWithinSamePage());
			}
			// 更新节点的标题列表
			ArrayList<String> templist = new ArrayList<String>(steps);
			templist.addAll(node.getTitleWithinSamePage());
			node.setTitles(templist);
//				node.activityname = activityname;
//			InterestValue value = adapter.preferencelist.get(node.key);
//				value.isadapted = true;
//				value.activityname = activityname;
		}
	}
	
	/**
	 * 生成恢复命令
	 * 用于从当前状态恢复到探索状态栈中的状态
	 * 
	 * @return 恢复命令字符串，如果状态栈为空返回null
	 */
	public String getResumeCom() {
		if (explorestates.isEmpty()) {
			System.out.println("error : the explorestates is empty when resume!!!");
			return null;
		}
		String cmd = "";
		// 获取第一个状态的Activity名称
		String activityname = explorestates.get(0).currentactivity;
		// 添加停止命令（带覆盖率收集）
		cmd += "stop---" + Main.packagename + "---" + numcount++ + "|";
		// 添加启动Activity命令
		cmd += "start---" + Main.packagename + "/" + activityname;
		// 获取最后一个状态
		ExploreState lastState = explorestates.peek();
		// 为除最后一个状态外的所有状态添加触摸命令
		for (ExploreState nowState : explorestates) {
			if (lastState != nowState) {
				for (String step : nowState.currentsteps) {
					cmd += "|touch---" + step;
				}
			}
		}
		// 重置最后一个状态
		lastState.reset();
		return cmd;
	}
	
	
}
