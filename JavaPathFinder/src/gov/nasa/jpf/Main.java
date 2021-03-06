//
// Copyright (C) 2009 United States Government as represented by the
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
package gov.nasa.jpf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * this class is a wrapper for starting JPF so that it sets the classpath
 * automatically from the configures JPF extensions
 */
public class Main {

  public static void main (String[] args) {

    String appClass = "gov.nasa.jpf.JPF";

    if (args.length > 1 && args[0].equals("-a")){
      appClass = checkClassName(args[1]);
      if (appClass == null){
        System.err.println("error: not a valid class name: " + args[1]);
      }

      String[] a = new String[args.length - 2];
      System.arraycopy(args, 2, a, 0, a.length);
      args = a;
    }

    try {
      JPFClassLoader cl = new JPFClassLoader(args);
      Class<?> jpfCls = cl.loadClass( appClass);

      Class<?>[] argTypes = { String[].class };
		  Method mainMth = jpfCls.getMethod("main", argTypes);

      int modifiers = mainMth.getModifiers();
      if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)){
        System.err.println("no \"public static void main(String[])\" method in" + appClass);
        return;
      }

      mainMth.invoke(null, new Object[] { args });

    } catch (ClassNotFoundException cnfx){
      System.err.println("error: cannot find " + appClass);
    } catch (NoSuchMethodException nsmx){
      System.err.println("error: no main(String[]) method found in " + appClass);
    } catch (IllegalAccessException iax){
      // we already checked for that, but anyways
      System.err.println("no \"public static void main(String[])\" method in " + appClass);
    } catch (InvocationTargetException ix) {
      ix.getCause().printStackTrace();
      // should already be reported by JPF
    } catch (JPFClassLoaderException ex){
      System.err.println(ex);
    }
  }

  private static String checkClassName (String cls){
    if (cls == null || cls.isEmpty()){
      return null;
    }

    if (cls.charAt(0) == '.'){
      cls = "gov.nasa.jpf" + cls;
    }
    
    return cls;
  }
}
