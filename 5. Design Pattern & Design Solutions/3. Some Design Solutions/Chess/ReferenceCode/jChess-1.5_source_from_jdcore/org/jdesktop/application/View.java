package org.jdesktop.application;

import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JToolBar;












































public class View
  extends AbstractBean
{
  private static final Logger logger = Logger.getLogger(View.class.getName());
  private final Application application;
  private ResourceMap resourceMap = null;
  private JRootPane rootPane = null;
  private JComponent component = null;
  private JMenuBar menuBar = null;
  private List<JToolBar> toolBars = Collections.emptyList();
  private JComponent toolBarsPanel = null;
  private JComponent statusBar = null;
  






  public View(Application paramApplication)
  {
    if (paramApplication == null) {
      throw new IllegalArgumentException("null application");
    }
    application = paramApplication;
  }
  







  public final Application getApplication()
  {
    return application;
  }
  









  public final ApplicationContext getContext()
  {
    return getApplication().getContext();
  }
  






  public ResourceMap getResourceMap()
  {
    if (resourceMap == null) {
      resourceMap = getContext().getResourceMap(getClass(), View.class);
    }
    return resourceMap;
  }
  











  public JRootPane getRootPane()
  {
    if (rootPane == null) {
      rootPane = new JRootPane();
      rootPane.setOpaque(true);
    }
    return rootPane;
  }
  
  private void replaceContentPaneChild(JComponent paramJComponent1, JComponent paramJComponent2, String paramString) {
    Container localContainer = getRootPane().getContentPane();
    if (paramJComponent1 != null) {
      localContainer.remove(paramJComponent1);
    }
    if (paramJComponent2 != null) {
      localContainer.add(paramJComponent2, paramString);
    }
  }
  





  public JComponent getComponent()
  {
    return component;
  }
  










  public void setComponent(JComponent paramJComponent)
  {
    JComponent localJComponent = component;
    component = paramJComponent;
    replaceContentPaneChild(localJComponent, component, "Center");
    firePropertyChange("component", localJComponent, component);
  }
  





  public JMenuBar getMenuBar()
  {
    return menuBar;
  }
  
  public void setMenuBar(JMenuBar paramJMenuBar) {
    JMenuBar localJMenuBar = getMenuBar();
    menuBar = paramJMenuBar;
    getRootPane().setJMenuBar(paramJMenuBar);
    firePropertyChange("menuBar", localJMenuBar, paramJMenuBar);
  }
  
  public List<JToolBar> getToolBars() {
    return toolBars;
  }
  
  public void setToolBars(List<JToolBar> paramList) {
    if (paramList == null) {
      throw new IllegalArgumentException("null toolbars");
    }
    List localList = getToolBars();
    toolBars = Collections.unmodifiableList(new ArrayList(paramList));
    JComponent localJComponent = toolBarsPanel;
    Object localObject = null;
    if (toolBars.size() == 1) {
      localObject = (JComponent)paramList.get(0);
    }
    else if (toolBars.size() > 1) {
      localObject = new JPanel();
      for (JToolBar localJToolBar : toolBars) {
        ((JComponent)localObject).add(localJToolBar);
      }
    }
    replaceContentPaneChild(localJComponent, (JComponent)localObject, "North");
    firePropertyChange("toolBars", localList, toolBars);
  }
  
  public final JToolBar getToolBar() {
    List localList = getToolBars();
    return localList.size() == 0 ? null : (JToolBar)localList.get(0);
  }
  
  public final void setToolBar(JToolBar paramJToolBar) {
    JToolBar localJToolBar = getToolBar();
    List localList = Collections.emptyList();
    if (paramJToolBar != null) {
      localList = Collections.singletonList(paramJToolBar);
    }
    setToolBars(localList);
    firePropertyChange("toolBar", localJToolBar, paramJToolBar);
  }
  
  public JComponent getStatusBar() {
    return statusBar;
  }
  
  public void setStatusBar(JComponent paramJComponent) {
    JComponent localJComponent = statusBar;
    statusBar = paramJComponent;
    replaceContentPaneChild(localJComponent, statusBar, "South");
    firePropertyChange("statusBar", localJComponent, statusBar);
  }
}
