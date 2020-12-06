package similarity;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.python.antlr.ast.Str;
import util.*;
import myEncrypt.MyEncrypt;
import java.io.*;

public class Similarity  extends Application {
//    public Similarity(String mode) {
//        System.out.println(mode);
//    }
    //  UI
    @FXML
    private TextField te_referenceSample;
    @FXML
    private TextField te_targetSample;
    @FXML
    private Button pb_referenceSample;
    @FXML
    private void initialize() {
        java.lang.StackTraceElement[] classArray= new Exception().getStackTrace() ;
        for(int i=0;i<classArray.length;i++){
            if (classArray[i].getClassName().equals("main.Controller")&&classArray[i].getMethodName().equals("sl_computeWithDatabase")){
                this.pb_referenceSample.setVisible(false);
                this.te_referenceSample.setVisible(false);
                break;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("similarity.fxml"));
        primaryStage.setTitle("Similarity Window");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private File referenceFile;
    private File targetFile;

    public void sl_selectReferenceSample() throws IOException {
        this.referenceFile =Util.openFileWindow();
        if (this.referenceFile != null) {
            this.te_referenceSample.setText(this.referenceFile.getCanonicalFile().getName());
        }
    }

    public void sl_selectTargetSample() throws IOException {
        this.targetFile =Util.openFileWindow();
        if (this.targetFile != null) {
            this.te_targetSample.setText(this.targetFile.getCanonicalFile().getName());
        }
    }

    public void sl_compute() throws IOException {
        String out_file_name = null;
        //computeWithDatabase
        if (this.referenceFile==null){
            MyEncrypt myEncrypt = new MyEncrypt();
            myEncrypt.compute(this.targetFile);
            out_file_name = "output\\SimilarityResult_DATABASE_" + this.targetFile.getName().split("\\.")[0] + ".xlsx";
            Util.showDialog("INFORMATION",out_file_name+" has been written.");
        }
        else{
            computeWithinSamples();
        }
    }

   private void computeWithinSamples(){
        //call python function
        Process proc;
        try {
            proc = Runtime.getRuntime().exec(getCurPath()+"py_src\\pred.exe " +"compute "+getCurPath()+"trained_DATABASE_model "+this.referenceFile+" "+this.targetFile);// 执行py文件
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
 }
