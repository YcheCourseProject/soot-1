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

package gov.nasa.jpf.jvm;

import org.apache.bcel.classfile.AnnotationEntry;

/**
 * common root for ClassInfo, MethodInfo, FieldInfo (and maybe more to follow)
 * 
 * so far, it's used to factorize the annotation support, but we can also
 * move the attributes up here
 */
public abstract class InfoObject {

  // he number of annotations per class/method/field is usually
  // small enough so that simple arrays are more efficient than HashMaps
  AnnotationInfo[] annotations;

  protected void loadAnnotations (AnnotationEntry[] ae){
    
    if ((ae != null) && (ae.length > 0)){
      AnnotationInfo[] ai = new AnnotationInfo[ae.length];
      for (int i=0; i<ae.length; i++){
        ai[i] = new AnnotationInfo(ae[i]);
      }
      
      annotations = ai;
    }
  }
  
  public AnnotationInfo[] getAnnotations() {
    return annotations;
  }
  
  public AnnotationInfo getAnnotation (String name){
    AnnotationInfo[] ai = annotations;
    if (ai != null){
      for (int i=0; i<ai.length; i++){
        if (ai[i].getName().equals(name)){
          return ai[i];
        }
      }
    }
    return null;
  }

  /**
   * return the ClassInfo this object represents or belongs to
   */
  public abstract ClassInfo getClassInfo();
}
