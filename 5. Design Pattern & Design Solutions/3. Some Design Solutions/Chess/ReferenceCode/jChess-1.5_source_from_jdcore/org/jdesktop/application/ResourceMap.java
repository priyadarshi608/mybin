package org.jdesktop.application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
















































public class ResourceMap
{
  private static Logger logger = Logger.getLogger(ResourceMap.class.getName());
  private static final Object nullResource = new String("null resource");
  private final ClassLoader classLoader;
  private final ResourceMap parent;
  private final List<String> bundleNames;
  private final String resourcesDir;
  private Map<String, Object> bundlesMapP = null;
  private Locale locale = Locale.getDefault();
  private Set<String> bundlesMapKeysP = null;
  private boolean bundlesLoaded = false;
  












































  public ResourceMap(ResourceMap paramResourceMap, ClassLoader paramClassLoader, List<String> paramList)
  {
    if (paramClassLoader == null) {
      throw new IllegalArgumentException("null ClassLoader");
    }
    if ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException("no bundle specified");
    }
    for (Object localObject1 = paramList.iterator(); ((Iterator)localObject1).hasNext();) { localObject2 = (String)((Iterator)localObject1).next();
      if ((localObject2 == null) || (((String)localObject2).length() == 0)) {
        throw new IllegalArgumentException("invalid bundleName: \"" + (String)localObject2 + "\"");
      }
    }
    localObject1 = bundlePackageName((String)paramList.get(0));
    for (Object localObject2 = paramList.iterator(); ((Iterator)localObject2).hasNext();) { String str = (String)((Iterator)localObject2).next();
      if (!((String)localObject1).equals(bundlePackageName(str))) {
        throw new IllegalArgumentException("bundles not colocated: \"" + str + "\" != \"" + (String)localObject1 + "\"");
      }
    }
    parent = paramResourceMap;
    classLoader = paramClassLoader;
    bundleNames = Collections.unmodifiableList(new ArrayList(paramList));
    resourcesDir = (((String)localObject1).replace(".", "/") + "/");
  }
  
  private String bundlePackageName(String paramString) {
    int i = paramString.lastIndexOf(".");
    return i == -1 ? "" : paramString.substring(0, i);
  }
  




  public ResourceMap(ResourceMap paramResourceMap, ClassLoader paramClassLoader, String... paramVarArgs)
  {
    this(paramResourceMap, paramClassLoader, Arrays.asList(paramVarArgs));
  }
  






  public ResourceMap getParent()
  {
    return parent;
  }
  





  public List<String> getBundleNames()
  {
    return bundleNames;
  }
  





  public ClassLoader getClassLoader()
  {
    return classLoader;
  }
  











  public String getResourcesDir()
  {
    return resourcesDir;
  }
  




  private synchronized Map<String, Object> getBundlesMap()
  {
    Locale localLocale = Locale.getDefault();
    if (locale != localLocale) {
      bundlesLoaded = false;
      locale = localLocale;
    }
    if (!bundlesLoaded) {
      ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
      for (int i = bundleNames.size() - 1; i >= 0; i--) {
        try {
          String str1 = (String)bundleNames.get(i);
          ResourceBundle localResourceBundle = ResourceBundle.getBundle(str1, locale, classLoader);
          Enumeration localEnumeration = localResourceBundle.getKeys();
          while (localEnumeration.hasMoreElements()) {
            String str2 = (String)localEnumeration.nextElement();
            localConcurrentHashMap.put(str2, localResourceBundle.getObject(str2));
          }
        }
        catch (MissingResourceException localMissingResourceException) {}
      }
      



      bundlesMapP = localConcurrentHashMap;
      bundlesLoaded = true;
    }
    return bundlesMapP;
  }
  
  private void checkNullKey(String paramString) {
    if (paramString == null) {
      throw new IllegalArgumentException("null key");
    }
  }
  
  private synchronized Set<String> getBundlesMapKeys() {
    if (bundlesMapKeysP == null) {
      HashSet localHashSet = new HashSet(getResourceKeySet());
      ResourceMap localResourceMap = getParent();
      if (localResourceMap != null) {
        localHashSet.addAll(localResourceMap.keySet());
      }
      bundlesMapKeysP = Collections.unmodifiableSet(localHashSet);
    }
    return bundlesMapKeysP;
  }
  






  public Set<String> keySet()
  {
    return getBundlesMapKeys();
  }
  







  public boolean containsKey(String paramString)
  {
    checkNullKey(paramString);
    if (containsResourceKey(paramString)) {
      return true;
    }
    
    ResourceMap localResourceMap = getParent();
    return localResourceMap != null ? localResourceMap.containsKey(paramString) : false;
  }
  





  public static class LookupException
    extends RuntimeException
  {
    private final Class type;
    




    private final String key;
    




    public LookupException(String paramString1, String paramString2, Class paramClass)
    {
      super();
      key = paramString2;
      type = paramClass;
    }
    



    public Class getType()
    {
      return type;
    }
    



    public String getKey()
    {
      return key;
    }
  }
  















  protected Set<String> getResourceKeySet()
  {
    Map localMap = getBundlesMap();
    if (localMap == null) {
      return Collections.emptySet();
    }
    
    return localMap.keySet();
  }
  



















  protected boolean containsResourceKey(String paramString)
  {
    checkNullKey(paramString);
    Map localMap = getBundlesMap();
    return (localMap != null) && (localMap.containsKey(paramString));
  }
  





















  protected Object getResource(String paramString)
  {
    checkNullKey(paramString);
    Map localMap = getBundlesMap();
    Object localObject = localMap != null ? localMap.get(paramString) : null;
    return localObject == nullResource ? null : localObject;
  }
  



















  protected void putResource(String paramString, Object paramObject)
  {
    checkNullKey(paramString);
    Map localMap = getBundlesMap();
    if (localMap != null) {
      localMap.put(paramString, paramObject == null ? nullResource : paramObject);
    }
  }
  


















































  public Object getObject(String paramString, Class paramClass)
  {
    checkNullKey(paramString);
    if (paramClass == null) {
      throw new IllegalArgumentException("null type");
    }
    if (paramClass.isPrimitive()) {
      if (paramClass == Boolean.TYPE) { paramClass = Boolean.class;
      } else if (paramClass == Character.TYPE) { paramClass = Character.class;
      } else if (paramClass == Byte.TYPE) { paramClass = Byte.class;
      } else if (paramClass == Short.TYPE) { paramClass = Short.class;
      } else if (paramClass == Integer.TYPE) { paramClass = Integer.class;
      } else if (paramClass == Long.TYPE) { paramClass = Long.class;
      } else if (paramClass == Float.TYPE) { paramClass = Float.class;
      } else if (paramClass == Double.TYPE) paramClass = Double.class;
    }
    Object localObject1 = null;
    ResourceMap localResourceMap = this;
    



    while (localResourceMap != null) {
      if (localResourceMap.containsResourceKey(paramString)) {
        localObject1 = localResourceMap.getResource(paramString);
        break;
      }
      localResourceMap = localResourceMap.getParent();
    }
    



    if (((localObject1 instanceof String)) && (((String)localObject1).contains("${"))) {
      localObject1 = evaluateStringExpression((String)localObject1);
      localResourceMap.putResource(paramString, localObject1);
    }
    








    if (localObject1 != null) {
      Class localClass = localObject1.getClass();
      if (!paramClass.isAssignableFrom(localClass)) { Object localObject2;
        if ((localObject1 instanceof String)) {
          localObject2 = ResourceConverter.forType(paramClass);
          String str1; if (localObject2 != null) {
            str1 = (String)localObject1;
            try {
              localObject1 = ((ResourceConverter)localObject2).parseString(str1, localResourceMap);
              localResourceMap.putResource(paramString, localObject1);
            }
            catch (ResourceConverter.ResourceConverterException localResourceConverterException) {
              String str2 = "string conversion failed";
              LookupException localLookupException = new LookupException(str2, paramString, paramClass);
              localLookupException.initCause(localResourceConverterException);
              throw localLookupException;
            }
          }
          else {
            str1 = "no StringConverter for required type";
            throw new LookupException(str1, paramString, paramClass);
          }
        }
        else {
          localObject2 = "named resource has wrong type";
          throw new LookupException((String)localObject2, paramString, paramClass);
        }
      }
    }
    return localObject1;
  }
  








  private String evaluateStringExpression(String paramString)
  {
    if (paramString.trim().equals("${null}")) {
      return null;
    }
    StringBuffer localStringBuffer = new StringBuffer();
    int i = 0;int j = 0;
    while ((j = paramString.indexOf("${", i)) != -1) {
      if ((j == 0) || ((j > 0) && (paramString.charAt(j - 1) != '\\'))) {
        int k = paramString.indexOf("}", j);
        String str1; if ((k != -1) && (k > j + 2)) {
          str1 = paramString.substring(j + 2, k);
          String str2 = getString(str1, new Object[0]);
          localStringBuffer.append(paramString.substring(i, j));
          if (str2 != null) {
            localStringBuffer.append(str2);
          }
          else {
            String str3 = String.format("no value for \"%s\" in \"%s\"", new Object[] { str1, paramString });
            throw new LookupException(str3, str1, String.class);
          }
          i = k + 1;
        }
        else {
          str1 = String.format("no closing brace in \"%s\"", new Object[] { paramString });
          throw new LookupException(str1, "<not found>", String.class);
        }
      }
      else {
        localStringBuffer.append(paramString.substring(i, j - 1));
        localStringBuffer.append("${");
        i = j + 2;
      }
    }
    localStringBuffer.append(paramString.substring(i));
    return localStringBuffer.toString();
  }
  




















  public String getString(String paramString, Object... paramVarArgs)
  {
    if (paramVarArgs.length == 0) {
      return (String)getObject(paramString, String.class);
    }
    
    String str = (String)getObject(paramString, String.class);
    return str == null ? null : String.format(str, paramVarArgs);
  }
  










  public final Boolean getBoolean(String paramString)
  {
    return (Boolean)getObject(paramString, Boolean.class);
  }
  









  public final Integer getInteger(String paramString)
  {
    return (Integer)getObject(paramString, Integer.class);
  }
  









  public final Long getLong(String paramString)
  {
    return (Long)getObject(paramString, Long.class);
  }
  









  public final Short getShort(String paramString)
  {
    return (Short)getObject(paramString, Short.class);
  }
  









  public final Byte getByte(String paramString)
  {
    return (Byte)getObject(paramString, Byte.class);
  }
  









  public final Float getFloat(String paramString)
  {
    return (Float)getObject(paramString, Float.class);
  }
  









  public final Double getDouble(String paramString)
  {
    return (Double)getObject(paramString, Double.class);
  }
  













  public final Icon getIcon(String paramString)
  {
    return (Icon)getObject(paramString, Icon.class);
  }
  


































  public final ImageIcon getImageIcon(String paramString)
  {
    return (ImageIcon)getObject(paramString, ImageIcon.class);
  }
  





















  public final Font getFont(String paramString)
  {
    return (Font)getObject(paramString, Font.class);
  }
  


























  public final Color getColor(String paramString)
  {
    return (Color)getObject(paramString, Color.class);
  }
  














  public final KeyStroke getKeyStroke(String paramString)
  {
    return (KeyStroke)getObject(paramString, KeyStroke.class);
  }
  










  public Integer getKeyCode(String paramString)
  {
    KeyStroke localKeyStroke = getKeyStroke(paramString);
    return localKeyStroke != null ? new Integer(localKeyStroke.getKeyCode()) : null;
  }
  




  public static class PropertyInjectionException
    extends RuntimeException
  {
    private final String key;
    



    private final Component component;
    


    private final String propertyName;
    



    public PropertyInjectionException(String paramString1, String paramString2, Component paramComponent, String paramString3)
    {
      super();
      key = paramString2;
      component = paramComponent;
      propertyName = paramString3;
    }
    



    public String getKey()
    {
      return key;
    }
    



    public Component getComponent()
    {
      return component;
    }
    



    public String getPropertyName()
    {
      return propertyName;
    }
  }
  
  private void injectComponentProperty(Component paramComponent, PropertyDescriptor paramPropertyDescriptor, String paramString) {
    Method localMethod = paramPropertyDescriptor.getWriteMethod();
    Class localClass = paramPropertyDescriptor.getPropertyType();
    Object localObject; String str1; if ((localMethod != null) && (localClass != null) && (containsKey(paramString))) {
      localObject = getObject(paramString, localClass);
      str1 = paramPropertyDescriptor.getName();
      
      try
      {
        if (("text".equals(str1)) && ((paramComponent instanceof AbstractButton))) {
          MnemonicText.configure(paramComponent, (String)localObject);
        }
        else if (("text".equals(str1)) && ((paramComponent instanceof JLabel))) {
          MnemonicText.configure(paramComponent, (String)localObject);
        }
        else {
          localMethod.invoke(paramComponent, new Object[] { localObject });
        }
      }
      catch (Exception localException) {
        String str2 = paramPropertyDescriptor.getName();
        String str3 = "property setter failed";
        PropertyInjectionException localPropertyInjectionException = new PropertyInjectionException(str3, paramString, paramComponent, str2);
        localPropertyInjectionException.initCause(localException);
        throw localPropertyInjectionException;
      }
    } else {
      if (localClass != null) {
        localObject = paramPropertyDescriptor.getName();
        str1 = "no value specified for resource";
        throw new PropertyInjectionException(str1, paramString, paramComponent, (String)localObject);
      }
      if (localMethod == null) {
        localObject = paramPropertyDescriptor.getName();
        str1 = "can't set read-only property";
        throw new PropertyInjectionException(str1, paramString, paramComponent, (String)localObject);
      }
    }
  }
  
  private void injectComponentProperties(Component paramComponent) { String str1 = paramComponent.getName();
    Object localObject2; Object localObject3; PropertyDescriptor[] arrayOfPropertyDescriptor; if (str1 != null)
    {


      int i = 0;
      for (Object localObject1 = keySet().iterator(); ((Iterator)localObject1).hasNext();) { String str2 = (String)((Iterator)localObject1).next();
        int j = str2.lastIndexOf(".");
        if ((j != -1) && (str1.equals(str2.substring(0, j)))) {
          i = 1;
          break;
        }
      }
      if (i == 0) {
        return;
      }
      localObject1 = null;
      try {
        localObject1 = Introspector.getBeanInfo(paramComponent.getClass());
      }
      catch (IntrospectionException localIntrospectionException) {
        localObject2 = "introspection failed";
        localObject3 = new PropertyInjectionException((String)localObject2, null, paramComponent, null);
        ((RuntimeException)localObject3).initCause(localIntrospectionException);
        throw ((Throwable)localObject3);
      }
      arrayOfPropertyDescriptor = ((BeanInfo)localObject1).getPropertyDescriptors();
      if ((arrayOfPropertyDescriptor != null) && (arrayOfPropertyDescriptor.length > 0)) {
        for (localObject2 = keySet().iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (String)((Iterator)localObject2).next();
          int k = ((String)localObject3).lastIndexOf(".");
          String str3 = k == -1 ? null : ((String)localObject3).substring(0, k);
          if (str1.equals(str3)) {
            if (k + 1 == ((String)localObject3).length())
            {


              str4 = "component resource lacks property name suffix";
              logger.warning(str4);
              break;
            }
            String str4 = ((String)localObject3).substring(k + 1);
            int m = 0;
            for (PropertyDescriptor localPropertyDescriptor : arrayOfPropertyDescriptor) {
              if (localPropertyDescriptor.getName().equals(str4)) {
                injectComponentProperty(paramComponent, localPropertyDescriptor, (String)localObject3);
                m = 1;
                break;
              }
            }
            if (m == 0) {
              ??? = String.format("[resource %s] component named %s doesn't have a property named %s", new Object[] { localObject3, str1, str4 });
              

              logger.warning((String)???);
            }
          }
        }
      }
    }
  }
  












































  public void injectComponent(Component paramComponent)
  {
    if (paramComponent == null) {
      throw new IllegalArgumentException("null target");
    }
    injectComponentProperties(paramComponent);
  }
  









  public void injectComponents(Component paramComponent)
  {
    injectComponent(paramComponent);
    Object localObject; Component localComponent; if ((paramComponent instanceof JMenu))
    {





      localObject = (JMenu)paramComponent;
      for (localComponent : ((JMenu)localObject).getMenuComponents()) {
        injectComponents(localComponent);
      }
    }
    else if ((paramComponent instanceof Container)) {
      localObject = (Container)paramComponent;
      for (localComponent : ((Container)localObject).getComponents()) {
        injectComponents(localComponent);
      }
    }
  }
  




  public static class InjectFieldException
    extends RuntimeException
  {
    private final Field field;
    


    private final Object target;
    


    private final String key;
    



    public InjectFieldException(String paramString1, Field paramField, Object paramObject, String paramString2)
    {
      super();
      field = paramField;
      target = paramObject;
      key = paramString2;
    }
    



    public Field getField()
    {
      return field;
    }
    



    public Object getTarget()
    {
      return target;
    }
    



    public String getKey()
    {
      return key;
    }
  }
  
  private void injectField(Field paramField, Object paramObject, String paramString) {
    Class localClass = paramField.getType();
    Object localObject1; Object localObject2; Object localObject3; if (localClass.isArray()) {
      localClass = localClass.getComponentType();
      localObject1 = Pattern.compile(paramString + "\\[([\\d]+)\\]");
      ArrayList localArrayList = new ArrayList();
      for (localObject2 = keySet().iterator(); ((Iterator)localObject2).hasNext();) { localObject3 = (String)((Iterator)localObject2).next();
        Matcher localMatcher = ((Pattern)localObject1).matcher((CharSequence)localObject3);
        if (localMatcher.matches())
        {




          Object localObject4 = getObject((String)localObject3, localClass);
          if (!paramField.isAccessible()) {
            paramField.setAccessible(true);
          }
          try {
            int i = Integer.parseInt(localMatcher.group(1));
            Array.set(paramField.get(paramObject), i, localObject4);


          }
          catch (Exception localException2)
          {

            String str = "unable to set array element";
            InjectFieldException localInjectFieldException = new InjectFieldException(str, paramField, paramObject, paramString);
            localInjectFieldException.initCause(localException2);
            throw localInjectFieldException;
          }
        }
      }
    }
    else {
      localObject1 = getObject(paramString, localClass);
      if (localObject1 != null) {
        if (!paramField.isAccessible()) {
          paramField.setAccessible(true);
        }
        try {
          paramField.set(paramObject, localObject1);

        }
        catch (Exception localException1)
        {

          localObject2 = "unable to set field's value";
          localObject3 = new InjectFieldException((String)localObject2, paramField, paramObject, paramString);
          ((InjectFieldException)localObject3).initCause(localException1);
          throw ((Throwable)localObject3);
        }
      }
    }
  }
  




































  public void injectFields(Object paramObject)
  {
    if (paramObject == null) {
      throw new IllegalArgumentException("null target");
    }
    Class localClass = paramObject.getClass();
    if (localClass.isArray()) {
      throw new IllegalArgumentException("array target");
    }
    String str1 = localClass.getSimpleName() + ".";
    for (Field localField : localClass.getDeclaredFields()) {
      Resource localResource = (Resource)localField.getAnnotation(Resource.class);
      if (localResource != null) {
        String str2 = localResource.key();
        String str3 = str1 + localField.getName();
        injectField(localField, paramObject, str3);
      }
    }
  }
  


  static
  {
    ResourceConverter[] arrayOfResourceConverter1 = { new ColorStringConverter(), new IconStringConverter(), new ImageStringConverter(), new FontStringConverter(), new KeyStrokeStringConverter(), new DimensionStringConverter(), new PointStringConverter(), new RectangleStringConverter(), new InsetsStringConverter(), new EmptyBorderStringConverter() };
    










    for (ResourceConverter localResourceConverter : arrayOfResourceConverter1) {
      ResourceConverter.register(localResourceConverter);
    }
  }
  


  private static String resourcePath(String paramString, ResourceMap paramResourceMap)
  {
    String str = paramString;
    if (paramString == null) {
      str = null;
    }
    else if (paramString.startsWith("/")) {
      str = paramString.length() > 1 ? paramString.substring(1) : null;
    }
    else {
      str = paramResourceMap.getResourcesDir() + paramString;
    }
    return str;
  }
  
  private static ImageIcon loadImageIcon(String paramString, ResourceMap paramResourceMap)
    throws ResourceConverter.ResourceConverterException
  {
    String str1 = resourcePath(paramString, paramResourceMap);
    if (str1 == null) {
      localObject = String.format("invalid image/icon path \"%s\"", new Object[] { paramString });
      throw new ResourceConverter.ResourceConverterException((String)localObject, paramString);
    }
    Object localObject = paramResourceMap.getClassLoader().getResource(str1);
    if (localObject != null) {
      return new ImageIcon((URL)localObject);
    }
    
    String str2 = String.format("couldn't find Icon resource \"%s\"", new Object[] { paramString });
    throw new ResourceConverter.ResourceConverterException(str2, paramString);
  }
  
  private static class FontStringConverter extends ResourceConverter
  {
    FontStringConverter() {
      super();
    }
    

    public Object parseString(String paramString, ResourceMap paramResourceMap)
      throws ResourceConverter.ResourceConverterException
    {
      return Font.decode(paramString);
    }
  }
  
  private static class ColorStringConverter extends ResourceConverter {
    ColorStringConverter() {
      super();
    }
    
    private void error(String paramString1, String paramString2, Exception paramException) throws ResourceConverter.ResourceConverterException { throw new ResourceConverter.ResourceConverterException(paramString1, paramString2, paramException); }
    
    private void error(String paramString1, String paramString2) throws ResourceConverter.ResourceConverterException
    {
      error(paramString1, paramString2, null);
    }
    




    public Object parseString(String paramString, ResourceMap paramResourceMap)
      throws ResourceConverter.ResourceConverterException
    {
      Color localColor = null;
      int j; if (paramString.startsWith("#")) {
        switch (paramString.length())
        {
        case 7: 
          localColor = Color.decode(paramString);
          break;
        
        case 9: 
          int i = Integer.decode(paramString.substring(0, 3)).intValue();
          j = Integer.decode("#" + paramString.substring(3)).intValue();
          localColor = new Color(i << 24 | j, true);
          break;
        default: 
          throw new ResourceConverter.ResourceConverterException("invalid #RRGGBB or #AARRGGBB color string", paramString);
        }
      }
      else {
        String[] arrayOfString = paramString.split(",");
        if ((arrayOfString.length < 3) || (arrayOfString.length > 4))
          throw new ResourceConverter.ResourceConverterException("invalid R, G, B[, A] color string", paramString);
        try {
          int k;
          int m;
          if (arrayOfString.length == 4) {
            j = Integer.parseInt(arrayOfString[0].trim());
            k = Integer.parseInt(arrayOfString[1].trim());
            m = Integer.parseInt(arrayOfString[2].trim());
            int n = Integer.parseInt(arrayOfString[3].trim());
            localColor = new Color(j, k, m, n);
          } else {
            j = Integer.parseInt(arrayOfString[0].trim());
            k = Integer.parseInt(arrayOfString[1].trim());
            m = Integer.parseInt(arrayOfString[2].trim());
            localColor = new Color(j, k, m);
          }
        }
        catch (NumberFormatException localNumberFormatException) {
          throw new ResourceConverter.ResourceConverterException("invalid R, G, B[, A] color string", paramString, localNumberFormatException);
        }
      }
      return localColor;
    }
  }
  
  private static class IconStringConverter extends ResourceConverter {
    IconStringConverter() {
      super();
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      return ResourceMap.loadImageIcon(paramString, paramResourceMap);
    }
    
    public boolean supportsType(Class paramClass) {
      return (paramClass.equals(Icon.class)) || (paramClass.equals(ImageIcon.class));
    }
  }
  
  private static class ImageStringConverter extends ResourceConverter {
    ImageStringConverter() {
      super();
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      return ResourceMap.loadImageIcon(paramString, paramResourceMap).getImage();
    }
  }
  
  private static class KeyStrokeStringConverter extends ResourceConverter {
    KeyStrokeStringConverter() {
      super();
    }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) {
      if (paramString.contains("shortcut")) {
        int i = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        paramString = paramString.replaceAll("shortcut", i == 4 ? "meta" : "control");
      }
      return KeyStroke.getKeyStroke(paramString);
    }
  }
  



  private static List<Double> parseDoubles(String paramString1, int paramInt, String paramString2)
    throws ResourceConverter.ResourceConverterException
  {
    String[] arrayOfString1 = paramString1.split(",", paramInt + 1);
    if (arrayOfString1.length != paramInt) {
      throw new ResourceConverter.ResourceConverterException(paramString2, paramString1);
    }
    
    ArrayList localArrayList = new ArrayList(paramInt);
    for (String str : arrayOfString1) {
      try {
        localArrayList.add(Double.valueOf(str));
      }
      catch (NumberFormatException localNumberFormatException) {
        throw new ResourceConverter.ResourceConverterException(paramString2, paramString1, localNumberFormatException);
      }
    }
    return localArrayList;
  }
  
  private static class DimensionStringConverter
    extends ResourceConverter
  {
    DimensionStringConverter() { super(); }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      List localList = ResourceMap.parseDoubles(paramString, 2, "invalid x,y Dimension string");
      Dimension localDimension = new Dimension();
      localDimension.setSize(((Double)localList.get(0)).doubleValue(), ((Double)localList.get(1)).doubleValue());
      return localDimension;
    }
  }
  
  private static class PointStringConverter
    extends ResourceConverter {
    PointStringConverter() { super(); }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      List localList = ResourceMap.parseDoubles(paramString, 2, "invalid x,y Point string");
      Point localPoint = new Point();
      localPoint.setLocation(((Double)localList.get(0)).doubleValue(), ((Double)localList.get(1)).doubleValue());
      return localPoint;
    }
  }
  
  private static class RectangleStringConverter
    extends ResourceConverter {
    RectangleStringConverter() { super(); }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      List localList = ResourceMap.parseDoubles(paramString, 4, "invalid x,y,width,height Rectangle string");
      Rectangle localRectangle = new Rectangle();
      localRectangle.setFrame(((Double)localList.get(0)).doubleValue(), ((Double)localList.get(1)).doubleValue(), ((Double)localList.get(2)).doubleValue(), ((Double)localList.get(3)).doubleValue());
      return localRectangle;
    }
  }
  
  private static class InsetsStringConverter
    extends ResourceConverter {
    InsetsStringConverter() { super(); }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      List localList = ResourceMap.parseDoubles(paramString, 4, "invalid top,left,bottom,right Insets string");
      return new Insets(((Double)localList.get(0)).intValue(), ((Double)localList.get(1)).intValue(), ((Double)localList.get(2)).intValue(), ((Double)localList.get(3)).intValue());
    }
  }
  
  private static class EmptyBorderStringConverter
    extends ResourceConverter {
    EmptyBorderStringConverter() { super(); }
    
    public Object parseString(String paramString, ResourceMap paramResourceMap) throws ResourceConverter.ResourceConverterException {
      List localList = ResourceMap.parseDoubles(paramString, 4, "invalid top,left,bottom,right EmptyBorder string");
      return new EmptyBorder(((Double)localList.get(0)).intValue(), ((Double)localList.get(1)).intValue(), ((Double)localList.get(2)).intValue(), ((Double)localList.get(3)).intValue());
    }
  }
}
