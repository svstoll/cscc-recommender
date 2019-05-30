package ch.uzh.ifi.ase.csccrecommender.evaluation;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
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

    public void trainAndEvaluateOnContextDataset(int crossValidationNum) {
        List<String[]> ds = splitContextDataset(crossValidationNum);
        int currectItr = 0;
        // 1. train the model
        // mask the data to be evaluated on
        List<String> maskedFiles = prepareContextDataset(ds.get(currectItr));
        Injector injector = Guice.createInjector(new ProductionModule());
        MethodInvocationIndexer methodInvocationIndexer = injector.getInstance(MethodInvocationIndexer.class);
        //methodInvocationIndexer.indexAllAvailableContexts(true);
        // change back
        clearup(maskedFiles);
        maskedFiles.clear();
        // 2. evaluate on the remaining one
        // flip the mask
        for (int j = 0; j < crossValidationNum; j++) {
            if (j == currectItr)
                continue;
            maskedFiles.addAll(prepareContextDataset(ds.get(j)));
        }
        // evaluate
        MethodInvocationRecommender methodInvocationRecommender = injector.getInstance(MethodInvocationRecommender.class);
        methodInvocationRecommender.recommend4AllInvocationsInAvailableContexts();
        // change back
        clearup(maskedFiles);
    }

    public Statistics calculateStatistics(int recommendations){
        Statistics statistic = new Statistics();
        // get precision and recall
        try(BufferedReader in = new BufferedReader(new FileReader("./tmp/" + recommendations + "/out.txt"))){
            String line;
            double madeAndRelevant = 0;
            double made = 0;
            int requested = 0;
            while ((line = in.readLine()) != null) {
                String[] result = line.split(",");
                requested += 1;
                if (Double.parseDouble(result[0]) > 0)
                    madeAndRelevant += 1.0;
                if (Double.parseDouble(result[1]) > 0)
                    made += 1;
            }
            in.close();
            double precision = 0;
            if(made != 0)
                precision = madeAndRelevant / made;
            double recall = 0;
            if(requested != 0)
                recall = made / requested;
            statistic.setPrecision(precision);
            statistic.setRecall(recall);
            statistic.setTotalCases(requested);
        } catch (IOException e) {
            LOGGER.info("errors in calculateStatistics");
        }
        return statistic;
    }

    private List<String[]> splitContextDataset(int num) {
        File file = new File(contextsDirectoryPath);
        String[] directories = file.list((current, name) -> new File(current, name).isDirectory());

        LOGGER.info("Split Context Dataset into:");
        Collections.shuffle(Arrays.asList(directories));
        int groupSize = directories.length / num;
        ArrayList<String[]> list = new ArrayList<String[]>();
        for (int i = 0; i < num; i++) {
            String[] groupNames = new String[groupSize];
            for (int j = 0; j < groupSize; j++) {
                groupNames[j] = directories[i * groupSize + j];
            }
            LOGGER.info(i + " " + Arrays.toString(groupNames));
            list.add(groupNames);
        }
        return list;
    }

    private List<String> prepareContextDataset(String[] toBeMasked) {
        ArrayList<String> maskedZips = new ArrayList<String>();
        for (int i = 0; i < toBeMasked.length; i++) {
            List<String> zips = findAllZipFilePaths(contextsDirectoryPath + "/" + toBeMasked[i]);
            for (int j = 0; j < zips.size(); j++) {
                File file = new File(zips.get(j));
                file.renameTo(new File(file + "mask"));
                maskedZips.add(file + "mask");
            }
        }
        return maskedZips;
    }

    private void clearup(List<String> maskedZips) {
        for (int i = 0; i < maskedZips.size(); i++) {
            File file = new File(maskedZips.get(i));
            String fileName = file.getAbsolutePath();
            file.renameTo(new File(fileName.substring(0, fileName.length() - 4)));
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

        int[] recommendationNum = {1, 3, 10};
        String resultFileName = "result" + new SimpleDateFormat("yyyyMMddHHmm'.csv'").format(new Date());
        try(PrintWriter pw = new PrintWriter(new FileWriter(resultFileName)))
        {
            for (int rNum : recommendationNum) {
                pw.println("MAX_RECOMMENDATIONS = " + rNum);
                Statistics statistic = evaluator.calculateStatistics(rNum);
                pw.println(statistic.getPrecision() + "," + statistic.getRecall() + "," + statistic.getF_Measure() + "," + statistic.getTotalCases());
            }
        } catch (IOException e) {
            LOGGER.info("errors occured in writing result");
        }


    }

}
