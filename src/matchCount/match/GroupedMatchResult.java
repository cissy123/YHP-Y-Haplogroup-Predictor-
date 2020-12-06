package matchCount.match;

import lombok.Getter;

public class GroupedMatchResult {
    @Getter private String populationName_1;
    @Getter private String populationName_2;
    @Getter private String groupName_1;
    @Getter private String groupName_2;
    @Getter private int startIndex;
    @Getter private int endIndex;

    public GroupedMatchResult(String populationName_1,String populationName_2,String groupName_1,String groupName_2,int startIndex,int endIndex){
        this.populationName_1=populationName_1;
        this.populationName_2=populationName_2;
        this.groupName_1=groupName_1;
        this.groupName_2=groupName_2;
        this.startIndex=startIndex;
        this.endIndex=endIndex;
    }
}
