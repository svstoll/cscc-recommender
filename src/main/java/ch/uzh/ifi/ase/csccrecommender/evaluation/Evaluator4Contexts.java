package ch.uzh.ifi.ase.csccrecommender.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

import ch.uzh.ifi.ase.csccrecommender.ProductionModule;
import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndexer;
import ch.uzh.ifi.ase.csccrecommender.properties.ConfigProperties;
import ch.uzh.ifi.ase.csccrecommender.utility.StringUtility;

public class Evaluator4Contexts {
	
	  private static final Logger LOGGER = LoggerFactory.getLogger(Evaluator4Contexts.class);
	
	  private String contextsDirectoryPath;
	
	@Inject
	public Evaluator4Contexts(@Named(ConfigProperties.CONTEXTS_DIRECTORY_PROPERTY) String contextsDirectoryPath) {
	    this.contextsDirectoryPath = contextsDirectoryPath;
	  }
	
	public List<String[]> loadExistedSplittingFrom(String path) {
		ArrayList<String[]> groups = new ArrayList<String[]>();
		BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(path));
			String line;
			while((line = in.readLine()) != null)
			{
				String[] group = line.split(",");
				groups.add(group);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return groups;
	}
	
	public void trainAndEvaluateOnContextDataset(int crossValidationNum) {
		List<String[]> ds = splitContextDataset(crossValidationNum);
		//List<String[]> ds = loadExistedSplittingFrom("./data/groups.txt");
		int currectItr = 0;
		// train the model
			// mask the data to be evaluated on
		List<String> maskedFiles = prepareContextDataset(ds.get(currectItr));
	    Injector injector = Guice.createInjector(new ProductionModule());
	    MethodInvocationIndexer methodInvocationIndexer = injector.getInstance(MethodInvocationIndexer.class);
	    methodInvocationIndexer.indexAllAvailableContexts(true);
	    	// change back
    	clearup(maskedFiles);
    	maskedFiles.clear();
	    // evaluate on the remaining one	
    		// flip the mask
	    for(int j = 0; j < crossValidationNum; j++) {
	    	if(j == currectItr)
	    		continue;
	    	maskedFiles.addAll(prepareContextDataset(ds.get(j)));
	    }
    		// evaluate
    	MethodInvocationRecommender methodInvocationRecommender = injector.getInstance(MethodInvocationRecommender.class);
    	methodInvocationRecommender.recommend4AllInvocationsInAvailableContexts();
    		// change back
    	clearup(maskedFiles);
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
			statistics.add(statistic);
		}
		
		return statistics;
	}
	
	private List<String[]> splitContextDataset(int num){
		File file = new File(contextsDirectoryPath);
		String[] directories = file.list(new FilenameFilter() {
		  @Override
		  public boolean accept(File current, String name) {
		    return new File(current, name).isDirectory();
		  }
		});
	    LOGGER.info("Split Context Dataset into:");
		Collections.shuffle(Arrays.asList(directories));
		int groupSize = directories.length/num;
		ArrayList<String[]> list = new ArrayList<String[]>();
		for(int i = 0; i < num; i++) {
			String[] groupNames = new String[groupSize];
			for(int j = 0; j < groupSize; j++) {
				groupNames[j] = directories[i*groupSize+j];
			}
		    LOGGER.info(i + " " + Arrays.toString(groupNames));
			list.add(groupNames);
		}
		return list;
	}
	
	private List<String> prepareContextDataset(String[] toBeMasked) {
		ArrayList<String> maskedZips = new ArrayList<String>();
		for(int i = 0; i < toBeMasked.length; i++) {
			List<String> zips = findAllZipFilePaths(contextsDirectoryPath + "/" + toBeMasked[i]);
			for(int j = 0; j < zips.size(); j++) {
				File file = new File(zips.get(j));
				file.renameTo(new File(file+"mask"));
				maskedZips.add(file+"mask");
			}
		}
		return maskedZips;
	}
	
	private void clearup(List<String> maskedZips) {
		for(int i = 0; i < maskedZips.size(); i++) {
			File file = new File(maskedZips.get(i));
			String fileName = file.getAbsolutePath();
			file.renameTo(new File(fileName.substring(0, fileName.length()-4)));
		}
	}
	
	private static List<String> findAllZipFilePaths(String directoryPath) {
	    if (StringUtility.isNullOrEmpty(directoryPath)) {
	      return Collections.emptyList();
	    }

	    return FileUtils.listFiles(new File(directoryPath), new String[]{"zip"}, true)
	        .stream()
	        .map(File::getAbsolutePath)
	        .collect(Collectors.toList());
	  }
	public static void main(String[] args) {
		int crossValidationNum = 2;
		Injector injector = Guice.createInjector(new ProductionModule());
		Evaluator4Contexts evaluator = injector.getInstance(Evaluator4Contexts.class);
		evaluator.trainAndEvaluateOnContextDataset(crossValidationNum);
		
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
				System.out.println(statistic.getType()+","+statistic.getPrecision()+","+statistic.getRecall()+","+statistic.getF_Measure());
			}
		}
		

		
	}

}
