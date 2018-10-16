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
package org.sosy_lab.cpachecker.intelligence;

import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import org.sosy_lab.common.configuration.AnnotatedValue;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.intelligence.learn.IRankLearner;
import org.sosy_lab.cpachecker.intelligence.learn.RPCLearner;
import org.sosy_lab.cpachecker.intelligence.learn.binary.LinearPretrainedType;
import org.sosy_lab.cpachecker.intelligence.learn.binary.PredictorBatchBuilder;
import org.sosy_lab.cpachecker.intelligence.learn.sample.FeatureRegistry;
import org.sosy_lab.cpachecker.intelligence.learn.sample.IProgramSample;
import org.sosy_lab.cpachecker.intelligence.learn.sample.SampleRegistry;

@Options(prefix="linearOracle")
public class LinearPredictiveOracle implements IConfigOracle {

  @Option(secure=true,
          description = "file path to label-path mapping")
  private String labelPath = null;

  @Option(secure = true,
          description = "pretrained parameter of linear SVM")
  private String pretrained = null;


  private Map<String, AnnotatedValue<Path>> labelToPath;
  private IProgramSample currentSample;
  private LogManager logger;


  private List<String> labelRanking;
  private List<AnnotatedValue<Path>> unknown = new ArrayList<>();
  private int pos = 0;


  public LinearPredictiveOracle(LogManager pLogger, Configuration config, List<AnnotatedValue<Path>> configPaths, CFA pCFA)
      throws InvalidConfigurationException {

    SampleRegistry registry = new SampleRegistry(
        new FeatureRegistry(), 0, 5
    );

    init(pLogger, config, configPaths, registry.registerSample("randId", pCFA));
  }

  LinearPredictiveOracle(LogManager pLogger, Configuration config, List<AnnotatedValue<Path>> configPaths, IProgramSample pSample)
      throws InvalidConfigurationException {
      init(pLogger, config, configPaths, pSample);
  }

  private void init(LogManager pLogger, Configuration pConfiguration, List<AnnotatedValue<Path>> configPaths, IProgramSample pSample)
      throws InvalidConfigurationException {
    pConfiguration.inject(this);

    this.logger = pLogger;

    initLabelToPath(configPaths);

    currentSample = pSample;
  }

  private void initLabelToPath(List<AnnotatedValue<Path>> list){
    //TODO: Support loading

    if(labelPath == null) {
      Map<String, String> revLabel = new HashMap<>();
      revLabel.put("svcomp18--01-valueAnalysis.properties", "VA-NoCegar");
      revLabel.put("svcomp18--02-valueAnalysis-itp.properties", "VA-Cegar");
      revLabel.put("svcomp18--03-predicateAnalysis.properties", "PA");
      revLabel.put("svcomp18--04-kInduction.properties", "KI");
      revLabel.put("svcomp18--recursion.properties", "BAM");
      revLabel.put("svcomp18--bmc.properties", "BMC");

      labelToPath = new HashMap<>();

      for(AnnotatedValue<Path> p: list){

        String n = p.value().getFileName().toString();

        if(!revLabel.containsKey(n)){
          unknown.add(p);
          continue;
        }

        labelToPath.put(revLabel.get(n), p);
      }
    }

  }

  private void initRanking(){
    if(labelRanking != null)return;

    long time = System.currentTimeMillis();

    PredictorBatchBuilder batchBuilder = new PredictorBatchBuilder(
        new LinearPretrainedType(pretrained), null
    );

    List<IProgramSample> samples = Arrays.asList(currentSample);

    IRankLearner learner = new RPCLearner(batchBuilder.build());
    labelRanking = learner.predict(samples).get(0);

    logger.log(Level.INFO, "Finished ranking after "+ (System.currentTimeMillis() - time)+ "ms");
    logger.log(Level.INFO, "Predicted ranking: "+labelRanking.toString());
  }


  private AnnotatedValue<Path> get(int i){
    initRanking();
    if(i >= labelRanking.size()){
      i = i - labelRanking.size();
      if(i < unknown.size()){
        return unknown.get(i);
      }
    }else{
      String n = labelRanking.get(i);
      if(!labelToPath.containsKey(n)){
        logger.log(Level.INFO, "Unknown label "+n+". Skip.");
        return get(++i);
      }

      return labelToPath.get(n);
    }
    throw new NoSuchElementException();
  }

  @Override
  public AnnotatedValue<Path> peek() {
    return get(pos);
  }

  @Override
  public boolean hasNext() {
    initRanking();
    return pos < labelRanking.size() + unknown.size();
  }

  @Override
  public AnnotatedValue<Path> next() {
    return get(pos++);
  }

  @Override
  public void remove() {
    throw new IllegalStateException();
  }
}