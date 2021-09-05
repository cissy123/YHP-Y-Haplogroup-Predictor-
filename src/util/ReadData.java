package util;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import lombok.Getter;

public class ReadData {
    @Getter private Set<String> groupList = new HashSet<>();
    @Getter private Set<String> populationList = new HashSet<>();
//    @Getter private Set<String> regionList = new HashSet<>();
    @Getter private List<String> alleleList = new ArrayList<>();
    @Getter private List<Sample> sampleList = new ArrayList<>();
    @Getter private Map<String, Map<String, List<Integer>>> popuGroupMap = new HashMap<>();
    private File file;

    public ReadData(File file){
        this.file = file;
        readData();
    }

    public void readData() {
        //not support .txt anymore
//        if (this.fileName.endsWith(".txt")) {
//            try {
//                InputStream is_file = new FileInputStream(file);
//            } catch (IOException e) {
//                System.out.print(" TXT Exception");
//            }
//        } else {
            try {
                Workbook wb = null;
                InputStream excel_file = new FileInputStream(this.file);
                wb = Workbook.getWorkbook(excel_file);

//                int sheetSize = wb.getNumberOfSheets();
//                only the first sheet
                Sheet sheet = wb.getSheet(0);
                int row_total = sheet.getRows();
                int col_total = sheet.getColumns();
//                  read row 0
                Cell[] cells_0 = sheet.getRow(0);
                for (int j = 3; j < col_total; j++) {
                    this.alleleList.add(cells_0[j].getContents().trim());
                }

//                read each row
                for (int i = 1; i < row_total; i++) {
                    Cell[] cells = sheet.getRow(i);
                    ArrayList<Float> tmp_allale = new ArrayList<Float>();
//                    allale read
                    for (int j = 3; j < col_total; j++) {
                        String cell = cells[j].getContents();
                        if(cell==null||cell.equals("")){
                            Util.showDialog("WARNING","ERROR! Please check "+file.getName()+" Row "+(i+1)+" Column"+(j)+". If you don't have data in this row, we suggest you to delete this row and try again! ");
                            return;
                        }
                        tmp_allale.add(Float.parseFloat(cells[j].getContents().trim()));
                    }
//                    create a new Sample
                    Sample new_ = new Sample(i - 1, cells[0].getContents().trim(), cells[1].getContents().trim(), cells[2].getContents().trim(), tmp_allale);
                    this.sampleList.add(new_);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BiffException e) {
                e.printStackTrace();
            }
//            return this;
            groupData();
        }

    private void groupData() {
        // group by popu and then group, it is a nest Map
//        Map<String, Map<String,List<Sample>>> popuGroupMap = new HashMap<>();
        if (this.sampleList != null) {
            this.populationList = new HashSet<>();
            this.groupList = new HashSet<>();
            this.popuGroupMap = new HashMap<>();
            for (int i = 0; i < this.sampleList.size(); i++) {
                Sample sample = this.sampleList.get(i);
                // grouped by 'population'
                Map<String, List<Integer>> tempMap = this.popuGroupMap.get(sample.getPopulation());
                //grouped by 'group', then update Map
                if (tempMap == null) {
                    tempMap = new HashMap<>();
                    groupByGroup(tempMap,sample,i);
                } else {
                    groupByGroup(tempMap,sample,i);
                }
            }
            updateList();

//        check popuGroupMap results, 嵌套遍历
//        for(Map.Entry<String,Map<String,List<Sample>>> pp : popuGroupMap.entrySet()){
//            for(Map.Entry<String,List<Sample>> gg : pp.getValue().entrySet()){
//                System.out.println(gg.getKey());
//                List<Sample> tmpList = gg.getValue();
//                for (int k = 0; k < tmpList.size(); k++) {
//                    System.out.println(tmpList.get(k).getAlleleData());
//                }
//            }
//
//        }
        }
    }

    private void groupByGroup(Map<String, List<Integer>> tempMap,Sample sample,int i){
        List<Integer> tempMapMap = tempMap.get(sample.getGroup());
        if (tempMapMap == null) {
            tempMapMap = new ArrayList<>();
            tempMapMap.add(i);
            tempMap.put(sample.getGroup(), tempMapMap);
            this.popuGroupMap.put(sample.getPopulation(), tempMap);
        } else {
            tempMapMap.add(i);
        }
    }

    private void updateList(){
        //get popu list
        this.populationList = this.popuGroupMap.keySet();
        // get group list
        for (Map.Entry<String, Map<String, List<Integer>>> pp : this.popuGroupMap.entrySet()) {
            for (Map.Entry<String, List<Integer>> gg : pp.getValue().entrySet()) {
                this.groupList.add(gg.getKey());
            }
        }
    }
}
