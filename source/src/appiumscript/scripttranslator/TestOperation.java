package appiumscript.scripttranslator;

public class TestOperation {
	public TestOperationType type = TestOperationType.DEFAULT;
	public String operationType = null;
	public String resourceId = null;
	public String className = null;
	public String contentDesc = null;
	public String direction = null;
	public String text = null;
	public int instance = -1;
	public double fromx = 0;
	public double tox = 0;
	public double fromy = 0;
	public double toy = 0;
	private String total = null;
	private String element = null;
	private String operation = null;
	
	public TestOperation() {
		type = TestOperationType.NORMAL;
	}
	
	
	public String getTestLine(int tab) {
		String tabs = "";
		for (int i = 0; i < tab; i++) {
			tabs += "\t";
		}
		switch (this.type) {
		case MENU : {
			total = tabs + "driver.press_keycode(82)";
			break;
		}
		case BACK : {
			total = tabs + "driver.press_keycode(4)";
			break;
		}
		case SLEEP : {
			total = tabs + "time.sleep(1)";
		}
		case NORMAL : {
			operation = getTestOperation(tabs);
			element = getTestElement(tabs);
			total = element + operation;
			break;
		}
		}
		return total;
	}
	
	private String getTestOperation(String tabstr) {
		if (null == operationType) {
			System.out.println();
		}
		switch(operationType) {
		case "scroll" : {
			if (null == direction || direction.equals("default")) {
				operation = tabstr + "swipe(driver, " + fromx + ", " + fromy + ", " + tox + ", " + toy + ")";
			} else if (direction.equals("down")) {
				operation = tabstr + "swipe(driver, 0.5, 0.2, 0.5, 0.7)";
			} else if (direction.equals("up")) {
				operation = tabstr + "swipe(driver, 0.5, 0.7, 0.5, 0.2)";
			} else if (direction.equals("left")) {
				operation = tabstr + "swipe(driver, 0.7, 0.5, 0.2, 0.5)";
			} else if (direction.equals("right")) {
				operation = tabstr + "swipe(driver, 0.2, 0.5, 0.7, 0.5)";
			}
			break;
		}
		case "click" : {
			operation = tabstr + "TouchAction(driver).tap(element).perform()";
			break;
		}
		case "clickLong" : {
			operation = tabstr + "TouchAction(driver).long_press(element).release().perform()";
			break;
		}
		case "edit" : {
			operation = tabstr + "element.clear()\r\n" + tabstr + "element.send_keys(\"" + text + "\");";
		}
		}
		return operation;
	}
	
	private String getTestElement(String tabstr) {
		if (operationType.equals("scroll")) {
			return "";
		}
		element = null;

		String uiautomation1 = null;
		String uiautomation2 = null;
		boolean usetext = false;
		
		uiautomation2 = "\"new UiSelector()";
		if (null != resourceId) {
			uiautomation2 += ".resourceId(\\\"" + resourceId + "\\\")";
		}
		if (null != className) {
			uiautomation2 += ".className(\\\"" + className + "\\\")";
		}
		if (null != contentDesc) {
			uiautomation2 += ".description(\\\"" + contentDesc + "\\\")";
		}
		if (instance > 0) {
			uiautomation2 += ".instance(" + instance + ")";
		}
		if (null != text && !text.equals("")) {
			if (!"edit".equals(operationType)) {
				usetext = true; 
			}
			uiautomation1 = "\"new UiSelector().text(\\\"" + text + "\\\")\"";           
		}
		if (usetext) {
			element = tabstr + "element = getElememtBack(driver, " + uiautomation1 + ", " + uiautomation2;
		} else {
			element = tabstr + "element = getElememt(driver, " + uiautomation2;
		}
		
		element = element + "\")\r\n";
		return element;
	}
}
