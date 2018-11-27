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
package org.sosy_lab.cpachecker.intelligence.ast;

import java.util.ArrayList;
import java.util.List;
import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.cfa.model.CFANode;

public class CFAIterator {

  private ShutdownNotifier notifier;
  private List<IEdgeListener> listeners = new ArrayList<>();


  public CFAIterator(List<IEdgeListener> pListeners, ShutdownNotifier pShutdownNotifier) {
    this.notifier = pShutdownNotifier;
    this.listeners = pListeners;
  }

  public void iterate(CFA pCFA) throws InterruptedException {
    for(CFANode node: pCFA.getAllNodes()){
      for(int i = 0; i < node.getNumLeavingEdges(); i++){
          for(IEdgeListener listener: listeners){
              if(notifier != null){
                notifier.shutdownIfNecessary();
              }
              listener.listen(node.getLeavingEdge(i));
          }
      }
    }
  }




}
