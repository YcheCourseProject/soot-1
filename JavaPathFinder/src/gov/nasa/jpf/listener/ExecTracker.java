//
// Copyright (C) 2006 United States Government as represented by the
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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.jvm.bytecode.FieldInstruction;
import gov.nasa.jpf.jvm.bytecode.InstanceFieldInstruction;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.jvm.bytecode.LockInstruction;
import gov.nasa.jpf.jvm.ClassInfo;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.MethodInfo;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.Step;
import java.io.PrintWriter;

import gov.nasa.jpf.util.Left;

/**
 * Listener tool to monitor JPF execution. This class can be used as a drop-in
 * replacement for JPF, which is called by ExecTracker.
 * ExecTracker is mostly a VMListener of 'instructionExecuted' and
 * a SearchListener of 'stateAdvanced' and 'statehBacktracked'
 */
public class ExecTracker extends ListenerAdapter {
  
  boolean printInsn = true;
  boolean printSrc = false;
  boolean printMth = false;
  boolean skipInit = false;
  
  PrintWriter out;
  Step lastStep;
  MethodInfo lastMi;
  String linePrefix;
  
  boolean skip;
  MethodInfo miMain; // just to make init skipping more efficient
  
  public ExecTracker (Config config) {
    printInsn = config.getBoolean("et.print_insn", true);
    printSrc = config.getBoolean("et.print_src", true);
    printMth = config.getBoolean("et.print_mth", false);
    skipInit = config.getBoolean("et.skip_init", true);
    
    if (skipInit) {
      skip = true;
    }
    
    out = new PrintWriter(System.out, true);
  }
  
  /******************************************* SearchListener interface *****/
  
  public void stateRestored(Search search) {
    int id = search.getStateNumber();
    out.println("----------------------------------- [" +
                       search.getDepth() + "] restored: " + id);
  }
    
  //--- the ones we are interested in
  public void searchStarted(Search search) {
    out.println("----------------------------------- search started");
    if (skipInit) {
      ClassInfo ci = search.getVM().getMainClassInfo();
      miMain = ci.getMethod("main([Ljava/lang/String;)V", false);
      
      out.println("      [skipping static init instructions]");
    }
  }

  public void stateAdvanced(Search search) {
    int id = search.getStateNumber();
    
    out.print("----------------------------------- [" +
                     search.getDepth() + "] forward: " + id);
    if (search.isNewState()) {
      out.print(" new");
    } else {
      out.print(" visited");
    }
    
    if (search.isEndState()) {
      out.print(" end");
    }
    
    out.println();
    
    lastStep = null; // in case we report by source line
    lastMi = null;
    linePrefix = null;
  }

  public void stateProcessed (Search search) {
    int id = search.getStateNumber();
    out.println("----------------------------------- [" +
                       search.getDepth() + "] done: " + id);
  }

  public void stateBacktracked(Search search) {
    int id = search.getStateNumber();

    lastStep = null;
    lastMi = null;

    out.println("----------------------------------- [" +
                       search.getDepth() + "] backtrack: " + id);
  }
  
  public void searchFinished(Search search) {
    out.println("----------------------------------- search finished");
  }

  /******************************************* VMListener interface *********/

  public void gcEnd(JVM vm) {
    out.println("\t\t # garbage collection");
  }

  //--- the ones we are interested in
  public void instructionExecuted(JVM jvm) {
    
    if (skip) {
      Instruction insn = jvm.getLastInstruction();
      MethodInfo mi = insn.getMethodInfo();
      if (mi == miMain) {
        skip = false; // start recording
      } else {
        return;  // skip
      }
    }
    
    ThreadInfo ti = jvm.getLastThreadInfo();
    int nNoSrc = 0;
    
    if (linePrefix == null) {
      linePrefix = Integer.toString( ti.getIndex()) + " : ";
    }
    
    // that's pretty redundant to what is done in the ConsolePublisher, but we don't want 
    // presentation functionality in Step anymore
    if (printSrc) {
      Step s = jvm.getLastStep(); // might have been skipped
      if ((s != null) && !s.equals(lastStep)) {
        String line = s.getLineString();
        if (line != null) {
          if (nNoSrc > 0){
            out.println("      [" + nNoSrc + " insn w/o sources]");
          }
          
          if (!s.sameSourceLocation(lastStep)){
            out.print("        ");
            out.print(Left.format(s.getLocationString(),30));
            out.print(" : ");
            out.println(line.trim());
          }
          nNoSrc = 0;
          
        } else { // no source
          nNoSrc++;
        }
      }
      lastStep = s;
    }
    
    if (printInsn) {
      Instruction insn = jvm.getLastInstruction();
      
      if (printMth) {
        MethodInfo mi = insn.getMethodInfo();
        if (mi != lastMi){
          ClassInfo mci = mi.getClassInfo();
          out.print("      ");
          if (mci != null) {
            out.print(mci.getName());
            out.print(".");
          }
          out.println(mi.getUniqueName());
          lastMi = mi;
        }
      }
      
      out.print( linePrefix);
      
      out.print('[');
      out.print(insn.getOffset());
      out.print("] ");
      
      out.print(insn);
        
      // annotate (some of) the bytecode insns with their arguments
      if (insn instanceof InvokeInstruction) {
        MethodInfo callee = ((InvokeInstruction)insn).getInvokedMethod(); 
        if ((callee != null) && callee.isMJI()) { // Huhh? why do we have to check this?
          out.print(" [native]");
        }
      } else if (insn instanceof FieldInstruction) {
        out.print(" ");
        if (insn instanceof InstanceFieldInstruction){
          InstanceFieldInstruction iinsn = (InstanceFieldInstruction)insn;
          out.print(iinsn.getId(iinsn.getLastElementInfo()));
        } else {
          out.print(((FieldInstruction)insn).getVariableId());
        }
      } else if (insn instanceof LockInstruction) {
        out.print(" ");
        out.print(((LockInstruction)insn).getLastLockElementInfo());
      }
      out.println();
    }
  }

  public void threadStarted(JVM jvm) {
    ThreadInfo ti = jvm.getLastThreadInfo();

    out.println( "\t\t # thread started: " + ti.getName() + " index: " + ti.getIndex());
  }

  public void threadTerminated(JVM jvm) {
    ThreadInfo ti = jvm.getLastThreadInfo();
    
    out.println( "\t\t # thread terminated: " + ti.getName() + " index: " + ti.getIndex());
  }
  
  public void notifyExceptionThrown (JVM jvm) {
    ElementInfo ei = jvm.getLastElementInfo();
    MethodInfo mi = jvm.getLastThreadInfo().getMethod();
    out.println("\t\t\t\t # exception: " + ei + " in " + mi);
  }
  
  public void choiceGeneratorAdvanced (JVM jvm) {
    out.println("\t\t # choice: " + jvm.getLastChoiceGenerator());
  }
  
  /****************************************** private stuff ******/

  void filterArgs (String[] args) {
    for (int i=0; i<args.length; i++) {
      if (args[i] != null) {
        if (args[i].equals("-print-lines")) {
          printSrc = true;
          args[i] = null;
        }
      }
    }
  }
}

