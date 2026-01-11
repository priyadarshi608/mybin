package org.jdesktop.application;

import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
















public class ActionManager
  extends AbstractBean
{
  private static final Logger logger = Logger.getLogger(ActionManager.class.getName());
  private final ApplicationContext context;
  private final WeakHashMap<Object, WeakReference<ApplicationActionMap>> actionMaps;
  private ApplicationActionMap globalActionMap = null;
  
  protected ActionManager(ApplicationContext paramApplicationContext) {
    if (paramApplicationContext == null) {
      throw new IllegalArgumentException("null context");
    }
    context = paramApplicationContext;
    actionMaps = new WeakHashMap();
  }
  
  protected final ApplicationContext getContext() {
    return context;
  }
  

  private ApplicationActionMap createActionMapChain(Class paramClass1, Class paramClass2, Object paramObject, ResourceMap paramResourceMap)
  {
    ArrayList localArrayList = new ArrayList();
    for (Object localObject1 = paramClass1;; localObject1 = ((Class)localObject1).getSuperclass()) {
      localArrayList.add(localObject1);
      if (localObject1.equals(paramClass2)) break;
    }
    Collections.reverse(localArrayList);
    
    localObject1 = getContext();
    Object localObject2 = null;
    for (Class localClass : localArrayList) {
      ApplicationActionMap localApplicationActionMap = new ApplicationActionMap((ApplicationContext)localObject1, localClass, paramObject, paramResourceMap);
      localApplicationActionMap.setParent((ActionMap)localObject2);
      localObject2 = localApplicationActionMap;
    }
    return localObject2;
  }
  























  public ApplicationActionMap getActionMap()
  {
    if (globalActionMap == null) {
      ApplicationContext localApplicationContext = getContext();
      Application localApplication = localApplicationContext.getApplication();
      Class localClass = localApplicationContext.getApplicationClass();
      ResourceMap localResourceMap = localApplicationContext.getResourceMap();
      globalActionMap = createActionMapChain(localClass, Application.class, localApplication, localResourceMap);
      initProxyActionSupport();
    }
    return globalActionMap;
  }
  
  private void initProxyActionSupport() {
    KeyboardFocusManager localKeyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    localKeyboardFocusManager.addPropertyChangeListener(new KeyboardFocusPCL());
  }
  







































  public ApplicationActionMap getActionMap(Class paramClass, Object paramObject)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("null actionsClass");
    }
    if (paramObject == null) {
      throw new IllegalArgumentException("null actionsObject");
    }
    if (!paramClass.isAssignableFrom(paramObject.getClass())) {
      throw new IllegalArgumentException("actionsObject not instanceof actionsClass");
    }
    synchronized (actionMaps) {
      WeakReference localWeakReference = (WeakReference)actionMaps.get(paramObject);
      ApplicationActionMap localApplicationActionMap = localWeakReference != null ? (ApplicationActionMap)localWeakReference.get() : null;
      if ((localApplicationActionMap == null) || (localApplicationActionMap.getActionsClass() != paramClass)) {
        ApplicationContext localApplicationContext = getContext();
        Class localClass = paramObject.getClass();
        ResourceMap localResourceMap = localApplicationContext.getResourceMap(localClass, paramClass);
        localApplicationActionMap = createActionMapChain(localClass, paramClass, paramObject, localResourceMap);
        Object localObject1 = localApplicationActionMap;
        while (((ActionMap)localObject1).getParent() != null) {
          localObject1 = ((ActionMap)localObject1).getParent();
        }
        ((ActionMap)localObject1).setParent(getActionMap());
        actionMaps.put(paramObject, new WeakReference(localApplicationActionMap));
      }
      return localApplicationActionMap;
    }
  }
  
  private final class KeyboardFocusPCL implements PropertyChangeListener {
    private final TextActions textActions;
    
    KeyboardFocusPCL() { textActions = new TextActions(getContext()); }
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
      if (paramPropertyChangeEvent.getPropertyName() == "permanentFocusOwner") {
        JComponent localJComponent1 = getContext().getFocusOwner();
        Object localObject = paramPropertyChangeEvent.getNewValue();
        JComponent localJComponent2 = (localObject instanceof JComponent) ? (JComponent)localObject : null;
        textActions.updateFocusOwner(localJComponent1, localJComponent2);
        getContext().setFocusOwner(localJComponent2);
        ActionManager.this.updateAllProxyActions(localJComponent1, localJComponent2);
      }
    }
  }
  


  private void updateAllProxyActions(JComponent paramJComponent1, JComponent paramJComponent2)
  {
    ActionMap localActionMap;
    
    if (paramJComponent2 != null) {
      localActionMap = paramJComponent2.getActionMap();
      if (localActionMap != null) {
        updateProxyActions(getActionMap(), localActionMap, paramJComponent2);
        for (WeakReference localWeakReference : actionMaps.values()) {
          ApplicationActionMap localApplicationActionMap = (ApplicationActionMap)localWeakReference.get();
          if (localApplicationActionMap != null)
          {

            updateProxyActions(localApplicationActionMap, localActionMap, paramJComponent2);
          }
        }
      }
    }
  }
  



  private void updateProxyActions(ApplicationActionMap paramApplicationActionMap, ActionMap paramActionMap, JComponent paramJComponent)
  {
    for (ApplicationAction localApplicationAction : paramApplicationActionMap.getProxyActions()) {
      String str = localApplicationAction.getName();
      Action localAction = paramActionMap.get(str);
      if (localAction != null) {
        localApplicationAction.setProxy(localAction);
        localApplicationAction.setProxySource(paramJComponent);
      }
      else {
        localApplicationAction.setProxy(null);
        localApplicationAction.setProxySource(null);
      }
    }
  }
}
