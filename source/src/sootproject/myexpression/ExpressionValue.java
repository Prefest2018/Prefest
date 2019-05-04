package sootproject.myexpression;

public class ExpressionValue {
	public Object value = null;
	public ResultType type = ResultType.DEFAULT;
	public ExpressionValue (Object value, ResultType type) {
		this.value =value;
		this.type =type;
	}
	
	public boolean equals(Object another) {
		if (another instanceof ExpressionValue) {
			if (null != value && null !=((ExpressionValue) another).value) {
				return value.equals(((ExpressionValue) another).value);
			} else {
				return false;
			}
		} else {
			return value.equals(another);
		}
	}
}
