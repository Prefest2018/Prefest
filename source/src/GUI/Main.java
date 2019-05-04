package GUI;

import appiumscript.scriptexecutor.*;
import appiumscript.scripttranslator.StoatScriptLoader;
import data.TestCaseData;
import soot.*;
import soot.options.Options;
import sootproject.preferenceAnalyse.PreferenceAnalyser;
import sootproject.resourceLoader.PreferenceTreeNode;
import sootproject.soot.PreferenceAnalyseTransformer;
import sootproject.soot.StubTransformer;
import sun.awt.OSInfo;
import tools.JsonHelper;
import tools.Logger;
import tools.PathHelper;
import tools.ProcessExecutor;
import uiautomationexploration.Adapter;
import uiautomationexploration.ExplorerServer;
import uiautomationexploration.PreferenceExplorer;

import java.io.File;
import java.util.*;

import org.json.simple.JSONObject;

public class Main {
    public static boolean debug = true;
    public static boolean blockmode = false;
    public static boolean resetWhenError = true;
    public static boolean shouldExplorePreference = true;
    public static boolean shouldAddNoBranchTargets = true;
	public static boolean resetForEachRun = false;
    public static String avdname = null;

    public static String packagename = null;
    public static String luanchactivityname = null;
    public final static String PRESCRIPT = "preference_pre";
    public static String[] skipstaticmethods = {"$jacocoInit"};

    public static String home = null;
    public static String apkinfo = null;
    public static String cmdlog = null;
    public static String tempfolder = null;
    public static String mcmctxt = null;
    public static String firstcases = null;
    public static String extra = null;
    public static String firstcasesexeresultfile = null;
    public static String firstcasesloc = null;
    public static String firstcasescoverage = null;
    public static String testcaseinfofile = null;
    public static String testcaseinfofileold = null;
    public static String firstcaseerror = null;
    public static String testadapter = null;
    public static String testadpatercoverage = null;
    public static String interestplan = null;
    public static String preferencetxt = null;
    public static String interestcases = null;
    public static String interestcasesexeresultfile = null;
    public static String interestcaseloc = null;
    public static String interestcaseinfofile = null;
    public static String interestcasescoverage = null;
    public static String interesterror = null;
    public static String interestallplanfile = null;
    public static String interestallcases = null;
    public static String interestallcaseloc = null;
    public static String interestallcasesexeresultfile = null;
    public static String interestallcaseinfofile = null;
    public static String interestallcasescoverage = null;
    public static String interestallerror = null;
    public static String allpreferenceprecase = null;
    public static String allpreferenceprecaselog = null;
    public static String allpreferencecases = null;
    public static String allpreferencecaseloc = null;
    public static String allpreferenceinfofile = null;
    public static String allpreferencecoverage = null;
//	public static String ofotpreferenceprecase = null;
//	public static String ofotpreferenceprecaselog = null;
    public static String pwpreferencecases = null;
    public static String pwpreferencecaseloc = null;
    public static String pwpreferencecoverage = null;
    public static String pwpreferenceplanfile = null;
    public static String pwpreferencecaseinfo = null;
    public static String pwpreferenceresultfile = null;
    public static String monkeyinfo = null;

    public void updateHome(String home) {
        Main.packagename = null;
        Main.luanchactivityname = null;
        Main.home = home;
        String sepHome = home + File.separator;
        Main.tempfolder = sepHome + "temp";
        Main.extra = sepHome + "extra";
        Main.apkinfo = sepHome + "app" + File.separator + "apkinfo.json";
        Main.cmdlog = sepHome + "log" + File.separator + "cmdlog.txt";
        Main.mcmctxt = sepHome + "testcase" + File.separator + "mcmc_all_history_testsuites.txt";
        Main.firstcases = sepHome + "testcase" + File.separator + "firstcases";
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
        Main.interestplan = sepHome + "testcase" + File.separator + "interestplan.txt";
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
        allpreferenceprecase = sepHome + "testcase" + File.separator + "allpreferencecases" + File.separator + "precase.py";
        allpreferenceprecaselog = sepHome + "testcase" + File.separator + "allpreferencecases" + File.separator + "precase_log.py";
        allpreferencecases = sepHome + "testcase" + File.separator + "allpreferencecases";
        allpreferencecaseloc = sepHome + "exeresult" + File.separator + "allpreferenceresult";
        allpreferenceinfofile = sepHome + "testcase" + File.separator + "allpreferencetestcaseinfo.json";
        allpreferencecoverage = sepHome + "coverage" + File.separator + "allpreferencecoverage";
        pwpreferencecases = sepHome + "testcase" + File.separator + "pwpreferencecases";
//		pwpreferencecaseloc = sepHome + "exeresult" + File.separator + "pwpreferenceresult";
        pwpreferencecoverage = sepHome + "coverage" + File.separator + "pwpreferencecoverage";
        pwpreferenceplanfile = sepHome + "testcase" + File.separator + "pwplan.json";
        pwpreferencecaseinfo = sepHome + "testcase" + File.separator + "pwpreferencecaseinfo";
        pwpreferenceresultfile = sepHome + "exeresult" + File.separator + "pwexecutionresult.txt";

        monkeyinfo = sepHome + "monkey";
    }

    public static File getAPKFile() {
        File appfolder = new File(home + File.separator + "app");
        for (File file : appfolder.listFiles()) {
            if (file.getName().endsWith(".apk") && !file.getName().endsWith("_stub.apk")) {
                return file;
            }
        }
        return null;
    }

    public static File getStubAPKFile() {
        File appfolder = new File(home + File.separator + "app");
        for (File file : appfolder.listFiles()) {
            if (file.getName().endsWith("_stub.apk")) {
                return file;
            }
        }
        return null;
    }

    public static void initfiles() {
        File mainfolder = new File(home + File.separator + "app");
        if (!mainfolder.exists()) {
        	System.out.println("init error: itvalid project path!!");
            return;
        }
        mainfolder = new File(home + File.separator + "log");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        mainfolder = new File(home + File.separator + "exeresult");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        mainfolder = new File(home + File.separator + "testcase");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        mainfolder = new File(home + File.separator + "coverage");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }
        mainfolder = new File(home + File.separator + "error");
        if (!mainfolder.exists()) {
            mainfolder.mkdir();
        }


        File cmdlogfile = new File(cmdlog);
        if (cmdlogfile.exists()) {
            cmdlogfile.delete();
        }
        File tempfile = new File(tempfolder);
        if (tempfile.exists()) {
            for (File file : tempfile.listFiles()) {
                file.delete();
            }
        }
    }

    private void useSoot(Transformer myTransformer, String apkfilepath, String outputdir) {
        Options.v().set_allow_phantom_refs(true);

        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk);

        //output as APK, too//-f J
        Options.v().set_output_format(Options.output_format_dex);
        Options.v().set_prepend_classpath(true);
        Options.v().set_validate(true);
        // resolve the PrintStream and System soot-classes
        Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
        Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
        //Scene.v().addBasicClass("com.fasterxml.jackson", SootClass.SIGNATURES);
        Options.v().set_whole_program(false);
//       Options.v().set_force_overwrite(true);
//       Options.v().set_java_version(Options.java_version_1_7);
        Options.v().set_android_api_version(21);

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
        Options.v().ignore_classpath_errors();
        Options.v().ignore_resolution_errors();
        Options.v().ignore_resolving_levels();
        Scene.v().addBasicClass("javax.annotation.meta.When", 2);
        Scene.v().loadNecessaryClasses();


        PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", myTransformer));
        //String params[] = {"-android-jars", "C:" + File.separator + "Users" + File.separator + "yifeiLu" + File.separator + "Documents" + File.separator + "AndroidStudio" + File.separator + "sdk" + File.separator + "platforms" + File.separator + "", "-process-dir", projectHome};

//       for (String classAsSignature : classesAsSignature) {
//           Scene.v().addBasicClass(classAsSignature, SootClass.SIGNATURES);
//       }
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
                jadx = "jadx.bat";
            } else {
                jadx = "/Users/heleninsa/jadx/bin/jadx";
            }
            ProcessExecutor.processlogincmdlog(jadx, "-dr", home + File.separator + "app", apkfile.getAbsolutePath());
        }

        List<String> apkinfolist = ProcessExecutor.processlogincmdlog("aapt", "dump", "badging", apkfile.getAbsolutePath());
        JSONObject jsonob = JsonHelper.saveApkInfo(apkinfolist);

        System.out.println(jsonob.toJSONString());
        Transformer stubTransformer = new StubTransformer((String)jsonob.get("packagename"));
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
//		for (File innerfile : firstcasefolder.listFiles()) {
//			innerfile.delete();
//		}

        File firstcaseresultfolder = new File(Main.firstcasesloc);
        if (!firstcaseresultfolder.exists()) {
            firstcaseresultfolder.mkdirs();
        }
//		for (File innerfile : firstcaseresultfolder.listFiles()) {
//			innerfile.delete();
//		}

        File firstcasecoveragefolder = new File(Main.firstcasescoverage);
        if (!firstcasecoveragefolder.exists()) {
            firstcasecoveragefolder.mkdirs();
        }
//		for (File innerfile : firstcasecoveragefolder.listFiles()) {
//			innerfile.delete();
//		}

//		File firstcaseresultfile = new File(Main.firstcasesexeresultfile);
//		if (firstcaseresultfile.exists()) {
//			firstcaseresultfile.delete();
//		}
    }

    public void initForPREFEST_T() {
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

    public void firstexe() {
        initfiles();
        initForfirstexe();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
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

        ScriptExecutor.scriptexecute(ourScripts, (String)apkinfo.get("packagename"));
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


    public void analysis(boolean analyzelog) {
        initfiles();
        Date beforeTime = new Date();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
        Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, false);
        PreferenceAnalyseTransformer analyseTransformer = new PreferenceAnalyseTransformer((String)apkinfo.get("packagename"));
        Logger.setTempLogFile(home + File.separator + "log" + File.separator + "soot_analyse.log", true);
        useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
//        analyseTransformer.analyzeoverride();
        analyseTransformer.initpreferences2activity();
        analyseTransformer.analyzeoverride();
        PreferenceAnalyser analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
        if (analyzelog) {
            analyser.analysePreferenceFromLogs();
            File testcaseoldfile = new File(Main.testcaseinfofileold);
            if (testcaseoldfile.exists()) {
            	testcaseoldfile.delete();
            }
        } else {
        	analyser.analysePreferenceOnlyFromCode();
        }
        Date afterTime = new Date();
        System.out.println("execution time is: " + (afterTime.getTime() - beforeTime.getTime()) / 1000.00);

        if (shouldExplorePreference) {
        	Adapter adapter = analyser.getBasicAdapter();
        	JsonHelper.saveadapterWithpreferencelistFromtestcasedata(adapter, Main.testadapter, Main.testcaseinfofile);
        }
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
            JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
            Main.packagename = (String)apkinfo.get("packagename");
            Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
    	}
    	Adapter adapter = JsonHelper.getadapter(Main.testadapter);
		try {
			ExplorerServer exploreServer = new ExplorerServer(adapter);
	    	exploreServer.exploreForAdapter();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void PREFEST_T() {
        initfiles();
        initForPREFEST_T();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        if (shouldExplorePreference) {
            exploreForAdapter();
        }
        Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, true);
        ScriptExecutor.scriptexecuteforPREFEST_T(datas, (String)apkinfo.get("packagename"));
    }


    public void PREFEST_N() {
        initfiles();
        initForPREFEST_N();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
        File planfile = new File(Main.interestallplanfile);
        InterestAllPlan plan = null;
        Logger.setTempLogFile(Main.interestallcasesexeresultfile, true);
        if (!planfile.exists()) {
            Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, true);
            PreferenceAnalyseTransformer analyseTransformer = new PreferenceAnalyseTransformer((String)apkinfo.get("packagename"));
            useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
            analyseTransformer.initpreferences2activity();
            PreferenceAnalyser analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
            Map<String, List<PreferenceTreeNode>> preferencetree = analyser.analysepreferencetree();
            plan = new InterestAllPlan(datas, preferencetree);
        } else {
            plan = JsonHelper.getinterestallplanAdapt(Main.interestallplanfile);
        }
        plan.execute();
    }

    //all
    public void nonDefault() {
        initfiles();
        initFornonDefault();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
        Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, false);
        PreferenceAnalyseTransformer analyseTransformer = new PreferenceAnalyseTransformer((String)apkinfo.get("packagename"));
        Logger.setTempLogFile(home + File.separator + "log" + File.separator + "preferencetree_analyse.log", true);
        useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
        analyseTransformer.initpreferences2activity();
        PreferenceAnalyser analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
        Map<String, List<PreferenceTreeNode>> preferencetree = analyser.analysepreferencetree();
        ScriptExecutor.scriptexecuteforec(preferencetree);
    }

    //pairwise
    public void pairwise() {
        initfiles();
        initForpairwise();
        JSONObject apkinfo = JsonHelper.getJsonObject(Main.apkinfo);
        Main.packagename = (String)apkinfo.get("packagename");
        Main.luanchactivityname = (String)apkinfo.get("luanchactivity");
        File planfile = new File(Main.pwpreferenceplanfile);
        PWPlan plan = null;
        if (!planfile.exists()) {
            Map<String, TestCaseData> datas = JsonHelper.gettestcasesdataAdapt(Main.testcaseinfofile, false);
            PreferenceAnalyseTransformer analyseTransformer = new PreferenceAnalyseTransformer(Main.packagename);
            Logger.setTempLogFile(home + File.separator + "log" + File.separator + "preferencetree_analyse.log", true);
            useSoot(analyseTransformer, getStubAPKFile().getAbsolutePath(), tempfolder);
            analyseTransformer.initpreferences2activity();
            PreferenceAnalyser analyser = new PreferenceAnalyser(analyseTransformer, datas, home + File.separator + "app" + File.separator + "res", home + File.separator + "app" + File.separator + "sources");
            Map<String, List<PreferenceTreeNode>> preferencetree = analyser.analysepreferencetree();
            plan = new PWPlan(preferencetree, datas);
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

            LocThread locthread = new LocThread();
            locthread.setFile(Main.monkeyinfo + "//log" + i + ".txt");
            locthread.start();
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
            ProcessExecutor.processnolog("adb", "shell", "am", "broadcast", "-a", "com.example.pkg.END_EMMA", "--es", "name", "monkey" + i);
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

}
