/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
package org.sosy_lab.cpachecker.intelligence.graph.dominator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CachedGraphNavigator implements IGraphNavigator {

  private IGraphNavigator base;

  private Map<String, Set<String>> successor = new HashMap<>();
  private Map<String, Set<String>> predecessor = new HashMap<>();


  public CachedGraphNavigator(IGraphNavigator pBase) {
    base = pBase;
  }

  @Override
  public Set<String> successor(String node) {

    if(!successor.containsKey(node)){
      successor.put(node, base.successor(node));
    }

    return successor.get(node);
  }

  @Override
  public Set<String> predecessor(String node) {
    if(!predecessor.containsKey(node)){
      predecessor.put(node, base.predecessor(node));
    }
    return predecessor.get(node);
  }

  @Override
  public Set<String> nodes() {
    return base.nodes();
  }
}
