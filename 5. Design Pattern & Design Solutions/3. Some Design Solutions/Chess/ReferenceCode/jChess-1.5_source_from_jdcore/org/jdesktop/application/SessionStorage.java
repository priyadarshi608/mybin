package org.jdesktop.application;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;






































































public class SessionStorage
{
  private static Logger logger = Logger.getLogger(SessionStorage.class.getName());
  














  private final Map<Class, Property> propertyMap;
  













  private final ApplicationContext context;
  














  protected SessionStorage(ApplicationContext paramApplicationContext)
  {
    if (paramApplicationContext == null) {
      throw new IllegalArgumentException("null context");
    }
    context = paramApplicationContext;
    propertyMap = new HashMap();
    propertyMap.put(Window.class, new WindowProperty());
    propertyMap.put(JTabbedPane.class, new TabbedPaneProperty());
    propertyMap.put(JSplitPane.class, new SplitPaneProperty());
    propertyMap.put(JTable.class, new TableProperty());
  }
  
  protected final ApplicationContext getContext()
  {
    return context;
  }
  
  private void checkSaveRestoreArgs(Component paramComponent, String paramString) {
    if (paramComponent == null) {
      throw new IllegalArgumentException("null root");
    }
    if (paramString == null) {
      throw new IllegalArgumentException("null fileName");
    }
  }
  

  private String getComponentName(Component paramComponent)
  {
    return paramComponent.getName();
  }
  
























  private String getComponentPathname(Component paramComponent)
  {
    String str = getComponentName(paramComponent);
    if (str == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder(str);
    while ((paramComponent.getParent() != null) && (!(paramComponent instanceof Window)) && (!(paramComponent instanceof Applet))) {
      paramComponent = paramComponent.getParent();
      str = getComponentName(paramComponent);
      if (str == null) {
        int i = paramComponent.getParent().getComponentZOrder(paramComponent);
        if (i >= 0) {
          Class localClass = paramComponent.getClass();
          str = localClass.getSimpleName();
          if (str.length() == 0) {
            str = "Anonymous" + localClass.getSuperclass().getSimpleName();
          }
          str = str + i;

        }
        else
        {
          logger.warning("Couldn't compute pathname for " + paramComponent);
          return null;
        }
      }
      localStringBuilder.append("/").append(str);
    }
    return localStringBuilder.toString();
  }
  






  private void saveTree(List<Component> paramList, Map<String, Object> paramMap)
  {
    ArrayList localArrayList = new ArrayList();
    for (Component localComponent : paramList) { Object localObject1;
      if (localComponent != null) {
        localObject1 = getProperty(localComponent);
        if (localObject1 != null) {
          String str = getComponentPathname(localComponent);
          if (str != null) {
            Object localObject2 = ((Property)localObject1).getSessionState(localComponent);
            if (localObject2 != null) {
              paramMap.put(str, localObject2);
            }
          }
        }
      }
      if ((localComponent instanceof Container)) {
        localObject1 = ((Container)localComponent).getComponents();
        if ((localObject1 != null) && (localObject1.length > 0)) {
          Collections.addAll(localArrayList, (Object[])localObject1);
        }
      }
    }
    if (localArrayList.size() > 0) {
      saveTree(localArrayList, paramMap);
    }
  }
  













































  public void save(Component paramComponent, String paramString)
    throws IOException
  {
    checkSaveRestoreArgs(paramComponent, paramString);
    HashMap localHashMap = new HashMap();
    saveTree(Collections.singletonList(paramComponent), localHashMap);
    LocalStorage localLocalStorage = getContext().getLocalStorage();
    localLocalStorage.save(localHashMap, paramString);
  }
  







  private void restoreTree(List<Component> paramList, Map<String, Object> paramMap)
  {
    ArrayList localArrayList = new ArrayList();
    for (Component localComponent : paramList) { Object localObject1;
      if (localComponent != null) {
        localObject1 = getProperty(localComponent);
        if (localObject1 != null) {
          String str = getComponentPathname(localComponent);
          if (str != null) {
            Object localObject2 = paramMap.get(str);
            if (localObject2 != null) {
              ((Property)localObject1).setSessionState(localComponent, localObject2);
            }
            else {
              logger.warning("No saved state for " + localComponent);
            }
          }
        }
      }
      if ((localComponent instanceof Container)) {
        localObject1 = ((Container)localComponent).getComponents();
        if ((localObject1 != null) && (localObject1.length > 0)) {
          Collections.addAll(localArrayList, (Object[])localObject1);
        }
      }
    }
    if (localArrayList.size() > 0) {
      restoreTree(localArrayList, paramMap);
    }
  }
  














  public void restore(Component paramComponent, String paramString)
    throws IOException
  {
    checkSaveRestoreArgs(paramComponent, paramString);
    LocalStorage localLocalStorage = getContext().getLocalStorage();
    Map localMap = (Map)localLocalStorage.load(paramString);
    if (localMap != null) {
      restoreTree(Collections.singletonList(paramComponent), localMap);
    }
  }
  












  public static abstract interface Property
  {
    public abstract Object getSessionState(Component paramComponent);
    












    public abstract void setSessionState(Component paramComponent, Object paramObject);
  }
  











  public static class WindowState
  {
    private final Rectangle bounds;
    










    private Rectangle gcBounds = null;
    private int screenCount;
    private int frameState = 0;
    
    public WindowState() { bounds = new Rectangle(); }
    
    public WindowState(Rectangle paramRectangle1, Rectangle paramRectangle2, int paramInt1, int paramInt2) {
      if (paramRectangle1 == null) {
        throw new IllegalArgumentException("null bounds");
      }
      if (paramInt1 < 1) {
        throw new IllegalArgumentException("invalid screenCount");
      }
      bounds = paramRectangle1;
      gcBounds = paramRectangle2;
      screenCount = paramInt1;
      frameState = paramInt2;
    }
    
    public Rectangle getBounds() { return new Rectangle(bounds); }
    
    public void setBounds(Rectangle paramRectangle) {
      bounds.setBounds(paramRectangle);
    }
    
    public int getScreenCount() { return screenCount; }
    
    public void setScreenCount(int paramInt) {
      screenCount = paramInt;
    }
    
    public int getFrameState() { return frameState; }
    
    public void setFrameState(int paramInt) {
      frameState = paramInt;
    }
    
    public Rectangle getGraphicsConfigurationBounds() { return gcBounds == null ? null : new Rectangle(gcBounds); }
    
    public void setGraphicsConfigurationBounds(Rectangle paramRectangle) {
      gcBounds = (paramRectangle == null ? null : new Rectangle(paramRectangle));
    }
  }
  








  public static class WindowProperty
    implements SessionStorage.Property
  {
    public WindowProperty() {}
    







    private void checkComponent(Component paramComponent)
    {
      if (paramComponent == null) {
        throw new IllegalArgumentException("null component");
      }
      if (!(paramComponent instanceof Window)) {
        throw new IllegalArgumentException("invalid component");
      }
    }
    
    private int getScreenCount() {
      return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
    }
    












    public Object getSessionState(Component paramComponent)
    {
      checkComponent(paramComponent);
      int i = 0;
      if ((paramComponent instanceof Frame)) {
        i = ((Frame)paramComponent).getExtendedState();
      }
      GraphicsConfiguration localGraphicsConfiguration = paramComponent.getGraphicsConfiguration();
      Rectangle localRectangle1 = localGraphicsConfiguration == null ? null : localGraphicsConfiguration.getBounds();
      Rectangle localRectangle2 = paramComponent.getBounds();
      



      if (((paramComponent instanceof JFrame)) && (0 != (i & 0x6))) {
        String str = "WindowState.normalBounds";
        Object localObject = ((JFrame)paramComponent).getRootPane().getClientProperty(str);
        if ((localObject instanceof Rectangle)) {
          localRectangle2 = (Rectangle)localObject;
        }
      }
      return new SessionStorage.WindowState(localRectangle2, localRectangle1, getScreenCount(), i);
    }
    

























    public void setSessionState(Component paramComponent, Object paramObject)
    {
      checkComponent(paramComponent);
      if ((paramObject != null) && (!(paramObject instanceof SessionStorage.WindowState))) {
        throw new IllegalArgumentException("invalid state");
      }
      Window localWindow = (Window)paramComponent;
      if ((!localWindow.isLocationByPlatform()) && (paramObject != null)) {
        SessionStorage.WindowState localWindowState = (SessionStorage.WindowState)paramObject;
        Rectangle localRectangle1 = localWindowState.getGraphicsConfigurationBounds();
        int i = localWindowState.getScreenCount();
        GraphicsConfiguration localGraphicsConfiguration = paramComponent.getGraphicsConfiguration();
        Rectangle localRectangle2 = localGraphicsConfiguration == null ? null : localGraphicsConfiguration.getBounds();
        int j = getScreenCount();
        if ((localRectangle1 != null) && (localRectangle1.equals(localRectangle2)) && (i == j)) {
          boolean bool = true;
          if ((localWindow instanceof Frame)) {
            bool = ((Frame)localWindow).isResizable();
          }
          else if ((localWindow instanceof Dialog)) {
            bool = ((Dialog)localWindow).isResizable();
          }
          if (bool) {
            localWindow.setBounds(localWindowState.getBounds());
          }
        }
        if ((localWindow instanceof Frame)) {
          ((Frame)localWindow).setExtendedState(localWindowState.getFrameState());
        }
      }
    }
  }
  



  public static class TabbedPaneState
  {
    private int selectedIndex;
    


    private int tabCount;
    


    public TabbedPaneState()
    {
      selectedIndex = -1;
      tabCount = 0;
    }
    
    public TabbedPaneState(int paramInt1, int paramInt2) { if (paramInt2 < 0) {
        throw new IllegalArgumentException("invalid tabCount");
      }
      if ((paramInt1 < -1) || (paramInt1 > paramInt2)) {
        throw new IllegalArgumentException("invalid selectedIndex");
      }
      selectedIndex = paramInt1;
      tabCount = paramInt2; }
    
    public int getSelectedIndex() { return selectedIndex; }
    
    public void setSelectedIndex(int paramInt) { if (paramInt < -1) {
        throw new IllegalArgumentException("invalid selectedIndex");
      }
      selectedIndex = paramInt; }
    
    public int getTabCount() { return tabCount; }
    
    public void setTabCount(int paramInt) { if (paramInt < 0) {
        throw new IllegalArgumentException("invalid tabCount");
      }
      tabCount = paramInt;
    }
  }
  








  public static class TabbedPaneProperty
    implements SessionStorage.Property
  {
    public TabbedPaneProperty() {}
    







    private void checkComponent(Component paramComponent)
    {
      if (paramComponent == null) {
        throw new IllegalArgumentException("null component");
      }
      if (!(paramComponent instanceof JTabbedPane)) {
        throw new IllegalArgumentException("invalid component");
      }
    }
    












    public Object getSessionState(Component paramComponent)
    {
      checkComponent(paramComponent);
      JTabbedPane localJTabbedPane = (JTabbedPane)paramComponent;
      return new SessionStorage.TabbedPaneState(localJTabbedPane.getSelectedIndex(), localJTabbedPane.getTabCount());
    }
    













    public void setSessionState(Component paramComponent, Object paramObject)
    {
      checkComponent(paramComponent);
      if ((paramObject != null) && (!(paramObject instanceof SessionStorage.TabbedPaneState))) {
        throw new IllegalArgumentException("invalid state");
      }
      JTabbedPane localJTabbedPane = (JTabbedPane)paramComponent;
      SessionStorage.TabbedPaneState localTabbedPaneState = (SessionStorage.TabbedPaneState)paramObject;
      if (localJTabbedPane.getTabCount() == localTabbedPaneState.getTabCount()) {
        localJTabbedPane.setSelectedIndex(localTabbedPaneState.getSelectedIndex());
      }
    }
  }
  











  public static class SplitPaneState
  {
    private int dividerLocation = -1;
    private int orientation = 1;
    
    private void checkOrientation(int paramInt) { if ((paramInt != 1) && (paramInt != 0))
      {
        throw new IllegalArgumentException("invalid orientation"); }
    }
    
    public SplitPaneState() {}
    
    public SplitPaneState(int paramInt1, int paramInt2) { checkOrientation(paramInt2);
      if (paramInt1 < -1) {
        throw new IllegalArgumentException("invalid dividerLocation");
      }
      dividerLocation = paramInt1;
      orientation = paramInt2; }
    
    public int getDividerLocation() { return dividerLocation; }
    
    public void setDividerLocation(int paramInt) { if (paramInt < -1) {
        throw new IllegalArgumentException("invalid dividerLocation");
      }
      dividerLocation = paramInt; }
    
    public int getOrientation() { return orientation; }
    
    public void setOrientation(int paramInt) { checkOrientation(paramInt);
      orientation = paramInt;
    }
  }
  








  public static class SplitPaneProperty
    implements SessionStorage.Property
  {
    public SplitPaneProperty() {}
    







    private void checkComponent(Component paramComponent)
    {
      if (paramComponent == null) {
        throw new IllegalArgumentException("null component");
      }
      if (!(paramComponent instanceof JSplitPane)) {
        throw new IllegalArgumentException("invalid component");
      }
    }
    















    public Object getSessionState(Component paramComponent)
    {
      checkComponent(paramComponent);
      JSplitPane localJSplitPane = (JSplitPane)paramComponent;
      return new SessionStorage.SplitPaneState(localJSplitPane.getUI().getDividerLocation(localJSplitPane), localJSplitPane.getOrientation());
    }
    













    public void setSessionState(Component paramComponent, Object paramObject)
    {
      checkComponent(paramComponent);
      if ((paramObject != null) && (!(paramObject instanceof SessionStorage.SplitPaneState))) {
        throw new IllegalArgumentException("invalid state");
      }
      JSplitPane localJSplitPane = (JSplitPane)paramComponent;
      SessionStorage.SplitPaneState localSplitPaneState = (SessionStorage.SplitPaneState)paramObject;
      if (localJSplitPane.getOrientation() == localSplitPaneState.getOrientation()) {
        localJSplitPane.setDividerLocation(localSplitPaneState.getDividerLocation());
      }
    }
  }
  








  public static class TableState
  {
    private int[] columnWidths = new int[0];
    
    private int[] copyColumnWidths(int[] paramArrayOfInt) { if (paramArrayOfInt == null) {
        throw new IllegalArgumentException("invalid columnWidths");
      }
      int[] arrayOfInt = new int[paramArrayOfInt.length];
      System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, paramArrayOfInt.length);
      return arrayOfInt; }
    
    public TableState() {}
    
    public TableState(int[] paramArrayOfInt) { columnWidths = copyColumnWidths(paramArrayOfInt); }
    
    public int[] getColumnWidths() { return copyColumnWidths(columnWidths); }
    
    public void setColumnWidths(int[] paramArrayOfInt) { columnWidths = copyColumnWidths(paramArrayOfInt); }
  }
  









  public static class TableProperty
    implements SessionStorage.Property
  {
    public TableProperty() {}
    









    private void checkComponent(Component paramComponent)
    {
      if (paramComponent == null) {
        throw new IllegalArgumentException("null component");
      }
      if (!(paramComponent instanceof JTable)) {
        throw new IllegalArgumentException("invalid component");
      }
    }
    















    public Object getSessionState(Component paramComponent)
    {
      checkComponent(paramComponent);
      JTable localJTable = (JTable)paramComponent;
      int[] arrayOfInt = new int[localJTable.getColumnCount()];
      int i = 0;
      for (int j = 0; j < arrayOfInt.length; j++) {
        TableColumn localTableColumn = localJTable.getColumnModel().getColumn(j);
        arrayOfInt[j] = (localTableColumn.getResizable() ? localTableColumn.getWidth() : -1);
        if (localTableColumn.getResizable()) {
          i = 1;
        }
      }
      return i != 0 ? new SessionStorage.TableState(arrayOfInt) : null;
    }
    












    public void setSessionState(Component paramComponent, Object paramObject)
    {
      checkComponent(paramComponent);
      if (!(paramObject instanceof SessionStorage.TableState)) {
        throw new IllegalArgumentException("invalid state");
      }
      JTable localJTable = (JTable)paramComponent;
      int[] arrayOfInt = ((SessionStorage.TableState)paramObject).getColumnWidths();
      if (localJTable.getColumnCount() == arrayOfInt.length) {
        for (int i = 0; i < arrayOfInt.length; i++) {
          if (arrayOfInt[i] != -1) {
            TableColumn localTableColumn = localJTable.getColumnModel().getColumn(i);
            if (localTableColumn.getResizable()) {
              localTableColumn.setPreferredWidth(arrayOfInt[i]);
            }
          }
        }
      }
    }
  }
  
  private void checkClassArg(Class paramClass) {
    if (paramClass == null) {
      throw new IllegalArgumentException("null class");
    }
  }
  

















  public Property getProperty(Class paramClass)
  {
    checkClassArg(paramClass);
    while (paramClass != null) {
      Property localProperty = (Property)propertyMap.get(paramClass);
      if (localProperty != null) return localProperty;
      paramClass = paramClass.getSuperclass();
    }
    return null;
  }
  
















  public void putProperty(Class paramClass, Property paramProperty)
  {
    checkClassArg(paramClass);
    propertyMap.put(paramClass, paramProperty);
  }
  































  public final Property getProperty(Component paramComponent)
  {
    if (paramComponent == null) {
      throw new IllegalArgumentException("null component");
    }
    if ((paramComponent instanceof Property)) {
      return (Property)paramComponent;
    }
    
    Object localObject1 = null;
    if ((paramComponent instanceof JComponent)) {
      Object localObject2 = ((JComponent)paramComponent).getClientProperty(Property.class);
      localObject1 = (localObject2 instanceof Property) ? (Property)localObject2 : null;
    }
    return localObject1 != null ? localObject1 : getProperty(paramComponent.getClass());
  }
}
