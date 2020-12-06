package util;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
//import java.util.List;
public class Sample {
    @Getter private int index;
    @Getter @Setter private String group;
    @Getter @Setter private String population;
    @Getter @Setter private String name;
    @Getter @Setter private ArrayList<Integer> alleleData;
    @Getter private String alleleType;

    public Sample(int index,String name,String population, String group, ArrayList<Integer> allaleData) {
        super();
        this.index = index;
        this.group = group;
        this.population = population;
        this.name = name;
        this.alleleData = allaleData;
        this.alleleType=String.join("-",allaleData.toString());
    }
}
