package uiautomationexploration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;


import GUI.Main;
import data.InterestValue;
import tools.Logger;
import tools.PathHelper;
import tools.ProcessExecutor;
 
/**
 * 探索服务器
 * 负责启动和管理UI自动化探索过程，与Python脚本交互执行探索命令
 */
public class ExplorerServer {
    // 偏好设置探索器
    private PreferenceExplorer explorer = null;
    // 适配器对象
    private Adapter adapter = null;
    
    /**
     * 构造函数
     * 
     * @param adapter 适配器对象，包含偏好设置相关信息
     */
    public ExplorerServer(Adapter adapter) {
    	PreferenceExplorer explorer = new PreferenceExplorer(adapter);
        this.explorer = explorer;
        this.adapter = adapter;
    }
    
    /**
     * 启动探索过程
     * 循环执行探索命令，直到探索完成
     */
    public void start() {

        System.out.println("in handling..");
        Date startTime = new Date();
        String commond = null;
    	// 循环执行探索命令
    	while(true) {
    		// 获取下一个探索命令
    		commond = explorer.givecommond();
        	System.out.println(commond);
        	// 如果命令是"stop"，结束探索
        	if (commond.equals("stop")) {
        		break;
        	}
        	Process p = null;
        	// 创建Python进程执行UI探索脚本
			ProcessBuilder builder = ProcessExecutor.getPBInstance("python", "uiexplore.py", commond);
			try {
				// 设置工作目录为UIAutomator客户端路径
				builder.directory(new File(PathHelper.getUIAutomatorClientPath()));
				p = builder.start();
		    	BufferedReader p_stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
		    	String contentline = null;
	            StringBuilder successsb = new StringBuilder();
	            StringBuilder contentsb = new StringBuilder();
	            int index = 0;
	            // 读取Python脚本的输出
	            while((contentline = p_stdout.readLine())!= null) {
	            	// 如果遇到"end"标记，结束读取
	            	if (contentline.equals("end")) {
	            		break;
	            	}
	            	// 如果遇到"---"分隔符，切换到下一个输出部分
	            	if (contentline.equals("---")) {
	            		index++;
	            		continue;
	            	}
	            	// 根据索引将内容添加到对应的StringBuilder
	            	switch (index) {
	            	case 0: successsb.append(contentline);break;  // 第一部分：成功/失败状态
	            	case 1: contentsb.append(contentline);break;  // 第二部分：UI内容
	            	}
	            	
//		            System.out.println(contentline);
	            }
	            // 更新探索状态
	            explorer.updatestate(contentsb.toString(), successsb.toString().equals("success")?true:false);
//	            System.out.println("done.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	// 计算探索耗时
    	 Date endTime = new Date();
    	 long milltimes = endTime.getTime() - startTime.getTime();
    	 Logger.setTempLogFile(Main.explorationfile, true);
    	 String timelog = "This exploration took time: " + milltimes/1000 + "s";
    	 System.out.println(timelog);
    	 Logger.log(timelog);
    	 // 拉取覆盖率数据文件
    	 for (int i = 0; i < ExploreState.numcount; i++) {
			ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/" + "coverage" + i + ".ec", Main.testadpatercoverage + File.separator  + "adapter" + i + ".ec");
    	 }
    	 Logger.setTempLogFile(Main.interestplan, true);

    	 
    }
    
    
    /**
     * 为适配器执行探索
     * 如果适配器未探索且存在未适配的偏好设置，则启动探索
     */
    public void exploreForAdapter() {
    	// 如果适配器未探索
    	if (!this.adapter.explored) { 
    		// 检查是否存在未适配的偏好设置
    		if (!checkOKInterestValue()) {
    	    	// 如果存在未适配的偏好设置，启动探索
    	    	start();
    		} else {
    			// 如果所有偏好设置都已适配，标记为已探索
    			this.adapter.explored = true;
    		}
	    	// 保存适配器
	    	explorer.saveAdater();
		}
    }
    
    /**
     * 检查所有兴趣值是否都已适配
     * 
     * @return 如果所有偏好设置类型的兴趣值都已适配返回true，否则返回false
     */
    private boolean checkOKInterestValue() {

    	boolean ok = true;
    	// 遍历所有兴趣值
    	for (InterestValue value : this.adapter.preferencelist.values()) {
    		// 如果是偏好设置类型且未适配，返回false
    		if ("preference".equals(value.generaltype) && !value.isadapted) {
    			ok = false;
    			break;
    		}
    	}
    	return ok;
    }
 
 
}
 
//	ServerSocket ss = null;
//    Socket s = null;
//    int id = -1;
//    PreferenceExplorer explorer = null;
// 
//    	this.ss = ss;
//        this.s = s;
//        this.id = id;
//        this.explorer = explorer;
// 
//    @Override
//        System.out.println("in handling..");
//        String commond = null;
//	        	
//	            InputStream is = s.getInputStream();
//	            BufferedReader in = new BufferedReader(new InputStreamReader(is));
//	            String contentline = null;
//	            StringBuilder successsb = new StringBuilder();
//	            StringBuilder contentsb = new StringBuilder();
//	            int index = 0;
//	            		break;
//	            		index++;
//	            		continue;
//	            	case 0: successsb.append(contentline);break;
//	            	case 1: contentsb.append(contentline);break;
//	            	
//		            System.out.println(contentline);
//	            explorer.updatestate(contentsb.toString(), successsb.toString().equals("success")?true:false);
//	            System.out.println("done.");
//	            e.printStackTrace();
//	        		break;
