package org.apache.log4j.config;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.LogLog;
























public class PropertyGetter
{
  protected static final Object[] NULL_ARG = new Object[0];
  



  protected Object obj;
  


  protected PropertyDescriptor[] props;
  



  public PropertyGetter(Object obj)
    throws IntrospectionException
  {
    BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
    props = bi.getPropertyDescriptors();
    this.obj = obj;
  }
  
  public static void getProperties(Object obj, PropertyCallback callback, String prefix)
  {
    try
    {
      new PropertyGetter(obj).getProperties(callback, prefix);
    } catch (IntrospectionException ex) {
      LogLog.error("Failed to introspect object " + obj, ex);
    }
  }
  
  public void getProperties(PropertyCallback callback, String prefix)
  {
    for (int i = 0; i < props.length; i++) {
      Method getter = props[i].getReadMethod();
      if ((getter != null) && 
        (isHandledType(getter.getReturnType())))
      {


        String name = props[i].getName();
        try {
          Object result = getter.invoke(obj, NULL_ARG);
          
          if (result != null) {
            callback.foundProperty(obj, prefix, name, result);
          }
        } catch (IllegalAccessException ex) {
          LogLog.warn("Failed to get value of property " + name);
        } catch (InvocationTargetException ex) {
          if (((ex.getTargetException() instanceof InterruptedException)) || ((ex.getTargetException() instanceof InterruptedIOException)))
          {
            Thread.currentThread().interrupt();
          }
          LogLog.warn("Failed to get value of property " + name);
        } catch (RuntimeException ex) {
          LogLog.warn("Failed to get value of property " + name);
        }
      }
    }
  }
  
  protected boolean isHandledType(Class type) {
    return (String.class.isAssignableFrom(type)) || (Integer.TYPE.isAssignableFrom(type)) || (Long.TYPE.isAssignableFrom(type)) || (Boolean.TYPE.isAssignableFrom(type)) || (Priority.class.isAssignableFrom(type));
  }
  
  public static abstract interface PropertyCallback
  {
    public abstract void foundProperty(Object paramObject1, String paramString1, String paramString2, Object paramObject2);
  }
}
