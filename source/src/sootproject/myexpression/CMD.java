package sootproject.myexpression;

import java.util.HashMap;

import soot.Body;
import sootproject.data.MyMethodDeclaration;
public class CMD {
	public Object instrument = null;
	public Object arg = null;
	public CMDType type = null;
	public Body body = null;
	public String preloc = null;
	public MyMethodDeclaration invokemethod = null;
	public HashMap<Integer, MyVariable> localmap = null;
	public HashMap<Integer, MyVariable> templocalmap = null;

	public CMD(Object instrument, Object arg, CMDType type, Body body, HashMap<Integer, MyVariable> localmap, HashMap<Integer, MyVariable> templocalmap) {
		this.instrument = instrument;
		this.arg = arg;
		this.type = type;
		this.body = body;
		this.localmap = localmap;
		this.templocalmap = templocalmap;
	}
	
	public CMD append(String preloc) {
		this.preloc = preloc;
		return this;
	}
	public CMD append(MyMethodDeclaration invokemethod) {
		this.invokemethod = invokemethod;
		return this;
	}
}
