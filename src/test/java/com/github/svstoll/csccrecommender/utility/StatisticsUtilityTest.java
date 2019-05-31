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

package com.github.svstoll.csccrecommender.utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsUtilityTest {

  @Test
  public void calculateRecall_ReqRecommendationsIsLowerThan0_shouldThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> StatisticsUtility.calculateRecall(-1, 1));
  }

  @Test
  public void calculateRecall_recommendationsMadeIsLowerThan0_shouldThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> StatisticsUtility.calculateRecall(1, -1));
  }

  @Test
  public void calculateRecall_reqAndMadeRecommendationsAre0_shouldReturn1() {
    assertEquals(1, StatisticsUtility.calculateRecall(0,0));
  }

  @Test
  public void calculateRecall_recommendationsMadeIsGreaterThanRequested_shouldReturn1() {
    assertEquals(1, StatisticsUtility.calculateRecall(1,2));
  }

  @Test
  public void calculateRecall_reqAndMadeRecommendationsAreGreaterThan0_shouldReturnCorrectRecall() {
    assertEquals(0.5, StatisticsUtility.calculateRecall(2,1));
  }

  @Test
  public void calculatePrecision_recommendationsMadeIsLowerThan0_shouldThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> StatisticsUtility.calculatePrecision(-1, 1));
  }

  @Test
  public void calculatePrecision_relevantRecommendationsIsLowerThan0_shouldThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> StatisticsUtility.calculatePrecision(1, -1));
  }

  @Test
  public void calculatePrecision_relevantAndMadeRecommendationsAre0_shouldReturn1() {
    assertEquals(1, StatisticsUtility.calculatePrecision(0,0));
  }

  @Test
  public void calculatePrecision_recommendationsRelevantIsGreaterThanRelevant_shouldReturn1() {
    assertEquals(1, StatisticsUtility.calculatePrecision(1,2));
  }

  @Test
  public void calculatePrecision_relevantAndMadeRecommendationsAreGreaterThan0_shouldReturnCorrectPrecision() {
    assertEquals(0.5, StatisticsUtility.calculatePrecision(2,1));
  }

  @Test
  public void calculateFMeasure_givenPrecisionLowerThan0_shouldThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> StatisticsUtility.calculateFMeasure(-1, 0.5));
  }

  @Test
  public void calculateFMeasure_givenRecallLowerThan0_shouldThrowException() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> StatisticsUtility.calculateFMeasure(0.5, -1));
  }

  @Test
  public void calculateFMeasure_givenPrecisionIs0AndRecallIs0_shouldReturn0() {
    assertEquals(0, StatisticsUtility.calculateFMeasure(0, 0));
  }

  @Test
  public void calculateFMeasure_givenPrecisionIs0_shouldReturn0() {
    assertEquals(0, StatisticsUtility.calculateFMeasure(0, 0.5));
  }

  @Test
  public void calculateFMeasure_givenRecallIs0_shouldReturn0() {
    assertEquals(0, StatisticsUtility.calculateFMeasure(0.5, 0));
  }

  @Test
  public void calculateFMeasure_givenRecallAndPrecisionGreaterThan0_shouldReturnCorrectFMeasure() {
    assertEquals(0.5, StatisticsUtility.calculateFMeasure(0.5, 0.5));
  }
}