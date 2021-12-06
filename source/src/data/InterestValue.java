package data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestValue {
	public String generaltype = null;
	public String innertype = null;
	public String type = null;
	public String name = null;
	public String value = null;
	public String activityname = null;
	public String activityextra = null;
	public String catalog = null;
	public List<String> preferencesteps = null;
	public int index = -1;
	public boolean isadapted = false;
	public Map<String, Object> extradatas = null;
	public InterestValue dependency = null;
	
	public InterestValue(String generaltype, String innertype, String type, String name, String value, int index, String activityname) {
		this.generaltype = generaltype;
		this.innertype = innertype;
		this.type = type;
		this.name = name;
		this.value = value;
		this.index = index;
		this.activityname = activityname;
		this.extradatas = new HashMap<String, Object>();
	}

	public InterestValue(InterestValue oldone, String newvalue) {
		this.generaltype = oldone.generaltype;
		this.innertype = oldone.innertype;
		this.type = oldone.type;
		this.name = oldone.name;
		this.value = newvalue;
		this.index = oldone.index;
		this.activityname = oldone.activityname;
		this.activityextra = oldone.activityextra;
		this.extradatas = oldone.extradatas;
	}
	
	public boolean equals(Object anothervalue) {
		if (anothervalue != null && anothervalue instanceof InterestValue) {
			if (this.index != -1 || ((InterestValue)anothervalue).index != -1) {
				return this.index == ((InterestValue)anothervalue).index;
			} else {
				return this.name.equals(((InterestValue)anothervalue).name) && this.value.equals(((InterestValue)anothervalue).value);
			}
 
		}
		return false;
	}
	
	public boolean equalsWithNameAndValue(Object anothervalue) {
		if (anothervalue != null && anothervalue instanceof InterestValue) {
			if (this.value == null) {
				return ((InterestValue)anothervalue).value == null;
			} else {
				return this.name.equals(((InterestValue)anothervalue).name) && this.value.equals(((InterestValue)anothervalue).value);
			}
		}
		return false;
	}
	
	public InterestValue() {
		
	}
	
	public boolean notinject() {
		if ("systemservice".equals(generaltype) || index != -1) {
			return true;
		}
		return false;
	}
}
