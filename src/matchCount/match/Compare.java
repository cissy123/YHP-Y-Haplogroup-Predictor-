package matchCount.match;

import lombok.Getter;
import util.Sample;

public class Compare {
    @Getter private Sample sample_1;
    @Getter private Sample sample_2;
    @Getter private int misMatchNum = 0;
    @Getter private int misMatchSteps = 0;
    @Getter private double misMatchRatio = 0;

    //to show in table
    @Getter private String sampleNamePair="";
    @Getter private String samplePopuPair="";
    @Getter private String sampleGroupPair="";
    @Getter private String samplePopuPairInOrder="";
    @Getter private String sampleGroupPairInOrder="";
    @Getter private String misMatchDetail ="";

    public Compare(Sample sample_1,Sample sample_2){
        super();
        this.sample_1 = sample_1;
        this.sample_2 = sample_2;
        this.sampleNamePair = sample_1.getName()+","+sample_2.getName();
        this.samplePopuPair = sample_1.getPopulation()+","+sample_2.getPopulation();
        this.sampleGroupPair = sample_1.getGroup()+","+sample_2.getGroup();
        this.samplePopuPairInOrder = sample_1.getPopulation().compareTo(sample_2.getPopulation())<0 ? this.samplePopuPair: sample_2.getPopulation()+","+sample_1.getPopulation();
        this.sampleGroupPairInOrder=sample_1.getGroup().compareTo(sample_2.getGroup())<0 ? this.sampleGroupPair: sample_2.getGroup()+","+sample_1.getGroup();
        computeResult();
    }
    public void computeResult() {
        //misMatch mode
        for (int i=0;i<this.sample_1.getAlleleData().size();i++){
            int allele_1= this.sample_1.getAlleleData().get(i);
            int allele_2= this.sample_2.getAlleleData().get(i);
            int diff = 0;
            if (allele_1!=allele_2){
                this.misMatchNum+=1;
                diff=allele_1-allele_2;
                diff = ((diff<0)?-diff:diff);
                this.misMatchSteps+=diff;
            }else {
                this.misMatchSteps+=diff;
            }
            this.misMatchDetail+=(","+String.format("%d", diff)+"("+String.format("%d", allele_1)+ "," +String.format("%d", allele_2)+ ")");
        }
        this.misMatchDetail = this.misMatchDetail.substring(1);
        this.misMatchRatio = (double)this.misMatchSteps/this.misMatchNum;
    }

    public Integer getMatchNum(){return this.sample_1.getAlleleData().size()-misMatchNum;}
    public String getMisMatchRatio(){return String.format("%.2f", misMatchRatio);}
}
