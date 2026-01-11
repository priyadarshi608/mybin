package org.apache.log4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.ThreadLocalMap;






































public class MDC
{
  static final MDC mdc = new MDC();
  
  static final int HT_SIZE = 7;
  
  boolean java1;
  
  Object tlm;
  
  private Method removeMethod;
  
  private MDC()
  {
    java1 = Loader.isJava1();
    if (!java1) {
      tlm = new ThreadLocalMap();
    }
    try
    {
      removeMethod = ThreadLocal.class.getMethod("remove", null);
    }
    catch (NoSuchMethodException e) {}
  }
  











  public static void put(String key, Object o)
  {
    if (mdc != null) {
      mdc.put0(key, o);
    }
  }
  






  public static Object get(String key)
  {
    if (mdc != null) {
      return mdc.get0(key);
    }
    return null;
  }
  






  public static void remove(String key)
  {
    if (mdc != null) {
      mdc.remove0(key);
    }
  }
  




  public static Hashtable getContext()
  {
    if (mdc != null) {
      return mdc.getContext0();
    }
    return null;
  }
  




  public static void clear()
  {
    if (mdc != null) {
      mdc.clear0();
    }
  }
  

  private void put0(String key, Object o)
  {
    if ((java1) || (tlm == null)) {
      return;
    }
    Hashtable ht = (Hashtable)((ThreadLocalMap)tlm).get();
    if (ht == null) {
      ht = new Hashtable(7);
      ((ThreadLocalMap)tlm).set(ht);
    }
    ht.put(key, o);
  }
  

  private Object get0(String key)
  {
    if ((java1) || (tlm == null)) {
      return null;
    }
    Hashtable ht = (Hashtable)((ThreadLocalMap)tlm).get();
    if ((ht != null) && (key != null)) {
      return ht.get(key);
    }
    return null;
  }
  


  private void remove0(String key)
  {
    if ((!java1) && (tlm != null)) {
      Hashtable ht = (Hashtable)((ThreadLocalMap)tlm).get();
      if (ht != null) {
        ht.remove(key);
        
        if (ht.isEmpty()) {
          clear0();
        }
      }
    }
  }
  

  private Hashtable getContext0()
  {
    if ((java1) || (tlm == null)) {
      return null;
    }
    return (Hashtable)((ThreadLocalMap)tlm).get();
  }
  

  private void clear0()
  {
    if ((!java1) && (tlm != null)) {
      Hashtable ht = (Hashtable)((ThreadLocalMap)tlm).get();
      if (ht != null) {
        ht.clear();
      }
      if (removeMethod != null) {
        try
        {
          removeMethod.invoke(tlm, null);
        }
        catch (IllegalAccessException e) {}catch (InvocationTargetException e) {}
      }
    }
  }
}
