//
// Copyright (C) 2007 United States Government as represented by the
// Administrator of the National Aeronautics and Space Administration
// (NASA).  All Rights Reserved.
//
// This software is distributed under the NASA Open Source Agreement
// (NOSA), version 1.3.  The NOSA has been approved by the Open Source
// Initiative.  See the file NOSA-1.3-JPF at the top of the distribution
// directory tree for the complete NOSA document.
//
// THE SUBJECT SOFTWARE IS PROVIDED "AS IS" WITHOUT ANY WARRANTY OF ANY
// KIND, EITHER EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT
// LIMITED TO, ANY WARRANTY THAT THE SUBJECT SOFTWARE WILL CONFORM TO
// SPECIFICATIONS, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR
// A PARTICULAR PURPOSE, OR FREEDOM FROM INFRINGEMENT, ANY WARRANTY THAT
// THE SUBJECT SOFTWARE WILL BE ERROR FREE, OR ANY WARRANTY THAT
// DOCUMENTATION, IF PROVIDED, WILL CONFORM TO THE SUBJECT SOFTWARE.
//

package gov.nasa.jpf.listener;

import java.util.Random;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.ChoiceGenerator;
import gov.nasa.jpf.jvm.ChoicePoint;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.StringSetMatcher;

/**
 * this is a listener that only executes single choices until it detects
 * that it should start to search. If nothing is specified, this is pretty
 * much a simulator that randomly picks choices. Otherwise the user can
 * give it any combination of
 *  - a set of thread names
 *  - a set of method names
 *  - a start search depth
 * to turn on the search. If more than one condition is given, all have to be
 * satisfied
 */
public class ChoiceSelector extends ListenerAdapter {

  Random random;
  boolean singleChoice = true;

  // those are our singleChoice end conditions (i.e. where we start the search)
  StringSetMatcher threadSet; // we start when all threads in the set are active
  boolean threadsAlive = true;;

  StringSetMatcher calls; // .. when any of the methods is called
  boolean callSeen = true;

  int startDepth; // .. when reaching this depth
  boolean depthReached = true;

  // set if we replay a trace
  ChoicePoint trace;

  // start the search when reaching the end of the stored trace
  boolean searchAfterTrace;

  public ChoiceSelector (Config config, JPF jpf) {
    random = new Random( config.getInt("choice.seed", 42));

    threadSet = StringSetMatcher.getNonEmpty(config.getStringArray("choice.threads"));
    if (threadSet != null) {
      threadsAlive = false;
    }

    calls = StringSetMatcher.getNonEmpty(config.getStringArray("choice.calls"));
    callSeen = false;

    startDepth = config.getInt("choice.depth", -1);
    if (startDepth != -1) {
      depthReached = false;
    }

    // if nothing was specified, we just do single choice (simulation)
    if ((threadSet == null) && (calls == null) && (startDepth == -1)) {
      threadsAlive = false;
      callSeen = false;
      depthReached = false;
    }

    JVM vm = jpf.getVM();
    trace = ChoicePoint.readTrace(config.getString("choice.use_trace"),
                                       vm.getMainClassName(), vm.getArgs());
    searchAfterTrace = config.getBoolean("choice.search_after_trace", true);
    vm.setTraceReplay(trace != null);
  }

  void checkSingleChoiceCond() {
    singleChoice = !(depthReached && callSeen && threadsAlive);
  }

  public void choiceGeneratorAdvanced (JVM vm) {
    ChoiceGenerator<?> cg = vm.getLastChoiceGenerator();
    int n = cg.getTotalNumberOfChoices();

    if (trace != null) { // this is a replay

      // <2do> maybe that should just be a warning, and then a single choice
      assert cg.getClass().getName().equals(trace.getCgClassName()) :
        "wrong choice generator class, expecting: " + trace.getCgClassName()
        + ", read: " + cg.getClass().getName();

      cg.select(trace.getChoice());

    } else {
      if (singleChoice) {
        if (n > 1) {
          int r = random.nextInt(n);
          cg.select(r); // sets it done, so we never backtrack into it
        }
      }
    }
  }

  public void threadStarted(JVM vm) {
    if (singleChoice && (threadSet != null)) {
      ThreadInfo ti = vm.getLastThreadInfo();
      String tname = ti.getName();
      if (threadSet.matchesAny( tname)){
        threadsAlive = true;
        checkSingleChoiceCond();
      }
    }
  }

  public void executeInstruction(JVM vm) {
    if (singleChoice && !callSeen && (calls != null)) {
      Instruction insn = vm.getLastInstruction();
      ThreadInfo ti = vm.getLastThreadInfo();
      if (insn instanceof InvokeInstruction) {
        String mthName = ((InvokeInstruction)insn).getInvokedMethod(ti).getBaseName();

        if (calls.matchesAny(mthName)){
          callSeen = true;
          checkSingleChoiceCond();
        }
      }
    }
  }

  public void stateAdvanced(Search search) {

    if (trace != null) {
      // there is no backtracking or restoring as long as we replay
      trace = trace.getNext();

      if (trace == null){
        search.getVM().setTraceReplay(false);
        if (searchAfterTrace){
          singleChoice = false;
        }
      }

    } else {
      if (singleChoice && !depthReached && (startDepth >= 0)) {
        if (search.getDepth() == startDepth) {
          depthReached = true;
          checkSingleChoiceCond();
        }
      }
    }
  }

}
