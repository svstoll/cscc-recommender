package ch.uzh.ifi.ase.csccrecommender.evaluation;

import ch.uzh.ifi.ase.csccrecommender.index.MethodInvocationIndex;
import ch.uzh.ifi.ase.csccrecommender.mining.CsccContext;
import ch.uzh.ifi.ase.csccrecommender.mining.RecommendingLineContextVisitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class R4InvocationLineContextVisitor extends RecommendingLineContextVisitor {

    public R4InvocationLineContextVisitor(MethodInvocationIndex methodInvocationIndex) {
        super(methodInvocationIndex);
    }

    @Override
    protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
        super.handleCompletionExpression(invocationType, csccContext);
        // record in txt
        String correctRecommendation = methodName;
        String isRecommendationCorrectFor1 = "0";
        String isRecommendationCorrectFor3 = "0";
        String isRecommendationCorrectFor10 = "0";

        List<String> recommendations = super.getRecommendationResults().get(0).getRecommendedMethods();
        Double recallFor1 = recommendations.size() / 1.0;
        Double recallFor3 = recommendations.size() / 3.0;
        Double recallFor10 = recommendations.size() / 10.0;
        int i = 0;
        for (String recommendation : recommendations) {
            if (recommendation.equalsIgnoreCase(correctRecommendation)) {
                isRecommendationCorrectFor10 = "1";
                if (i < 1) {
                    isRecommendationCorrectFor1 = "1";
                    isRecommendationCorrectFor3 = "1";
                } else if (i < 3)
                    isRecommendationCorrectFor3 = "1";

            }
            i++;
        }
        PrintWriter pw;
        try {
            // 1
            File directory = new File("./tmp/1/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            pw = new PrintWriter(new FileWriter("./tmp/1/out.txt", true));
            pw.println(isRecommendationCorrectFor1 + "," + recallFor1.toString());
            pw.close();
            // 3
            directory = new File("./tmp/3/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            pw = new PrintWriter(new FileWriter("./tmp/3/out.txt", true));
            pw.println(isRecommendationCorrectFor3 + "," + recallFor3.toString());
            pw.close();
            // 10
            directory = new File("./tmp/10/");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            pw = new PrintWriter(new FileWriter("./tmp/10/out.txt", true));
            pw.println(isRecommendationCorrectFor10 + "," + recallFor10.toString());
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;

    }

}
