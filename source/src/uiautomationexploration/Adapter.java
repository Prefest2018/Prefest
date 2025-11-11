package uiautomationexploration;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import data.InterestValue;
import sootproject.resourceLoader.PreferenceTreeNode;

/**
 * 适配器类
 * 存储偏好设置相关的信息，包括兴趣值、可能的Activity、偏好设置文件与Activity的映射等
 */
public class Adapter {
	// 是否已探索的标志
	public boolean explored = false;
	// 偏好设置兴趣值映射（键 -> 兴趣值）
	public Map<String, InterestValue> preferencelist = null;
	// 可能的Activity集合
	public Set<String> possibleactivities = null;
	// 偏好设置文件名到Activity的映射
	public Map<String, String> preferencefilename2activity = null;
	// XML内容列表（文件名 -> 偏好设置节点列表）
	public Map<String, List<PreferenceTreeNode>> xmlcontentlist = null;
	
	/**
	 * 默认构造函数
	 */
	public Adapter() {}
	
	/**
	 * 带参数的构造函数
	 * 
	 * @param possibleactivities 可能的Activity集合
	 * @param preferencefilename2activity 偏好设置文件名到Activity的映射
	 * @param xmlcontentlist XML内容列表
	 */
	public Adapter(Set<String> possibleactivities, Map<String, String> preferencefilename2activity, Map<String, List<PreferenceTreeNode>> xmlcontentlist) {
		this.possibleactivities = possibleactivities;
		this.preferencefilename2activity = preferencefilename2activity;
		this.xmlcontentlist = xmlcontentlist;
	}
}
