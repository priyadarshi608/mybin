package org.jdesktop.application;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.KeyStroke;
































































































public class ApplicationAction
  extends AbstractAction
{
  private static final Logger logger = Logger.getLogger(ApplicationAction.class.getName());
  private final ApplicationActionMap appAM;
  private final ResourceMap resourceMap;
  private final String actionName;
  private final Method actionMethod;
  private final String enabledProperty;
  private final Method isEnabledMethod;
  private final Method setEnabledMethod;
  private final String selectedProperty;
  private final Method isSelectedMethod;
  private final Method setSelectedMethod;
  private final Task.BlockingScope block;
  private Action proxy = null;
  private Object proxySource = null;
  private PropertyChangeListener proxyPCL = null;
  

















  private static final String SELECTED_KEY = "SwingSelectedKey";
  

















  private static final String DISPLAYED_MNEMONIC_INDEX_KEY = "SwingDisplayedMnemonicIndexKey";
  

















  private static final String LARGE_ICON_KEY = "SwingLargeIconKey";
  


















  public ApplicationAction(ApplicationActionMap paramApplicationActionMap, ResourceMap paramResourceMap, String paramString1, Method paramMethod, String paramString2, String paramString3, Task.BlockingScope paramBlockingScope)
  {
    if (paramApplicationActionMap == null) {
      throw new IllegalArgumentException("null appAM");
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("null baseName");
    }
    appAM = paramApplicationActionMap;
    resourceMap = paramResourceMap;
    actionName = paramString1;
    actionMethod = paramMethod;
    enabledProperty = paramString2;
    selectedProperty = paramString3;
    block = paramBlockingScope;
    



    if (paramString2 != null) {
      setEnabledMethod = propertySetMethod(paramString2, Boolean.TYPE);
      isEnabledMethod = propertyGetMethod(paramString2);
      if (isEnabledMethod == null) {
        throw newNoSuchPropertyException(paramString2);
      }
    }
    else {
      isEnabledMethod = null;
      setEnabledMethod = null;
    }
    



    if (paramString3 != null) {
      setSelectedMethod = propertySetMethod(paramString3, Boolean.TYPE);
      isSelectedMethod = propertyGetMethod(paramString3);
      if (isSelectedMethod == null) {
        throw newNoSuchPropertyException(paramString3);
      }
      super.putValue("SwingSelectedKey", Boolean.FALSE);
    }
    else {
      isSelectedMethod = null;
      setSelectedMethod = null;
    }
    
    if (paramResourceMap != null) {
      initActionProperties(paramResourceMap, paramString1);
    }
  }
  


  ApplicationAction(ApplicationActionMap paramApplicationActionMap, ResourceMap paramResourceMap, String paramString)
  {
    this(paramApplicationActionMap, paramResourceMap, paramString, null, null, null, Task.BlockingScope.NONE);
  }
  
  private IllegalArgumentException newNoSuchPropertyException(String paramString) {
    String str1 = appAM.getActionsClass().getName();
    String str2 = String.format("no property named %s in %s", new Object[] { paramString, str1 });
    return new IllegalArgumentException(str2);
  }
  







  String getEnabledProperty()
  {
    return enabledProperty;
  }
  






  String getSelectedProperty()
  {
    return selectedProperty;
  }
  








  public Action getProxy()
  {
    return proxy;
  }
  























  public void setProxy(Action paramAction)
  {
    Action localAction = proxy;
    proxy = paramAction;
    if (localAction != null) {
      localAction.removePropertyChangeListener(proxyPCL);
      proxyPCL = null;
    }
    if (proxy != null) {
      updateProxyProperties();
      proxyPCL = new ProxyPCL(null);
      paramAction.addPropertyChangeListener(proxyPCL);
    }
    else if (localAction != null) {
      setEnabled(false);
      setSelected(false);
    }
    firePropertyChange("proxy", localAction, proxy);
  }
  








  public Object getProxySource()
  {
    return proxySource;
  }
  








  public void setProxySource(Object paramObject)
  {
    Object localObject = proxySource;
    proxySource = paramObject;
    firePropertyChange("proxySource", localObject, proxySource);
  }
  
  private void maybePutDescriptionValue(String paramString, Action paramAction) {
    Object localObject = paramAction.getValue(paramString);
    if ((localObject instanceof String)) {
      putValue(paramString, (String)localObject);
    }
  }
  
  private void updateProxyProperties() {
    Action localAction = getProxy();
    if (localAction != null) {
      setEnabled(localAction.isEnabled());
      Object localObject = localAction.getValue("SwingSelectedKey");
      setSelected(((localObject instanceof Boolean)) && (((Boolean)localObject).booleanValue()));
      maybePutDescriptionValue("ShortDescription", localAction);
      maybePutDescriptionValue("LongDescription", localAction);
    }
  }
  
  private class ProxyPCL
    implements PropertyChangeListener
  {
    private ProxyPCL() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent)
    {
      String str = paramPropertyChangeEvent.getPropertyName();
      if ((str == null) || ("enabled".equals(str)) || ("selected".equals(str)) || ("ShortDescription".equals(str)) || ("LongDescription".equals(str)))
      {



        ApplicationAction.this.updateProxyProperties();
      }
    }
  }
  










  private void initActionProperties(ResourceMap paramResourceMap, String paramString)
  {
    int i = 0;
    Object localObject = null;
    

    String str = paramResourceMap.getString(paramString + ".Action.text", new Object[0]);
    if (str != null) {
      MnemonicText.configure(this, str);
      i = 1;
    }
    
    Integer localInteger1 = paramResourceMap.getKeyCode(paramString + ".Action.mnemonic");
    if (localInteger1 != null) {
      putValue("MnemonicKey", localInteger1);
    }
    
    Integer localInteger2 = paramResourceMap.getInteger(paramString + ".Action.displayedMnemonicIndex");
    if (localInteger2 != null) {
      putValue("SwingDisplayedMnemonicIndexKey", localInteger2);
    }
    
    KeyStroke localKeyStroke = paramResourceMap.getKeyStroke(paramString + ".Action.accelerator");
    if (localKeyStroke != null) {
      putValue("AcceleratorKey", localKeyStroke);
    }
    
    Icon localIcon1 = paramResourceMap.getIcon(paramString + ".Action.icon");
    if (localIcon1 != null) {
      putValue("SmallIcon", localIcon1);
      putValue("SwingLargeIconKey", localIcon1);
      i = 1;
    }
    
    Icon localIcon2 = paramResourceMap.getIcon(paramString + ".Action.smallIcon");
    if (localIcon2 != null) {
      putValue("SmallIcon", localIcon2);
      i = 1;
    }
    
    Icon localIcon3 = paramResourceMap.getIcon(paramString + ".Action.largeIcon");
    if (localIcon3 != null) {
      putValue("SwingLargeIconKey", localIcon3);
      i = 1;
    }
    
    putValue("ShortDescription", paramResourceMap.getString(paramString + ".Action.shortDescription", new Object[0]));
    

    putValue("LongDescription", paramResourceMap.getString(paramString + ".Action.longDescription", new Object[0]));
    

    putValue("ActionCommandKey", paramResourceMap.getString(paramString + ".Action.command", new Object[0]));
    


    if (i == 0) {
      putValue("Name", actionName);
    }
  }
  
  private String propertyMethodName(String paramString1, String paramString2) {
    return paramString1 + paramString2.substring(0, 1).toUpperCase() + paramString2.substring(1);
  }
  
  private Method propertyGetMethod(String paramString) {
    String[] arrayOfString1 = { propertyMethodName("is", paramString), propertyMethodName("get", paramString) };
    


    Class localClass = appAM.getActionsClass();
    for (String str : arrayOfString1) {
      try {
        return localClass.getMethod(str, new Class[0]);
      }
      catch (NoSuchMethodException localNoSuchMethodException) {}
    }
    return null;
  }
  
  private Method propertySetMethod(String paramString, Class paramClass) {
    Class localClass = appAM.getActionsClass();
    try {
      return localClass.getMethod(propertyMethodName("set", paramString), new Class[] { paramClass });
    }
    catch (NoSuchMethodException localNoSuchMethodException) {}
    return null;
  }
  
























  public String getName()
  {
    return actionName;
  }
  




  public ResourceMap getResourceMap()
  {
    return resourceMap;
  }
  



















































  protected Object getActionArgument(Class paramClass, String paramString, ActionEvent paramActionEvent)
  {
    Object localObject = null;
    if (paramClass == ActionEvent.class) {
      localObject = paramActionEvent;
    }
    else if (paramClass == Action.class) {
      localObject = this;
    }
    else if (paramClass == ActionMap.class) {
      localObject = appAM;
    }
    else if (paramClass == ResourceMap.class) {
      localObject = resourceMap;
    }
    else if (paramClass == ApplicationContext.class) {
      localObject = appAM.getContext();
    }
    else if (paramClass == Application.class) {
      localObject = appAM.getContext().getApplication();
    }
    else {
      IllegalArgumentException localIllegalArgumentException = new IllegalArgumentException("unrecognized @Action method parameter");
      actionFailed(paramActionEvent, localIllegalArgumentException);
    }
    return localObject;
  }
  
  private Task.InputBlocker createInputBlocker(Task paramTask, ActionEvent paramActionEvent)
  {
    Object localObject = paramActionEvent.getSource();
    if (block == Task.BlockingScope.ACTION) {
      localObject = this;
    }
    return new DefaultInputBlocker(paramTask, block, localObject, this);
  }
  
  private void noProxyActionPerformed(ActionEvent paramActionEvent) {
    Object localObject1 = null;
    



    Annotation[][] arrayOfAnnotation = actionMethod.getParameterAnnotations();
    Class[] arrayOfClass = actionMethod.getParameterTypes();
    Object[] arrayOfObject = new Object[arrayOfClass.length];
    Object localObject3; for (int i = 0; i < arrayOfClass.length; i++) {
      localObject3 = null;
      for (Annotation localAnnotation : arrayOfAnnotation[i]) {
        if ((localAnnotation instanceof Action.Parameter)) {
          localObject3 = ((Action.Parameter)localAnnotation).value();
          break;
        }
      }
      arrayOfObject[i] = getActionArgument(arrayOfClass[i], (String)localObject3, paramActionEvent);
    }
    


    try
    {
      Object localObject2 = appAM.getActionsObject();
      localObject1 = actionMethod.invoke(localObject2, arrayOfObject);
    }
    catch (Exception localException) {
      actionFailed(paramActionEvent, localException);
    }
    
    if ((localObject1 instanceof Task)) {
      Task localTask = (Task)localObject1;
      if (localTask.getInputBlocker() == null) {
        localTask.setInputBlocker(createInputBlocker(localTask, paramActionEvent));
      }
      localObject3 = appAM.getContext();
      ((ApplicationContext)localObject3).getTaskService().execute(localTask);
    }
  }
  












  public void actionPerformed(ActionEvent paramActionEvent)
  {
    Action localAction = getProxy();
    if (localAction != null) {
      paramActionEvent.setSource(getProxySource());
      localAction.actionPerformed(paramActionEvent);
    }
    else if (actionMethod != null) {
      noProxyActionPerformed(paramActionEvent);
    }
  }
  











  public boolean isEnabled()
  {
    if ((getProxy() != null) || (isEnabledMethod == null)) {
      return super.isEnabled();
    }
    try
    {
      Object localObject = isEnabledMethod.invoke(appAM.getActionsObject(), new Object[0]);
      return ((Boolean)localObject).booleanValue();
    }
    catch (Exception localException) {
      throw newInvokeError(isEnabledMethod, localException, new Object[0]);
    }
  }
  












  public void setEnabled(boolean paramBoolean)
  {
    if ((getProxy() != null) || (setEnabledMethod == null)) {
      super.setEnabled(paramBoolean);
    } else {
      try
      {
        setEnabledMethod.invoke(appAM.getActionsObject(), new Object[] { Boolean.valueOf(paramBoolean) });
      }
      catch (Exception localException) {
        throw newInvokeError(setEnabledMethod, localException, new Object[] { Boolean.valueOf(paramBoolean) });
      }
    }
  }
  





  public boolean isSelected()
  {
    Object localObject;
    



    if ((getProxy() != null) || (isSelectedMethod == null)) {
      localObject = getValue("SwingSelectedKey");
      return (localObject instanceof Boolean) ? ((Boolean)localObject).booleanValue() : false;
    }
    try
    {
      localObject = isSelectedMethod.invoke(appAM.getActionsObject(), new Object[0]);
      return ((Boolean)localObject).booleanValue();
    }
    catch (Exception localException) {
      throw newInvokeError(isSelectedMethod, localException, new Object[0]);
    }
  }
  












  public void setSelected(boolean paramBoolean)
  {
    if ((getProxy() != null) || (setSelectedMethod == null)) {
      super.putValue("SwingSelectedKey", Boolean.valueOf(paramBoolean));
    } else {
      try
      {
        super.putValue("SwingSelectedKey", Boolean.valueOf(paramBoolean));
        if (paramBoolean != isSelected()) {
          setSelectedMethod.invoke(appAM.getActionsObject(), new Object[] { Boolean.valueOf(paramBoolean) });
        }
      }
      catch (Exception localException) {
        throw newInvokeError(setSelectedMethod, localException, new Object[] { Boolean.valueOf(paramBoolean) });
      }
    }
  }
  






  public void putValue(String paramString, Object paramObject)
  {
    if (("SwingSelectedKey".equals(paramString)) && ((paramObject instanceof Boolean))) {
      setSelected(((Boolean)paramObject).booleanValue());
    }
    else {
      super.putValue(paramString, paramObject);
    }
  }
  


  private Error newInvokeError(Method paramMethod, Exception paramException, Object... paramVarArgs)
  {
    String str1 = paramVarArgs.length == 0 ? "" : paramVarArgs[0].toString();
    for (int i = 1; i < paramVarArgs.length; i++) {
      str1 = str1 + ", " + paramVarArgs[i];
    }
    String str2 = appAM.getActionsObject().getClass().getName();
    String str3 = String.format("%s.%s(%s) failed", new Object[] { str2, paramMethod, str1 });
    return new Error(str3, paramException);
  }
  





  void forwardPropertyChangeEvent(PropertyChangeEvent paramPropertyChangeEvent, String paramString)
  {
    if (("selected".equals(paramString)) && ((paramPropertyChangeEvent.getNewValue() instanceof Boolean))) {
      putValue("SwingSelectedKey", (Boolean)paramPropertyChangeEvent.getNewValue());
    }
    firePropertyChange(paramString, paramPropertyChangeEvent.getOldValue(), paramPropertyChangeEvent.getNewValue());
  }
  




  private void actionFailed(ActionEvent paramActionEvent, Exception paramException)
  {
    throw new Error(paramException);
  }
  










  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getClass().getName());
    localStringBuilder.append(" ");
    boolean bool = isEnabled();
    if (!bool) localStringBuilder.append("(");
    localStringBuilder.append(getName());
    Object localObject1 = getValue("SwingSelectedKey");
    if (((localObject1 instanceof Boolean)) && 
      (((Boolean)localObject1).booleanValue())) {
      localStringBuilder.append("+");
    }
    
    if (!bool) localStringBuilder.append(")");
    Object localObject2 = getValue("Name");
    if ((localObject2 instanceof String)) {
      localStringBuilder.append(" \"");
      localStringBuilder.append((String)localObject2);
      localStringBuilder.append("\"");
    }
    proxy = getProxy();
    if (proxy != null) {
      localStringBuilder.append(" Proxy for: ");
      localStringBuilder.append(proxy.toString());
    }
    return localStringBuilder.toString();
  }
}
