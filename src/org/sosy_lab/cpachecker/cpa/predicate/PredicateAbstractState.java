/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.sosy_lab.cpachecker.util.AbstractStates.extractStateByType;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.io.Serializable;
import org.sosy_lab.common.collect.PathCopyingPersistentTreeMap;
import org.sosy_lab.common.collect.PersistentMap;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.FormulaReportingState;
import org.sosy_lab.cpachecker.core.interfaces.Graphable;
import org.sosy_lab.cpachecker.core.interfaces.NonMergeableAbstractState;
import org.sosy_lab.cpachecker.core.interfaces.Partitionable;
import org.sosy_lab.cpachecker.util.AbstractStates;
import org.sosy_lab.cpachecker.util.predicates.AbstractionFormula;
import org.sosy_lab.cpachecker.util.predicates.pathformula.PathFormula;
import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.java_smt.api.BooleanFormula;

/**
 * AbstractState for Symbolic Predicate Abstraction CPA
 */
public abstract class PredicateAbstractState implements AbstractState, Partitionable, Serializable {

  private static final long serialVersionUID = -265763837277453447L;

  public final static Predicate<AbstractState> CONTAINS_ABSTRACTION_STATE =
      Predicates.compose(
          PredicateAbstractState::isAbstractionState,
          AbstractStates.toState(PredicateAbstractState.class));

  public static PredicateAbstractState getPredicateState(AbstractState pState) {
    return checkNotNull(extractStateByType(pState, PredicateAbstractState.class));
  }

  /**
   * Marker type for abstract states that were generated by computing an
   * abstraction.
   */
  private static class AbstractionState extends PredicateAbstractState implements NonMergeableAbstractState, Graphable, FormulaReportingState {

    private static final long serialVersionUID = 8341054099315063986L;

    private AbstractionState(PathFormula pf,
        AbstractionFormula pA, PersistentMap<CFANode, Integer> pAbstractionLocations) {
      super(pf, pA, pAbstractionLocations);
      // Check whether the pathFormula of an abstraction element is just "true".
      // partialOrder relies on this for optimization.
      //Preconditions.checkArgument(bfmgr.isTrue(pf.getFormula()));
      // Check uncommented because we may pre-initialize the path formula
      // with an invariant.
      // This is no problem for the partial order because the invariant
      // is always the same when the location is the same.
    }

    @Override
    public Object getPartitionKey() {
      if (super.abstractionFormula.isFalse()) {
        // put unreachable states in a separate partition to avoid merging
        // them with any reachable states
        return Boolean.FALSE;
      } else {
        return null;
      }
    }

    @Override
    public boolean isAbstractionState() {
      return true;
    }

    @Override
    public String toString() {
      return "Abstraction location: true, Abstraction: " + super.abstractionFormula;
    }

    @Override
    public String toDOTLabel() {
      return super.abstractionFormula.toString();
    }

    @Override
    public boolean shouldBeHighlighted() {
      return true;
    }

    @Override
    public BooleanFormula getFormulaApproximation(FormulaManagerView pManager) {
      return super.abstractionFormula.asFormulaFromOtherSolver(pManager);
    }
  }

  private static class NonAbstractionState extends PredicateAbstractState {
    private static final long serialVersionUID = -6912172362012773999L;
    /**
     * The abstract state this element was merged into.
     * Used for fast coverage checks.
     */
    private transient PredicateAbstractState mergedInto = null;

    private NonAbstractionState(PathFormula pF, AbstractionFormula pA,
        PersistentMap<CFANode, Integer> pAbstractionLocations) {
      super(pF, pA, pAbstractionLocations);
    }

    @Override
    public boolean isAbstractionState() {
      return false;
    }

    @Override
    PredicateAbstractState getMergedInto() {
      return mergedInto;
    }

    @Override
    void setMergedInto(PredicateAbstractState pMergedInto) {
      Preconditions.checkNotNull(pMergedInto);
      mergedInto = pMergedInto;
    }

    @Override
    public Object getPartitionKey() {
      return getAbstractionFormula();
    }

    @Override
    public String toString() {
      return "Abstraction location: false";
    }
  }

  public static PredicateAbstractState mkAbstractionState(
      PathFormula pF, AbstractionFormula pA,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    return new AbstractionState(pF, pA, pAbstractionLocations);
  }

  public static PredicateAbstractState mkNonAbstractionStateWithNewPathFormula(PathFormula pF,
      PredicateAbstractState oldState) {
    return new NonAbstractionState(pF, oldState.getAbstractionFormula(),
                                        oldState.getAbstractionLocationsOnPath());
  }

  static PredicateAbstractState mkNonAbstractionState(
      PathFormula pF,
      AbstractionFormula pA,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    return new NonAbstractionState(pF, pA, pAbstractionLocations);
  }

  /** The path formula for the path from the last abstraction node to this node.
   * it is set to true on a new abstraction location and updated with a new
   * non-abstraction location */
  private PathFormula pathFormula;

  /** The abstraction which is updated only on abstraction locations */
  private AbstractionFormula abstractionFormula;

  /** How often each abstraction location was visited on the path to the current state. */
  private final transient PersistentMap<CFANode, Integer> abstractionLocations;

  private PredicateAbstractState(PathFormula pf, AbstractionFormula a,
      PersistentMap<CFANode, Integer> pAbstractionLocations) {
    this.pathFormula = pf;
    this.abstractionFormula = a;
    this.abstractionLocations = pAbstractionLocations;
  }

  public abstract boolean isAbstractionState();

  PredicateAbstractState getMergedInto() {
    throw new UnsupportedOperationException("Assuming wrong PredicateAbstractStates were merged!");
  }

  /**
   * @param pMergedInto the state that should be set as merged
   */
  void setMergedInto(PredicateAbstractState pMergedInto) {
    throw new UnsupportedOperationException("Merging wrong PredicateAbstractStates!");
  }

  public PersistentMap<CFANode, Integer> getAbstractionLocationsOnPath() {
    return abstractionLocations;
  }

  public AbstractionFormula getAbstractionFormula() {
    return abstractionFormula;
  }

  /**
   * Replace the abstraction formula part of this element.
   * THIS IS POTENTIALLY UNSOUND!
   *
   * Call this function only during refinement if you also change all successor
   * elements and consider the coverage relation.
   */
  void setAbstraction(AbstractionFormula pAbstractionFormula) {
    if (isAbstractionState()) {
      abstractionFormula = checkNotNull(pAbstractionFormula);
    } else {
      throw new UnsupportedOperationException("Changing abstraction formula is only supported for abstraction elements");
    }
  }

  public PathFormula getPathFormula() {
    return pathFormula;
  }

  protected Object readResolve() {
    if (this instanceof AbstractionState) {
      // consistency check
      /*Pair<String,Integer> splitName;
      FormulaManagerView mgr = GlobalInfo.getInstance().getFormulaManager();
      SSAMap ssa = pathFormula.getSsa();

      for (String var : mgr.extractFreeVariableMap(abstractionFormula.asInstantiatedFormula()).keySet()) {
        splitName = FormulaManagerView.parseName(var);

        if (splitName.getSecond() == null) {
          if (ssa.containsVariable(splitName.getFirst())) {
            throw new StreamCorruptedException("Proof is corrupted, abort reading");
          }
          continue;
        }

        if(splitName.getSecond()!=ssa.getIndex(splitName.getFirst())) {
          throw new StreamCorruptedException("Proof is corrupted, abort reading");
        }
      }*/

      return new AbstractionState(pathFormula,
          abstractionFormula, PathCopyingPersistentTreeMap.<CFANode, Integer> of());
    }
    return new NonAbstractionState(pathFormula, abstractionFormula,
        PathCopyingPersistentTreeMap.<CFANode, Integer>of());
  }
}