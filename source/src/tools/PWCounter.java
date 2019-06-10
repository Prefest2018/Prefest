package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;


public class PWCounter {
	private Map<Integer, LinkedList<Boolean[]>> pwmap = null;
	private int maxsize = -1;
	private int initnum = -1;
	public PWCounter () {
		pwmap = new HashMap<Integer, LinkedList<Boolean[]>>();
	}
	public class PWMatric{
		private Set<String> covermap = null;
		public PWMatric(int number) {
			covermap = new HashSet<String>();
			for (int i = 0; i < number; i++) {
				for (int j = i + 1; j < number; j++) {
					covermap.add(i + "_" + j);
					covermap.add("-" + i + "_" + j);
					covermap.add(i + "_" + "-" + j);
					covermap.add("-" + i + "_" + "-" + j);
				}
			}
		}

		public int checkcover(Boolean[] nowcase, int number) {
			int result = 0;
			for (int i = 0; i < number; i++) {
				for (int j = i + 1; j < number; j++) {
					String x = nowcase[i]?i + "":"-" + i;
					String y = nowcase[j]?j + "":"-" + j;
					String key = x + "_" + y;
					if (covermap.contains(key)) {
						result++;
					}
				}
			}
			return result;
		}
		
		public void remove(Boolean[] nowcase, int number) {
			for (int i = 0; i < number; i++) {
				for (int j = i + 1; j < number; j++) {
					String x = nowcase[i]?i + "":"-" + i;
					String y = nowcase[j]?j + "":"-" + j;
					String key = x + "_" + y;
					if (covermap.contains(key)) {
						covermap.remove(key);
					}
				}
			}
		}
		
		public Boolean isallcovered() {
			return covermap.isEmpty();
		}
	}

	public Boolean[] getValues(int size, int order) {
		LinkedList<Boolean[]> list = pwmap.get(size);
		int turn = list.size();
		int remind = order % turn;
		return list.get(remind);
	}
	
	
	public int initfromPICT(int largestnumber) {
		if (pwmap.isEmpty()) {
			//assume that at most 40 preferences
			for (int id = 1; id <= 40; id++) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(PathHelper.getPICTStream(id)));
					br.readLine();
					String content = "";
					LinkedList<Boolean[]> boollist = new LinkedList<Boolean[]>();
					pwmap.put(id, boollist);
					while((content = br.readLine()) != null) {
						String[] bools = content.split("\t");
						Boolean[] newbools = new Boolean[bools.length];
						for (int i = 0; i < bools.length; i++) {
							if (bools[i].equals("0")) {
								newbools[i] = false;
							} else {
								newbools[i] = true;
							}
						}
						boollist.add(newbools);
					}
					br.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pwmap.get(largestnumber).size();
	}
	
}
