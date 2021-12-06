package data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Value;
import soot.jimple.ThisRef;
import sootproject.myexpression.MyArrayContent;
import sootproject.myexpression.MyExpressionTree;
import sootproject.myexpression.MySPArrayContent;
import sootproject.myexpression.ResultType;

public class RefValMap extends AbstractValMap{
	public Map<Value, MyExpressionTree> trees = null;
	public Map<String, MyExpressionTree> sptrees = null;
	public RefValMap() {
		super();
		trees = new HashMap<Value, MyExpressionTree>();
		sptrees = new HashMap<String, MyExpressionTree>();
	}
	
//		MyExpressionTree tree = trees.get(value);
//			tree = MyExpressionTree.createInitTree();
//			trees.put(value, tree);
//		return tree;
//	
//		MyExpressionTree tree = trees.get(classname);
//			tree = MyExpressionTree.createInitTree();
//			trees.put(classname, tree);
//		return tree;
	

	public void setTree(Value value, MyExpressionTree tree) {
		trees.put(value, tree);
		String typename = value.getType().toString();
		setSPTree(typename, tree);
	}
	
	public void setSPTree(String typename, MyExpressionTree tree) {
		MyExpressionTree originalTree = sptrees.get(typename);
//			System.out.println();
		if (null != originalTree) {
			if (originalTree != tree && originalTree.isArrayContent() && tree.isArrayContent()) {
				tree.arrayContentAdd(originalTree);
			}
		}

		sptrees.put(typename, tree);
	}
	
//			System.out.println();
//			sptrees.put(typename, tree);
	
	public MyExpressionTree getTree(Value value) {
		return trees.get(value);
	}
	
	
	public MyExpressionTree getTree(Value value, boolean shouldSearchTypetrees) {
		MyExpressionTree rettree = trees.get(value);
		if (null == rettree && shouldSearchTypetrees) {
			rettree = sptrees.get(value.getType().toString());
		}
		return rettree;
	}
	
	public MyExpressionTree getOrCreateSPTree(String typename) {
		MyExpressionTree retree = sptrees.get(typename);
		if (null == retree) {
			retree = MyExpressionTree.createInitTree();
			setSPTree(typename, retree);
		}
		return retree;
	}
	
	public MyExpressionTree getOrCreateSPTree(MySPArrayContent sparray) {
		MyExpressionTree tree = sptrees.get(sparray.spname);
		if (null == tree) {
			tree = MyExpressionTree.createSPTree(sparray);
			setSPTree(sparray.spname, tree);
		}
		return tree;
	}

}
