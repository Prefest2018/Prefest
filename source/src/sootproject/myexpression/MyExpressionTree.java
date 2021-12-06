package sootproject.myexpression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GUI.Main;
import data.RefValMap;
import soot.ArrayType;
import soot.Value;
import sootproject.analysedata.MyInterest;

public class MyExpressionTree extends MyExpressionInterface{
	public boolean isRef = false;//
	public Set<MyExpressionTree> parents = null;
	protected String name = null;
	protected MyExpressionTree() {
		parents = new HashSet<MyExpressionTree>();
		content = null;
		type = ResultType.DEFAULT;
	}
	
	public static MyExpressionTree createInitTree() {
		MyExpressionTree rettree = new MyExpressionTree();
//		rettree.content = new MyArrayContent();
//		rettree.type = ResultType.ARRAY;
		return rettree;
	}
	
	public boolean isEmypty() {
		if ((null == content) || (content instanceof MyArrayContent)&& ((MyArrayContent)content).isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isArrayContent() {
		if (null != content && content instanceof MyArrayContent) {
			return true;
		} else {
			return false;
		}
	}
	
	public void arrayContentAdd(MyExpressionTree another) {
			MyArrayContent temp = (MyArrayContent)this.content;
			this.content = another.content;
			((MyArrayContent)content).addAll(temp);
			this.interestRelated = another.interestRelated;
			this.isRef = another.isRef;
			this.name =another.name;
			this.unknown = another.unknown;
			this.parents.addAll(another.parents);

				
	}
		
	public static MyExpressionTree createSPTree(MySPArrayContent arrayContent) {
		MyExpressionTree rettree = new MyExpressionTree();
		rettree.name = arrayContent.spname;
		rettree.content = arrayContent;
		rettree.type = ResultType.ARRAY;
		return rettree;
	}
	
	public static MyEnumInstance createEnumInstance() {
		MyEnumInstance instance = new MyEnumInstance();
		return instance;
	}
	
	private void setLeaf(MyExpressionInterface inputExp) {
		if (null != inputExp) {
			content = inputExp.content;
			param1 = inputExp.param1;
			param2 = inputExp.param2;
			operator = inputExp.operator;
			interestRelated = inputExp.interestRelated;
			unknown = inputExp.unknown;
			type = inputExp.type;
			resultType = inputExp.resultType;
		} else {
			content = new MyArrayContent();
			type = ResultType.ARRAY;
		}

	}
	
	private static MyExpressionTree getLeaf(MyExpressionTree tree, MyExpressionInterface inputExp) {
		MyExpressionTree rettree = tree;
		if (null == tree) {
			rettree = new MyExpressionTree();
		}
		if (null != inputExp) {
			rettree.setLeaf(inputExp);
		}
		return rettree;
	}
	
	public static MyExpressionTree createLeaf(MyExpressionInterface inputExp) {
		return getLeaf(null, inputExp);
	}
	
	
//	private static HashMap<MyInterest, String> emptymap = new HashMap<MyInterest, String>();
//		ExpressionValue value = calculate(emptymap);
//			return value.value;
//		return null;
	
	public void turnToArray() {
		this.content = new MyArrayContent();
		if (this.type != ResultType.ENUM) {
			this.type = ResultType.ARRAY;
		}
	}
	
	private void checkArrayType() {
		if (this.content == null) {
			turnToArray();
		}
	}
	
	public MyExpressionTree getChild(MyExpressionInterface indexexp, boolean isarray) {
		MyExpressionTree childtree = null;
		checkArrayType();
		if (null == indexexp) {
			return null;
		}
		if (content instanceof MyArrayContent && !indexexp.interestRelated) {
			MyArrayContent arraycontent = (MyArrayContent)content;
			Object indexvalue = indexexp.calculate();
			if (null == indexvalue) {
				return null;
			}
			childtree = (MyExpressionTree) arraycontent.get(indexvalue);
			if (null == childtree) {
				if (!isarray) {
					childtree = MyExpressionTree.createLeaf(null);
				} else {
					childtree = MyExpressionTree.createInitTree();
				}
				childtree.parents.add(this);
				arraycontent.put(indexvalue, childtree);
			}
			
		} else {
			MyExpression vcontent = null;
			if (content instanceof MyExpression) {
				vcontent = (MyExpression)content;
			} else if (content instanceof MyArrayContent) {
				vcontent = new MyExpression(((MyArrayContent)content).getClone());
			}

			MyExpression childexp = new MyExpression(OperationType.SELECT, vcontent, indexexp);
			childexp.content = null;
			childexp.interestRelated = this.interestRelated || indexexp.interestRelated;
			childexp.unknown = this.unknown || indexexp.unknown;
			childtree = MyExpressionTree.createLeaf(childexp);
			childtree.parents.add(this);
			//arraycontent.put(indexvalue, childtree);
		}
		return childtree;
	}
	public void setChild(MyExpressionInterface indexexp, MyExpressionInterface inputexp) {
		checkArrayType();
		if (!indexexp.interestRelated && content instanceof MyArrayContent) {
			MyArrayContent arraycontent = (MyArrayContent)content;
			Object indexvalue = indexexp.calculate();

			if (null != indexvalue) {
				MyExpressionTree childtree = (MyExpressionTree) arraycontent.get(indexvalue);
				if (null != childtree) {
					childtree.parents.remove(this);
				}
				if (inputexp instanceof MyExpressionTree) {
					childtree = (MyExpressionTree)inputexp;
				} else {
					childtree = MyExpressionTree.createLeaf(null);
					childtree.setLeaf(inputexp);
				}
				childtree.parents.add(this);
				arraycontent.put(indexvalue, childtree);

			}
		} else {
			MyExpression vcontent = null;
			if (content instanceof MyExpression) {
				vcontent = (MyExpression)content;
			} else if (content instanceof MyArrayContent) {
				vcontent = new MyExpression(((MyArrayContent)content).getClone());
			}
			vcontent = new MyExpression(OperationType.PUT, vcontent, indexexp);
			vcontent.content = inputexp;
			content = vcontent;
		}
	}
	

	
	public MyExpressionTree getChild(String fieldname, Value value, RefValMap refmap) {
		checkArrayType();
		if (!(content instanceof MyArrayContent)) {
			turnToArray();
		}
		MyArrayContent arraycontent = (MyArrayContent)content;
		MyExpressionTree childtree = arraycontent.get(fieldname);
		if (null == childtree) {
			if (value.getType().toString().contains(Main.packagename)) {
				childtree = refmap.getTree(value, true);
			}
			if (null == childtree) {
				if (value.getType() instanceof ArrayType) {
					childtree = MyExpressionTree.createLeaf(null);
				} else {
					childtree = MyExpressionTree.createInitTree();
				}
			}
			arraycontent.put(fieldname, childtree);
			childtree.parents.add(this);
		}
		return childtree;
	}
	

	public MyExpressionTree setChild(String fieldname, MyExpressionInterface contentexp) {
		checkArrayType();
		MyArrayContent arraycontent = (MyArrayContent)content;
		MyExpressionTree childtree = arraycontent.get(fieldname);
		if (null != childtree) {
			childtree.parents.remove(this);
		}
		if (contentexp instanceof MyExpressionTree) {
			childtree = (MyExpressionTree)contentexp;
		} else {
			childtree = MyExpressionTree.createLeaf(contentexp);
		}
		arraycontent.put(fieldname, childtree);
		childtree.parents.add(this);
		return childtree;
	}
	
}
