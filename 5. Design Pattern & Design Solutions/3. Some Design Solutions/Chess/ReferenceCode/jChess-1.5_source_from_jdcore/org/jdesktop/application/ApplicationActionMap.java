package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ActionMap;











































public class ApplicationActionMap
  extends ActionMap
{
  private final ApplicationContext context;
  private final ResourceMap resourceMap;
  private final Class actionsClass;
  private final Object actionsObject;
  private final List<ApplicationAction> proxyActions;
  
  public ApplicationActionMap(ApplicationContext paramApplicationContext, Class paramClass, Object paramObject, ResourceMap paramResourceMap)
  {
    if (paramApplicationContext == null) {
      throw new IllegalArgumentException("null context");
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("null actionsClass");
    }
    if (paramObject == null) {
      throw new IllegalArgumentException("null actionsObject");
    }
    if (!paramClass.isInstance(paramObject)) {
      throw new IllegalArgumentException("actionsObject not an instanceof actionsClass");
    }
    context = paramApplicationContext;
    actionsClass = paramClass;
    actionsObject = paramObject;
    resourceMap = paramResourceMap;
    proxyActions = new ArrayList();
    addAnnotationActions(paramResourceMap);
    maybeAddActionsPCL();
  }
  
  public final ApplicationContext getContext() {
    return context;
  }
  
  public final Class getActionsClass() {
    return actionsClass;
  }
  
  public final Object getActionsObject() {
    return actionsObject;
  }
  











  public List<ApplicationAction> getProxyActions()
  {
    ArrayList localArrayList = new ArrayList(proxyActions);
    ActionMap localActionMap = getParent();
    while (localActionMap != null) {
      if ((localActionMap instanceof ApplicationActionMap)) {
        localArrayList.addAll(proxyActions);
      }
      localActionMap = localActionMap.getParent();
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private String aString(String paramString1, String paramString2) {
    return paramString1.length() == 0 ? paramString2 : paramString1;
  }
  
  private void putAction(String paramString, ApplicationAction paramApplicationAction) {
    if (get(paramString) != null) {}
    

    put(paramString, paramApplicationAction);
  }
  



  private void addAnnotationActions(ResourceMap paramResourceMap)
  {
    Class localClass = getActionsClass();
    Object localObject2;
    Object localObject3; for (Method localMethod : localClass.getDeclaredMethods()) {
      localObject2 = (Action)localMethod.getAnnotation(Action.class);
      if (localObject2 != null) {
        localObject3 = localMethod.getName();
        String str1 = aString(((Action)localObject2).enabledProperty(), null);
        String str2 = aString(((Action)localObject2).selectedProperty(), null);
        String str3 = aString(((Action)localObject2).name(), (String)localObject3);
        Task.BlockingScope localBlockingScope = ((Action)localObject2).block();
        ApplicationAction localApplicationAction = new ApplicationAction(this, paramResourceMap, str3, localMethod, str1, str2, localBlockingScope);
        
        putAction(str3, localApplicationAction);
      }
    }
    
    ??? = (ProxyActions)localClass.getAnnotation(ProxyActions.class);
    if (??? != null) {
      for (localObject2 : ((ProxyActions)???).value()) {
        localObject3 = new ApplicationAction(this, paramResourceMap, (String)localObject2);
        ((ApplicationAction)localObject3).setEnabled(false);
        putAction((String)localObject2, (ApplicationAction)localObject3);
        proxyActions.add(localObject3);
      }
    }
  }
  





  private void maybeAddActionsPCL()
  {
    int i = 0;
    Object[] arrayOfObject = keys();
    if (arrayOfObject != null) {
      for (Object localObject3 : arrayOfObject) {
        javax.swing.Action localAction = get(localObject3);
        if ((localAction instanceof ApplicationAction)) {
          ApplicationAction localApplicationAction = (ApplicationAction)localAction;
          if ((localApplicationAction.getEnabledProperty() != null) || (localApplicationAction.getSelectedProperty() != null))
          {
            i = 1;
            break;
          }
        }
      }
      if (i != 0) {
        try {
          ??? = getActionsClass();
          localObject2 = ((Class)???).getMethod("addPropertyChangeListener", new Class[] { PropertyChangeListener.class });
          ((Method)localObject2).invoke(getActionsObject(), new Object[] { new ActionsPCL(null) });
        }
        catch (Exception localException) {
          Object localObject2 = "addPropertyChangeListener undefined " + actionsClass;
          throw new Error((String)localObject2, localException);
        }
      }
    }
  }
  
  private class ActionsPCL implements PropertyChangeListener
  {
    private ActionsPCL() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      Object[] arrayOfObject1 = keys();
      if (arrayOfObject1 != null) {
        for (Object localObject : arrayOfObject1) {
          javax.swing.Action localAction = get(localObject);
          if ((localAction instanceof ApplicationAction)) {
            ApplicationAction localApplicationAction = (ApplicationAction)localAction;
            if (str.equals(localApplicationAction.getEnabledProperty())) {
              localApplicationAction.forwardPropertyChangeEvent(paramPropertyChangeEvent, "enabled");
            }
            else if (str.equals(localApplicationAction.getSelectedProperty())) {
              localApplicationAction.forwardPropertyChangeEvent(paramPropertyChangeEvent, "selected");
            }
          }
        }
      }
    }
  }
}
