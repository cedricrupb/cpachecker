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
package org.sosy_lab.cpachecker.intelligence.graph;

import java.util.Map.Entry;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.util.dependencegraph.DGNode;
import org.sosy_lab.cpachecker.util.dependencegraph.DependenceGraph;
import org.sosy_lab.cpachecker.util.dependencegraph.DependenceGraph.DependenceType;
import org.sosy_lab.cpachecker.util.dependencegraph.DependenceGraph.TraversalDirection;

public class NativeGraphAnalyser extends GraphAnalyser {

  private CFA cfa;
  private DependenceGraph dependenceGraph;

  public NativeGraphAnalyser(
      CFA pCFA,
      StructureGraph pGraph,
      ShutdownNotifier pShutdownNotifier,
      LogManager pLogger) throws InterruptedException {
    super(pGraph, pShutdownNotifier, pLogger);
    cfa = pCFA;
    dependenceGraph = cfa.getDependenceGraph().orElse(null);

  }

  public NativeGraphAnalyser(CFA pCFA, StructureGraph pGraph) throws InterruptedException {
    this(pCFA, pGraph, null, null);
  }


  @Override
  public void applyDD() throws InterruptedException{
      if(dependenceGraph != null){
        applyDependencies(dependenceGraph, DependenceType.FLOW);
      }else {
        super.applyDD();
      }
  }

  @Override
  public void applyCD() throws InterruptedException{
    if(dependenceGraph != null){
      applyDependencies(dependenceGraph, DependenceType.CONTROL);
    }else {
      super.applyCD();
    }
  }

  private void applyDependencies(DependenceGraph pGraph, DependenceType pType)
      throws InterruptedException {

    for(DGNode node : pGraph.getAllNodes()){
      String baseId = "N"+node.getCfaEdge().getPredecessor().getNodeNumber();

      for(Entry<DGNode, DependenceType> edge : pGraph.getAdjacentNodes(node, TraversalDirection.FORWARD).entrySet()){

        shutdownNotifier.shutdownIfNecessary();

        if(edge.getValue().equals(pType)){

          String succId = "N"+edge.getKey().getCfaEdge().getPredecessor().getNodeNumber();

          if(pType.equals(DependenceType.FLOW)) {
            this.graph.addDDEdge(baseId, succId);
          }else{
            this.graph.addCDEdge(baseId, succId);
          }


        }


      }

    }

  }


}
