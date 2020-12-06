package matchCount.count;

import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import matchCount.match.MatchSamples;
import util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WriteExcel {
    Util dialogBox = new Util();
    public void writeStaticExcel(String fileName, Map<String, Map<String, Map<String, List<Integer>>>> groupTypeMap, List<Sample> sampleList, Set<String> groupList, String excelName) {
        File filePath = new File("output//Result_"+fileName);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
        //        Determine whether the file is occupied
        File file = new File(filePath+File.separator+excelName);
        if(file.exists() && !file.renameTo(file)){
            Util.showDialog("INFORMATION","please make sure "+ file.getName()+" is not opening.");
            return;
        }
        XSSFWorkbook Newworkbook = new XSSFWorkbook();
        XSSFSheet sheet1 = initSheet(Newworkbook);
        //init all rows
        List<XSSFRow> rows = new ArrayList<>();
        for (int i = 0; i < sampleList.size(); i++) {
            XSSFRow curRow = sheet1.createRow(i + 1);
            rows.add(curRow);
        }
        ////////////////////////////////////////////////////
        //groupTypeMap的分组标准是group、type、population
        //为了计数需要merge的表格cell的开始行数，以下分别为：sample开始行数，group开始行数，type开始行数，popu开始行数
        int rowCount = 1;
        int rowGroupSample = 1;
        int rowGroupTypeSample = 1;
        int rowGroupTypePopuSample = 1;
        for (String tempGroup : groupList) {
            //tempMap: mapped by group
            Map<String, Map<String, List<Integer>>> tempMap = groupTypeMap.get(tempGroup);
            int typeSize = tempMap.size();//type size in one group
            int tempGroupSampleCount = 0;//matchCount.count samples in one group
            if (tempMap != null) {
                for (Map.Entry<String, Map<String, List<Integer>>> tempMapMap : tempMap.entrySet()) {
                    if (tempMapMap != null) {
                        int tempTypeSampleCount = 0;//matchCount.count samples in one type
                        for (Map.Entry<String, List<Integer>> tempMapMapMap : tempMapMap.getValue().entrySet()) {
                            int tempPopuSampleCount = tempMapMapMap.getValue().size();//matchCount.count samples in one population
                            if (tempPopuSampleCount > 1) {
                                // merge cells of same population in one type of one group
                                sheet1.addMergedRegion(new CellRangeAddress(rowGroupTypePopuSample, rowGroupTypePopuSample + tempPopuSampleCount - 1, 5, 5));
                                sheet1.addMergedRegion(new CellRangeAddress(rowGroupTypePopuSample, rowGroupTypePopuSample + tempPopuSampleCount - 1, 6, 6));
                            }

                            // fill up 'sampleID'
                            for (int j = 0; j < tempPopuSampleCount; j++) {
                                Sample sample = sampleList.get(tempMapMapMap.getValue().get(j));
                                XSSFRow curRow = rows.get(rowCount - 1);
                                XSSFCell cell_0 = curRow.createCell(4);
                                cell_0.setCellValue(sample.getName());
                                rowCount += 1;
                            }
                            // fill up 'popu' and 'frequency'
                            XSSFRow curRow = rows.get(rowGroupTypePopuSample - 1);
                            XSSFCell cell_0 = curRow.createCell(5);
                            cell_0.setCellValue(tempMapMapMap.getKey());
                            XSSFCell cell_1 = curRow.createCell(6);
                            cell_1.setCellValue(tempPopuSampleCount);

                            rowGroupTypePopuSample += tempPopuSampleCount;
                            tempTypeSampleCount += tempPopuSampleCount;
                        }
                        //merge cells of same type in one group
                        if (tempTypeSampleCount > 1) {
                            sheet1.addMergedRegion(new CellRangeAddress(rowGroupTypeSample, rowGroupTypeSample + tempTypeSampleCount - 1, 2, 2));
                            sheet1.addMergedRegion(new CellRangeAddress(rowGroupTypeSample, rowGroupTypeSample + tempTypeSampleCount - 1, 3, 3));
                        }
                        //fill up 'type' and 'total frequency'
                        XSSFRow curRow = rows.get(rowGroupTypeSample - 1);
                        XSSFCell cell_0 = curRow.createCell(2);
                        cell_0.setCellValue(tempMapMap.getKey());
                        XSSFCell cell_1 = curRow.createCell(3);
                        cell_1.setCellValue(tempTypeSampleCount);

                        rowGroupTypeSample += tempTypeSampleCount;
                        tempGroupSampleCount += tempTypeSampleCount;
                    }
                }
                //merge cells of same group
                if (tempGroupSampleCount > 1) {
                    sheet1.addMergedRegion(new CellRangeAddress(rowGroupSample, rowGroupSample + tempGroupSampleCount - 1, 0, 0));
                    sheet1.addMergedRegion(new CellRangeAddress(rowGroupSample, rowGroupSample + tempGroupSampleCount - 1, 1, 1));
                }
                //fill up 'group' and 'number of haplotypes'
                XSSFRow curRow = rows.get(rowGroupSample - 1);
                XSSFCell cell_0 = curRow.createCell(0);
                cell_0.setCellValue(tempGroup);
                XSSFCell cell_1 = curRow.createCell(1);
                cell_1.setCellValue(typeSize);

                rowGroupSample += tempGroupSampleCount;
            }
        }
        ////////////////////////////////////////////////////
        //write excel file
        try {
            FileOutputStream fout = new FileOutputStream(file);
            Newworkbook.write(fout);
            fout.close();
            Util.showDialog("INFORMATION",file+" has been written.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private XSSFSheet initSheet(XSSFWorkbook Newworkbook) {
        // create a new sheet
        XSSFSheet sheet1 = Newworkbook.createSheet("sheet1");
        //create rowMenu
        XSSFRow row = sheet1.createRow(0);
        XSSFCell cell_00 = row.createCell(0);
        cell_00.setCellValue("haplogroup");
        XSSFCell cell_01 = row.createCell(1);
        cell_01.setCellValue("number of haplotypes");
        XSSFCell cell_02 = row.createCell(2);
        cell_02.setCellValue("haplotype");
        XSSFCell cell_03 = row.createCell(3);
        cell_03.setCellValue("total frequency");
        XSSFCell cell_04 = row.createCell(4);
        cell_04.setCellValue("sampleID");
        XSSFCell cell_05 = row.createCell(5);
        cell_05.setCellValue("population");
        XSSFCell cell_06 = row.createCell(6);
        cell_06.setCellValue("frequency");
        return sheet1;
    }

}
