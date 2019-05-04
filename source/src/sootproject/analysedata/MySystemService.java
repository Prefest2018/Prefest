package sootproject.analysedata;

import java.util.ArrayList;
import java.util.List;

import data.InterestValue;
import sootproject.myexpression.ResultType;

public class MySystemService extends MyInterest{
	public MySystemService(String name, String typestr) {
		super(name);
		setResultType(typestr);
		this.setUnknown(false);
	}
	@Override
	public ResultType getResultType() {
		// TODO Auto-generated method stub
		return this.type;
	}

	@Override
	public void setResultType(String typestr) {
		if ("boolean".equals(typestr)) {
			this.type = ResultType.BOOLEAN;
			this.possibleValues = new ArrayList<String>();
			this.possibleValues.add("0");
			this.possibleValues.add("1");
		}
	}
	@Override
	public InterestValue getInterestValue(String value) {
		String type = null;
		switch (this.type) {
			case BOOLEAN : {
				type = "boolean";
				if (value.equals("true")) {value = "1";}else if (value.equals("false")) {value = "0";} 
				break;
			}
		}
		InterestValue interestvalue = new InterestValue("systemservice", null, type, name, value, -1, null);
		return interestvalue;
	}
	@Override
	public List<InterestValue> getallpossibleValues() {
		List<InterestValue> interestvalues = new ArrayList<InterestValue>();
		for (String value: possibleValues) {
			InterestValue interestvalue = new InterestValue("systemservice", null, "boolean", name, value, -1, null);
			interestvalues.add(interestvalue);
		}
		return interestvalues;
	}

}
