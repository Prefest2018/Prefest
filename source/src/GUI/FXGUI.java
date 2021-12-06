package GUI;

import appiumscript.scriptexecutor.AVDThread;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import tools.PathHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FXGUI extends Application {

    private static String defaultProjectHome = null;
    private static String tester = "APPIUM";

//            // win
//            // mac/linux
//            defaultProjectHome = "/Users/heleninsa/Desktop/PREFEST/projecthome";

    private Main main = null;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(PathHelper.getFXMLURL());
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        main = new Main();
        init_variables();
        init_files();
        init_views(root, primaryStage);
    }
    
    private void init_variables() throws Exception{
    	defaultProjectHome = System.getProperty("user.dir");
    	File configFile = new File(PathHelper.getConfigFilePath());
    	Main.newenvs = readConfigFile(configFile);
//    	setEnv(Main.newenvs);
    }

    private void init_files() {
        main.updateHome(defaultProjectHome);
    }

    private static TextField projectfield = null;
    private static Thread avdthread = null;

    private void init_views(Parent parent, Stage stage) {
        Button setlocationbutton = (Button) parent.lookup("#setlocationbutton");
        projectfield = (TextField) parent.lookup("#locationtext");
        projectfield.setText(defaultProjectHome);
        setlocationbutton.setOnAction(ev -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File(defaultProjectHome));
            File file = directoryChooser.showDialog(stage);

            String path = file.getPath();
            projectfield.setText(path);
            main.updateHome(path);
        });

        CheckBox autoavdcheckbox = (CheckBox) parent.lookup("#autoavdcheckbox");
        autoavdcheckbox.setOnAction(ev -> {
            boolean isselected = autoavdcheckbox.isSelected();
            if (isselected) {
                if (null == avdthread) {
                    avdthread = new AVDThread();
                    avdthread.start();
                }
            } else {
                if (null != avdthread) {
                    avdthread.stop();
                    avdthread = null;
                }
            }
        });
        
        
        ChoiceBox<String> testerComboBox = (ChoiceBox)parent.lookup("#testercombobox");
        testerComboBox.getItems().addAll("APPIUM", "ESPRESSO");
//        tester = "ESPRESSO";
        testerComboBox.setValue(tester);
        testerComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override 
            public void changed(ObservableValue ov, String t, String t1) {                
            	tester = t1.toUpperCase();
            }    
        });

        //Our approach
        //stub
        Button stubbutton = (Button) parent.lookup("#stubbutton");
        stubbutton.setOnAction(arg0 -> main.stub());

        //firstExe
        Button firstexebutton = (Button) parent.lookup("#firstexebutton");
        firstexebutton.setOnAction(arg0 -> {if(tester.equals("APPIUM")) {main.firstexe();} else if (tester.equals("ESPRESSO")) {main.Espresso();}});

        //analysis
        Button analysisbutton = (Button) parent.lookup("#analysisbutton");
        analysisbutton.setOnAction(arg0 -> main.analysis(true));

        //PREFEST(T)
        Button prefest_tbutton = (Button) parent.lookup("#prefest_tbutton");
        prefest_tbutton.setOnAction(arg0 -> {if(tester.equals("APPIUM")) {main.PREFEST_T();} else if (tester.equals("ESPRESSO")) {main.PREFEST_TWithEspresso();}});

        //PREFEST(N)
        Button prefest_nbutton = (Button) parent.lookup("#prefest_nbutton");
        prefest_nbutton.setOnAction(arg0 -> main.PREFEST_N());


        //Other approach
        //NonDefault
        Button nondefaultbutton = (Button) parent.lookup("#nondefaultbutton");
        nondefaultbutton.setOnAction(arg0 -> main.nonDefault());

        //Pairwise
        Button pairwisebutton = (Button) parent.lookup("#pairwisebutton");
        pairwisebutton.setOnAction(arg0 -> main.pairwise());

        //monkey
        Button monkeybutton = (Button) parent.lookup("#monkeybutton");
        monkeybutton.setOnAction(arg0 -> main.monkey());

    }

    public static void main(String[] arg) {
        launch(arg);
    }
    
    public void setEnv(Map<String, String> newenv) throws Exception {
    	  try {
    	    Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
    	    java.lang.reflect.Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
    	    theEnvironmentField.setAccessible(true);
    	    Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
    	    env.putAll(newenv);
    	    java.lang.reflect.Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
    	    theCaseInsensitiveEnvironmentField.setAccessible(true);
    	    Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
    	    cienv.putAll(newenv);
    	  } catch (NoSuchFieldException e) {
    	    Class[] classes = Collections.class.getDeclaredClasses();
    	    Map<String, String> env = System.getenv();
    	    for(Class cl : classes) {
    	      if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
    	    	java.lang.reflect.Field field = cl.getDeclaredField("m");
    	        field.setAccessible(true);
    	        Object obj = field.get(env);
    	        Map<String, String> map = (Map<String, String>) obj;
    	        map.clear();
    	        map.putAll(newenv);
    	      }
    	    }
    	  }
    }
    
    
    private static String[] valnames = {"JAVA_HOME", "ANDROID_HOME", "JADX_HOME", "APPIUM_HOME", "PYTHON_HOME", "ANDROID_LIB"};
    private static String[] spenames = {"avdname"};
    public Map<String, String> readConfigFile(File configFile) {
    	Map<String, String> newenvs = new HashMap<String, String>();
    	try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
			String content = null;
			all: while ((content = br.readLine()) != null) {
				String[] pathcontent = content.split("=");
				if (pathcontent.length == 2) {
					String key = pathcontent[0].trim();
					String value = pathcontent[1].trim();
					for (String valname : valnames) {
						if (key.equals(valname)) {
							newenvs.put(key, value);
							continue all;
						}
					}
					switch(key) {
					case "avd_name":Main.avdname = value;break;
					case "proxy":Main.proxy = value;break;
					case "reset_when_error":Main.resetWhenError = "false".equals(value)?false:true;break;
					case "preference_explore":Main.shouldExplorePreference = "true".equals(value)?true:false;break;
					case "reset_for_each_run":Main.resetForEachRun = "true".equals(value)?true:false;break;
					case "test_setting_operations":Main.testSettingOperations = "true".equals(value)?true:false;break;
					case "default_projecthome":{
						File file = new File(value);
						if (file.exists()) {
							defaultProjectHome = value;
							main.updateHome(value);
						}
						break;
					}
					}
				} else {
					//TODO
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("file \'config-win.txt\' is not found!");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	// add several variables to path
    	String path = System.getenv("PATH");
    	
//    	String javahome = newenvs.get("JAVA_HOME");
//    		javahome = updateCurrentRefer(javahome);
//        	path = path + File.pathSeparator + javahome + File.separator + "bin";
//    	String androidhome = newenvs.get("ANDROID_HOME");
//    		androidhome = updateCurrentRefer(androidhome);
//    		path = path + File.pathSeparator + androidhome;
//        	path = path + File.pathSeparator + androidhome + File.separator + "tools";
//        	path = path + File.pathSeparator + androidhome + File.separator + "emulator";
//        	path = path + File.pathSeparator + androidhome + File.separator + "platform-tools";
////        	path = path + File.pathSeparator + androidhome + File.separator + "ndk-bundle";
//    	String jadxhome = newenvs.get("JADX_HOME");
//				jadxhome = new File(".").getCanonicalPath() + "lib" + File.separator + "jadx";
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//    		jadxhome = updateCurrentRefer(jadxhome);
//    		path = path + File.pathSeparator + jadxhome + File.separator + "bin";

//    	String appiumhome = newenvs.get("APPIUM_HOME");
//    		path = path + File.pathSeparator + appiumhome;
//    	String pythonhome = newenvs.get("PYTHON_HOME");
//    		pythonhome = updateCurrentRefer(pythonhome);
//    		path = path + File.pathSeparator + pythonhome;
//    		path = path + File.pathSeparator + pythonhome + File.separator + "Scripts";
//    	String androidlib = newenvs.get("ANDROID_LIB");
//    		androidlib = updateCurrentRefer(androidlib);
//    		path = path + File.pathSeparator + androidlib;

    	newenvs.put("PATH", path);
    	return newenvs;
    }
    
    
    private static String updateCurrentRefer(String path) {
    	if (null != path && path.startsWith(".\\")) {
    		String current = null;
    		try {
    			current = new java.io.File( "." ).getCanonicalPath();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		path = current + path.substring(2);
    	}
    	return path;
    }

}
