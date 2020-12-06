package util;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.util.*;

public class Util {
    public static File openFileWindow(){
        File file;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        //        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("XLS", "*.xls"),
//                new FileChooser.ExtensionFilter("TXT", "*.txt"));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XLS", "*.xls"),
                new FileChooser.ExtensionFilter("XLSX", "*.xlsx"));
        file = fileChooser.showOpenDialog(null);
        return file;
    }

    public static File openDirectoryWindow(){
        DirectoryChooser directoryChooser=new DirectoryChooser();
        File file = directoryChooser.showDialog(null);
        return file;
    }

    public static void showDialog(String TYPE, String content){
        Alert alert=new Alert(Alert.AlertType.NONE);
        if(TYPE=="WARNING"){
            alert = new Alert(Alert.AlertType.WARNING);
        }else if (TYPE=="INFORMATION"){
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setContentText(content);
        alert.show();
    }
}
