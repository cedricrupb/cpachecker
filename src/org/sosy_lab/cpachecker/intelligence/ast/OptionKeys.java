/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2019  Dirk Beyer
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
package org.sosy_lab.cpachecker.intelligence.ast;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.checkerframework.checker.units.qual.K;
import org.sosy_lab.cpachecker.cfa.ast.c.CDeclaration;
import org.sosy_lab.cpachecker.cfa.ast.c.CExpression;
import org.sosy_lab.cpachecker.cfa.ast.c.CStatement;
import org.sosy_lab.cpachecker.intelligence.graph.analysis.pointer.PEGraph;
import org.sosy_lab.cpachecker.intelligence.graph.model.Options;
import org.sosy_lab.cpachecker.intelligence.graph.model.Options.Key;
import org.sosy_lab.cpachecker.intelligence.graph.model.StructureGraph;
import org.sosy_lab.cpachecker.util.Pair;

public class OptionKeys {

  public static final Options.Key<Boolean> TRUTH = new Key<>("truth");
  public static final Options.Key<Integer> RPO = new Key<>("reversePostorder");
  public static final Options.Key<Integer> START_POS = new Key<>("start_position");
  public static final Options.Key<Integer> END_POS = new Key<>("end_position");

  public static final Options.Key<Set<String>> VARS = new Key<>("variables");

  public static final Options.Key<Set<String>> DECL_VARS = new Key<>("output");

  public static final Options.Key<CExpression> CEXPR = new Key<>("expression");
  public static final Options.Key<CDeclaration> CDECL = new Key<>("declaration");
  public static final Options.Key<String> FUNC_CALL = new Key<>("function_call");
  public static final Options.Key<String> FUNC_NAME = new Key<>("function_name");
  public static final Options.Key<String> PARENT_FUNC = new Key<>("global.parent");
  public static final Options.Key<List<CExpression>> ARGS = new Key<>("function_args");
  public static final Options.Key<CStatement> CSTATEMENT = new Key<>("statement");

  public static final Options.Key<StructureGraph> AST = new Key<>("ast_tree");
  public static final Options.Key<String> AST_ROOT = new Key<>("ast_root");
  public static final Options.Key<Boolean> REPLACE_ID = new Key<>("global.replace_id");

  public static final Options.Key<Set<String>> INVOKED_FUNCS = new Key<>("global.invokes");
  public static final Options.Key<Map<String, Pair<String, String>>> FUNC_BOUNDRY = new Key<>("global.func_boundry");
  public static final Options.Key<PEGraph> POINTER_GRAPH = new Key<>("global.pointer_graph");
  public static final Options.Key<Boolean> RECURSION = new Key<>("global.recursion");
  public static final Options.Key<Boolean> CONCURRENCY = new Key<>("global.concurrency");
  public static final Options.Key<Boolean> POINTER = new Key<>("global.pointer");

  public static final Options.Key<Boolean> PROCESSED = new Key<>("processed");
}
