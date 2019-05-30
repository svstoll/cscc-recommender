package ch.uzh.ifi.ase.csccrecommender.evaluation;

class Statistics {


    private int totalCases;
    private double Precision;
    private double Recall;

    int getTotalCases() {
        return totalCases;
    }

    void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    double getPrecision() {
        return Precision;
    }

    void setPrecision(double precision) {
        Precision = precision;
    }

    double getRecall() {
        return Recall;
    }

    void setRecall(double recall) {
        Recall = recall;
    }

    double getF_Measure() {
        return 2 * Precision * Recall / (Precision + Recall);
    }


}
