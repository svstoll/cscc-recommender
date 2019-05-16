package ch.uzh.ifi.ase.csccrecommender.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import ch.uzh.ifi.ase.csccrecommender.ProductionModule;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndexer;
import ch.uzh.ifi.ase.csccrecommender.mining.CompletionEventExtractor;

public class Evaluator4CompletionEvents {
	
	public void trainAndEvaluateOnEventsDataset() {
		Injector injector = Guice.createInjector(new ProductionModule());
	    MethodInvocationIndexer methodInvocationIndexer = injector.getInstance(MethodInvocationIndexer.class);
	    methodInvocationIndexer.indexAllAvailableContexts(true);
	    CompletionEventRecommender completionEventRecommender = injector.getInstance(CompletionEventRecommender.class);
	    
	    CompletionEventExtractor completionEventExtractor = injector.getInstance(CompletionEventExtractor.class);
	    completionEventExtractor.processAllCompletionEvents(completionEvents -> {
	        for (CompletionEvent completionEvent : completionEvents) {
	        	completionEventRecommender.recommendMethods(completionEvent);
	        }
	      });
	}
	
	public List<Statistics> calculateStatistics(int recommendations) throws IOException {
		File file = new File("./tmp/" + recommendations + "/");
		String[] types = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
		ArrayList<Statistics> statistics = new ArrayList<Statistics>();
		for(String type: types) {
			Statistics statistic = new Statistics();
			statistic.setType(type);
			// get precision and recall
			BufferedReader in = new BufferedReader(new FileReader("./tmp/" + recommendations + "/" + type + "/out.txt"));
			String line;
			double precision = 0;
			double recall = 0;
			int lines = 0;
			while((line = in.readLine()) != null)
			{
				String[] result = line.split(",");
				lines+=1;
				precision+=Double.parseDouble(result[0]);
				recall+=Double.parseDouble(result[1]);
			}
			in.close();
			statistic.setPrecision(precision/lines);
			statistic.setRecall(recall/lines);
			statistic.setF_Measure();
			statistic.setTotalCases(lines);
			statistics.add(statistic);
		}
		
		return statistics;
	}

	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new ProductionModule());
		Evaluator4CompletionEvents evaluator = injector.getInstance(Evaluator4CompletionEvents.class);
		evaluator.trainAndEvaluateOnEventsDataset();
		
		int[] recommendationNum = {1,3,10};
		for(int rNum: recommendationNum) {
			System.out.println("MAX_RECOMMENDATIONS = " + rNum);
			List<Statistics> statistics = new ArrayList<Statistics>();
			try {
				statistics = evaluator.calculateStatistics(rNum);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(Statistics statistic: statistics) {
				System.out.println(statistic.getType()+","+statistic.getPrecision()+","+statistic.getRecall()+","+statistic.getF_Measure()+","+statistic.getTotalCases());
			}
		}
		
	}

}
