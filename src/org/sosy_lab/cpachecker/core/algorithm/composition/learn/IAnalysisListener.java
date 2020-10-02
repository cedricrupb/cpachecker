// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

package org.sosy_lab.cpachecker.core.algorithm.composition.learn;

import org.sosy_lab.cpachecker.intelligence.graph.model.GEdge;

public interface IAnalysisListener {

  public void listen(GEdge pGEdge);

  public void reset();

}
