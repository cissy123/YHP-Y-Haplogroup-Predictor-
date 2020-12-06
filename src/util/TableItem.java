package util;

public class TableItem {
    private boolean matchMode;
    private int idx;
    private String name;
    private String populationName;
    private String groupName;
    private String examplePair;

    TableItem(boolean matchMode,int idx,String name,String populationName,String groupName,String examplePair) {
        this.matchMode = matchMode;
        this.idx = idx;
        this.name = name;
        this.populationName = populationName;
        this.groupName=groupName;
        this.examplePair = examplePair;
    }

}
