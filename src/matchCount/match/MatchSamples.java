package matchCount.match;
import javafx.scene.control.Alert;
import lombok.Getter;
import util.*;

import java.util.*;

public class MatchSamples {
    private List<String> popuNames;
    private List<String> groupNames;
    private Map<String, Map<String, List<Integer>>> popuGroupMap;
    private List<Sample> sampleList;
    @Getter private List<Compare> compareResults = new ArrayList<>();
    @Getter private List<Integer> fourCompareNum = new ArrayList<>();
    @Getter
    private List<List<GroupedMatchResult>> groupedResultIndex = new ArrayList<>();
    @Getter
    private List<Boolean> showCompareTable = new ArrayList<>();
    private int compareIndex[][];//N*N, diagonal cells are 0
    private int compareCount = 0;

    public MatchSamples(Map<String, Map<String, List<Integer>>> popuGroupMap,List<String> popuNames, List<String> groupNames,List<Sample> sampleList){
        this.popuNames=popuNames;
        this.groupNames=groupNames;
        this.popuGroupMap=popuGroupMap;
        this.sampleList=sampleList;
        this.compareIndex = new int[this.sampleList.size()][this.sampleList.size()];

        matchSameSame();
        matchSameDiff();
        matchDiffSame();
        matchDiffDiff();
    }
    private void matchSameSame() {
        if (this.groupNames.size() < 1 || this.popuNames.size()<1) {
            Util.showDialog("WARNING", "please select populations and groups");
            this.showCompareTable.add(false);
            return;
        }
        List< GroupedMatchResult> curGroupedResultIndex = new ArrayList<>();
        int initCount;
        int tempCount=0;

        for (int i = 0; i < this.popuNames.size(); i++) {
            String tempPopu = this.popuNames.get(i);
            //tempMap: mapped by popu
            Map<String, List<Integer>> tempMap = this.popuGroupMap.get(tempPopu);
            if (tempMap != null) {
                for (Map.Entry<String, List<Integer>> tempMapMap : tempMap.entrySet()) {
                    initCount= tempCount;
                    //tempMapMap: mapped by group, judge if map is empty and whether in groupSelectedList and the size of elements is larger than 1
                    if (tempMapMap != null & this.groupNames.contains(tempMapMap.getKey()) & tempMapMap.getValue().size() >= 2) {
                        System.out.println(tempMapMap.getKey());
                        for (int j = 0; j < tempMapMap.getValue().size(); j++) {
                            for (int k = j + 1; k < tempMapMap.getValue().size(); k++) {
                                Sample sample_1 = this.sampleList.get(tempMapMap.getValue().get(j));
                                Sample sample_2 = this.sampleList.get(tempMapMap.getValue().get(k));
                                logCompareResult(sample_1,sample_2);
                                tempCount=1;                            }
                        }
                    curGroupedResultIndex.add(new GroupedMatchResult(tempPopu,tempPopu,tempMapMap.getKey(),tempMapMap.getKey(),initCount,tempCount));
                }
                }
            }
        }
        this.showCompareTable.add(true);
        this.fourCompareNum.add(this.compareCount);
        groupedResultIndex.add(curGroupedResultIndex);
    }

    private void matchSameDiff() {
        if (this.groupNames.size() < 2 || this.popuNames.size()<1) {
            Util.showDialog("WARNING", "please select more than 1 population");
            this.showCompareTable.add(false);
            return;
        }
        List< GroupedMatchResult> curGroupedResultIndex = new ArrayList<>();
        int initCount;
        int tempCount=0;

        //here is what I'm thinking. When the data format is Map, traversaling by index doesnot work. So I'll express the nested loop 'for i=0;i++; for j=i+1;j++;' in another way.
        // the iterator 'it_1' always starts at the first group of a certain one population. 'totalGroupNum' is total groups number in current population.
        //'loop' means current i and how many times we skip the 'it_1',  and then 'it_1_value' is current group, 'it_2_value' represents other groups 'j' to be compared.
        for (int pp = 0; pp < this.popuNames.size(); pp++) {
            // get the current population
            String tempPopu = this.popuNames.get(pp);
            Map<String, List<Integer>> tempMap = this.popuGroupMap.get(tempPopu);

            int loop = 0;
            int totalGroupNum = 0;
            while (tempMap != null & tempMap.size() >= 2 & loop <= totalGroupNum) {
                //starts at the first group of the ith loop
                Iterator<Map.Entry<String, List<Integer>>> it_1 = tempMap.entrySet().iterator();
                //skip previous usless groups
                int skip_times = 0;
                while (skip_times < loop) {
                    it_1.hasNext();
                    it_1.next();
                    skip_times += 1;
                }

                //compare
                if (it_1.hasNext()) {
                    Map.Entry<String, List<Integer>> it_1_value = it_1.next();
                    if (this.groupNames.contains(it_1_value.getKey())) {
                        while (it_1.hasNext()) {
                            Map.Entry<String, List<Integer>> it_2_value = it_1.next();
                            if (this.groupNames.contains(it_2_value.getKey())) {
//                                    System.out.println("it_1_value= " + it_1_value.getValue() + " and it_2_value= " + it_2_value.getValue());
                                initCount= tempCount;
                                for (int i_1 = 0; i_1 < it_1_value.getValue().size(); i_1++) {
                                    for (int i_2 = 0; i_2 < it_2_value.getValue().size(); i_2++) {
                                        Sample sample_1 = this.sampleList.get(it_1_value.getValue().get(i_1));
                                        Sample sample_2 = this.sampleList.get(it_2_value.getValue().get(i_2));
                                        logCompareResult(sample_1,sample_2);
                                        tempCount+=1;
                                    }
                                }
                                if (loop == 0) {
                                    totalGroupNum += 1;
                                }
                                curGroupedResultIndex.add(new GroupedMatchResult(tempPopu,tempPopu,it_1_value.getKey(),it_2_value.getKey(),initCount,tempCount));
                            }
                        }
                    }
                }
                loop += 1;
            }
        }
        this.showCompareTable.add(true);
        this.fourCompareNum.add(this.compareCount);
        groupedResultIndex.add(curGroupedResultIndex);
    }

    private void matchDiffSame() {
        if (this.popuNames.size() < 2 || this.groupNames.size()<1) {
            Util.showDialog("WARNING", "please select more than 1 group");
            this.showCompareTable.add(false);
            return;
        }

        List< GroupedMatchResult> curGroupedResultIndex = new ArrayList<>();
        int initCount;
        int tempCount = 0;

        for (int rr = 0; rr < this.groupNames.size(); rr++) {
            // traversal all slelected groups in all populations
            String tempGroup = this.groupNames.get(rr);
            List<List<Integer>> seletedGroup = new ArrayList<>();
            for (Map.Entry<String, Map<String, List<Integer>>> tempMap : this.popuGroupMap.entrySet()) {
                Map<String, List<Integer>> tempMapValue = tempMap.getValue();
                for (Map.Entry<String, List<Integer>> tempMapMap : tempMapValue.entrySet()) {
                    //pick the same group of different populations
                    if (tempGroup.equals(tempMapMap.getKey())) {
                        List<Integer> tempSeletedGroup = tempMapMap.getValue();
                        seletedGroup.add(tempSeletedGroup);
                    }
                }
            }
            //compare
            if (seletedGroup.size() >= 2) {
                for (int i = 0; i < seletedGroup.size(); i++) {
                    List<Integer> group_1 = seletedGroup.get(i);
                    for (int j = i + 1; j < seletedGroup.size(); j++) {
                        initCount = tempCount;
                        List<Integer> group_2 = seletedGroup.get(j);
                            for (int ii = 0; ii < group_1.size(); ii++) {
                                for (int jj = 0; jj < group_2.size(); jj++) {
                                    Sample sample_1 = this.sampleList.get(group_1.get(ii));
                                    Sample sample_2 = this.sampleList.get(group_2.get(jj));
                                    logCompareResult(sample_1, sample_2);
                                    tempCount += 1;
                                }
                            }
                            curGroupedResultIndex.add(new GroupedMatchResult(this.sampleList.get(group_1.get(0)).getPopulation(), this.sampleList.get(group_2.get(0)).getPopulation(), tempGroup, tempGroup, initCount, tempCount));
                    }
                }
            }
        }
        this.showCompareTable.add(true);
        this.fourCompareNum.add(this.compareCount);
        groupedResultIndex.add(curGroupedResultIndex);
    }

    private void matchDiffDiff() {
        //here we first compare all sample pairs, and then group results by popuPairName and groupPairName,
        // last traverse them and save to this.compareResults. save the curGroupedResultIndex at the same time.

        //compare all samples
        if (this.popuNames.size() < 2 | this.groupNames.size() < 2) {
            Util.showDialog("WARNING", "please select more than 1 group and population");
            this.showCompareTable.add(false);
            return;
        }
        List<Compare> curCompareResults = new ArrayList<>();
        for (int i = 0; i < this.sampleList.size(); i++) {
            Sample sample_1 = this.sampleList.get(i);
            if (this.popuNames.contains(sample_1.getPopulation()) & this.groupNames.contains(sample_1.getGroup())) {
                for (int j = i + 1; j < this.sampleList.size(); j++) {
                    Sample sample_2 = this.sampleList.get(j);
                    if (this.popuNames.contains(sample_2.getPopulation()) & this.groupNames.contains(sample_2.getGroup())
                            & sample_1.getPopulation() != sample_2.getPopulation() & sample_1.getGroup() != sample_2.getGroup()) {
//                        logCompareResult(sample_1,sample_2);
                        curCompareResults.add(new Compare(sample_1, sample_2));
                    }
                }
            }
        }

        if (curCompareResults!=null){
            //group
            Map<String, Map<String, List<Compare>>> map=nestedMapGrouping(curCompareResults);
            List< GroupedMatchResult> curGroupedResultIndex = new ArrayList<>();
            int initCount;
            int tempCount=0;

            for(Map.Entry<String,Map<String,List<Compare>>> pp : map.entrySet()){
                for(Map.Entry<String,List<Compare>> gg : pp.getValue().entrySet()){
                    List<Compare> tmpList = gg.getValue();
                    initCount = tempCount;
                    Sample sample_1 = null;
                    Sample sample_2= null;
                    for (int k = 0; k < tmpList.size(); k++) {
                        sample_1 = tmpList.get(k).getSample_1();
                        sample_2 = tmpList.get(k).getSample_2();
                        logCompareResult(sample_1,sample_2);
                        tempCount+=1;
                    }
                    curGroupedResultIndex.add(new GroupedMatchResult(sample_1.getPopulation(),sample_2.getPopulation(),sample_1.getGroup(),sample_2.getGroup(),initCount,tempCount));
                }
            }
            groupedResultIndex.add(curGroupedResultIndex);
        }
        this.showCompareTable.add(true);
        this.fourCompareNum.add(this.compareCount);
    }

    private void logCompareResult(Sample sample_1,Sample sample_2){
        Compare result = new Compare(sample_1, sample_2);
        this.compareResults.add(result);
        this.compareIndex[sample_1.getIndex()][sample_2.getIndex()] = this.compareCount;
        this.compareIndex[sample_2.getIndex()][sample_1.getIndex()] = this.compareCount;
        this.compareCount += 1;
    }

    private Map<String, Map<String, List<Compare>>> nestedMapGrouping(List<Compare> curCompareResults) {
        Map<String, Map<String, List<Compare>>> map = new HashMap<>();
        for (int i = 0; i < curCompareResults.size(); i++) {
            Compare compare = curCompareResults.get(i);
            // grouped by 'populationPair'
            Map<String, List<Compare>> tempMap = map.get(compare.getSamplePopuPairInOrder());
            //grouped by 'groupPair', then update Map
            if (tempMap == null) {
                tempMap = new HashMap<>();
                tempMap=groupByGroup(curCompareResults,tempMap,compare,i);
                map.put(compare.getSamplePopuPairInOrder(), tempMap);
            } else {
//                List<Compare> tempMapMap = tempMap.get(compare.getSampleGroupPairInOrder());
//                if (tempMapMap == null) {
//                    tempMapMap = new ArrayList<>();
//                    tempMapMap.add(curCompareResults.get(i));
//                    tempMap.put(compare.getSampleGroupPairInOrder(), tempMapMap);
//                    map.put(compare.getSamplePopuPairInOrder(), tempMap);
//                } else {
//                    tempMapMap.add(curCompareResults.get(i));
//                }
                tempMap=groupByGroup(curCompareResults,tempMap,compare,i);
                map.put(compare.getSamplePopuPairInOrder(), tempMap);
            }
        }
        return map;
    }

    private Map<String, List<Compare>> groupByGroup(List<Compare> curCompareResults,Map<String, List<Compare>> tempMap,Compare compare,int i){
        List<Compare> tempMapMap = tempMap.get(compare.getSampleGroupPairInOrder());
        if (tempMapMap == null) {
            tempMapMap = new ArrayList<>();
            tempMapMap.add(curCompareResults.get(i));
            tempMap.put(compare.getSampleGroupPairInOrder(), tempMapMap);

        } else {
            tempMapMap.add(curCompareResults.get(i));
        }
        return tempMap;
    }
}
