/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2018  Dirk Beyer
 *  All rights reserved.
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
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package org.sosy_lab.cpachecker.intelligence.oracle.ranking.heuristics;

public class EventuallyOperator implements HOperator {

  private HCondition condition = new TrueCondition();

  @Override
  public boolean eventuallyActive() {
    return true;
  }

  @Override
  public boolean isActive(int timeStep, int precision, String label) {
    return condition.satisfied(timeStep, precision, label);
  }

  @Override
  public void setConditon(HCondition pConditon) {
    this.condition = pConditon;
  }

  @Override
  public HCondition getCondition() {
    return condition;
  }
}