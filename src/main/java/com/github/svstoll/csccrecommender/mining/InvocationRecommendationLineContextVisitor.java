/*
 *  Copyright 2019 Sven Stoll, Dingguang Jin, Tran Phan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.github.svstoll.csccrecommender.mining;

import com.github.svstoll.csccrecommender.evaluation.ContextEvaluationStatistics;
import com.github.svstoll.csccrecommender.index.MethodInvocationIndex;
import com.github.svstoll.csccrecommender.utility.SstUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InvocationRecommendationLineContextVisitor extends RecommendingLineContextVisitor {

  public static final Logger LOGGER = LoggerFactory.getLogger(InvocationRecommendationLineContextVisitor.class);

  private final ContextEvaluationStatistics contextEvaluationStatistics;

  public InvocationRecommendationLineContextVisitor(MethodInvocationIndex methodInvocationIndex,
                                                    ContextEvaluationStatistics contextEvaluationStatistics) {
    super(methodInvocationIndex);
    this.contextEvaluationStatistics = contextEvaluationStatistics;
  }

  @Override
  protected void handleMethodInvocation(String methodName, String invocationType, CsccContext csccContext) {
    if (!SstUtility.isValidToken(invocationType) || !SstUtility.isValidToken(methodName)) {
      return;
    }

    getRecommendationResults().clear();
    super.handleCompletionExpression(invocationType, csccContext);
    if (getRecommendationResults().isEmpty()) {
      return;
    }

    List<String> recommendations = getRecommendationResults().get(0).getRecommendedMethods();
    boolean isTop1 = false;
    boolean isTop3 = false;
    boolean isTop10 = false;
    for (int i = 0; i < recommendations.size() && i < 10; i++) {
      if (recommendations.get(i).equals(methodName)) {
        isTop10 = true;
        if (i < 1) {
          isTop1 = true;
          isTop3 = true;
        }
        else if (i < 3) {
          isTop3 = true;
        }
        break;
      }
    }
    contextEvaluationStatistics.update(!recommendations.isEmpty(), isTop1, isTop3, isTop10);
  }
}
