package tools;

import exception.EspressoTagnameNotFoundException;

/**
 * 通用工具类
 * 提供各种通用的字符串处理和转换方法
 */
public class GeneralUtils {
	/**
	 * 从Espresso标签名中移除数字前缀后缀
	 * 例如：将"123_456_tagname#id"转换为"tagname#id"
	 * 
	 * @param tagname 原始标签名对象
	 * @return 移除数字前缀后的标签名，必须包含"#"
	 * @throws EspressoTagnameNotFoundException 如果结果不包含"#"则抛出异常
	 */
	public static String removeSuffixInEspressoTagname(Object tagname) {
		String newTagname = tagname.toString();
		// 按"_"分割标签名
		String[] temps = newTagname.split("_");
		int startNum = 0;
		// 找到第一个非数字的部分，作为标签名的起始位置
		for (String t : temps) {
			try {
				// 尝试将部分解析为整数，如果是数字则继续
				Integer.parseInt(t);
			} catch (Exception e) {
				// 找到第一个非数字部分，记录其起始位置
				startNum = newTagname.indexOf(t);
				break;
			}
		}
		// 截取从第一个非数字部分开始的子字符串
		newTagname = newTagname.substring(startNum);
		
		
		// 验证结果必须包含"#"
		if (newTagname.contains("#")) {
			return newTagname;
		}
		// 如果不包含"#"，抛出异常
		throw EspressoTagnameNotFoundException.getException();
	}
	
	/**
	 * 获取标签名中第二个下划线之后的部分
	 * 例如：将"prefix_suffix_tagname"转换为"tagname"
	 * 
	 * @param newTagname 标签名字符串
	 * @return 如果包含下划线，返回第二个下划线之后的部分；否则返回原字符串
	 */
	public static String getTagnameInPreTurn(String newTagname) {
		// 如果不包含下划线，直接返回原字符串
		if (!newTagname.contains("_")) {
			return newTagname;
		}
		// 统计下划线数量
		int _num = 0;
		String result = null;
		// 遍历字符串，找到第二个下划线
		for (int i = 0;i < newTagname.length(); i++) {
			char c = newTagname.charAt(i);
			if (c == '_') {
				_num++;
			}
			// 找到第二个下划线后，提取之后的部分
			if (_num == 2) {
				result = newTagname.substring(i + 1);
				break;
			}
		}
		return result;
	}
	
	
}
