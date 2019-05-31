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

package com.github.svstoll.csccrecommender.recommender;

import cc.kave.commons.model.events.completionevents.CompletionEvent;
import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import com.github.svstoll.csccrecommender.evaluation.ContextEvaluationStatistics;
import com.github.svstoll.csccrecommender.index.MethodInvocationIndex;
import com.github.svstoll.csccrecommender.mining.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Singleton
public class CsccRecommender {

  public static final Logger LOGGER = LoggerFactory.getLogger(CsccRecommender.class);

  private final MethodInvocationIndex methodInvocationIndex;
  private final ContextExtractor contextExtractor;

  @Inject
  protected CsccRecommender(
      MethodInvocationIndex methodInvocationIndex,
      ContextExtractor contextExtractor) {
    this.methodInvocationIndex = methodInvocationIndex;
    this.contextExtractor = contextExtractor;
  }

  /**
   * Recommend method names for a given KaVe method completion event based on the specified index
   * directory.
   *
   * @param completionEvent a KaVE method completion event.
   * @return a ranked list of method name recommendations.
   *
   * @throws IllegalStateException if the last selected proposal of the completion event is
   *                               {@code null} or is not a {@code IMethodName}.
   */
  public List<RecommendationResult> recommendMethods(CompletionEvent completionEvent) {
    if (completionEvent.getLastSelectedProposal() == null ||
        !(completionEvent.getLastSelectedProposal().getName() instanceof IMethodName)) {
      throw new IllegalArgumentException("Provide a valid method completion event where the last selected proposal is a IMethodName.");
    }

    LOGGER.debug("Recommendation process started. Originally applied selection: {}",
        ((IMethodName) completionEvent.getLastSelectedProposal().getName()).getFullName());
    CsccContextVisitor csccContextVisitor = new CsccContextVisitor();
    RecommendingLineContextVisitor recommendingLineContextVisitor
        = new RecommendingLineContextVisitor(methodInvocationIndex);
    CsccContext csccContext = new CsccContext(recommendingLineContextVisitor);
    completionEvent.getContext().getSST().accept(csccContextVisitor, csccContext);

    return recommendingLineContextVisitor.getRecommendationResults();
  }

  /**
   * Uses the specified index directory to perform a recommendation for every method invocation
   * within the projects provided in the contexts dataset.
   *
   * @param statistics used to log overall recommendation statistics.
   */
  public void recommendForAllInvocationsInAvailableContexts(ContextEvaluationStatistics statistics) {
    contextExtractor.processAllContexts(contexts -> {
      LOGGER.info("Performing recommendations for all method invocations within {} contexts.", contexts.size());
      InvocationRecommendationLineContextVisitor invocationRecommendationLineContextVisitor
          = new InvocationRecommendationLineContextVisitor(methodInvocationIndex, statistics);
      for (Context context : contexts) {
        context.getSST().accept(new CsccContextVisitor(), new CsccContext(invocationRecommendationLineContextVisitor));
      }
    });
  }
}
