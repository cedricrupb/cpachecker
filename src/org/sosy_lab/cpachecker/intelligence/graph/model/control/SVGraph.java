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
package org.sosy_lab.cpachecker.intelligence.graph.model.control;


import org.sosy_lab.cpachecker.intelligence.ast.OptionKeys;
import org.sosy_lab.cpachecker.intelligence.graph.model.StructureGraph;

public class SVGraph extends StructureGraph {


  public boolean addCFGEdge(String source, String target){
    return addEdge(new CFGEdge(super.getNode(source), super.getNode(target)));
  }

  public boolean addCDEdge(String source, String target){
    return addEdge(new CDEdge(super.getNode(source), super.getNode(target)));
  }

  public boolean addDDEdge(String source, String target){
    return addEdge(new DDEdge(super.getNode(source), super.getNode(target)));
  }

  public boolean addSEdge(String source, String target){
    return addEdge(new SEdge(super.getNode(source), super.getNode(target)));
  }

  public boolean addDummyEdge(String source, String target){
    return addEdge(new DummyEdge(super.getNode(source), super.getNode(target)));
  }

  @Override
  public String toDot(){
    return super.toDot(
        n -> n.getId().startsWith("N"),
        e -> {
          if(e instanceof DummyEdge){
            return "grey";
          }else if(e instanceof CFGEdge){
            return "black";
          }
          if(e instanceof DDEdge){
            return "red";
          }
          if(e instanceof CDEdge){
            return "green";
          }
          if(e instanceof SEdge){
            return "blue";
          }

          return "opaque";
        },
        n -> {

          if(n.getOption(OptionKeys.PARENT_FUNC) != null){
            return n.getOption(OptionKeys.PARENT_FUNC);
          }

          return "main";
        }
    );
  }

}
