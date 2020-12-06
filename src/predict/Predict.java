package predict;
import javafx.scene.control.ComboBox;
import util.*;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
public class Predict extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("predict.fxml"));
        primaryStage.setTitle("Predict Window");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    //  UI
    @FXML
    private TextField te_trainData;
    @FXML
    private TextField te_testData;
    @FXML
    private ComboBox<String> cb_trainData;

    File trainFileDirectory;
    File testFile;

    public void sl_trainMode() throws IOException {
        if(this.cb_trainData.getValue().equals("DEFAULT")){
            this.te_trainData.setText("SCU data");
        }else if(this.cb_trainData.getValue().equals("CUSTOMIZED")) {
            selectTrainData();
        }
    }

    public void sl_selectTestData() throws IOException {
        this.testFile =Util.openFileWindow();
        if (this.testFile != null) {
            this.te_testData.setText(this.testFile.getCanonicalFile().getName());
        }
    }

    public void sl_train(){
        //call python function
        if (this.trainFileDirectory==null){
            return;
        }
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(getCurPath()+"py_src\\pred.exe " +"train "+this.trainFileDirectory);// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                Util.showDialog("INFORMATION",line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sl_test(){
        //call python function
        Process proc;
        try {
            if (this.trainFileDirectory == null){
                proc = Runtime.getRuntime().exec(getCurPath()+"py_src\\pred.exe " +"test DATABASE "+getCurPath()+"trained_DATABASE_model"+" "+this.testFile);
            }
            else {
                proc = Runtime.getRuntime().exec( getCurPath()+"py_src\\pred.exe "  +"test others "+this.trainFileDirectory+" "+this.testFile);// 执行py文件
            }

            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                Util.showDialog("INFORMATION",line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getCurPath(){
        //obtain current working path cause program will call .py function
        String curPath=null;
        String curJar = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        curJar=curJar.replace("production","artifacts");
        int lastIndex = curJar.lastIndexOf('/') + 1;

        curPath = curJar.substring(1,lastIndex);
        curPath = curPath.replace("/", "\\");
        return curPath;
    }

    public void selectTrainData() throws IOException {
        this.trainFileDirectory =Util.openDirectoryWindow();
        if (this.trainFileDirectory != null) {
            this.te_trainData.setText(this.trainFileDirectory.getCanonicalPath());
        }
    }
}
