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


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.ast.c.CDeclaration;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.c.CDeclarationEdge;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;
import org.sosy_lab.cpachecker.intelligence.ast.visitors.CDeclarationDefCollectorVisitor;
import org.sosy_lab.cpachecker.intelligence.ast.visitors.CDeclarationUseCollectorVisitor;
import org.sosy_lab.cpachecker.intelligence.ast.visitors.CSimpleDeclASTVisitor;
import org.sosy_lab.cpachecker.intelligence.graph.StructureGraph;

public class DeclarationListener extends AEdgeListener {

  public DeclarationListener(int pDepth, StructureGraph pGraph) {
    super(pDepth, pGraph);
  }

  @Override
  public void listen(CFAEdge edge) {
    if(edge instanceof CDeclarationEdge){
      CDeclarationEdge declEdge = (CDeclarationEdge)edge;
      CDeclaration decl = declEdge.getDeclaration();

      String label = ASTNodeLabel.DECL.name();
      if(decl.isGlobal()){
        label = label + "_"+ ASTNodeLabel.GLOBAL.name();
      }

      String id = "N"+declEdge.getPredecessor().getNodeNumber();
      String idS = "N"+declEdge.getSuccessor().getNodeNumber();
      graph.addNode(id, label);
      graph.addNode(idS);
      graph.addCFGEdge(id, idS);

      try {
        String subTreeId = decl.accept(new CSimpleDeclASTVisitor(
            graph, depth
        ));
        graph.addSEdge(subTreeId, id);

        Map<String, Object> options = graph.getNode(id).getOptions();
        Set<String> vars = decl.accept(new CDeclarationUseCollectorVisitor(edge.getPredecessor()));
        if(vars != null && !vars.isEmpty())
          options.put("variables", vars);
        String declVar = decl.accept(new CDeclarationDefCollectorVisitor());
        if(declVar != null) {
          Set<String> declVars = new HashSet<>();
          declVars.add(declVar);
          options.put("output", declVars);
        }

      } catch (CPATransferException pE) {
        pE.printStackTrace();
      }


    }
  }
}