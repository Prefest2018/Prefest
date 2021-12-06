package appiumscript.scriptexecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadTimeFromPlanLog {
	public static void main(String[] arg) {
		String planFilePath = "C:\\Users\\yifeilu\\Documents\\TestProjectHome_tosem\\suntimes_bak\\testcase\\interestplan.txt";
		String startStr = "index.1,  interestcase LocationDialogTest#test_setLocationCurrent start:";
		String endStr = "interestcase 0_30_SuntimesScreenshots#makeScreenshot end";
		double totalTime = 0;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(planFilePath))));
			String content = null;
			boolean isCounting = false;
			while((content = br.readLine()) != null) {
				if (content.startsWith(startStr)) {
					isCounting = true;
				}else if (content.startsWith(endStr)) {
					isCounting = false;
				}
				if (isCounting) {
					if (content.startsWith("consumed time:")) {
						String timeStr = content.replace("consumed time:", "").replace("s", "").trim();
						totalTime += Double.parseDouble(timeStr);
					} else if (content.startsWith("Time: ")) {
						String timeStr = content.replace("Time: ", "").replace(",", ".").trim();
						totalTime += Double.parseDouble(timeStr);
					}
				}
			}
			br.close();
			System.out.println("total time:" + totalTime);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
