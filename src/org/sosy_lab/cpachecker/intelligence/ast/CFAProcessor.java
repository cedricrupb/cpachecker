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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.intelligence.graph.StructureGraph;

public class CFAProcessor {

  public static class Builder{

    private Set<Listener> config = new HashSet<>();

    public static Builder full(){
      return empty().initExit().assumption().blank().declaration().funcCall().statement();
    }

    public static Builder empty(){
      return new Builder();
    }

    public Builder initExit(){
      config.add(Listener.INIT_EXIT);
      return this;
    }

    public Builder assumption(){
      config.add(Listener.ASSUMPTION);
      return this;
    }

    public Builder blank(){
      config.add(Listener.BLANK);
      return this;
    }

    public Builder declaration(){
      config.add(Listener.DECLARATION);
      return this;
    }

    public Builder funcCall(){
      config.add(Listener.FUNC_CALL);
      return this;
    }

    public Builder statement(){
      config.add(Listener.STATEMENT);
      return this;
    }

    public CFAProcessor build(){
      return new CFAProcessor(config);
    }

  }

  private enum Listener{
    INIT_EXIT, ASSUMPTION, BLANK, DECLARATION, FUNC_CALL, STATEMENT
  }

  public CFAProcessor(){
    config = new HashSet<>();
    config.add(Listener.INIT_EXIT);
    config.add(Listener.STATEMENT);
    config.add(Listener.ASSUMPTION);
    config.add(Listener.BLANK);
    config.add(Listener.DECLARATION);
    config.add(Listener.FUNC_CALL);
  }

  private CFAProcessor(Set<Listener> pConfig) {
    config = pConfig;
  }

  private Set<Listener> config;

  private List<IEdgeListener> construct(StructureGraph pGraph, int depth){
    List<IEdgeListener> e = new ArrayList<>();

    if(config.contains(Listener.INIT_EXIT)){
      e.add(new InitExitListener(depth, pGraph));
    }

    if(config.contains(Listener.ASSUMPTION)){
      e.add(new AssumptionListener(depth, pGraph));
    }

    if(config.contains(Listener.BLANK)){
      e.add(new BlankEdgeListener(depth, pGraph));
    }

    if(config.contains(Listener.DECLARATION)){
      e.add(new DeclarationListener(depth, pGraph));
    }

    if(config.contains(Listener.FUNC_CALL)){
      e.add(new FunctionCallListener(depth, pGraph));
      e.add(new ReturnFunctionListener(depth, pGraph));
    }

    if(config.contains(Listener.STATEMENT)){
      e.add(new StatementListener(depth, pGraph));
      e.add(new ReturnStatementListener(depth, pGraph));
    }

    return e;
  }


  public StructureGraph process(CFA pCFA, int depth){
      StructureGraph out = new StructureGraph();
      List<IEdgeListener> listeners = construct(out, depth);
      CFAIterator it = new CFAIterator(listeners);
      it.iterate(pCFA);
      return out;
  }

}
