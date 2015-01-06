/*
 * CPAchecker is a tool for configurable software verification.
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
package org.sosy_lab.cpachecker.cpa.constraints.constraint.expressions;

import java.util.Objects;

import org.sosy_lab.cpachecker.cfa.types.Type;

/**
 * A binary {@link ConstraintExpression}.
 * Represents all <code>ConstraintExpression</code>s that consist of two operands.
 */
public abstract class BinaryConstraintExpression implements ConstraintExpression {

  private final ConstraintExpression operand1;
  private final ConstraintExpression operand2;

  /**
   * {@link Type} the operands are cast to during calculation.
   */
  private final Type calculationType;

  /**
   * {@link Type} of the binary expression
   */
  private Type expressionType;

  protected BinaryConstraintExpression(
      ConstraintExpression pOperand1,
      ConstraintExpression pOperand2,
      Type pExpressionType,
      Type pCalculationType) {

    operand1 = pOperand1;
    operand2 = pOperand2;
    expressionType = pExpressionType;
    calculationType = pCalculationType;
  }

  @Override
  public Type getExpressionType() {
    return expressionType;
  }

  public Type getCalculationType() {
    return calculationType;
  }

  public ConstraintExpression getOperand1() {
    return operand1;
  }

  public ConstraintExpression getOperand2() {
    return operand2;
  }

  @Override
  public boolean equals(Object pObj) {
    if (this == pObj) {
      return true;
    }
    if (pObj == null || getClass() != pObj.getClass()) {
      return false;
    }

    BinaryConstraintExpression that = (BinaryConstraintExpression) pObj;

    return operand1.equals(that.operand1) && operand2.equals(that.operand2) && expressionType
        .equals(that.expressionType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClass(), operand1, operand2, expressionType);
  }
}
