package main;

import matchCount.match.*;
import matchCount.count.*;
import util.*;
import predict.Predict;
import similarity.Similarity;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.*;

import javafx.stage.Stage;

import javax.xml.soap.Text;

public class Controller {
    //  UI
    @FXML
    private TextField te_specialallele;
    @FXML
    private TextField te_data;
    @FXML
    private TableView tv_dataTable;

    @FXML
    private Label ll_not_ava;

    @FXML
    private TableColumn tb_idx;
    //  varaible
    File file;
    private String specialallele;
    private Set<String> groupList = new HashSet<>();
    private Set<String> populationList = new HashSet<>();
    //    private Set<String> regionList = new HashSet<>();
    private List<String> alleleList = new ArrayList<>();
    private List<Sample> sampleList = new ArrayList<>();
    // the grouped popu_group data, saved as Map format
    private Map<String, Map<String, List<Integer>>> popuGroupMap = new HashMap<>();
    //compare results
    private List<Compare> compareResults = new ArrayList<>();
    private List<Integer> fourCompareNum = new ArrayList<>();
    private List<List<GroupedMatchResult>> groupedResultIndex = new ArrayList<>();
    private List<Boolean> showCompareTable = new ArrayList<>();

    // onAction implement
    public void sl_getDataFile() throws IOException {
        this.file = Util.openFileWindow();
        if (this.file != null) {
            this.te_data.setText(this.file.getCanonicalFile().getName());
//            Util.showDialog("INFORMATION",this.file.getAbsoluteFile().toString());
            readData(this.file);
            typeStatistics();
        }
    }

//    public String sl_getSpecialAllele(ActionEvent event) {
//        return this.te_specialallele.getText().trim();
//    }

    public void sl_populationSelect(ActionEvent event) {
        GroupWidget openGroupWidget = new GroupWidget(setItems(this.populationList), "popu");
        try {
            openGroupWidget.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sl_groupSelect(ActionEvent event) {
        GroupWidget openGroupWidget = new GroupWidget(setItems(this.groupList), "group");
        try {
            openGroupWidget.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sl_match() throws IOException {
        match(true);
    }

    public void sl_mismatch() throws IOException {
        match(false);
    }

    public void typeStatistics() {
        Statistics statis = new Statistics(this.sampleList);
        try {
            writeStatisticsExcel(statis.getGroupTypeMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // other functions
    private void readData(File file) {
        //for test
        ReadData read = new ReadData(file);
        this.populationList = read.getPopulationList();
        this.groupList = read.getGroupList();
        this.popuGroupMap = read.getPopuGroupMap();
        this.sampleList = read.getSampleList();
        this.alleleList = read.getAlleleList();
        if (this.sampleList.size() > 0) {
            //show in table
            showDataTable(this.sampleList);
            this.ll_not_ava.setVisible(false);
        }
    }

    private void match(boolean matchMode) throws IOException {
        //这里我把same和diff不加区分，因为感觉这个功能略多余
        //for test
        List<String> groupNames;
        List<String> popuNames;
        if (Middle.SelectedGroupIndexs.size() == 0) {
            groupNames = new ArrayList<>(this.groupList);
            popuNames = new ArrayList<>(this.populationList);
        } else {
            groupNames = idx2items(Middle.SelectedGroupIndexs, this.groupList);
            popuNames = idx2items(Middle.SelectedPopuIndexs, this.populationList);
        }
        MatchSamples match = new MatchSamples(this.popuGroupMap, popuNames, groupNames, this.sampleList);

        this.compareResults = match.getCompareResults();
        this.fourCompareNum = match.getFourCompareNum();
        this.groupedResultIndex = match.getGroupedResultIndex();
        this.showCompareTable = match.getShowCompareTable();
        showCompareTable(matchMode);
    }

    private void writeStatisticsExcel(Map<String, Map<String, Map<String, List<Integer>>>> groupTypeMap) throws IOException {
        WriteExcel write = new WriteExcel();
        String fileName = this.file.getCanonicalFile().getName().split("\\.")[0];
        write.writeStaticExcel(fileName, groupTypeMap, this.sampleList, this.groupList, "haploTypeStatistics.xls");
    }

    private List<Item> setItems(Set<String> name) {
        List<Item> tmpList = new ArrayList<>();
        for (String s : name) {
            tmpList.add(new Item(s));
        }
        return tmpList;
    }

    private List<String> idx2items(List<Integer> indexs, Set<String> list) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < indexs.size(); i++) {
            items.add(new ArrayList<>(list).get(indexs.get(i)));
        }
        return items;
    }

    private void showDataTable(List<Sample> sampleList) {
        ObservableList<Sample> items = FXCollections.observableArrayList();

        TableColumn<Sample, String> column_sampleID = new TableColumn<>("SampleID");
        TableColumn<Sample, String> column_population = new TableColumn<>("Population");
//        TableColumn<Sample, String> column_region = new TableColumn<>("region");
        TableColumn<Sample, String> column_haplogroup = new TableColumn<>("Haplogroup");
        TableColumn<Sample, ArrayList<Integer>> column_alleleData = new TableColumn<>("AlleleData");
        items.addAll(this.sampleList);

        this.tb_idx.setCellValueFactory(new PropertyValueFactory<>("index"));
        column_sampleID.setCellValueFactory(new PropertyValueFactory<>("name"));
        column_population.setCellValueFactory(new PropertyValueFactory<>("population"));
//        column_region.setCellValueFactory(new PropertyValueFactory<>("region"));
        column_haplogroup.setCellValueFactory(new PropertyValueFactory<>("group"));
        column_alleleData.setCellValueFactory(new PropertyValueFactory<>("alleleData"));

        this.tv_dataTable.setItems(items);
        this.tv_dataTable.getColumns().addAll(column_sampleID, column_population, column_haplogroup, column_alleleData);
    }

    private void showCompareTable(boolean matchMode) throws IOException {
        String fileName = this.file.getCanonicalFile().getName().split("\\.")[0];
        try {
            if (this.showCompareTable.get(0)) {
                TableWidget openTableSameSame = new TableWidget(fileName, "TableSameSame", this.compareResults.subList(0, this.fourCompareNum.get(0)), this.groupedResultIndex.get(0), this.alleleList.size(), matchMode);
                openTableSameSame.start(new Stage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (this.showCompareTable.get(1)) {
                TableWidget openTableSameDiff = new TableWidget(fileName, "TableSameDiff", this.compareResults.subList(this.fourCompareNum.get(0), this.fourCompareNum.get(1)), this.groupedResultIndex.get(1), this.alleleList.size(), matchMode);
                openTableSameDiff.start(new Stage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (this.showCompareTable.get(2)) {
                TableWidget openTableDiffSame = new TableWidget(fileName, "TableDiffSame", this.compareResults.subList(this.fourCompareNum.get(1), this.fourCompareNum.get(2)), this.groupedResultIndex.get(2), this.alleleList.size(), matchMode);
                openTableDiffSame.start(new Stage());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        try {
            if (this.showCompareTable.get(3)) {
                TableWidget openTableDiffDiff = new TableWidget(fileName, "TableDiffDiff", this.compareResults.subList(this.fourCompareNum.get(2), this.fourCompareNum.get(3)), this.groupedResultIndex.get(3), this.alleleList.size(), matchMode);
                openTableDiffDiff.start(new Stage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ////////////////above all about match&count function/////////////////
    public void sl_openPredictWindow() throws Exception {
        try {
            Predict predictWindow = new Predict();
            predictWindow.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sl_computeWithDatabase() throws Exception {
        try {
            Similarity similarityWindow = new Similarity();
            similarityWindow.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sl_computeWithinSamples() {
        try {
            Similarity similarityWindow = new Similarity();
            similarityWindow.start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}