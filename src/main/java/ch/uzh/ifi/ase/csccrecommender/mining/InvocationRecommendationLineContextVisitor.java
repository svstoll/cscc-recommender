package ch.uzh.ifi.ase.csccrecommender.mining;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class InvocationRecommendationLineContextVisitor extends RecommendingLineContextVisitor {

  public static final Logger LOGGER = LoggerFactory.getLogger(InvocationRecommendationLineContextVisitor.class);

  private final String tempEvaluationResultsDirectoryPath;

  public InvocationRecommendationLineContextVisitor(MethodInvocationIndex methodInvocationIndex,
                                                    String tempEvaluationResultsDirectoryPath) {
    super(methodInvocationIndex);
    this.tempEvaluationResultsDirectoryPath = tempEvaluationResultsDirectoryPath;
  }

  @Override
  protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
    super.handleCompletionExpression(invocationType, csccContext);
    String isRecommendationCorrectFor1 = "0";
    String isRecommendationCorrectFor3 = "0";
    String isRecommendationCorrectFor10 = "0";

    List<String> recommendations = getRecommendationResults().get(0).getRecommendedMethods();
    double recallFor1 = recommendations.size() / 1.0;
    double recallFor3 = recommendations.size() / 3.0;
    double recallFor10 = recommendations.size() / 10.0;
    int i = 0;
    for (String recommendation : recommendations) {
      if (recommendation.equalsIgnoreCase(methodName)) {
        isRecommendationCorrectFor10 = "1";
        if (i < 1) {
          isRecommendationCorrectFor1 = "1";
          isRecommendationCorrectFor3 = "1";
        }
        else if (i < 3) {
          isRecommendationCorrectFor3 = "1";
        }
      }
      i++;
    }

    writeTempResult(1, isRecommendationCorrectFor1, recallFor1);
    writeTempResult(3, isRecommendationCorrectFor3, recallFor3);
    writeTempResult(10, isRecommendationCorrectFor10, recallFor10);
  }

  private void writeTempResult(int maxRecommendations, String isRecommendationCorrectForX, double recallForX) {
    String directoryPath = tempEvaluationResultsDirectoryPath + "/" + maxRecommendations + "/";
    String fileNamePath = directoryPath + "results.txt";
    File directory = new File(directoryPath);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    try (PrintWriter pw = new PrintWriter(new FileWriter(fileNamePath, true))){
      pw.println(isRecommendationCorrectForX + "," + recallForX);
    }
    catch (IOException e) {
      LOGGER.error("Error while writing temporary context evaluation result.", e);
    }
  }
}
