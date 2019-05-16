package ch.uzh.ifi.ase.csccrecommender.evaluation;

public class Statistics {
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	String type;
	public int getTotalCases() {
		return totalCases;
	}
	public void setTotalCases(int totalCases) {
		this.totalCases = totalCases;
	}
	int totalCases;
	double Precision;
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
		return F_Measure;
	}
	public void setF_Measure() {
		F_Measure = 2*Precision*Recall/(Precision+Recall);
	}
	double Recall;
	double F_Measure;


	
}
