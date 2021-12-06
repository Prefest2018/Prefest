package data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ITScene extends Scene {
	public int discoverit = 0;
	public Map<Object, Scene> discovertag = null;
	public boolean covered = false;
	public int trialtimes = 0;
	public ITScene() {}
	public ITScene(Scene oldone, int discoverit) {
		this.branchids = oldone.branchids;
		this.changebranchids = oldone.changebranchids;
		this.interests = oldone.interests;
		this.preinterests = oldone.preinterests;
		this.discoverit = discoverit;
		this.discovertag = new HashMap<Object, Scene>();
		this.covered = false;
	}
	
//					return interests.get(0).equalsWithNameAndValue(((Scene)scene).interests.get(0));
//					return true;
//		return false;
}
