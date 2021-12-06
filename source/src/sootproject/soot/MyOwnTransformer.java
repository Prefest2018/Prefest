package sootproject.soot;

import java.util.Set;

import soot.BodyTransformer;

public abstract class MyOwnTransformer extends BodyTransformer{
	protected String packagename = null;
	protected Set<String> extrapackagenames = null;
	protected boolean checkInTargetPackage(String currentPackageName) {
		if (currentPackageName.contains(packagename)) {
			return true;
		}
		if (null != extrapackagenames) {
			for (String extrapackagename : extrapackagenames) {
				if (currentPackageName.contains(extrapackagename)) {
					return true;
				}
			}
		}
		return false;
	}
}
