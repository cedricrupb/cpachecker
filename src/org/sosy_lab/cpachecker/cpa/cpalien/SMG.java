/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2012  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.cpalien;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

import org.sosy_lab.common.LogManager;

public class SMG {
  final private HashSet<SMGObject> objects = new HashSet<>();
  final private HashSet<Integer> values = new HashSet<>();
  final private HashSet<SMGEdgeHasValue> hv_edges = new HashSet<>();
  final private HashSet<SMGEdgePointsTo> pt_edges = new HashSet<>();

  /**
   * A special object representing NULL
   */
  final private SMGObject nullObject;
  /**
   * An address of the special object representing null
   */
  final private int nullAddress = 0;

  public SMG(){
    nullObject = new SMGObject();
    SMGEdgePointsTo nullPointer = new SMGEdgePointsTo(nullAddress, nullObject, 0);

    addObject(nullObject);
    addValue(nullAddress);
    addPointsToEdge(nullPointer);
  }

  public SMG(SMG pHeap) {
    nullObject = pHeap.nullObject;

    objects.addAll(pHeap.objects);
    values.addAll(pHeap.values);
    hv_edges.addAll(pHeap.hv_edges);
    pt_edges.addAll(pHeap.pt_edges);
  }

  final public SMGObject getNullObject(){
    return nullObject;
  }

  final public int getNullValue(){
    return nullAddress;
  }

  final public void addObject(SMGObject pObj) {
    this.objects.add(pObj);
  }

  final public void addValue(int pValue) {
    this.values.add(Integer.valueOf(pValue));
  }

  final public void addPointsToEdge(SMGEdgePointsTo pEdge){
    this.pt_edges.add(pEdge);
  }

  final public void addHasValueEdge(SMGEdgeHasValue pNewEdge) {
    this.hv_edges.add(pNewEdge);
  }

  final public String valuesToString(){
    return "values=" + values.toString();
  }

  final public String hvToString(){
    return "hasValue=" + hv_edges.toString();
  }

  final public String ptToString(){
    return "pointsTo=" + pt_edges.toString();
  }

  final public Set<Integer> getValues(){
    return Collections.unmodifiableSet(values);
  }

  final public Set<SMGObject> getObjects(){
    return Collections.unmodifiableSet(objects);
  }

  final public Set<SMGEdgeHasValue> getHVEdges(){
    return Collections.unmodifiableSet(hv_edges);
  }

  final public Set<SMGEdgePointsTo> getPTEdges(){
    return Collections.unmodifiableSet(pt_edges);
  }

  /**
   * @param value
   * @return
   *
   * TODO: More documentation
   * TODO: Test
   * TODO: Consistency check: no value can point to more objects
   */
  final public SMGObject getObjectPointedBy(Integer value){
    for (SMGEdgePointsTo edge: pt_edges){
      if (value == edge.getValue()){
        return edge.getObject();
      }
    }

    return null;
  }

  final private HashSet<SMGEdgeHasValue> getValuesForObject(SMGObject pObject, Integer pOffset){
    HashSet<SMGEdgeHasValue> toReturn = new HashSet<>();
    for (SMGEdgeHasValue edge: hv_edges){
      if (edge.getObject() == pObject && (pOffset == null || edge.getOffset() == pOffset)){
        toReturn.add(edge);
      }
    }

    return toReturn;
  }

  //TODO: Test
  final public HashSet<SMGEdgeHasValue> getValuesForObject(SMGObject pObject) {
    return getValuesForObject(pObject, null);
  }

  //TODO: Test
  final public HashSet<SMGEdgeHasValue> getValuesForObject(SMGObject pObject, int pOffset) {
    return getValuesForObject(pObject, Integer.valueOf(pOffset));
  }
}

class SMGConsistencyVerifier{
  private SMGConsistencyVerifier() {} /* utility class */

  static private boolean verifySMGProperty(boolean result, LogManager pLogger, String message){
    pLogger.log(Level.FINEST, message, ":", result);
    return result;
  }

  static private boolean verifyNullObject(LogManager pLogger, SMG smg){
    Integer null_value = null;

    for(Integer value: smg.getValues()){
      if (smg.getObjectPointedBy(value) == smg.getNullObject()){
        null_value = value;
      }
    }
    if (null_value == null){
      pLogger.log(Level.SEVERE, "SMG inconsistent: no value pointing to null object");
      return false;
    }

    if (! smg.getValuesForObject(smg.getNullObject()).isEmpty()){
      pLogger.log(Level.SEVERE, "SMG inconsistent: null object has some value");
      return false;
    }

    return true;
  }

  static public boolean verifySMG(LogManager pLogger, SMG smg){
    boolean toReturn = true;
    pLogger.log(Level.FINEST, "Starting constistency check of a SMG");

    toReturn = toReturn && verifySMGProperty(
        verifyNullObject(pLogger, smg),
        pLogger,
        "Checking SMG consistency: null object invariants hold");

    pLogger.log(Level.FINEST, "Ending consistency check of a SMG");

    return toReturn;
  }
}