package matchCount.match;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import org.jfree.chart.plot.XYPlot;

import java.util.*;
import java.io.*;

import java.util.List;

import org.jfree.chart.*;
import org.jfree.chart.ChartUtils;
import org.jfree.data.xy.*;
import util.Util;

// output four matchCount.match/mismatch results(.txt)
public class TableWidget extends Application{
    public TableWidget(String fileName, String tableName, List<Compare> itemsList, List<GroupedMatchResult> groupedResultIndex,int allaleNum,boolean matchMode){
        this.fileName = fileName;
        this.tableName=tableName;
        this.itemsList = itemsList;
        this.groupedResultIndex = groupedResultIndex;
        this.allaleNum=allaleNum;
        this.matchMode = matchMode;
    }

    private String tableName;
    private String fileName;
    private List<Compare> itemsList ;
    private List<GroupedMatchResult> groupedResultIndex;
    private int allaleNum;
    private ObservableList<Compare> items = FXCollections.observableArrayList();
    private boolean matchMode = true;

    public void start(Stage primaryStage) throws Exception {
        TableView<Compare> table = initTable(primaryStage);
        // button set
        Button pb_CLOSE = new Button("CLOSE");
        Button pb_SAVE = new Button("SAVE");
        Button pb_PLOT = new Button("PLOT");

        BorderPane root = new BorderPane();
        BorderPane rootButton = new BorderPane();
        rootButton.setLeft(pb_PLOT);
        rootButton.setCenter(pb_SAVE);
        rootButton.setRight(pb_CLOSE);
        root.setCenter(table);
        root.setBottom(rootButton);

        //no plot function if matchMode
        if(this.matchMode){
            pb_PLOT.setVisible(false);
        }

        //show
        primaryStage.setTitle(this.tableName);
        primaryStage.setScene(new Scene(root,500,300));
        primaryStage.show();

        // if not itemslist exist
        if (itemsList.size() == 0)
        {
            Label ll_not_ava = new Label("Not Avaliable");
            root.setCenter(ll_not_ava);
        }

        //action for save_button
        pb_SAVE.setOnAction((e)->{
            try {
                sl_save(table);
                primaryStage.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        pb_PLOT.setOnAction((e)->{
            try{
                sl_plot();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
        );
        pb_CLOSE.setOnAction((e) -> {primaryStage.close();});
    }

    // table
    private TableView<Compare> initTable(Stage stage) {
        //Create table and columns
        TableView<Compare> table = new TableView<>();
        TableColumn<Compare, String> column_name = new TableColumn<>("NamePair");
        TableColumn<Compare, String> column_popu = new TableColumn<>("PopuPair");
        TableColumn<Compare, String> column_group = new TableColumn<>("GroupPair");
        TableColumn<Compare, Integer> column_matchNum;
        TableColumn<Compare, Integer> column_matchSteps= new TableColumn<>("MisMatchSteps");
        TableColumn<Compare, String> column_matchRatio= new TableColumn<>("MisMatchRatio") ;
        TableColumn<Compare, String> column_matchDetail = new TableColumn<>("MisMatchDetail");
        if (this.matchMode){
            column_matchNum = new TableColumn<>("MatchNum");
        }else {
            column_matchNum = new TableColumn<>("MisMatchNum");
        }

        //Add items to ObservableList
        this.items.addAll(this.itemsList);

        //Change ValueFactory for each column
        column_name.setCellValueFactory(new PropertyValueFactory<>("sampleNamePair"));
        column_popu.setCellValueFactory(new PropertyValueFactory<>("samplePopuPair"));
        column_group.setCellValueFactory(new PropertyValueFactory<>("sampleGroupPair"));
        if(this.matchMode){
            column_matchNum.setCellValueFactory(new PropertyValueFactory<>("matchNum"));
            table.setItems(items);
            table.getColumns().addAll(column_name,column_popu,column_group,column_matchNum);

        }else {
            column_matchNum.setCellValueFactory(new PropertyValueFactory<>("misMatchNum"));
            column_matchSteps.setCellValueFactory(new PropertyValueFactory<>("misMatchSteps"));
            column_matchRatio.setCellValueFactory(new PropertyValueFactory<>("misMatchRatio"));
            column_matchDetail.setCellValueFactory(new PropertyValueFactory<>("misMatchDetail"));
            table.setItems(items);
            table.getColumns().addAll(column_name,column_popu,column_group,column_matchNum,column_matchSteps,column_matchRatio,column_matchDetail);
        }
        //Add columns to the table
        return table;
    }
    //save
    private void sl_save(TableView<Compare> table) throws IOException {
        File filePath = new File("output//Result_"+this.fileName+"/");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        if (this.matchMode){
            String out_file_name = filePath.getCanonicalPath()+ "\\match" +this.tableName+".txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(out_file_name));
            String ss = "samplePair\tpopulationPair\tgroupPair\tmatchNum";
            bw.write(ss);
            bw.newLine();
            for (int i=0;i<this.itemsList.size();i++){
                String content ="";
                Compare curCompare = this.itemsList.get(i);
                content+=(curCompare.getSampleNamePair()+"\t"+curCompare.getSamplePopuPair()+"\t"+curCompare.getSampleGroupPair()+"\t"+curCompare.getMatchNum());
                bw.write(content);
                bw.newLine();
            }
            bw.close();
            Util.showDialog("INFORMATION",out_file_name+" has been written");

        }else {
            String out_file_name = filePath.getCanonicalPath()+"\\misMatch"+this.tableName+".txt";
            BufferedWriter bw = new BufferedWriter(new FileWriter(out_file_name));
            String ss = "samplePair\tpopulationPair\tgroupPair\tmisMatchNum\tmisMatchSteps\tmisMatchRatio\tmisMatchDetail";
            bw.write(ss);
            bw.newLine();
            for (int i=0;i<this.itemsList.size();i++){
                String content ="";
                Compare curCompare = this.itemsList.get(i);
                content+=(curCompare.getSampleNamePair()+"\t"+curCompare.getSamplePopuPair()+"\t"+curCompare.getSampleGroupPair()+"\t"+curCompare.getMisMatchNum()
                        +"\t"+curCompare.getMisMatchSteps()+"\t"+curCompare.getMisMatchRatio()+"\t"+curCompare.getMisMatchDetail());
                bw.write(content);
                bw.newLine();
            }
            bw.close();
            Util.showDialog("INFORMATION",out_file_name+" has been written");
        }
    }

    private void sl_plot() throws IOException {
        File filePath = new File("output//Result_"+this.fileName+"//"+"histogram//"+this.tableName+"//");
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        if (!this.matchMode){
//            BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.getCanonicalPath()+ "/match" +this.tableName+".png"));
            for (int i=0;i<this.groupedResultIndex.size();i++){
                String saveFileName = filePath.getCanonicalPath()+"\\"+this.groupedResultIndex.get(i).getPopulationName_1()+"_"+
                        this.groupedResultIndex.get(i).getPopulationName_2()+"_"+this.groupedResultIndex.get(i).getGroupName_1()+"_"+
                        this.groupedResultIndex.get(i).getGroupName_2()+".png";
                int startIndex = this.groupedResultIndex.get(i).getStartIndex();
                int endIndex = this.groupedResultIndex.get(i).getEndIndex();

                int[] dataList = new int[endIndex-startIndex];
                for (int j=startIndex;j<endIndex;j++){
                    dataList[j-startIndex]=(this.itemsList.get(j).getMisMatchNum());
                }
                plot(dataList,saveFileName,this.allaleNum);
            }
            Util.showDialog("INFORMATION","Folder "+ filePath+" has been plotted");
        }
    }

    private void plot(int[] dataList,String fileName,int binNum) throws IOException {
        Map<Integer, Integer> countMap = count(dataList);

        DefaultTableXYDataset dataset = new DefaultTableXYDataset();
        XYSeries serie = new XYSeries("", true, false);
        // Given range
        if(!countMap.keySet().contains(0)){serie.add(0,0);}
        if(!countMap.keySet().contains(binNum)){serie.add(binNum,0);}
        //add counting data
        for (Map.Entry<Integer,Integer> tempMap : countMap.entrySet()){
            serie.add(tempMap.getKey(),tempMap.getValue());
        }
        dataset.addSeries(serie);
        JFreeChart chart = ChartFactory.createHistogram( "Histogram", "misMatchNum", "pairsNum",
                dataset);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(false);

        //save
        try {
            ChartUtils.saveChartAsPNG(new File(fileName), chart, 500, 300);
        } catch (IOException e) {e.printStackTrace();}
    }

    public static Map<Integer, Integer> count(int[] dataList) {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < dataList.length; i++) {
            map.put(dataList[i], map.get(dataList[i]) == null ? 1 : map.get(dataList[i]) + 1);
        }
        return map;
    }
}
