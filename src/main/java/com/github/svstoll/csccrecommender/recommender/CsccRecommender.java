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
import java.util.Objects;

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

  public List<RecommendationResult> recommendMethods(CompletionEvent completionEvent) {
    LOGGER.debug("Recommendation process started. Originally applied selection: {}",
        ((IMethodName) Objects.requireNonNull(completionEvent.getLastSelectedProposal().getName())).getFullName());
    CsccContextVisitor csccContextVisitor = new CsccContextVisitor();
    RecommendingLineContextVisitor recommendingLineContextVisitor
        = new RecommendingLineContextVisitor(methodInvocationIndex);
    CsccContext csccContext = new CsccContext(recommendingLineContextVisitor);
    completionEvent.getContext().getSST().accept(csccContextVisitor, csccContext);

    return recommendingLineContextVisitor.getRecommendationResults();
  }

  public void recommendForAllInvocationsInAvailableContexts(ContextEvaluationStatistics contextEvaluationStatistics) {
    contextExtractor.processAllContexts(contexts -> {
      LOGGER.info("Performing recommendation for all method invocations within {} contexts.", contexts.size());
      InvocationRecommendationLineContextVisitor invocationRecommendationLineContextVisitor
          = new InvocationRecommendationLineContextVisitor(methodInvocationIndex, contextEvaluationStatistics);
      for (Context context : contexts) {
        context.getSST().accept(new CsccContextVisitor(), new CsccContext(invocationRecommendationLineContextVisitor));
      }
    });
  }
}
