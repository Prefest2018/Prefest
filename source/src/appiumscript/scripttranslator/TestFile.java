package appiumscript.scripttranslator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import GUI.Main;
import appiumscript.util.ScriptGenerationUtil;
public class TestFile {
	private static String[] possibleops = new String[] {"click", "clickLong", "edit", "scroll", "menu", "back"};
	
	protected LinkedList<LinkedList<TestOperation>> testcases = null;
	private static DecimalFormat nf = new DecimalFormat("000");
	protected int num = -1;
	public TestFile(int num) {
		this.num = num;
		this.testcases = new LinkedList<>();
	}
	
	protected void addTestCase(String testcasestr) {
		LinkedList<TestOperation> testcase = new LinkedList<TestOperation>();
		
		boolean inpunctuation = false;
		boolean indoublepunctuation = false;
		boolean inbracket = false;
		int startnum = 0;
		String tempstr = null;
		String nowattr = null;
		
		
		TestOperation nowOperation = new TestOperation();
		for (int i = 0; i < testcasestr.length(); i++) {
			if (!inpunctuation && !indoublepunctuation && !inbracket) {
				if (i + 4 <= testcasestr.length()) {
					String teststr = testcasestr.substring(i, i+4);
					if (teststr.equals("back")) {
						nowOperation.type = TestOperationType.BACK;
						testcase.add(nowOperation);
						nowOperation = new TestOperation();
						i+=3;
						startnum=i+1;
						continue;
					} else if (teststr.equals("menu")) {
						nowOperation.type = TestOperationType.MENU;
						testcase.add(nowOperation);
						nowOperation = new TestOperation();
						i+=3;
						startnum=i+1;
						continue;
					}
				}
			}
			
			char nowchar = testcasestr.charAt(i);
			switch(nowchar){
			case '(' : {
				if (!inpunctuation && !indoublepunctuation && !inbracket) {
					tempstr = testcasestr.substring(startnum, i);
					nowOperation.operationType = tempstr;
					startnum = i + 1;
				}
				inbracket = true;
				break;
			}
			case '=' : {
				if (!inpunctuation && !indoublepunctuation && inbracket) {
					nowattr = testcasestr.substring(startnum, i);
					startnum = i + 1;
				}
				break;
			}
			case '\'' : {
				if (!indoublepunctuation && inbracket) {
					if (inpunctuation) {
						tempstr = testcasestr.substring(startnum, i);
						switch(nowattr) {
						case "resource-id": {
							nowOperation.resourceId = tempstr;
							break;
						}
						case "content-desc" : {
							nowOperation.contentDesc = tempstr;
							break;
						}
						case "className" : {
							nowOperation.className = tempstr;
							break;
						}
						case "direction" : {
							nowOperation.direction = tempstr;
							break;
						}
						case "instance" : {
							nowOperation.instance = Integer.parseInt(tempstr);
							break;
						}
						}
						nowattr = "";
					}
				}
				startnum = i + 1;
				inpunctuation = !inpunctuation;
				break;
			}
			case ',' : {
				if (!inpunctuation && !indoublepunctuation && inbracket) {
					startnum = i + 1;
				}
				break;
			}
			case ')' : {
				if (!inpunctuation && !indoublepunctuation && inbracket) {
					inbracket = false;
					startnum = i + 1;
					if ((i+1>=testcasestr.length()) || testcasestr.charAt(i+1) != ':') {
						testcase.add(nowOperation);
						nowOperation = new TestOperation();
					}
				}
				break;
			}

			case ':' : {
				if (!inpunctuation && !indoublepunctuation) {
					startnum = i + 1;
				}
				break;
			}
			case '@' : {
				if (!inpunctuation && !indoublepunctuation && !inbracket) {
					tempstr = testcasestr.substring(startnum, i);
					nowOperation.className = tempstr;
					startnum = i + 1;
				}
				break;
			}
			case '\"' : {
				if (!inpunctuation && !inbracket) {
					if (indoublepunctuation) {
						if (i == testcasestr.length() - 1) {
							tempstr = testcasestr.substring(startnum, i);
							nowOperation.text = tempstr;
							testcase.add(nowOperation);
							nowOperation = new TestOperation();
						} else{
							String teststr = testcasestr.substring(i+1);
							for (String inner : possibleops) {
								if (teststr.startsWith(inner)) {
									tempstr = testcasestr.substring(startnum, i);
									nowOperation.text = tempstr;
									testcase.add(nowOperation);
									nowOperation = new TestOperation();
									break;
								}
							}
						}

					}
				}
				startnum = i + 1;
				indoublepunctuation = !indoublepunctuation;
				break;
			}
			}
		}
		
		this.testcases.add(testcase);
	}

	protected List<File> outputtestfile(String fileName) {
		List<File> files = new ArrayList<File>();

		for (int i = 0; i < testcases.size(); i++) {
			String idstr = nf.format(i);
			File file = new File(fileName + "_" + idstr + ".py");
			if (file.exists()) {
				file.delete();
			}
			try {
				file.createNewFile();
				BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
				String filecontent = generateFileContent(idstr, testcases.get(i));
				wr.write(filecontent);
				wr.close();
				files.add(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return files;
	}

	
	private String generateFileContent(String idstr, List<TestOperation> testcase) {
		StringBuilder sb = new StringBuilder();
		sb.append(ScriptGenerationUtil.getPrefixFunctions_General(true, false, null));
		StringBuilder testopsb = new StringBuilder();
		for (TestOperation opt : testcase) {
			testopsb.append(opt.getTestLine(1) + "\r\n");
		}
		sb.append(ScriptGenerationUtil.getTestcase_FirstExe(num + "_" + idstr, testopsb.toString(), null));
		return sb.toString();
	}
}
