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
package gov.nasa.jpf.jvm;

import java.util.Random;

import gov.nasa.jpf.Config;
import gov.nasa.jpf.jvm.bytecode.Instruction;
import java.util.LinkedList;
import java.util.List;

/**
* abstract root class for configurable choice generators
*/
public abstract class ChoiceGenerator<T> implements Cloneable {

  // the marker for the current choice used in String conversion
  public static final char MARKER = '>';

  protected static Random random = new Random(42);

  // want the id to be visible to subclasses outside package
  public String id;

  // for subsequent access, there is no need to translate a JPF String object reference
  // into a host VM String anymore (we just need it for creation to look up
  // the class if this is a named CG)
  int idRef;

  // used to cut off further choice enumeration
  protected boolean isDone;

  // we keep a linked list of CG's
  ChoiceGenerator<?> prev;

  // the instruction that created this CG
  Instruction insn;

  // and the thread that executed this insn
  ThreadInfo ti;

  // free attributes (set on demand)
  LinkedList<Object> attrs;


  // in case this is initalized from a JVM context
  public static void init (Config config) {
	  
	  random.setSeed(config.getLong("cg.seed", 42));
	  
	  SystemState.RANDOMIZATION randomization = config.getEnum("cg.randomize_choices",
			  SystemState.RANDOMIZATION.values(), SystemState.RANDOMIZATION.def);
	  
	  // if the randomize_choices is set to random then we need to 
	  // pick the seed based on the system time. 
	  
	  if (randomization == SystemState.RANDOMIZATION.random) {
		  random.setSeed(System.currentTimeMillis());
	  } 
  }
 

  protected ChoiceGenerator () {
    this.id = "-";
  }

  protected ChoiceGenerator (String id) {
    this.id = id;
  }

  public abstract T getNextChoice();
  public abstract Class<T> getChoiceType();

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public String getId () {
    return id;
  }

  public int getIdRef () {
    return idRef;
  }

  public void setIdRef (int idRef) {
    this.idRef = idRef;
  }

  public void setId (String id) {
    this.id = id;
  }

  //--- the getters and setters for the CG creation info
  public void setThreadInfo (ThreadInfo ti) {
    this.ti = ti;
  }
  public ThreadInfo getThreadInfo () {
    return ti;
  }
  public void setInsn (Instruction insn) {
    this.insn = insn;
  }
  public Instruction getInsn () {
    return insn;
  }

  public String getSourceLocation() {
    return insn.getSourceLocation();
  }

  public void setPreviousChoiceGenerator (ChoiceGenerator<?> cg) {
    prev = cg;
  }

  public ChoiceGenerator<?> getPreviousChoiceGeneratorOfType(Class<?> cls) {
    ChoiceGenerator<?> cg = prev;

    while (cg != null) {
      if (cls.isInstance(cg)){
        return cg;
      }
      cg = cg.prev;
    }
    return null;
  }

  public abstract boolean hasMoreChoices ();

  /**
   * advance to the next choice
   */
  public abstract void advance ();

  /**
   * advance n choices
   * pretty braindead generic solution, but if more speed is needed, we can easily override
   * in the concrete CGs (it's used for path replay)
   */
  public void advance (int nChoices) {
    while (nChoices-- > 0) {
      advance();
    }
  }

  public void select (int nChoice) {
    advance(nChoice);
    setDone();
  }

  public boolean isDone () {
    return isDone;
  }

  public void setDone() {
    isDone = true;
  }

  public abstract void reset ();

  public abstract int getTotalNumberOfChoices ();

  public abstract int getProcessedNumberOfChoices ();


  //--- the generic attribute API (one attr per type)

  public void setAttr(Object a){
    if (a != null){
      if (attrs == null) {
        attrs = new LinkedList<Object>();
        attrs.add(a);

      } else {

        // replace if we already have such a type
        Class<?> aClass = a.getClass();
        int nAttrs = attrs.size();
        for (int i=0; i<nAttrs; i++){
          Object aa = attrs.get(i);
          if (aa.getClass() == aClass){
            attrs.set(i, a);
            return;
          }
        }

        // add if there was none
        attrs.add(a);
      }
    }
  }

  public List<?> getAttrs(){
    return attrs;
  }

  public <T> T getAttr (Class<T> type){
    if (attrs != null){
      for (Object a : attrs){
        if (a.getClass() == type) {
          return (T) a;
        }
      }
    }

    return null;
  }

  public void removeAttr (Object a){
    if (a != null && attrs != null){
      int nAttrs = attrs.size();
      for (int i = 0; i < nAttrs; i++) {
        Object aa = attrs.get(i);
      }
    }
  }


  public String toString () {
    StringBuilder b = new StringBuilder( getClass().getName());
    b.append(" {id:\"");
    b.append(id);
    b.append("\" ,");
    b.append(getProcessedNumberOfChoices());
    b.append('/');
    b.append(getTotalNumberOfChoices());

    if (attrs != null){
      b.append(", attrs:[");
      int i=0;
      for (Object a: attrs){
        if (i++ > 1){
          b.append(',');
        }
        b.append(a);
      }
      b.append(']');
    }

    b.append('}');

    return b.toString();
  }

  public ChoiceGenerator<?> getPreviousChoiceGenerator() {
    return prev;
  }

  /**
   * turn the order of choices random (if it isn't already). Only
   * drawback of this generic method (which might be a decorator
   * factory) is that our abstract type layer (e.g. IntChoiceGenerator)
   * has to guarantee type safety. But hey - this is the first case where
   * we can use covariant return types!
   *
   * NOTES:
   * - this method may alter this ChoiceGenerator and return that or return
   * a new "decorated" version.
   * - random data can be read from the "Random random" field in this class.
   */
  public abstract ChoiceGenerator<?> randomize ();
}
