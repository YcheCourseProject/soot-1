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
package gov.nasa.jpf.util;

import gov.nasa.jpf.jvm.ThreadInfo;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Misc {
  public static int hashCode(Object o) {
    return o == null ? 0 : o.hashCode();
  }

  public static boolean equal(Object a, Object b) {
    if (a == b) {
      return true;
    } else if (a == null || b == null) {
      // only one could be null
      return false;
    } else {
      return a.equals(b);
    }
  }

  @SuppressWarnings("unchecked")
  public static <E> Iterator<E> emptyIterator() {
    return (Iterator<E>) emptyIterator;
  }

  @SuppressWarnings("unchecked")
  public static <E> Iterable<E> emptyIterable() {
    return (Iterable<E>) emptyIterable;
  }

  public static <E> Iterable<E> iterableFromIterator(Iterator<E> iter) {
    return new Iteratorable<E>(iter);
  }

  public static final Object[] emptyObjectArray = new Object[] {};

  public static final Iterator<?> emptyIterator = new Iterator<Object>() {
    public boolean hasNext () { return false; }
    public Object next () { throw new NoSuchElementException(); }
    public void remove () { throw new NoSuchElementException(); }
  };

  public static final Iterable<?> emptyIterable = new Iterable<Object>() {
    @SuppressWarnings("unchecked")
    public Iterator<Object> iterator () {
      return (Iterator<Object>) emptyIterator;
    }
  };

  public static <E> void addAll(Collection<E> target, Iterable<? extends E> src) {
    for (E e : src) target.add(e);
  }

  public static <T> T[] getAddedElements (T[] oldArray, T[] newArray) {
    
    if (newArray == null || newArray.length == 0) {
      return oldArray; 
    }
    if (oldArray == null || oldArray.length == 0) {
      return newArray;
    }
    
    T[] na = newArray.clone();
    int n = na.length;
    
    for (int i=0; i<na.length; i++) {
      Object eNew = na[i];
      if (eNew != null) {
        for (int j=0; j<oldArray.length; j++) {
          if (eNew.equals(oldArray[j])) {
            na[i] = null;
            n--;
            break;
          }
        }
      } else {
        n--;
      }
    }
    
    Class<?> ct = newArray.getClass().getComponentType();
    T[] addedElements = (T[]) Array.newInstance(ct, n);
    
    for (int i=0, j=0; i<na.length; i++) {
      if (na[i] != null) {
        addedElements[j++] = na[i];
      }
    }
    
    return addedElements;
  }
  
  public static <T> T[] getRemovedElements (T[] oldArray, T[] newArray) {

    if (newArray == null || newArray.length == 0 || oldArray == null || oldArray.length == 0) {
      return null;
    }
    
    T[] oa = oldArray.clone();
    int n = oa.length;
    
    for (int i=0; i<oa.length; i++) {
      Object eOld = oa[i];
      if (eOld != null) {
        for (int j=0; j<newArray.length; j++) {
          if (eOld.equals(newArray[j])) {
            oa[i] = null;
            n--;
            break;
          }
        }
      } else {
        n--;
      }
    }
    
    Class<?> ct = oldArray.getClass().getComponentType();
    T[] addedElements = (T[]) Array.newInstance(ct, n);
    
    for (int i=0, j=0; i<oa.length; i++) {
      if (oa[i] != null) {
        addedElements[j++] = oa[i];
      }
    }
    
    return addedElements;
  }
  
  
  public static <K,V> ArrayList<String> getSortedKeyStrings (HashMap<K,V> map){
    ArrayList<String> list = new ArrayList<String>();

    nextKey:
    for (K key : map.keySet()){
      String ks = key.toString();

      for (int i=0; i<list.size(); i++){
        String k = list.get(i);
        if (ks.compareTo(k) > 0){
          list.add(i, ks);
          continue nextKey;
        }
      }

      list.add(ks);
    }

    return list;
  }

  public static <K,V> ArrayList<Map.Entry<K,V>> createSortedEntryList (HashMap<K,V> map,
                                                                       Comparator<Map.Entry<K,V>> comparer) {
    ArrayList<Map.Entry<K,V>> list = new ArrayList<Map.Entry<K,V>>();

    nextEntry:
      for (Map.Entry<K,V> e : map.entrySet()){

        for (int i=0; i<list.size(); i++){
          if (comparer.compare(e,list.get(i)) > 0) {
            list.add(i, e);
            continue nextEntry;
          }
        }
        list.add(e);
      }


    return list;
  }

  public static <K,V,E> ArrayList<E> createSortedList (HashMap<K,V> map,
                                                    TwoTypeComparator<Map.Entry<K,V>,E> comparer,
                                                    ElementCreator<Map.Entry<K,V>,E> creator) {
    ArrayList<E> list = new ArrayList<E>();

    nextEntry:
      for (Map.Entry<K,V> e : map.entrySet()){

        for (int i=0; i<list.size(); i++){
          if (comparer.compare(e,list.get(i)) > 0) {
            list.add(i, creator.create(e));
            continue nextEntry;
          }
        }
        list.add(creator.create(e));
      }


    return list;
  }

  public static int compare (Integer i1, Integer i2) {
    return Integer.signum(i1.intValue() - i2.intValue());
  }

  public static <E,T> HashMap<E,Integer> createOccurrenceMap (Collection<T> collection,
                                                            ElementCreator<T,E> creator) {
    HashMap<E,Integer> map = new HashMap<E,Integer>();

    for (T o : collection) {
      E e = creator.create(o);
      Integer nE = map.get(e);
      if (nE == null){
        map.put(e,1);
      } else {
        map.put(e,nE.intValue()+1);
      }
    }

    return map;
  }

  public static <T> T createObject (Class<T> cls, Class<?>[] argTypes, Object[] args){
    if (argTypes.length != args.length){
      return null;
    }

    while (argTypes.length >= 0){
      try {
        Constructor<T> ctor = cls.getConstructor(argTypes);
        return ctor.newInstance(args);

      } catch (NoSuchMethodException nsmx){
        Class<?>[] at = new Class<?>[argTypes.length-1];
        System.arraycopy(argTypes, 1, at,0, at.length);
        argTypes = at;

        Object[] a = new Object[at.length];
        System.arraycopy(args,1, a,0, a.length);
        args = a;

      } catch (Throwable t){
        return null;
      }
    }

    return null;
  }

  public static String toString( Iterable<?> collection,
                                 String prefix, String separator, String postfix) {
    StringBuilder sb = new StringBuilder();

    if (prefix != null) {
      sb.append(prefix);
    }

    int i=0;
    for (Object e : collection) {
      if (i++ > 0) {
        sb.append(separator);
      }
      sb.append(e);
    }

    if (postfix != null) {
      sb.append(postfix);
    }

    return sb.toString();
  }

  public static String toString( Object[] array,
                                 String prefix, String separator, String postfix) {
    StringBuilder sb = new StringBuilder();

    if (prefix != null) {
      sb.append(prefix);
    }

    for (int i=0; i<array.length; i++) {
      if (i > 0) {
        sb.append(separator);
      }
      sb.append(array[i]);
    }

    if (postfix != null) {
      sb.append(postfix);
    }

    return sb.toString();
  }


  public static <T> T[] newArray (T... elements) {
    return elements;
  }

  public static <T> T[] appendArray (T[] base, T... elements) {
    if (base == null || base.length == 0){
      return elements;
    } else if (elements == null || elements.length == 0){
      return base;
    } else {
      Class<?> componentType = base.getClass().getComponentType();
      T[] a = (T[]) Array.newInstance(componentType, base.length + elements.length);
      System.arraycopy(base, 0, a, 0, base.length);
      System.arraycopy(elements, 0, a, base.length, elements.length);
      return a;
    }
  }

  public static <T> T[] arrayWithoutFirst( T[] base, int nElements){
    if (base == null){
      return null;
    } else if (nElements >= base.length){
      Class<?> componentType = base.getClass().getComponentType();
      T[] a = (T[]) Array.newInstance(componentType, 0);
      return a;
    } else {
      int n = base.length - nElements;
      Class<?> componentType = base.getClass().getComponentType();
      T[] a = (T[]) Array.newInstance(componentType, n);
      System.arraycopy(base, nElements, a, 0, n);
      return a;
    }
  }


  /*=================== PRIVATE STUFF ===================*/

  private static final class Iteratorable<E> implements Iterable<E> {
    Iterator<E> iter;

    public Iteratorable(Iterator<E> iter) {
      this.iter = iter;
    }

    public Iterator<E> iterator () {
      Iterator<E> ret = iter;
      iter = null;
      return ret;
    }
  }
}
