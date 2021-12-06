package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Value;
import sootproject.myexpression.MyVariable;
import sootproject.myexpression.MyExpressionTree;

public abstract class AbstractValMap {
	protected Map<Integer, Set<MyVariable>> valmaps = null;
	public AbstractValMap() {
		valmaps = new HashMap<Integer, Set<MyVariable>>();
	}
	public MyVariable get(Value value) {
		Set<MyVariable> vals = valmaps.get(value.equivHashCode());
		if (null != vals) {
			for (MyVariable val : vals) {
				if (val.ifEquiv(value)) {
					return val;
				}
			}
		}
		return null;
	}
	
	public void put(Value value, MyVariable myval) {
		int hashcode = value.equivHashCode();
		Set<MyVariable> vals = valmaps.get(hashcode);
		if (null == vals) {
			vals = new HashSet<MyVariable>();
			valmaps.put(hashcode, vals);
		}
		vals.add(myval);
	}
	

	
	
	public Set<MyVariable> get(String valstr) {
		Set<MyVariable> retvals = new HashSet<MyVariable>();
		for (Set<MyVariable> vals : valmaps.values()) {
			for (MyVariable val : vals) {
				if (val.value.toString().equals(valstr)) {
					retvals.add(val);
				}
			}
		}
		return retvals;
	}
	
	public Set<MyVariable> contain(String valstr) {
		Set<MyVariable> retvals = new HashSet<MyVariable>();
		for (Set<MyVariable> vals : valmaps.values()) {
			for (MyVariable val : vals) {
				if (val.value.toString().contains(valstr)) {
					retvals.add(val);
				}
			}
		}
		return retvals;
	}
	
	public String[] allValuestr() {
		String[] strlist = new String[1000];
		int i = 0;
		for (Set<MyVariable> vals : valmaps.values()) {
			for (MyVariable val : vals) {
				strlist[i] = val.value.toString();
				i++;
			}
		}
		return strlist;
	}
}
