//
// Copyright (C) 2008 United States Government as represented by the
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

import java.io.PrintWriter;
import java.util.HashMap;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.StackFrame;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.ReturnInstruction;
import gov.nasa.jpf.jvm.bytecode.VirtualInvocation;
import gov.nasa.jpf.report.ConsolePublisher;
import gov.nasa.jpf.report.Publisher;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.StringSetMatcher;

/**
 * analyzes call/execute sequences of methods
 * closely modeled after the DeadlockAnalyzer, i.e. keeps it's own
 * log and doesn't require full instruction trace
 * 
 * <2do> this needs to be refactored with DeadlockAnalyzer - the whole
 * trace mgnt (except of the printing) can be made generic
 */
public class MethodAnalyzer extends ListenerAdapter {
  
  enum OpType { CALL, CALL_EXECUTE, EXECUTE, RETURN };
  static String[] opTypeMnemonic = { "C", "X", "E", "R" };

  static class MethodOp {
    OpType type;
    
    ThreadInfo ti;
    ElementInfo ei;
    Instruction insn; // the caller
    MethodInfo mi;    // the callee
    int stackDepth;
    
    // this is used to keep our own trace
    int stateId = Integer.MIN_VALUE;
    MethodOp prevTransition;
    MethodOp p;   // prev during execution
    
    MethodOp (OpType type, MethodInfo mi, ThreadInfo ti, ElementInfo ei, int stackDepth){
      this.type = type;
      this.ti = ti;
      this.mi = mi;
      this.ei = ei;
      this.stackDepth = stackDepth;
    }
    
    void printRawOn(PrintWriter pw) {
      pw.print(ti.getIndex());
      pw.print(": ");
      
      for (int i=0; i<stackDepth; i++) {
        pw.print('.');
      }
      
      pw.print(opTypeMnemonic[type.ordinal()]);
      pw.print(' ');
      pw.print(mi.getFullName());
      if (ei != null) {
        pw.print(", ");
        pw.print(ei);
      }
    }
    
    public String toString() {
      return "Op {" + ti.getName() + ',' + opTypeMnemonic[type.ordinal()] +
                   ',' + mi.getFullName() + ',' + ei + '}';
    }
  }
  
  StringSetMatcher includes = null; //  means all
  StringSetMatcher excludes = null; //  means none
  
  int maxHistory;
  String format;
  boolean skipInit;
  boolean showDepth;
  boolean showTransition;
  
  JVM vm;
  Search search;

  OpType opType;
  
  // this is used to keep our own trace
  MethodOp lastOp;
  MethodOp lastTransition;
  boolean isFirstTransition = true;

  // this is set after we call revertAndFlatten during reporting
  // (we can't call revertAndFlatten twice since it is destructive, but
  // we might have to report several times in case we have several publishers)
  MethodOp firstOp = null;
  
  // for HeuristicSearches. Ok, that's braindead but at least no need for cloning
  HashMap<Integer,MethodOp> storedTransition = new HashMap<Integer,MethodOp>();

  
  public MethodAnalyzer (Config config, JPF jpf){
    jpf.addPublisherExtension(ConsolePublisher.class, this);
    
    maxHistory = config.getInt("method.max_history", Integer.MAX_VALUE);
    format = config.getString("method.format", "raw");
    skipInit = config.getBoolean("method.skip_init", true);
    showDepth = config.getBoolean("method.show_depth", false);
    showTransition = config.getBoolean("method.show_transition", false);
    
    includes = StringSetMatcher.getNonEmpty(config.getStringArray("method.include"));
    excludes = StringSetMatcher.getNonEmpty(config.getStringArray("method.exclude"));
    
    vm = jpf.getVM();
    search = jpf.getSearch();
    
    
  }
  
  void addOp (JVM vm, OpType opType, MethodInfo mi, ThreadInfo ti, ElementInfo ei, int stackDepth){
    if (!(skipInit && isFirstTransition)) {
      MethodOp op = new MethodOp(opType, mi, ti, ei, stackDepth);
      if (lastOp == null){
        lastOp = op;
      } else {
        op.p = lastOp;
        lastOp = op;
      }
    }
  }
  
  boolean isAnalyzedMethod (MethodInfo mi){
    String mthName = mi.getFullName();
    return StringSetMatcher.isMatch(mthName, includes, excludes);
  }
  
  void printRaw (PrintWriter pw) {
    MethodOp start = firstOp;
    
    int lastStateId  = Integer.MIN_VALUE;
    int transition = skipInit ? 1 : 0;
    int lastTid = start.ti.getIndex();
    
    for (MethodOp op = start; op != null; op = op.p) {
      if (showTransition) {
        if (op.stateId != lastStateId) {
          lastStateId = op.stateId;
          pw.print("------------------------------------------ #");
          pw.println(transition++);
        }
      } else {
        int tid = op.ti.getIndex();
        if (tid != lastTid) {
          lastTid = tid;
          pw.println("------------------------------------------");
        }
      }
      
      op.printRawOn(pw);
      pw.println();
    }
  }
  
  // warning - this rotates pointers in situ, i.e. destroys the original structure
  MethodOp revertAndFlatten (MethodOp start) {

    MethodOp last = null;
    MethodOp prevTransition = start.prevTransition;

    for (MethodOp op = start; op != null;) {
      MethodOp opp = op.p;
      op.p = last;
      
      if (opp == null) {
        if (prevTransition == null) {
          return op;
        } else {
          last = op;
          op = prevTransition;
          prevTransition = op.prevTransition;
        }
      } else {
        last = op;
        op = opp;
      }
    }

    return null;
  }
  
  //--- SearchListener interface
  // <2do> this is the same as DeadlockAnalyzer, except of xxOp type -> refactor
  
  public void stateAdvanced (Search search){
    
    if (search.isNewState() && (lastOp != null)) {
      int stateId = search.getStateNumber();
      
      for (MethodOp op=lastOp; op != null; op=op.p) {
        op.stateId = stateId;
      }
      
      lastOp.prevTransition = lastTransition;
      lastTransition = lastOp;
    }
    
    lastOp = null;
    isFirstTransition = false;
  }
  
  public void stateBacktracked (Search search){
    int stateId = search.getStateNumber();
    while ((lastTransition != null) && (lastTransition.stateId > stateId)){
      lastTransition = lastTransition.prevTransition;
    }
    lastOp = null;
  }
  
  
  public void stateStored (Search search) {
    // always called after stateAdvanced
    storedTransition.put(search.getStateNumber(), lastTransition);
  }
  
  public void stateRestored (Search search) {
    int stateId = search.getStateNumber();
    MethodOp op = storedTransition.get(stateId);
    if (op != null) {
      lastTransition = op;
      storedTransition.remove(stateId);  // not strictly required, but we don't come back
    }
  }

  //--- VMlistener interface
  
  public void instructionExecuted (JVM vm) {
    Instruction insn = vm.getLastInstruction();
    ThreadInfo ti;
    MethodInfo mi;
    ElementInfo ei = null;
    
    if (insn instanceof InvokeInstruction) {
      InvokeInstruction call = (InvokeInstruction)insn;
      ti = vm.getLastThreadInfo();
      mi = call.getInvokedMethod(ti);
            
      if (isAnalyzedMethod(mi)) {
        OpType type;

        // check if this was actually executed, or is a blocked sync call
        if (ti.getNextPC() == call) { // re-executed -> blocked or overlayed
          type = OpType.CALL;

        } else { // executed
          if (ti.isFirstStepInsn()) {
            type = OpType.EXECUTE;
          } else {
            type = OpType.CALL_EXECUTE;
          }
        }

        if (!mi.isStatic()) {
          if (call instanceof VirtualInvocation) {
            ei = ((VirtualInvocation)call).getThisElementInfo(ti);
          }
        }
        
        addOp(vm,type,mi,ti,ei, ti.getStackDepth());
      }
      
    } else if (insn instanceof ReturnInstruction) {
      ReturnInstruction ret = (ReturnInstruction)insn;
      ti = vm.getLastThreadInfo();
      StackFrame frame = ret.getReturnFrame();
      mi = frame.getMethodInfo();

      if (isAnalyzedMethod(mi)) {
        if (!mi.isStatic()) {
          int ref = frame.getThis();
          if (ref != -1) {
            ei = ti.getElementInfo(ref);
          }
        }
        
        addOp(vm,OpType.RETURN,mi,ti,ei, ti.getStackDepth()+1); // postExec-> frame already popped
      }
    }
  }
  
  //--- the PubisherExtension part
  
  public void publishPropertyViolation (Publisher publisher) {

    if (firstOp == null && lastTransition != null){
      firstOp = revertAndFlatten(lastTransition);
    }

    PrintWriter pw = publisher.getOut();
    publisher.publishTopicStart("method ops " + publisher.getLastErrorId());
    printRaw(pw);
  }
}
