package ch.uzh.ifi.ase.csccrecommender.evaluation;

public class Statistics {


    private int totalCases;
    private double Precision;
    private double Recall;
    private double F_Measure;

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    public double getPrecision() {
        return Precision;
    }

    public void setPrecision(double precision) {
        Precision = precision;
    }

    public double getRecall() {
        return Recall;
    }

    public void setRecall(double recall) {
        Recall = recall;
    }

    public double getF_Measure() {
        F_Measure = 2 * Precision * Recall / (Precision + Recall);
        return F_Measure;
    }


}
