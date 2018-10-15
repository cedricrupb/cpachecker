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
package org.sosy_lab.cpachecker.intelligence.learn.binary.impl.math;

import java.util.Iterator;

public class DenseRow implements Iterable<Double> {

  private Matrix parent;
  private int coordX;

  DenseRow(Matrix pDenseMatrix, int x){
    this.parent = pDenseMatrix;
    this.coordX = x;
  }

  public double get(int col){
    return parent.get(coordX, col);
  }

  public void set(int col, double d){
    parent.set(coordX, col, d);
  }

  public int getCols(){
    return parent.getCols();
  }

  public DenseVector transpose(){
    DenseVector vector = new DenseVector(this.getCols());

    for(int i = 0; i < this.getCols(); i++)
      vector.set(i, this.get(i));

    return vector;

  }

  @Override
  public Iterator<Double> iterator() {
    DenseRow row = this;
    return new Iterator<Double>() {
      int i = 0;
      @Override
      public boolean hasNext() {
        return i < row.getCols();
      }

      @Override
      public Double next() {
        return Double.valueOf(row.get(i++));
      }
    };
  }
}
