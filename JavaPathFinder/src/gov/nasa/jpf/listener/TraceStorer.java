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

import gov.nasa.jpf.Config;
import gov.nasa.jpf.JPF;
import gov.nasa.jpf.ListenerAdapter;
import gov.nasa.jpf.jvm.JVM;
import gov.nasa.jpf.jvm.ThreadInfo;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import gov.nasa.jpf.jvm.bytecode.InvokeInstruction;
import gov.nasa.jpf.search.Search;
import gov.nasa.jpf.util.StringSetMatcher;

/**
 * tool to save traces upon various conditions like
 *  - property violation
 *  - call of a certain method
 *  - reaching a certain search depth
 *  - creating a certain thread
 */
public class TraceStorer extends ListenerAdapter {

  int nTrace = 1; 

  String traceFileName;
  
  // do we store to the same file? (i.e. overwrite previously stored traces)
  // if set to 'true', store all traces (in <traceFileName>.n)
  boolean storeMultiple;
  
  // do we want to terminate after first store, even if it's triggered by a
  // property violation?
  boolean terminateOnStore;
  
  // search depth at what we store the tace 
  int storeDepth;
  
  // calls that should trigger a store
  StringSetMatcher storeCalls;
  
  // thread starts that should trigger a store
  StringSetMatcher storeThreads;
  
  // do we want verbose output
  boolean verbose;
  
  Search search;
  JVM vm;
  
  public TraceStorer (Config config, JPF jpf){
    
    traceFileName = config.getString("trace.file", "trace");
    storeMultiple = config.getBoolean("trace.multiple", false);    
    storeDepth = config.getInt("trace.depth", Integer.MAX_VALUE);
    verbose = config.getBoolean("trace.verbose", false);
    
    terminateOnStore = config.getBoolean("trace.terminate", false);
    
    storeCalls = StringSetMatcher.getNonEmpty(config.getStringArray("trace.store_calls"));
    storeThreads = StringSetMatcher.getNonEmpty(config.getStringArray("trace.store_threads"));
    
    vm = jpf.getVM();
    search = jpf.getSearch();
  }
  
  void storeTrace(String reason) {
    String fname = traceFileName;
    
    if (storeMultiple){
      fname = fname  + '.' + nTrace++;
    }
    
    vm.storeTrace(fname, reason, verbose); // <2do> maybe some comment would be in order
  }
  
  public void propertyViolated (Search search){
    // Ok, this is unconditional
    storeTrace("violated property: " + search.getLastError().getDetails());
    
    // no need to terminate (and we don't want to interfere with search.multiple_errors)
  }
 
  public void stateAdvanced (Search search){
    if (search.getDepth() >= storeDepth){
      storeTrace("search depth reached: " + storeDepth);
      checkSearchTermination();
    }
  }
  
  public void instructionExecuted (JVM vm){
    if (storeCalls != null){
      Instruction insn = vm.getLastInstruction();
      if (insn instanceof InvokeInstruction) {
        InvokeInstruction iinsn = (InvokeInstruction)insn;
        String clsName = iinsn.getInvokedMethodClassName();
        String mthName = iinsn.getInvokedMethodName();
        String mn = clsName + '.' + mthName;
        
        if (storeCalls.matchesAny(mn)){
          storeTrace("call: " + mn);
          checkVMTermination();
        }
      }
    }
  }
  
  public void threadStarted(JVM vm) {
    if (storeThreads != null){
      ThreadInfo ti = vm.getLastThreadInfo();
      String tname = ti.getName();
      if (storeThreads.matchesAny( tname)){
        storeTrace("thread started: " + tname);
        checkVMTermination();
      }
    } 
  }

  boolean checkVMTermination() {
    if (terminateOnStore){
      vm.getLastThreadInfo().breakTransition();
      search.terminate();
      return true;
    }
    
    return false;
  }
  
  boolean checkSearchTermination() {
    if (terminateOnStore){
      search.terminate();
      return true;
    }
    
    return false;
  }
}
