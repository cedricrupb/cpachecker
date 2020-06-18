/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2020  Dirk Beyer
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
package org.sosy_lab.cpachecker.core.algorithm.faultlocalization.formula;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.util.faultlocalization.FaultContribution;
import org.sosy_lab.java_smt.api.BooleanFormula;
import org.sosy_lab.java_smt.api.FormulaType;

public class Selector extends FaultContribution implements AbstractTraceElement {

  private static Map<String, Selector> selectors = new HashMap<>();
  private static int maxIndex = 0;

  private String name;
  private CFAEdge edge;
  private BooleanFormula formula;
  private BooleanFormula selectorFormula;
  private FormulaContext context;
  private int index;

  public CFAEdge getEdge() {
    return edge;
  }

  public String getName() {
    return name;
  }

  public BooleanFormula getFormula() {
    return formula;
  }

  private Selector(
      int pUniqueIndex, BooleanFormula pFormula, CFAEdge pEdge, FormulaContext pContext) {
    super(pEdge);
    if (pEdge == null) {
      name = "S" + pUniqueIndex + ": unknown reference";
    } else {
      name = ("S" + pUniqueIndex + ": " + pEdge).replace("\t", " ");
    }
    index = pUniqueIndex;
    formula = pFormula;
    selectorFormula = pFormula;
    context = pContext;
    edge = pEdge;
  }
  /**
   * The truth value of the corresponding edge is essential. Enabling a selector is equivalent to
   * making the corresponding constraint hard.
   * Note as soon as the selector is part of a formula enable, disable and free will not have any effect.
   */
  public void enable() {
    formula = context.getSolver().getFormulaManager().getBooleanFormulaManager().makeTrue();
  }

  /**
   * The truth value of the corresponding edge is ignored
   * Note as soon as the selector is part of a formula enable, disable and free will not have any effect.
   */
  public void disable() {
    formula = context.getSolver().getFormulaManager().getBooleanFormulaManager().makeFalse();
  }

  /**
   * The solver does not know the truth value of this selector.
   * Note as soon as the selector is part of a formula enable, disable and free will not have any effect.
   */
  public void free() {
    formula = selectorFormula;
  }

  /**
   * Creates an unique Selector for every formula that is passed. If a selector for a formula has
   * already been created the existent selector is returned
   *
   * @param pContext the context of the TraceFormula
   * @param pFormula the formula that can be selected with the returned selector
   * @param pEdge the edge which corresponds to the selector
   * @return unique selector for edge pEdge
   */
  public static Selector makeSelector(
      FormulaContext pContext, BooleanFormula pFormula, CFAEdge pEdge) {
    Selector s = selectors.get(pFormula.toString());
    if (s != null) {
      return s;
    }

    s = new Selector(
        maxIndex,
            pContext
                .getSolver()
                .getFormulaManager()
                .makeVariable(FormulaType.BooleanType, "S" + maxIndex),
            pEdge,
            pContext);
    selectors.put(pFormula.toString(), s);
    maxIndex++;
    return s;
  }

  public int getIndex() {
    return index;
  }

  /**
   * Changes the selector formula. Needed for example after loop enrolling.
   * Statements on the exact same location in the program should have the same selector
   * @param selector This selector copies the formula from the given selector
   */
  public void changeSelectorFormula(Selector selector) {
    assert selector.correspondingEdge().getDescription().equals(correspondingEdge().getDescription());
    if(selectorFormula.equals(formula)){
      selectorFormula = selector.selectorFormula;
      formula = selectorFormula;
    } else {
      selectorFormula = selector.selectorFormula;
    }
  }

  public static Optional<Selector> of(BooleanFormula formula) {
    return Optional.ofNullable(selectors.get(formula.toString()));
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean equals(Object q) {
    if (q instanceof Selector) {
      return name.equals(((Selector) q).getName()) && super.equals(q);
    }
    return false;
  }

  @Override
  public int hashCode() {
    // super class changes hashcode on adding reasons but Selectors shouldn't
    // a selector stays a selector for one edge (reasons do not matter)
    return Objects.hash(31, name.hashCode());
  }

}
