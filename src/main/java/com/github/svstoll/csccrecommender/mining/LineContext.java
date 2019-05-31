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

import cc.kave.commons.model.ssts.visitor.ISSTNode;

import java.util.ArrayList;
import java.util.List;

import static com.github.svstoll.csccrecommender.utility.SstUtility.isValidToken;

public class LineContext {

  private final CsccContext csccContext;
  private final List<String> tokens = new ArrayList<>();
  private ISSTNode node;

  public LineContext(CsccContext csccContext) {
    this.csccContext = csccContext;
  }

  public CsccContext getCsccContext() {
    return csccContext;
  }

  public ISSTNode getNode() {
    return node;
  }

  public void setNode(ISSTNode node) {
    this.node = node;
  }

  public List<String> getTokens() {
    return tokens;
  }

  public void addToken(String token) {
    if (isValidToken(token)) {
      tokens.add(token);
    }
  }
}
