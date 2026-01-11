package org.jdesktop.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;










































public class ResourceManager
  extends AbstractBean
{
  private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
  private final Map<String, ResourceMap> resourceMaps;
  private final ApplicationContext context;
  private List<String> applicationBundleNames = null;
  private ResourceMap appResourceMap = null;
  
















  protected ResourceManager(ApplicationContext paramApplicationContext)
  {
    if (paramApplicationContext == null) {
      throw new IllegalArgumentException("null context");
    }
    context = paramApplicationContext;
    resourceMaps = new ConcurrentHashMap();
  }
  
  protected final ApplicationContext getContext()
  {
    return context;
  }
  






  private List<String> allBundleNames(Class paramClass1, Class paramClass2)
  {
    ArrayList localArrayList = new ArrayList();
    Class localClass1 = paramClass2.getSuperclass();
    for (Class localClass2 = paramClass1; localClass2 != localClass1; localClass2 = localClass2.getSuperclass()) {
      localArrayList.addAll(getClassBundleNames(localClass2));
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private String bundlePackageName(String paramString) {
    int i = paramString.lastIndexOf(".");
    return i == -1 ? "" : paramString.substring(0, i);
  }
  





  private ResourceMap createResourceMapChain(ClassLoader paramClassLoader, ResourceMap paramResourceMap, ListIterator<String> paramListIterator)
  {
    if (!paramListIterator.hasNext()) {
      return paramResourceMap;
    }
    
    String str1 = (String)paramListIterator.next();
    String str2 = bundlePackageName(str1);
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(str1);
    while (paramListIterator.hasNext()) {
      localObject = (String)paramListIterator.next();
      if (str2.equals(bundlePackageName((String)localObject))) {
        localArrayList.add(localObject);
      }
      else {
        paramListIterator.previous();
        break;
      }
    }
    Object localObject = createResourceMapChain(paramClassLoader, paramResourceMap, paramListIterator);
    return createResourceMap(paramClassLoader, (ResourceMap)localObject, localArrayList);
  }
  






  private ResourceMap getApplicationResourceMap()
  {
    if (appResourceMap == null) {
      List localList = getApplicationBundleNames();
      Object localObject = getContext().getApplicationClass();
      if (localObject == null) {
        logger.warning("getApplicationResourceMap(): no Application class");
        localObject = Application.class;
      }
      ClassLoader localClassLoader = ((Class)localObject).getClassLoader();
      appResourceMap = createResourceMapChain(localClassLoader, null, localList.listIterator());
    }
    return appResourceMap;
  }
  


  private ResourceMap getClassResourceMap(Class paramClass1, Class paramClass2)
  {
    String str = paramClass1.getName() + paramClass2.getName();
    ResourceMap localResourceMap1 = (ResourceMap)resourceMaps.get(str);
    if (localResourceMap1 == null) {
      List localList = allBundleNames(paramClass1, paramClass2);
      ClassLoader localClassLoader = paramClass1.getClassLoader();
      ResourceMap localResourceMap2 = getResourceMap();
      localResourceMap1 = createResourceMapChain(localClassLoader, localResourceMap2, localList.listIterator());
      resourceMaps.put(str, localResourceMap1);
    }
    return localResourceMap1;
  }
  
































































  public ResourceMap getResourceMap(Class paramClass1, Class paramClass2)
  {
    if (paramClass1 == null) {
      throw new IllegalArgumentException("null startClass");
    }
    if (paramClass2 == null) {
      throw new IllegalArgumentException("null stopClass");
    }
    if (!paramClass2.isAssignableFrom(paramClass1)) {
      throw new IllegalArgumentException("startClass is not a subclass, or the same as, stopClass");
    }
    return getClassResourceMap(paramClass1, paramClass2);
  }
  










  public final ResourceMap getResourceMap(Class paramClass)
  {
    if (paramClass == null) {
      throw new IllegalArgumentException("null class");
    }
    return getResourceMap(paramClass, paramClass);
  }
  












  public ResourceMap getResourceMap()
  {
    return getApplicationResourceMap();
  }
  
































  public List<String> getApplicationBundleNames()
  {
    if (applicationBundleNames == null) {
      Class localClass = getContext().getApplicationClass();
      if (localClass == null) {
        return allBundleNames(Application.class, Application.class);
      }
      
      applicationBundleNames = allBundleNames(localClass, Application.class);
    }
    
    return applicationBundleNames;
  }
  






  public void setApplicationBundleNames(List<String> paramList)
  {
    if (paramList != null) {
      for (localObject = paramList.iterator(); ((Iterator)localObject).hasNext();) { String str = (String)((Iterator)localObject).next();
        if ((str == null) || (paramList.size() == 0)) {
          throw new IllegalArgumentException("invalid bundle name \"" + str + "\"");
        }
      }
    }
    Object localObject = applicationBundleNames;
    if (paramList != null) {
      applicationBundleNames = Collections.unmodifiableList(new ArrayList(paramList));
    }
    else {
      applicationBundleNames = null;
    }
    resourceMaps.clear();
    firePropertyChange("applicationBundleNames", localObject, applicationBundleNames);
  }
  










  private String classBundleBaseName(Class paramClass)
  {
    String str = paramClass.getName();
    StringBuffer localStringBuffer = new StringBuffer();
    int i = str.lastIndexOf('.');
    if (i > 0) {
      localStringBuffer.append(str.substring(0, i));
      localStringBuffer.append(".resources.");
      localStringBuffer.append(paramClass.getSimpleName());
    }
    else {
      localStringBuffer.append("resources.");
      localStringBuffer.append(paramClass.getSimpleName());
    }
    return localStringBuffer.toString();
  }
  





























  protected List<String> getClassBundleNames(Class paramClass)
  {
    String str = classBundleBaseName(paramClass);
    return Collections.singletonList(str);
  }
  








  protected ResourceMap createResourceMap(ClassLoader paramClassLoader, ResourceMap paramResourceMap, List<String> paramList)
  {
    return new ResourceMap(paramResourceMap, paramClassLoader, paramList);
  }
  








  public String getPlatform()
  {
    return getResourceMap().getString("platform", new Object[0]);
  }
  





















  public void setPlatform(String paramString)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("null platform");
    }
    getResourceMap().putResource("platform", paramString);
  }
}
