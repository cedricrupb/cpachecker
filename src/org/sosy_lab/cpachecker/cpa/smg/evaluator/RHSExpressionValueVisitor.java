/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2017  Dirk Beyer
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
package org.sosy_lab.cpachecker.cpa.smg.evaluator;

import java.util.List;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CFunctionCallExpression;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cpa.smg.SMGBuiltins;
import org.sosy_lab.cpachecker.cpa.smg.SMGState;
import org.sosy_lab.cpachecker.cpa.smg.evaluator.SMGAbstractObjectAndState.SMGAddressValueAndState;
import org.sosy_lab.cpachecker.exceptions.CPATransferException;

class RHSExpressionValueVisitor extends ExpressionValueVisitor {

  private final SMGBuiltins builtins;

  public RHSExpressionValueVisitor(
      SMGRightHandSideEvaluator pSmgRightHandSideEvaluator,
      SMGBuiltins pBuiltins,
      CFAEdge pEdge,
      SMGState pSmgState) {
    super(pSmgRightHandSideEvaluator, pEdge, pSmgState);
    builtins = pBuiltins;
  }

  @Override
  public List<SMGAddressValueAndState> visit(CFunctionCallExpression pIastFunctionCallExpression)
      throws CPATransferException {

    CExpression fileNameExpression = pIastFunctionCallExpression.getFunctionNameExpression();
    String functionName = fileNameExpression.toASTString();

    //TODO extreme code sharing ...

    // If Calloc and Malloc have not been properly declared,
    // they may be shown to return void
    if (builtins.isABuiltIn(functionName)) {
      if (builtins.isConfigurableAllocationFunction(functionName)) {
        return builtins.evaluateConfigurableAllocationFunction(
            pIastFunctionCallExpression, getInitialSmgState(), getCfaEdge());
      }
      return builtins.handleBuiltinFunctionCall(
          getCfaEdge(), pIastFunctionCallExpression, functionName, getInitialSmgState());
    } else {
      return builtins.handleUnknownFunction(
          getCfaEdge(), pIastFunctionCallExpression, functionName, getInitialSmgState());
    }
  }
}