package org.jdesktop.application;

import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;





































































public abstract class SingleFrameApplication
  extends Application
{
  private static final Logger logger = Logger.getLogger(SingleFrameApplication.class.getName());
  private ResourceMap appResources = null;
  











  public SingleFrameApplication() {}
  











  public final JFrame getMainFrame()
  {
    return getMainView().getFrame();
  }
  



















  protected final void setMainFrame(JFrame paramJFrame)
  {
    getMainView().setFrame(paramJFrame);
  }
  
  private String sessionFilename(Window paramWindow) {
    if (paramWindow == null) {
      return null;
    }
    
    String str = paramWindow.getName();
    return str + ".session.xml";
  }
  



















  protected void configureWindow(Window paramWindow)
  {
    getContext().getResourceMap().injectComponents(paramWindow);
  }
  
  private void initRootPaneContainer(RootPaneContainer paramRootPaneContainer) {
    JRootPane localJRootPane = paramRootPaneContainer.getRootPane();
    
    String str1 = "SingleFrameApplication.initRootPaneContainer";
    if (localJRootPane.getClientProperty(str1) != null) {
      return;
    }
    localJRootPane.putClientProperty(str1, Boolean.TRUE);
    
    Container localContainer = localJRootPane.getParent();
    if ((localContainer instanceof Window)) {
      configureWindow((Window)localContainer);
    }
    
    JFrame localJFrame = getMainFrame();
    Object localObject; if (paramRootPaneContainer == localJFrame) {
      localJFrame.addWindowListener(new MainFrameListener(null));
      localJFrame.setDefaultCloseOperation(0);
    }
    else if ((localContainer instanceof Window)) {
      localObject = (Window)localContainer;
      ((Window)localObject).addHierarchyListener(new SecondaryWindowListener(null));
    }
    
    if ((localContainer instanceof JFrame)) {
      localContainer.addComponentListener(new FrameBoundsListener(null));
    }
    
    if ((localContainer instanceof Window)) {
      localObject = (Window)localContainer;
      if ((!localContainer.isValid()) || (localContainer.getWidth() == 0) || (localContainer.getHeight() == 0)) {
        ((Window)localObject).pack();
      }
      if ((!((Window)localObject).isLocationByPlatform()) && (localContainer.getX() == 0) && (localContainer.getY() == 0)) {
        Window localWindow = ((Window)localObject).getOwner();
        if (localWindow == null) {
          localWindow = localObject != localJFrame ? localJFrame : null;
        }
        ((Window)localObject).setLocationRelativeTo(localWindow);
      }
    }
    
    if ((localContainer instanceof Window)) {
      localObject = sessionFilename((Window)localContainer);
      if (localObject != null) {
        try {
          getContext().getSessionStorage().restore(localContainer, (String)localObject);
        }
        catch (Exception localException) {
          String str2 = String.format("couldn't restore sesssion [%s]", new Object[] { localObject });
          logger.log(Level.WARNING, str2, localException);
        }
      }
    }
  }
  





















  protected void show(JComponent paramJComponent)
  {
    if (paramJComponent == null) {
      throw new IllegalArgumentException("null JComponent");
    }
    JFrame localJFrame = getMainFrame();
    localJFrame.getContentPane().add(paramJComponent, "Center");
    initRootPaneContainer(localJFrame);
    localJFrame.setVisible(true);
  }
  

















  public void show(JDialog paramJDialog)
  {
    if (paramJDialog == null) {
      throw new IllegalArgumentException("null JDialog");
    }
    initRootPaneContainer(paramJDialog);
    paramJDialog.setVisible(true);
  }
  
















  public void show(JFrame paramJFrame)
  {
    if (paramJFrame == null) {
      throw new IllegalArgumentException("null JFrame");
    }
    initRootPaneContainer(paramJFrame);
    paramJFrame.setVisible(true);
  }
  
  private void saveSession(Window paramWindow) {
    String str = sessionFilename(paramWindow);
    if (str != null) {
      try {
        getContext().getSessionStorage().save(paramWindow, str);
      }
      catch (IOException localIOException) {
        logger.log(Level.WARNING, "couldn't save sesssion", localIOException);
      }
    }
  }
  
  private boolean isVisibleWindow(Window paramWindow) {
    return (paramWindow.isVisible()) && (((paramWindow instanceof JFrame)) || ((paramWindow instanceof JDialog)) || ((paramWindow instanceof JWindow)));
  }
  





  private List<Window> getVisibleSecondaryWindows()
  {
    ArrayList localArrayList = new ArrayList();
    Method localMethod = null;
    try {
      localMethod = Window.class.getMethod("getWindows", new Class[0]);
    } catch (Exception localException1) {}
    Object localObject1;
    Window localWindow;
    if (localMethod != null) {
      localObject1 = null;
      try {
        localObject1 = (Window[])localMethod.invoke(null, new Object[0]);
      }
      catch (Exception localException2) {
        throw new Error("HCTB - can't get top level windows list", localException2);
      }
      if (localObject1 != null) {
        for (localWindow : localObject1) {
          if (isVisibleWindow(localWindow)) {
            localArrayList.add(localWindow);
          }
        }
      }
    }
    else {
      localObject1 = Frame.getFrames();
      if (localObject1 != null) {
        for (localWindow : localObject1) {
          if (isVisibleWindow(localWindow)) {
            localArrayList.add(localWindow);
          }
        }
      }
    }
    return localArrayList;
  }
  




  protected void shutdown()
  {
    saveSession(getMainFrame());
    for (Window localWindow : getVisibleSecondaryWindows())
      saveSession(localWindow);
  }
  
  private class MainFrameListener extends WindowAdapter {
    private MainFrameListener() {}
    
    public void windowClosing(WindowEvent paramWindowEvent) { exit(paramWindowEvent); }
  }
  


  private class SecondaryWindowListener
    implements HierarchyListener
  {
    private SecondaryWindowListener() {}
    


    public void hierarchyChanged(HierarchyEvent paramHierarchyEvent)
    {
      if (((paramHierarchyEvent.getChangeFlags() & 0x4) != 0L) && 
        ((paramHierarchyEvent.getSource() instanceof Window))) {
        Window localWindow = (Window)paramHierarchyEvent.getSource();
        if (!localWindow.isShowing()) {
          SingleFrameApplication.this.saveSession(localWindow);
        }
      }
    }
  }
  

  private static class FrameBoundsListener
    implements ComponentListener
  {
    private FrameBoundsListener() {}
    
    private void maybeSaveFrameSize(ComponentEvent paramComponentEvent)
    {
      if ((paramComponentEvent.getComponent() instanceof JFrame)) {
        JFrame localJFrame = (JFrame)paramComponentEvent.getComponent();
        if ((localJFrame.getExtendedState() & 0x6) == 0) {
          String str = "WindowState.normalBounds";
          localJFrame.getRootPane().putClientProperty(str, localJFrame.getBounds());
        }
      } }
    
    public void componentResized(ComponentEvent paramComponentEvent) { maybeSaveFrameSize(paramComponentEvent); }
    

    public void componentMoved(ComponentEvent paramComponentEvent) {}
    

    public void componentHidden(ComponentEvent paramComponentEvent) {}
    

    public void componentShown(ComponentEvent paramComponentEvent) {}
  }
  
  private FrameView mainView = null;
  
  public FrameView getMainView() {
    if (mainView == null) {
      mainView = new FrameView(this);
    }
    return mainView;
  }
  
  public void show(View paramView) {
    if ((mainView == null) && ((paramView instanceof FrameView))) {
      mainView = ((FrameView)paramView);
    }
    RootPaneContainer localRootPaneContainer = (RootPaneContainer)paramView.getRootPane().getParent();
    initRootPaneContainer(localRootPaneContainer);
    ((Window)localRootPaneContainer).setVisible(true);
  }
}
