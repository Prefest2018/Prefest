package appiumscript.scripttranslator;


public class TestOperation {
	protected TestOperationType type = TestOperationType.DEFAULT;
	protected String operationType = null;
	protected String resourceId = null;
	protected String className = null;
	protected String contentDesc = null;
	protected String direction = null;
	protected String text = null;
	protected int instance = -1;
	
	private String total = null;
	private String element = null;
	private String operation = null;
	
	public TestOperation() {
		type = TestOperationType.NORMAL;
	}
	
	
	public String getTestLine() {
		switch (this.type) {
		case MENU : {
			total = "\tdriver.press_keycode(82)";
			break;
		}
		case BACK : {
			total = "\tdriver.press_keycode(4)";
			break;
		}
		case NORMAL : {
			operation = getTestOperation();
			element = getTestElement();
			total = element + operation;
			break;
		}
		}
		return total;
	}
	
	private String getTestOperation() {
		switch(operationType) {
		case "scroll" : {
			if (direction.equals("down")) {
				operation = "\tswipe(driver, 0.5, 0.2, 0.5, 0.8)";
			} else if (direction.equals("up")) {
				operation = "\tswipe(driver, 0.5, 0.8, 0.5, 0.2)";
			}
			break;
		}
		case "click" : {
			operation = "\tTouchAction(driver).tap(element).perform()";
			break;
		}
		case "clickLong" : {
			operation = "\tTouchAction(driver).long_press(element).release().perform()";
			break;
		}
		case "edit" : {
			operation = "\telement.clear()\r\n\telement.send_keys(\"" + text + "\");";
		}
		}
		return operation;
	}
	
	private String getTestElement() {
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
			element = "\telement = getElememtBack(driver, " + uiautomation1 + ", " + uiautomation2;
		} else {
			element = "\telement = getElememt(driver, " + uiautomation2;
		}
		
		element = element + "\")\r\n";
		return element;
	}
}
