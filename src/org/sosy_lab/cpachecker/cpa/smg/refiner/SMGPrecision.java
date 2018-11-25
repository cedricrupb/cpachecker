/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2016  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.smg.refiner;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.cpa.smg.SMGAbstractionBlock;
import org.sosy_lab.cpachecker.cpa.smg.refiner.SMGInterpolant.SMGPrecisionIncrement;
import org.sosy_lab.cpachecker.cpa.smg.util.PersistentMultimap;
import org.sosy_lab.cpachecker.util.predicates.BlockOperator;
import org.sosy_lab.cpachecker.util.states.MemoryLocation;

public abstract class SMGPrecision implements Precision {

  private final SMGPrecisionAbstractionOptions options;
  private final int threshold = 0;

  public SMGPrecision(SMGPrecisionAbstractionOptions pOptions) {
    options = pOptions;
  }

  public static SMGPrecision createStaticPrecision(boolean pEnableHeapAbstraction) {
    SMGPrecisionAbstractionOptions options =
        new SMGPrecisionAbstractionOptions(pEnableHeapAbstraction, false, false);
    return new SMGStaticPrecision(options);
  }

  public abstract Precision withIncrement(Map<CFANode, SMGPrecisionIncrement> pPrecisionIncrement);

  public abstract SMGPrecision join(SMGPrecision pPrecision);

  public static SMGPrecision createRefineablePrecision(SMGPrecision pPrecision) {
    return new SMGRefineablePrecision(
        new SMGPrecisionAbstractionOptions(pPrecision.options.allowsHeapAbstraction(), true, true),
        PersistentMultimap.of(),
        PersistentMultimap.of(),
        PersistentMultimap.of());
  }

  public abstract boolean isTracked(SMGMemoryPath pPath, CFANode pCfaNode);

  public abstract Set<SMGMemoryPath> getTrackedMemoryPathsOnNode(CFANode pCfaNode);

  public abstract Set<MemoryLocation> getTrackedStackVariablesOnNode(CFANode pCfaNode);

  public boolean allowsHeapAbstractionOnNode(CFANode pCfaNode, BlockOperator pBlockOperator) {
    return options.allowsHeapAbstraction() && pBlockOperator.isBlockEnd(pCfaNode, threshold);
  }

  public SMGPrecisionAbstractionOptions getAbstractionOptions() {
    return options;
  }

  public abstract Set<SMGAbstractionBlock> getAbstractionBlocks(CFANode location);

  private static class SMGRefineablePrecision extends SMGPrecision {

    private final PersistentMultimap<CFANode, SMGMemoryPath> trackedMemoryPaths;
    private final PersistentMultimap<CFANode, MemoryLocation> trackedStackVariables;
    private final PersistentMultimap<CFANode, SMGAbstractionBlock> abstractionBlocks;

    private SMGRefineablePrecision(
        SMGPrecisionAbstractionOptions pOptions,
        PersistentMultimap<CFANode, SMGMemoryPath> pTrackedMemoryPaths,
        PersistentMultimap<CFANode, SMGAbstractionBlock> pAbstractionBlocks,
        PersistentMultimap<CFANode, MemoryLocation> pTrackedStackVariables) {
      super(pOptions);
      trackedMemoryPaths = pTrackedMemoryPaths;
      abstractionBlocks = pAbstractionBlocks;
      trackedStackVariables = pTrackedStackVariables;
    }

    @Override
    public Set<SMGMemoryPath> getTrackedMemoryPathsOnNode(CFANode pLocationNode) {
      return trackedMemoryPaths.get(pLocationNode);
    }

    @Override
    public Set<MemoryLocation> getTrackedStackVariablesOnNode(CFANode pCfaNode) {
      return trackedStackVariables.get(pCfaNode);
    }

    @Override
    public Precision withIncrement(Map<CFANode, SMGPrecisionIncrement> pPrecisionIncrement) {

      PersistentMultimap<CFANode, SMGMemoryPath> resultMemoryPaths = trackedMemoryPaths;
      PersistentMultimap<CFANode, MemoryLocation> resultStackVariables = trackedStackVariables;
      PersistentMultimap<CFANode, SMGAbstractionBlock> resultAbstractionBlocks = abstractionBlocks;

      for (Entry<CFANode, SMGPrecisionIncrement> entry : pPrecisionIncrement.entrySet()) {
        SMGPrecisionIncrement inc = entry.getValue();
        CFANode cfaNode = entry.getKey();
        resultAbstractionBlocks = resultAbstractionBlocks.putAllAndCopy(cfaNode, inc.getAbstractionBlock());
        resultMemoryPaths = resultMemoryPaths.putAllAndCopy(cfaNode, inc.getPathsToTrack());
        resultStackVariables = resultStackVariables.putAllAndCopy(cfaNode, inc.getStackVariablesToTrack());
      }

      return new SMGRefineablePrecision(
          getAbstractionOptions(),
          resultMemoryPaths,
          resultAbstractionBlocks,
          resultStackVariables);
    }

    @Override
    public SMGPrecision join(SMGPrecision pPrecision) {

      if (pPrecision instanceof SMGStaticPrecision) {
        return pPrecision;
      }

      SMGRefineablePrecision other = (SMGRefineablePrecision) pPrecision;
      assert getAbstractionOptions().equals(pPrecision.getAbstractionOptions());

      return new SMGRefineablePrecision(
          getAbstractionOptions(),
          trackedMemoryPaths.putAllAndCopy(other.trackedMemoryPaths),
          abstractionBlocks.putAllAndCopy(other.abstractionBlocks),
          trackedStackVariables.putAllAndCopy(other.trackedStackVariables));
    }

    @Override
    public boolean isTracked(SMGMemoryPath pPath, CFANode pCfaNode) {
      return trackedMemoryPaths.get(pCfaNode).contains(pPath);
    }

    @Override
    public Set<SMGAbstractionBlock> getAbstractionBlocks(CFANode location) {
      return abstractionBlocks.get(location);
    }

    @Override
    public String toString() {
      return "SMGRefineablePrecision [trackedMemoryPaths=" + trackedMemoryPaths
          + ", trackedStackVariables=" + trackedStackVariables + ", abstractionBlocks="
          + abstractionBlocks + "]";
    }
  }

  private static class SMGStaticPrecision extends SMGPrecision {

    private SMGStaticPrecision(SMGPrecisionAbstractionOptions pAllowsHeapAbstraction) {
      super(pAllowsHeapAbstraction);
    }

    @Override
    public boolean isTracked(SMGMemoryPath pPath, CFANode pCfaNode) {
      return true;
    }

    @Override
    public Precision withIncrement(Map<CFANode, SMGPrecisionIncrement> pPrecisionIncrement) {
      return this;
    }

    @Override
    public SMGPrecision join(SMGPrecision pPrecision) {
      return this;
    }

    @Override
    public Set<SMGAbstractionBlock> getAbstractionBlocks(CFANode pLocation) {
      return ImmutableSet.of();
    }

    @Override
    public Set<SMGMemoryPath> getTrackedMemoryPathsOnNode(CFANode pCfaNode) {
      throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public Set<MemoryLocation> getTrackedStackVariablesOnNode(CFANode pCfaNode) {
      throw new UnsupportedOperationException("Method not yet implemented.");
    }

    @Override
    public String toString() {
      return "Static precision " + getAbstractionOptions().toString();
    }
  }

  public static final class SMGPrecisionAbstractionOptions {

    private final boolean heapAbstraction;
    private final boolean fieldAbstraction;
    private final boolean stackAbstraction;

    public SMGPrecisionAbstractionOptions(boolean pHeapAbstraction, boolean pFieldAbstraction,
        boolean pStackAbstraction) {
      super();
      heapAbstraction = pHeapAbstraction;
      fieldAbstraction = pFieldAbstraction;
      stackAbstraction = pStackAbstraction;
    }

    public boolean allowsHeapAbstraction() {
      return heapAbstraction;
    }

    public boolean allowsFieldAbstraction() {
      return fieldAbstraction;
    }

    public boolean allowsStackAbstraction() {
      return stackAbstraction;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(fieldAbstraction, heapAbstraction, stackAbstraction);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof SMGPrecisionAbstractionOptions)) { return false; }
      SMGPrecisionAbstractionOptions other = (SMGPrecisionAbstractionOptions) obj;
      return fieldAbstraction == other.fieldAbstraction
          && heapAbstraction == other.heapAbstraction
          && stackAbstraction == other.stackAbstraction;
    }

    @Override
    public String toString() {
      return "SMGPrecisionAbstractionOptions [heapAbstraction=" + heapAbstraction
          + ", fieldAbstraction=" + fieldAbstraction + ", stackAbstraction=" + stackAbstraction + "]";
    }
  }
}