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

package com.github.svstoll.csccrecommender.index;

import cc.kave.commons.model.events.completionevents.Context;
import com.github.svstoll.csccrecommender.mining.ContextExtractor;
import com.github.svstoll.csccrecommender.mining.CsccContext;
import com.github.svstoll.csccrecommender.mining.CsccContextVisitor;
import com.github.svstoll.csccrecommender.mining.IndexingLineContextVisitor;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodInvocationIndexer {

  private static final Logger LOGGER = LoggerFactory.getLogger(MethodInvocationIndexer.class);

  private final MethodInvocationIndex methodInvocationIndex;
  private final ContextExtractor contextExtractor;

  @Inject
  protected MethodInvocationIndexer(MethodInvocationIndex methodInvocationIndex,
                                    ContextExtractor contextExtractor) {
    this.methodInvocationIndex = methodInvocationIndex;
    this.contextExtractor = contextExtractor;
  }

  public void indexAllAvailableContexts(boolean resetIndex) {
    LOGGER.info("Start indexing.");
    if (resetIndex) {
      methodInvocationIndex.clearIndex();
    }

    contextExtractor.processAllContexts(contexts -> {
      long start = System.currentTimeMillis();
      IndexingLineContextVisitor indexingLineContextVisitor = new IndexingLineContextVisitor();
      for (Context context : contexts) {
        context.getSST().accept(new CsccContextVisitor(), new CsccContext(indexingLineContextVisitor));
      }
      long end = System.currentTimeMillis();
      LOGGER.info("SST traversals with document creation took {} ms.", end - start);

      methodInvocationIndex.indexDocuments(indexingLineContextVisitor.getCachedDocuments());
    });

    LOGGER.info("Indexing finished.");
  }
}
