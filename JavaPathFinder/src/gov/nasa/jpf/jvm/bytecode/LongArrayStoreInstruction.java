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
package gov.nasa.jpf.jvm.bytecode;

import gov.nasa.jpf.jvm.ArrayIndexOutOfBoundsExecutiveException;
import gov.nasa.jpf.jvm.ElementInfo;
import gov.nasa.jpf.jvm.ThreadInfo;


/**
 * absraction for long array stores
 *
 * ... array, index, long-value => ...
 */
public abstract class LongArrayStoreInstruction extends ArrayStoreInstruction {
  protected void setField (ElementInfo e, int index, long value)
                    throws ArrayIndexOutOfBoundsExecutiveException {
    e.checkLongArrayBounds(index);
    e.setLongElement(index, value);
  }

  protected int getElementSize () {
    return 2;
  }

  protected long getValue (ThreadInfo th) {
    return th.longPop();
  }
  
  protected int peekArrayRef(ThreadInfo ti) {
    return ti.peek(3);  // ..,ref,idx,long(value)
  }

  protected int peekIndex(ThreadInfo ti){
    return ti.peek(2);
  }
}
