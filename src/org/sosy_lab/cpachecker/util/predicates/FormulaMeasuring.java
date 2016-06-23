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
package org.sosy_lab.cpachecker.util.predicates;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sosy_lab.cpachecker.util.predicates.smt.FormulaManagerView;
import org.sosy_lab.solver.api.BooleanFormula;
import org.sosy_lab.solver.api.FunctionDeclaration;
import org.sosy_lab.solver.visitors.DefaultBooleanFormulaVisitor;
import org.sosy_lab.solver.visitors.TraversalProcess;

import com.google.common.collect.ImmutableSortedSet;


public class FormulaMeasuring {

  public static class FormulaMeasures {
    private int trues = 0;
    private int falses = 0;
    private int conjunctions = 0;
    private int disjunctions = 0;
    private int negations = 0;
    private int atoms = 0;
    private final Set<String> variables = new HashSet<>();

    public int getAtoms() { return atoms; }
    public int getConjunctions() { return conjunctions; }
    public int getDisjunctions() { return disjunctions; }
    public int getFalses() { return falses; }
    public int getNegations() { return negations; }
    public int getTrues() { return trues; }
    public ImmutableSortedSet<String> getVariables() { return ImmutableSortedSet.copyOf(this.variables); }
  }

  private final FormulaManagerView managerView;

  public FormulaMeasuring(FormulaManagerView pManagerView) {
    this.managerView = pManagerView;
  }

  public FormulaMeasures measure(BooleanFormula formula) {
    FormulaMeasures result = new FormulaMeasures();
    managerView.getBooleanFormulaManager().visitRecursively(
        new FormulaMeasuringVisitor(managerView, result), formula
    );
    return result;
  }

  private static class FormulaMeasuringVisitor
      extends DefaultBooleanFormulaVisitor<TraversalProcess> {

    private final FormulaMeasures measures;
    private final FormulaManagerView fmgr;

    FormulaMeasuringVisitor(FormulaManagerView pFmgr, FormulaMeasures pMeasures) {
      measures = pMeasures;
      fmgr = pFmgr;
    }

    @Override
    protected TraversalProcess visitDefault() {
      return TraversalProcess.CONTINUE;
    }

    @Override
    public TraversalProcess visitFalse() {
      measures.falses++;
      return TraversalProcess.CONTINUE;
    }


    @Override
    public TraversalProcess visitTrue() {
      measures.trues++;
      return TraversalProcess.CONTINUE;
    }

    @Override
    public TraversalProcess visitAtom(BooleanFormula pAtom, FunctionDeclaration decl) {
      measures.atoms++;

      BooleanFormula atom = fmgr.uninstantiate(pAtom);
      measures.variables.addAll(fmgr.extractVariableNames(atom));
      return TraversalProcess.CONTINUE;
    }

    @Override
    public TraversalProcess visitNot(BooleanFormula pOperand) {
      measures.negations++;
      return TraversalProcess.CONTINUE;
    }

    @Override
    public TraversalProcess visitAnd(List<BooleanFormula> pOperands) {
      measures.conjunctions++;
      return TraversalProcess.CONTINUE;
    }

    @Override
    public TraversalProcess visitOr(List<BooleanFormula> pOperands) {
      measures.disjunctions++;
      return TraversalProcess.CONTINUE;
    }
  }
}
