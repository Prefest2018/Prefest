package uiautomationexploration;

/**
 * 探索状态类型枚举
 * 定义了UI自动化探索过程中可能的状态类型
 */
public enum StateType {
	/** 开始状态 */
	START, 
	/** 结束状态 */
	END, 
	/** 探索中状态 */
	EXPLORING, 
	/** 暂停状态 */
	SUSPEND, 
	/** 恢复状态 */
	RESUME, 
	/** 全部恢复状态 */
	RESUMEALL, 
	/** 默认状态 */
	DEFAULT
}
