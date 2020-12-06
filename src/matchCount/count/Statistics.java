package matchCount.count;
import lombok.Getter;
import util.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Statistics {
    private List<Sample> sampleList;
    @Getter private Map<String, Map<String, Map<String, List<Integer>>>> groupTypeMap = new HashMap<>();

    public Statistics(List<Sample> sampleList) {
        this.sampleList = sampleList;
        statistics();
    }

    public void statistics() {
        //这里之后改成嵌套的比较好
        //grouped by group and then haplotype and then population
        for (int i = 0; i < sampleList.size(); i++) {
            Sample sample = sampleList.get(i);
            Map<String, Map<String, List<Integer>>> tempMap = this.groupTypeMap.get(sample.getGroup());
            if (tempMap == null) {
                tempMap = new HashMap<>();
                groupByType(tempMap,sample,i);
            } else {
                groupByType(tempMap,sample,i);
            }
        }
    }

    private void groupByType(Map<String, Map<String, List<Integer>>> tempMap,Sample sample,int i) {
        Map<String, List<Integer>> tempMapMap = tempMap.get(sample.getAlleleType());
        if (tempMapMap == null) {
            tempMapMap = new HashMap<>();
            groupByPopu(tempMap,tempMapMap,sample,i);
        } else {
            groupByPopu(tempMap,tempMapMap,sample,i);
        }
    }

    private void groupByPopu(Map<String, Map<String, List<Integer>>> tempMap,Map<String, List<Integer>> tempMapMap,Sample sample,int i){
        List<Integer> tempMapMapMap = tempMapMap.get(sample.getPopulation());
        if (tempMapMapMap == null) {
            tempMapMapMap = new ArrayList<>();
            tempMapMapMap.add(i);
            tempMapMap.put(sample.getPopulation(), tempMapMapMap);
            tempMap.put(sample.getAlleleType(), tempMapMap);
            this.groupTypeMap.put(sample.getGroup(), tempMap);
        } else {
            tempMapMapMap.add(i);
        }
    }
}
