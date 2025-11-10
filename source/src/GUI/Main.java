package GUI;

import appiumscript.scriptexecutor.*;
import appiumscript.scripttranslator.StoatScriptLoader;
import appiumscript.util.ScriptGenerationUtil;
import data.InterestValue;
import data.PreferenceAdaptData;
import data.TestCaseData;
import espresso.EspressoScriptExecutor;
import soot.*;
import soot.options.Options;
import sootproject.preferenceAnalyse.FailurePreferenceAdapter;
import sootproject.preferenceAnalyse.PreferenceAnalyser;
import sootproject.resourceLoader.PreferenceTreeNode;
import sootproject.soot.PreferenceAnalyseTransformer;
import sootproject.soot.StubTransformer;
import sun.awt.OSInfo;
import tools.CMDUtils;
import tools.JsonHelper;
import tools.Logger;
import tools.PathHelper;
import tools.ProcessExecutor;
import tools.tagselector.TagSelectType;
import uiautomationexploration.Adapter;
import uiautomationexploration.ExplorerServer;
import uiautomationexploration.PreferenceExplorer;

import java.io.File;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import GUI.Main;

/**
 * 主类
 * 提供Android应用偏好设置测试的主要功能，包括：
 * - APK分析和处理
 * - 偏好设置分析
 * - 测试用例生成和执行
 * - UI自动化探索
 * - 覆盖率收集
 */
public class Main {
    // 调试模式标志
    public static boolean debug = true;
//    public static boolean blockmode = false;
    // 发生错误时是否重置
    public static boolean resetWhenError = true;
    // 是否应该探索偏好设置
    public static boolean shouldExplorePreference = true;
    // 是否应该添加无分支目标
    public static boolean shouldAddNoBranchTargets = true;
	// 每次运行是否重置
	public static boolean resetForEachRun = false;
	// 是否测试设置操作
	public static boolean testSettingOperations = false;
	// 标签排序策略
	public static TagSelectType tagSortStrategy = TagSelectType.MOSTLOGIC;
	// 最少标签数量
	public static int lestTagNum = 1;
    // AVD（Android Virtual Device）名称
    public static String avdname = null;
    // 代理设置
    public static String proxy = null;
    // 新的环境变量映射
    public static Map<String, String> newenvs = null;
    // 偏好设置分析转换器
    public static PreferenceAnalyseTransformer analyseTransformer = null;
    // 偏好设置分析器
    public static PreferenceAnalyser analyser = null;
    // ADB命令路径
    public static String adb = null;
    // 模拟器命令路径
    public static String emulator = null;
    // Python命令路径
    public static String python = null;

    // 应用包名
    public static String packagename = null;
    // 额外包名集合
    public static Set<String> extrapackagenames = null;
    // 启动Activity名称
    public static String luanchactivityname = null;
    // 偏好设置脚本前缀
    public final static String PRESCRIPT = "preference_pre";
	// 设置方法标签前缀
	public final static String SETTINGMETHODTAG = "settingPref_";
    // 跳过的静态方法列表
    public static String[] skipstaticmethods = {"$jacocoInit"};

    // 项目主目录路径
    public static String home = null;
    // APK信息文件路径
    public static String apkinfo = null;
    // 测试用例目录路径
    public static String testcase = null;
    // 命令日志文件路径
    public static String cmdlog = null;
    // 临时文件夹路径
    public static String tempfolder = null;
    // MCMC测试套件文件路径
    public static String mcmctxt = null;
    // 首次测试用例目录路径
    public static String firstcases = null;
    // 额外文件目录路径
    public static String extra = null;
    // 失败偏好设置文件路径
    public static String failurepreferences = null;
    // 首次执行结果文件路径
    public static String firstcasesexeresultfile = null;
    // 首次执行结果目录路径
    public static String firstcasesloc = null;
    // 首次覆盖率目录路径
    public static String firstcasescoverage = null;
    // 测试用例信息文件路径
    public static String testcaseinfofile = null;
    // 旧测试用例信息文件路径
    public static String testcaseinfofileold = null;
    // 首次测试用例错误日志路径
    public static String firstcaseerror = null;
    // 首次覆盖率数据文件路径
    public static String firstcasecoverdata = null;
    // 测试适配器文件路径
    public static String testadapter = null;
    // 测试适配器覆盖率目录路径
    public static String testadpatercoverage = null;
    // 临时定位文件路径
    public static String templocfile = null;
    // 兴趣计划文件路径（文本格式）
    public static String interestplan = null;
    // 探索文件路径
    public static String explorationfile = null;
    // 兴趣计划文件路径（JSON格式）
    public static String interestplanfile = null;
    // 偏好设置文本文件路径
    public static String preferencetxt = null;
    // 兴趣测试用例目录路径
    public static String interestcases = null;
    // 兴趣覆盖率数据文件路径
    public static String interestcoverdata = null;
    // 兴趣执行结果文件路径
    public static String interestcasesexeresultfile = null;
    // 兴趣执行结果目录路径
    public static String interestcaseloc = null;
    // 兴趣测试用例信息文件路径
    public static String interestcaseinfofile = null;
    // 兴趣覆盖率目录路径
    public static String interestcasescoverage = null;
    // 兴趣错误日志路径
    public static String interesterror = null;
    // 目标信息文件路径
    public static String targetinfo = null;
    // 全部兴趣计划文件路径
    public static String interestallplanfile = null;
    // 全部兴趣测试用例目录路径
    public static String interestallcases = null;
    // 全部兴趣执行结果目录路径
    public static String interestallcaseloc = null;
    // 全部兴趣执行结果文件路径
    public static String interestallcasesexeresultfile = null;
    // 全部兴趣测试用例信息文件路径
    public static String interestallcaseinfofile = null;
    // 全部兴趣覆盖率目录路径
    public static String interestallcasescoverage = null;
    // 全部兴趣覆盖率数据文件路径
    public static String interestallcoveragedata = null;
    // 全部兴趣错误日志路径
    public static String interestallerror = null;
//    public static String allpreferenceprecase_reverse = null;
    // 所有偏好设置前置用例日志路径（反向）
    public static String allpreferenceprecaselog_reverse = null;
//    public static String allpreferenceprecase_default = null;
    // 所有偏好设置前置用例日志路径（默认）
    public static String allpreferenceprecaselog_default = null;
    // 所有偏好设置测试用例目录路径
    public static String allpreferencecases = null;
    // 所有偏好设置执行结果目录路径
    public static String allpreferencecaseloc = null;
    // 所有偏好设置信息文件路径
    public static String allpreferenceinfofile = null;
    // 所有偏好设置覆盖率目录路径
    public static String allpreferencecoverage = null;
    // 所有偏好设置覆盖率数据文件路径
    public static String allpreferencecoveragedata = null;
    // 所有偏好设置错误日志路径
    public static String allpreferenceerror = null;
//	public static String ofotpreferenceprecase = null;
//	public static String ofotpreferenceprecaselog = null;
    // 成对偏好设置测试用例目录路径
    public static String pwpreferencecases = null;
    // 成对偏好设置执行结果目录路径
    public static String pwpreferencecaseloc = null;
    // 成对偏好设置覆盖率目录路径
    public static String pwpreferencecoverage = null;
    // 成对偏好设置计划文件路径
    public static String pwpreferenceplanfile = null;
    // 成对偏好设置测试用例信息目录路径
    public static String pwpreferencecaseinfo = null;
    // 成对偏好设置执行结果文件路径
    public static String pwpreferenceresultfile = null;
    // 成对偏好设置覆盖率数据文件路径
    public static String pwpreferencecoveragedata = null;
    // 成对偏好设置错误日志路径
    public static String pwpreferenceerror = null;
    // Monkey测试信息目录路径
    public static String monkeyinfo = null;
    // Monkey测试错误日志路径
    public static String monkeyerror = null;
    
    // Espresso测试用例名称文件路径
    public static String espressocasenamefile = null;
    // Espresso执行结果目录路径
    public static String espressocaseloc = null;
    // Espresso覆盖率目录路径
    public static String espressocoverage = null;
    // Espresso错误日志路径
    public static String espressoerror = null;
    // Espresso偏好设置测试用例目录路径
    public static String espressopreferencecase = null;
    // Espresso偏好设置执行结果目录路径
    public static String espressopreferencecaseloc = null;
    // Espresso偏好设置覆盖率数据目录路径
    public static String espressopreferencecasecoveragedata = null;
    // Espresso偏好设置错误日志路径
    public static String espressopreferenceerror = null;
    // Espresso插桩命令文件路径
    public static String espressoinstrucmd = null;
    // Espresso命令列表
    public static List<String> espressoCMDs = null;
    // Appium脚本形式常量
    public static final String APPIUM = "APPIUM";
    // UIAutomator2脚本形式常量
    public static final String UIAUTOMATOR2 = "UIAUTOMATOR2";
    // 脚本形式（默认APPIUM）
    public static String scriptForm = APPIUM;// APPIUM
    // AVD版本
    public static String AVDVersion = "6.0";
    // 是否使用SeekBar
    public static final boolean USESEEKBAR = false;


    /**
     * 更新项目主目录路径并初始化所有相关路径
     * 
     * @param home 项目主目录路径
     */
    public void updateHome(String home) {
        // 重置包名和Activity信息
        Main.packagename = null;
        Main.luanchactivityname = null;
        Main.extrapackagenames = null;
        Main.home = home;
        String sepHome = home + File.separator;
        // 初始化各种路径
        Main.tempfolder = sepHome + "temp";
        Main.extra = sepHome + "extra";
        Main.failurepreferences = sepHome + "extra" + File.separator + "failurepreferences";
        Main.testcase = sepHome + "testcase";
        Main.apkinfo = sepHome + "app" + File.separator + "apkinfo.json";
        Main.cmdlog = sepHome + "log" + File.separator + "cmdlog.txt";
        Main.mcmctxt = sepHome + "testcase" + File.separator + "mcmc_all_history_testsuites.txt";
        Main.firstcases = sepHome + "testcase" + File.separator + "firstcases";
        Main.firstcasecoverdata = sepHome + "testcase" + File.separator + "firstcoverdata.json";
        Main.firstcasesexeresultfile = sepHome + "exeresult" + File.separator + "firstexecutionresult.txt";
        Main.firstcasesloc = sepHome + "exeresult" + File.separator + "firstresult";
        Main.interestcaseloc = sepHome + "exeresult" + File.separator + "interestresult";
        Main.firstcasescoverage = sepHome + "coverage" + File.separator + "firstcoverage";
        Main.firstcaseerror = sepHome + "error" + File.separator + "firstcaseerror.log";
        Main.interestcasescoverage = sepHome + "coverage" + File.separator + "interestcoverage";
        Main.interestcasesexeresultfile = sepHome + "exeresult" + File.separator + "interestexecutionresult.txt";
        Main.testcaseinfofile = sepHome + "testcase" + File.separator + "testcaseinfo.json";
        Main.testcaseinfofileold = sepHome + "testcase" + File.separator + "testcaseinfo_old.json";
        Main.interestcaseinfofile = sepHome + "testcase" + File.separator + "interestinfo.json";
        Main.targetinfo = sepHome + "testcase" + File.separator + "targetinfo.json";
        Main.interestplan = sepHome + "testcase" + File.separator + "interestplan.txt";
        Main.explorationfile = sepHome + "testcase" + File.separator + "exploration.txt";
        Main.interestcoverdata = sepHome + "testcase" + File.separator + "interestcoverdata.json";
        Main.interestplanfile = sepHome + "testcase" + File.separator + "interestplan.json";
        Main.interestcases = sepHome + "testcase" + File.separator + "interestcases";
        Main.interesterror = sepHome + "error" + File.separator + "interesterror.log";
        Main.preferencetxt = sepHome + "testcase" + File.separator + "preference.txt";
        Main.testadapter = sepHome + "testcase" + File.separator + "adapter.json";
        Main.testadpatercoverage = sepHome + "coverage" + File.separator + "adaptercoverage";
        Main.interestallcaseinfofile = sepHome + "testcase" + File.separator + "interestallinfo.json";
        Main.interestallcaseloc = sepHome + "exeresult" + File.separator + "interestallresult";
        Main.interestallcases = sepHome + "testcase" + File.separator + "interestallcases";
        Main.interestallplanfile = sepHome + "testcase" + File.separator + "interestallplan.json";
        Main.interestallcasesexeresultfile = sepHome + "exeresult" + File.separator + "interestallexecutionresult.txt";
        Main.interestallcasescoverage = sepHome + "coverage" + File.separator + "interestallcoverage";
        Main.interestallerror = sepHome + "error" + File.separator + "interestallerror.log";
        Main.interestallcoveragedata = sepHome + "testcase" + File.separator + "interestallcoveragedata.json";


//        allpreferenceprecase_reverse = sepHome + "testcase" + File.separator + "allpreferencecases" + File.separator + "precase.py";
        allpreferenceprecaselog_reverse = sepHome + "testcase" + File.separator + "allpreferencecases" + File.separator + "precase_log.py";
//        allpreferenceprecase_default = sepHome + "testcase" + File.separator + "allpreferencecases" + File.separator + "defaultprecase.py";
        allpreferenceprecaselog_default = sepHome + "testcase" + File.separator + "allpreferencecases" + File.separator + "defaultprecase_log.py";
        allpreferencecases = sepHome + "testcase" + File.separator + "allpreferencecases";
        allpreferencecaseloc = sepHome + "exeresult" + File.separator + "allpreferenceresult";
        allpreferenceinfofile = sepHome + "testcase" + File.separator + "allpreferencetestcaseinfo.json";
        allpreferencecoverage = sepHome + "coverage" + File.separator + "allpreferencecoverage";
        allpreferenceerror = sepHome + "error" + File.separator + "allpreferenceerror.log";
        allpreferencecoveragedata = sepHome + "testcase" + File.separator + "allpreferencecoveragedata.json";
        pwpreferencecases = sepHome + "testcase" + File.separator + "pwpreferencecases";
        pwpreferencecoverage = sepHome + "coverage" + File.separator + "pwpreferencecoverage";
        pwpreferenceplanfile = sepHome + "testcase" + File.separator + "pwplan.json";
        pwpreferencecaseinfo = sepHome + "testcase" + File.separator + "pwpreferencecaseinfo";
        pwpreferenceresultfile = sepHome + "exeresult" + File.separator + "pwexecutionresult.txt";
        pwpreferenceerror = sepHome + "error" + File.separator + "pwpreferenceerror.log";
        pwpreferencecoveragedata = sepHome + "testcase" + File.separator + "pwpreferencecoveragedata.json";

        
        monkeyinfo = sepHome + "monkey";
        monkeyerror = monkeyinfo + File.separator + "monkeyerror.log";
        
       
        espressocasenamefile = sepHome + "espresso" + File.separator + "espressoScriptName.txt";
        espressocaseloc = sepHome + File.separator + "exeresult" + File.separator + "espresso";
        espressocoverage = sepHome + "coverage" + File.separator + "espresso";
        espressoerror = sepHome + "error" + File.separator + "espresso.log";
        espressopreferencecase = sepHome + "espresso" + File.separator + "testcase";
        espressopreferencecaseloc = sepHome + "exeresult" + File.separator + "espresso_preference";
        espressopreferencecasecoveragedata = sepHome + "coverage" + File.separator + "espresso_preference";
        espressopreferenceerror =  sepHome + "error" + File.separator + "espresso_preference.log";
        espressoinstrucmd = sepHome + "espresso" + File.separator + "espressoInstruCMD.txt";
        if (new File(espressoinstrucmd).exists()) {
	        espressoCMDs = CMDUtils.readCMD(espressoinstrucmd);
        }
        templocfile = sepHome + "temp" + File.separator + "temploc.txt";

    }

    /**
     * 获取APK文件
     * 
     * @return APK文件对象，如果不存在返回null
     */
    public static File getAPKFile() {
        File appfolder = new File(home + File.separator + "app");
        // 查找不以"_stub.apk"结尾的APK文件
        for (File file : appfolder.listFiles()) {
            if (file.getName().endsWith(".apk") && !file.getName().endsWith("_stub.apk")) {
                return file;
            }
        }
        return null;
    }

    /**
     * 获取Stub APK文件（插桩后的APK）
     * 
     * @return Stub APK文件对象，如果不存在返回null
     */
    public static File getStubAPKFile() {
        File appfolder = new File(home + File.separator + "app");
        // 查找以"_stub.apk"结尾的APK文件
        for (File file : appfolder.listFiles()) {
            if (file.getName().endsWith("_stub.apk")) {
                return file;
            }
        }
        return null;
    }
    
    public static String getInterestTestCaseDataFilePath(int currentit) {
    	String path = Main.testcase + File.separator + "interestinfo_" + currentit + ".json";
    	return path;
    }
    
    public static String getInterestPlanBakFilePath(int currentit) {
    	String path = Main.testcase + File.separator + "interestplan_" + currentit + ".json";
    	return path;
    }

    /**
     * 初始化项目文件结构
     * 创建必要的目录并清理临时文件
     */
    public static void initfiles() {
        // 检查app目录是否存在
        File mainfolder = new File(home + File.separator + "app");
        if (!mainfolder.exists()) {
        	System.out.println("init error: itvalid project path!!");
            return;
        }
        // 创建log目录
        mainfolder = new File(home + File.separator + "log");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        // 创建exeresult目录
        mainfolder = new File(home + File.separator + "exeresult");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        // 创建testcase目录
        mainfolder = new File(home + File.separator + "testcase");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        // 创建coverage目录
        mainfolder = new File(home + File.separator + "coverage");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        // 创建error目录
        mainfolder = new File(home + File.separator + "error");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        // 创建extra目录
        mainfolder = new File(home + File.separator + "extra");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }


        // 删除旧的命令日志文件
        File cmdlogfile = new File(cmdlog);
        if (cmdlogfile.exists()) {
            cmdlogfile.delete();
        }
        // 清理临时文件夹
        File tempfile = new File(tempfolder);
        if (tempfile.exists()) {
            for (File file : tempfile.listFiles()) {
                file.delete();
            }
        }
    }

    private static void useSoot(Transformer myTransformer, String apkfilepath, String outputdir) {
        Options.v().set_allow_phantom_refs(true);

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(false);
        // resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        //Scene.v().addBasicClass("com.fasterxml.jackson", SootClass.SIGNATURES);
        Options.v().set_whole_program(false);
//       Options.v().set_force_overwrite(true);
//       Options.v().set_java_version(Options.java_version_1_7);
        Options.v().set_android_api_version(28);

        String androidsdk = PathHelper.getAndroidSDKHome();
        String jdk = PathHelper.getJavaHome();
        String soothome = PathHelper.getSootPath();
        Options.v().set_android_jars(androidsdk + "/platforms");
        Options.v().set_process_dir(Collections.singletonList(apkfilepath));
        Options.v().set_output_dir(outputdir);
//       Options.v().set_soot_classpath(jdk + "/jre/lib/rt.jar;" + jdk + "/jre/lib/jce.jar;" + soothome);
        Options.v().set_soot_classpath(soothome);
//       Options.v().set_soot_classpath("C:" + File.separator + "Program Files" + File.separator + "Android" + File.separator + "Android Studio" + File.separator + "jre" + File.separator + "jre" + File.separator + "lib" + File.separator + "rt.jar;C:" + File.separator + "Program Files" + File.separator + "Android" + File.separator + "Android Studio" + File.separator + "jre" + File.separator + "jre" + File.separator + "lib" + File.separator + "jce.jar;C:" + File.separator + "Users" + File.separator + "yifeiLu" + File.separator + "Documents" + File.separator + "AndroidAnalyseWorkingSpace" + File.separator + "FinalPreferenceProject" + File.separator + "lib" + File.separator + "sootclasses-trunk-jar-with-dependencies.jar");  
        Options.v().set_keep_line_number(true);
        Options.v().set_process_multiple_dex(true);
//        Options.v().set_no_bodies_for_excluded(true);
//        Options.v().set_no_writeout_body_releasing(true);
//        Options.v().set_no_output_source_file_attribute(true);
//        Options.v().set_no_output_inner_classes_attribute(true);
//        List<String> excludePkgs = new ArrayList<String>();
//        excludePkgs.add("net.time4j.*");
//        Options.v().set_exclude(excludePkgs);
//        List<String> includePkgs = new ArrayList<String>();
//        includePkgs.add("com.forrestguice.suntimeswidget.");
//        Options.v().set_debug(true);
//        Options.v().set_include(includePkgs);
        Options.v().ignore_classpath_errors();
        Options.v().ignore_resolution_errors();
        Options.v().ignore_resolving_levels();
        Scene.v().addBasicClass("javax.annotation.meta.When", 2);
        Scene.v().loadNecessaryClasses();


        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", myTransformer));

//           Scene.v().addBasicClass(classAsSignature, SootClass.SIGNATURES);
        Options.v().set_unfriendly_mode(true);
        try {
            soot.Main.main(new String[0]);
        } catch (soot.CompilationDeathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//       PackManager.v().runPacks();  
//       PackManager.v().writeOutput();  
    }

    //-----------------------------------------------------------------------------------------------

    public void stub() {
        initfiles();
        File apkfile = getAPKFile();
        File resfile = new File(home + File.separator + "app" + File.separator + "res");
        File sourcesfile = new File(home + File.separator + "app" + File.separator + "sources");
        if (!resfile.exists() || !sourcesfile.exists()) {
            String jadx;
            if (OSInfo.getOSType() == OSInfo.OSType.WINDOWS) {
                jadx = PathHelper.getJadxHomePath() + File.separator + "bin" + File.separator + "jadx.bat";
            } else {
                jadx = "/Users/heleninsa/jadx/bin/jadx";
            }
            ProcessExecutor.processlogincmdlog(jadx, "-dr", home + File.separator + "app", apkfile.getAbsolutePath());
        }

        List<String> apkinfolist = ProcessExecutor.processlogincmdlog("aapt", "dump", "badging", apkfile.getAbsolutePath());

        JSONObject jsonob = JsonHelper.saveApkInfo(apkinfolist);
        JsonHelper.getApkInfo();
        System.out.println(jsonob.toJSONString());
        Transformer stubTransformer = new StubTransformer(Main.packagename, Main.extrapackagenames);
        Logger.setTempLogFile(home + File.separator + "log" + File.separator + "soot_stub.log", true);
        useSoot(stubTransformer, apkfile.getAbsolutePath(), tempfolder);

        String signapkname = apkfile.getAbsolutePath().substring(0, apkfile.getAbsolutePath().length() - 4) + "_stub.apk";
        ProcessExecutor.processlogincmdlog("jarsigner", "-keystore", PathHelper.getDebugKeyPath(), "-storepass", "android", "-keypass", "android", "-signedjar", signapkname, tempfolder + File.separator + "" + apkfile.getName(), "androiddebugkey");


    }

    public void initForfirstexe() {
        File firstcasefolder = new File(Main.firstcases);
        if (!firstcasefolder.exists()) {
            firstcasefolder.mkdirs();
        }
//			innerfile.delete();

        File firstcaseresultfolder = new File(Main.firstcasesloc);
        if (!firstcaseresultfolder.exists()) {
            firstcaseresultfolder.mkdirs();
        }
//			innerfile.delete();

        File firstcasecoveragefolder = new File(Main.firstcasescoverage);
        if (!firstcasecoveragefolder.exists()) {
            firstcasecoveragefolder.mkdirs();
        }
//			innerfile.delete();

//		File firstcaseresultfile = new File(Main.firstcasesexeresultfile);
//			firstcaseresultfile.delete();
    }

    public void initForPREFEST_T() {
    	File interestplanfile = new File(Main.interestplanfile);
    	if (!interestplanfile.exists()) {
            File preferenceoutputfile = new File(Main.interestplan);
            if (preferenceoutputfile.exists()) {
                preferenceoutputfile.delete();
            }
            File interestcaselocfolder = new File(Main.interestcaseloc);
            if (!interestcaselocfolder.exists()) {
                interestcaselocfolder.mkdir();
            }
            for (File file : interestcaselocfolder.listFiles()) {
                file.delete();
            }
            File interestcasecoveragefolder = new File(Main.interestcasescoverage);
            if (!interestcasecoveragefolder.exists()) {
                interestcasecoveragefolder.mkdir();
            }
            for (File file : interestcasecoveragefolder.listFiles()) {
                file.delete();
            }
            File interestcasesfolder = new File(Main.interestcases);
            if (!interestcasesfolder.exists()) {
                interestcasesfolder.mkdir();
            }
            for (File file : interestcasesfolder.listFiles()) {
                file.delete();
            }
            File interestcoverdatafile = new File(Main.interestcoverdata);
            if (interestcoverdatafile.exists()) {
            	interestcoverdatafile.delete();
            }
    	}
    }

    public void initForPREFEST_N() {
//		File preferenceoutputfile = new File(Main.interestallplanfile);
        File interestcaselocfolder = new File(Main.interestallcaseloc);
        if (!interestcaselocfolder.exists()) {
            interestcaselocfolder.mkdir();
        }
        File interestcasecoveragefolder = new File(Main.interestallcasescoverage);
        if (!interestcasecoveragefolder.exists()) {
            interestcasecoveragefolder.mkdir();
        }
        File interestcasesfolder = new File(Main.interestallcases);
        if (!interestcasesfolder.exists()) {
            interestcasesfolder.mkdir();
        }
    }

    public void initFornonDefault() {
        File interestcaselocfolder = new File(Main.allpreferencecaseloc);
        if (!interestcaselocfolder.exists()) {
            interestcaselocfolder.mkdir();
        }
        File interestcasecoveragefolder = new File(Main.allpreferencecases);
        if (!interestcasecoveragefolder.exists()) {
            interestcasecoveragefolder.mkdir();
        }
        File interestcasesfolder = new File(Main.allpreferencecoverage);
        if (!interestcasesfolder.exists()) {
            interestcasesfolder.mkdir();
        }
    }

    public void initForpairwise() {
        File pwpreferencecaseinfo = new File(Main.pwpreferencecaseinfo);
        if (!pwpreferencecaseinfo.exists()) {
            pwpreferencecaseinfo.mkdir();
        }
        File pwpreferencecoverage = new File(Main.pwpreferencecoverage);
        if (!pwpreferencecoverage.exists()) {
            pwpreferencecoverage.mkdir();
        }
        File pwpreferencecases = new File(Main.pwpreferencecases);
        if (!pwpreferencecases.exists()) {
            pwpreferencecases.mkdir();
        }
    }
    
    public void initForEspresso() {
    	File espressocaseloc = new File (Main.espressocaseloc);
    	if (!espressocaseloc.exists()) {
    		espressocaseloc.mkdir();
    	}
    	File espressocoverage = new File(Main.espressocoverage);
    	if (!espressocoverage.exists()) {
    		espressocoverage.mkdir();
    	}
    }
    
    public void initForEspressoPreference() {
    	File espressopreferencecase = new File(Main.espressopreferencecase);
    	if (!espressopreferencecase.exists()) {
    		espressopreferencecase.mkdir();
    	}
    	File espressopreferencecaseloc = new File(Main.espressopreferencecaseloc);
    	if (!espressopreferencecaseloc.exists()) {
    		espressopreferencecaseloc.mkdir();
    	}
    	File espressopreferencecasecoveragedata = new File(Main.espressopreferencecasecoveragedata);
    	if (!espressopreferencecasecoveragedata.exists()) {
    		espressopreferencecasecoveragedata.mkdir();
    	}
    	File espressopreferenceerror = new File(Main.espressopreferenceerror);
    	if (!espressopreferenceerror.exists()) {
    		espressopreferenceerror.mkdir();
    	}
    }

    public void firstexe() {
        initfiles();
        initForfirstexe();
        JsonHelper.getApkInfo();
        File stoatScript = new File(Main.mcmctxt);
        File testcaseFolder = new File(Main.firstcases);
        List<File> ourScripts = null;
        if (stoatScript.exists()) {
            ourScripts = StoatScriptLoader.loadStoatScript(stoatScript, testcaseFolder);
        } else {
            ourScripts = new ArrayList<File>();
            for (File file : testcaseFolder.listFiles()) {
                ourScripts.add(file);
            }
        }

        ScriptExecutor.scriptexecute(ourScripts, Main.packagename);
    }

    public void firstexeWithNoTestCaseReGenerated() {
        initfiles();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        File testcaseFolder = new File(Main.firstcases);
        List<File> ourScripts = new ArrayList<File>();
        for (File appiumscript : testcaseFolder.listFiles()) {
            ourScripts.add(appiumscript);
        }
        ScriptExecutor.scriptexecute(ourScripts, (String)apkinfo.get("packagename"));
    }
    
    public static double analysisIt(String datafilename, Map<String, TestCaseData> datas, int currentit) {
    	initfiles();
        Date beforeTime = new Date();
        JsonHelper.getApkInfo();
        if (null == analyseTransformer) {
            analyseTransformer = new PreferenceAnalyseTransformer(Main.packagename, Main.extrapackagenames);
            Logger.setTempLogFile(home + File.separator + "log" + File.separator + "soot_analyse.log", true);
            useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
            analyseTransformer.initpreferences2activity();
            analyseTransformer.analyzeoverride();
        }
        analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
        analyser.analysePreferenceFromLogs(datafilename);
        if (shouldExplorePreference) {
        	if (currentit == 0) {
        		Adapter adapter = analyser.getBasicAdapter();
        		JsonHelper.saveadapterWithInterestValueSelfGen(adapter, Main.testadapter);
        	}else {
        		adaptData();
        	}
        }
        Date afterTime = new Date();
        double consumedTime = (afterTime.getTime() - beforeTime.getTime()) / 1000.00;
        return consumedTime;
    }

    public void analysis(boolean analyzelog) {
        initfiles();
        Date beforeTime = new Date();
        JsonHelper.getApkInfo();
        Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, false);
        analyseTransformer = new PreferenceAnalyseTransformer(Main.packagename, Main.extrapackagenames);
        Logger.setTempLogFile(home + File.separator + "log" + File.separator + "soot_analyse.log", true);
        useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
//        analyseTransformer.analyzeoverride();
        analyseTransformer.initpreferences2activity();
        analyseTransformer.analyzeoverride();
        analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
        if (analyzelog) {
            analyser.analysePreferenceFromLogs(Main.testcaseinfofile);
            File testcaseoldfile = new File(Main.testcaseinfofileold);
            if (testcaseoldfile.exists()) {
            	testcaseoldfile.delete();
            }
        } else {
        	analyser.analysePreferenceOnlyFromCode();
        }
        Date afterTime = new Date();
        System.out.println("execution time is: " + (afterTime.getTime() - beforeTime.getTime()) / 1000.00);

        	Adapter adapter = analyser.getBasicAdapter();
        	JsonHelper.saveadapterWithInterestValueSelfGen(adapter, Main.testadapter);
    }
    
    
    public void exploreForAdapter() {
    	File adapterCoverage = new File(Main.testadpatercoverage);
    	if (!adapterCoverage.exists()) {
    		adapterCoverage.mkdirs();
    	}
    	File adapterFile = new File(Main.testadapter);
    	if (!adapterFile.exists()) {
        	analysis(false);
    	} else {
    		JsonHelper.getApkInfo();
    	}
    	Adapter adapter = JsonHelper.getadapter(Main.testadapter);
		for (List<PreferenceTreeNode> nodes : adapter.xmlcontentlist.values()) {
			for (PreferenceTreeNode node : nodes) {
				node.initTitles();
			}
		}
		try {
			ExplorerServer exploreServer = new ExplorerServer(adapter);
	    	exploreServer.exploreForAdapter();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
    }
    
	public static void adaptData() {
		Adapter adapter = JsonHelper.getadapter(Main.testadapter);
		if (null == adapter || !adapter.explored) {
			return;
		}
		JSONObject testcaseorigin = JsonHelper.getJsonObject(Main.testcaseinfofile);
		if (!new File(Main.testcaseinfofileold).exists()) {
			JsonHelper.saveJsonFile(testcaseorigin, Main.testcaseinfofileold, false);
		}


		Map<String, InterestValue> adaptInterests = adapter.preferencelist;
		JSONArray interestmaparray = adapt(adaptInterests);
		testcaseorigin.put("interestmap", interestmaparray);
		JsonHelper.saveJsonFile(testcaseorigin, Main.testcaseinfofile, false);
		
		if (new File(Main.interestplanfile).exists()) {
			JSONObject interesplanorigin = JsonHelper.getJsonObject(Main.interestplanfile);
			interesplanorigin.put("interestmap", interestmaparray);
			JsonHelper.saveJsonFile(interesplanorigin, Main.interestplanfile, false);
		}
		
		int i = 0;
		while(true) {
			String path = Main.getInterestTestCaseDataFilePath(i);
			if (new File(path).exists()) {
				JSONObject interesplanorigin = JsonHelper.getJsonObject(path);
				interesplanorigin.put("interestmap", interestmaparray);
				JsonHelper.saveJsonFile(interesplanorigin, path, false);
				i++;
			} else {
				break;
			}
		}


	}
	
	private static JSONArray adapt(Map<String, InterestValue> adaptInterests) {
		JSONArray array = new JSONArray();
		for (String name : adaptInterests.keySet()) {
			JSONObject ob = JsonHelper.translatorInterestValue2Json(adaptInterests.get(name));
			array.add(ob);
		}
		return array;
	}

    public void PREFEST_T() {
        initfiles();
        initForPREFEST_T();
        JsonHelper.getApkInfo();
        Map<String, PreferenceAdaptData> failuremap = FailurePreferenceAdapter.getFailurePreferences();
		ScriptGenerationUtil.initAdaptDatas(failuremap);
        if (shouldExplorePreference) {
            exploreForAdapter();
            adaptData();
        }
        if (testSettingOperations) {
            testNonDefaultSettings();
        }

//        Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, true);
//        ScriptExecutor.scriptexecuteforPREFEST_T(datas, (String)apkinfo.get("packagename"));
        File planfile = new File(Main.interestplanfile);
        InterestPlan plan = null;
        if (planfile.exists()) {
        	plan = JsonHelper.getinterestplanAdapt(Main.interestplanfile);
        } else {
        	plan = new InterestPlan(Main.testcaseinfofile);
        }
        plan.execute();

    }
    
    private void testNonDefaultSettings() {
    	Adapter adapter = JsonHelper.getadapter(Main.testadapter);
    	Logger.setTempLogFile(Main.interestplan, true);
    	initFornonDefault();
        ScriptExecutor.scriptexecuteforNonDefault_testingSettings(adapter.xmlcontentlist, adapter.preferencelist);
    }
    
//		initfiles();
//		initforstep4_ver2();
//		JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
//		Main.packagename = apkinfo.getString("packagename");
//		Main.luanchactivityname = apkinfo.getString("luanchactivity");
//		File planfile = new File(Main.interestallplanfile);
//		InterestAllPlan plan = null;
//		Logger.setTempLogFile(Main.interestallcasesexeresultfile, true);
//			plan = new InterestAllPlan(datas, null);
//			plan = JsonHelper.getinterestallplan(Main.interestallplanfile);
//		plan.execute();

    public void PREFEST_N() {
        initfiles();
        initForPREFEST_N();
        JsonHelper.getApkInfo();
        Map<String, PreferenceAdaptData> failuremap = FailurePreferenceAdapter.getFailurePreferences();
		ScriptGenerationUtil.initAdaptDatas(failuremap);
        File planfile = new File(Main.interestallplanfile);
        InterestAllPlan plan = null;
        Logger.setTempLogFile(Main.interestallcasesexeresultfile, true);
        if (!planfile.exists()) {
            Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, true);
//            analyseTransformer = new PreferenceAnalyseTransformer((String)apkinfo.get("packagename"));
//            useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
//            analyseTransformer.initpreferences2activity();
//            analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
//            Map<String, List<PreferenceTreeNode>> preferencetree = analyser.analysepreferencetree();
            Adapter adapter = JsonHelper.getadapter(Main.testadapter);
            plan = new InterestAllPlan(datas, adapter.xmlcontentlist);
        } else {
            plan = JsonHelper.getinterestallplanAdapt(Main.interestallplanfile);
        }
        plan.execute();
    }

    //all
    public void nonDefault() {
        initfiles();
        initFornonDefault();
        JsonHelper.getApkInfo();
        Map<String, PreferenceAdaptData> failuremap = FailurePreferenceAdapter.getFailurePreferences();
		ScriptGenerationUtil.initAdaptDatas(failuremap);
        analyseTransformer = new PreferenceAnalyseTransformer(Main.packagename, Main.extrapackagenames);
        Logger.setTempLogFile(home + File.separator + "log" + File.separator + "preferencetree_analyse.log", true);
//        useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
//        analyseTransformer.initpreferences2activity();
//        analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
//        Map<String, List<PreferenceTreeNode>> preferencetree = analyser.analysepreferencetree();
    	Adapter adapter = JsonHelper.getadapter(Main.testadapter);

        ScriptExecutor.scriptexecuteforNonDefault(adapter.xmlcontentlist, adapter.preferencelist);
    }

    //pairwise
    public void pairwise() {
        initfiles();
        initForpairwise();
        JsonHelper.getApkInfo();
        File planfile = new File(Main.pwpreferenceplanfile);
        PWPlan plan = null;
        Map<String, PreferenceAdaptData> failuremap = FailurePreferenceAdapter.getFailurePreferences();
		ScriptGenerationUtil.initAdaptDatas(failuremap);
        if (!planfile.exists()) {
            Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, false);
//            analyseTransformer = new PreferenceAnalyseTransformer(Main.packagename);
            Logger.setTempLogFile(home + File.separator + "log" + File.separator + "preferencetree_analyse.log", true);
//            useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
//            analyseTransformer.initpreferences2activity();
//            analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
//            Map<String, List<PreferenceTreeNode>> preferencetree = analyser.analysepreferencetree();
            Adapter adapter = JsonHelper.getadapter(Main.testadapter);
            plan = new PWPlan(adapter, datas);
        } else {
            plan = JsonHelper.getpwplanAdapt(Main.pwpreferenceplanfile);
        }
        PWExecutor executor = new PWExecutor(plan);
        executor.execute();
    }

    public void monkey() {
        File monkeyfile = new File(Main.monkeyinfo);
        if (!monkeyfile.exists()) {
            monkeyfile.mkdir();
        }

        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        ProcessExecutor.processnolog("adb", "logcat", "-c");


        double costtime = 0;
        int i = 0;
        while (costtime < 3600) {
            long starttime = new Date().getTime();
            ErrorCollectThread errorthread = new ErrorCollectThread();
            LocThread locthread = new LocThread();
            locthread.setFile(Main.monkeyinfo + "//log" + i + ".txt");
            locthread.start();
            errorthread.setErrorFile(Main.monkeyerror);
            errorthread.start();
            Thread jacoco = new Thread(new Runnable() {
                @Override
                public void run() {
                    ProcessExecutor.processnolog("adb", "shell", "am", "instrument", "-w", Main.packagename + "/" + Main.packagename + ".JacocoInstrumentation");
                }
            });
            ProcessExecutor.processnolognoprint("adb", "shell", "monkey", "--ignore-crashes", "--ignore-security-exceptions", "--pct-touch", "60", "--pct-motion", "20", "--pct-nav", "5", "--pct-majornav", "5", "--pct-appswitch", "2", "--throttle", "200", "-p", Main.packagename, "-v", "5000");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            locthread.locstop();
            errorthread.errorlogstop();
            ProcessExecutor.processnolog("adb", "shell", "am", "broadcast", "-a", "com.example.pkg.END_EMMA", "-f", "16777216", "--es", "name", "monkey" + i);
            try {
                jacoco.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ProcessExecutor.processnolog("adb", "pull", "/mnt/sdcard/coverage/coveragemonkey" + i + ".ec", Main.monkeyinfo + "//monkey" + i + ".ec");
            long endtime = new Date().getTime();
            costtime += (endtime - starttime) / 1000;
            System.out.println("the" + i + " turn, cost time is:" + costtime);
            i++;
        }

    }
    
    public void Espresso() {
        initfiles();
        initForfirstexe();
        initForEspresso();
        JsonHelper.getApkInfo();
        EspressoScriptExecutor.init(Main.packagename);
        File scriptNameFile = new File(Main.espressocasenamefile);
        if (!scriptNameFile.exists()) return;
        EspressoScriptExecutor.scriptexecute(scriptNameFile, Main.packagename);
    }
    
    public void EspressoAnalysis() {
        initfiles();
        analysis(true);
    }
    
    public void PREFEST_TWithEspresso() {
        initfiles();
        initForEspressoPreference();
        initForPREFEST_T();
        File scriptNameFile = new File(Main.espressocasenamefile);
        EspressoScriptExecutor.getScriptNames(scriptNameFile);
        JsonHelper.getApkInfo();
        EspressoScriptExecutor.init(Main.packagename);
        Map<String, PreferenceAdaptData> failuremap = FailurePreferenceAdapter.getFailurePreferences();
		ScriptGenerationUtil.initAdaptDatas(failuremap);
        if (shouldExplorePreference) {
            exploreForAdapter();
            adaptData();
        }
        if (testSettingOperations) {
            testNonDefaultSettings();
        }
//        Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, true);
//        ScriptExecutor.scriptexecuteforPREFEST_T(datas, (String)apkinfo.get("packagename"));
        File planfile = new File(Main.interestplanfile);
        InterestPlan plan = null;
        if (planfile.exists()) {
        	plan = JsonHelper.getinterestplanAdapt(Main.interestplanfile);
        } else {
        	plan = new InterestPlan(Main.testcaseinfofile);
//                testNonDefaultSettings();
        }
        plan.setPlanMode(InterestPlanMode.ESPRESSO);
        plan.execute();
    }
    

}
