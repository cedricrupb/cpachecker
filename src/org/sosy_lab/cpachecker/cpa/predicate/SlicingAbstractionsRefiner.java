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
package org.sosy_lab.cpachecker.cpa.predicate;

import static com.google.common.collect.FluentIterable.from;

import com.google.common.base.Optional;
import java.util.Collection;
import java.util.logging.Level;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.core.counterexample.CounterexampleInfo;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.core.interfaces.Refiner;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.interfaces.StatisticsProvider;
import org.sosy_lab.cpachecker.core.reachedset.ReachedSet;
import org.sosy_lab.cpachecker.cpa.arg.ARGBasedRefiner;
import org.sosy_lab.cpachecker.cpa.arg.ARGCPA;
import org.sosy_lab.cpachecker.cpa.arg.ARGPath;
import org.sosy_lab.cpachecker.cpa.arg.ARGReachedSet;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;
import org.sosy_lab.cpachecker.cpa.arg.ARGUtils;
import org.sosy_lab.cpachecker.exceptions.CPAException;
import org.sosy_lab.cpachecker.util.AbstractStates;
import org.sosy_lab.cpachecker.util.CPAs;

/**
 * This is Refiner for Slicing Abstractions
 * like in the papers:
 * "Slicing Abstractions" (doi:10.1007/978-3-540-75698-9_2)
 * "Splitting via Interpolants" (doi:10.1007/978-3-642-27940-9_13)
 */

public class SlicingAbstractionsRefiner implements Refiner, StatisticsProvider {

  @SuppressWarnings("unused")
  private final Configuration config;
  private final LogManager logger;
  private final ARGBasedRefiner refiner;
  private final ARGCPA argCpa;

  public SlicingAbstractionsRefiner(Configuration pConfig, LogManager pLogger, ARGBasedRefiner pRefiner, ARGCPA pCpa) {
    this.config = pConfig;
    this.logger = pLogger;
    this.refiner = pRefiner;
    this.argCpa = pCpa;
  }

  public static Refiner create(ConfigurableProgramAnalysis pCpa) throws InvalidConfigurationException {
    PredicateCPA predicateCpa = CPAs.retrieveCPA(pCpa, PredicateCPA.class);
    ARGCPA argCpa = CPAs.retrieveCPA(pCpa,ARGCPA.class);
    if (predicateCpa == null) {
      throw new InvalidConfigurationException(SlicingAbstractionsRefiner.class.getSimpleName() + " needs a PredicateCPA");
    }

    RefinementStrategy strategy = new SlicingAbstractionsStrategy(
        predicateCpa.getConfiguration(),
        predicateCpa.getSolver(),
        predicateCpa.getPredicateManager(),
        predicateCpa.getPathFormulaManager());

    PredicateCPARefinerFactory factory = new PredicateCPARefinerFactory(pCpa);
    ARGBasedRefiner refiner =  factory.create(strategy);
    return new SlicingAbstractionsRefiner(predicateCpa.getConfiguration(), predicateCpa.getLogger(), refiner, argCpa);
  }

  /*
   * (non-Javadoc)
   * @see org.sosy_lab.cpachecker.core.interfaces.Refiner#performRefinement(org.sosy_lab.cpachecker.core.reachedset.ReachedSet)
   */
  @Override
  public boolean performRefinement(ReachedSet pReached) throws CPAException, InterruptedException {
    assert pReached.getWaitlist().size()==0;
    CounterexampleInfo result = null;
    Optional<AbstractState> optionalTargetState;
    while (true) {
      optionalTargetState =  from(pReached).firstMatch(AbstractStates.IS_TARGET_STATE);
      if (optionalTargetState.isPresent()) {
        AbstractState targetState = optionalTargetState.get();
        ARGPath errorPath = ARGUtils.getOnePathTo((ARGState) targetState);
        ARGReachedSet reached = new ARGReachedSet(pReached, argCpa);
        result = refiner.performRefinementForPath(reached, errorPath);
        if (!result.isSpurious()) {
          logger.log(Level.INFO, "Found counterexample!");
          ((ARGState)targetState).addCounterexampleInformation(result);
          // Returning false will make CEGARAlgorithm terminate!
          return false;
        } else {
          if (SlicingAbstractionsUtility.checkProgress(pReached, errorPath) == false) {
            throw new RuntimeException("Progress property is violated!");
          }
        }
      } else {
        break;
      }
    }
    logger.log(Level.INFO, "No target state found in reached-set!");
    // We always return false since we do not want to be restarted
    return false;
  }

  @Override
  public void collectStatistics(Collection<Statistics> pStatsCollection) {
    if (refiner instanceof StatisticsProvider) {
      ((StatisticsProvider) refiner).collectStatistics(pStatsCollection);
    }
  }

}
