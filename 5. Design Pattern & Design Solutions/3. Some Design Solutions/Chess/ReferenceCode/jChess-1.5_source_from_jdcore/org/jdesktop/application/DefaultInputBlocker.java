package org.jdesktop.application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JTextArea;
import javax.swing.RootPaneContainer;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;






final class DefaultInputBlocker
  extends Task.InputBlocker
{
  private static final Logger logger = Logger.getLogger(DefaultInputBlocker.class.getName());
  private JDialog modalDialog = null;
  
  DefaultInputBlocker(Task paramTask, Task.BlockingScope paramBlockingScope, Object paramObject, ApplicationAction paramApplicationAction) {
    super(paramTask, paramBlockingScope, paramObject, paramApplicationAction);
  }
  
  private void setActionTargetBlocked(boolean paramBoolean) {
    Action localAction = (Action)getTarget();
    localAction.setEnabled(!paramBoolean);
  }
  
  private void setComponentTargetBlocked(boolean paramBoolean) {
    Component localComponent = (Component)getTarget();
    localComponent.setEnabled(!paramBoolean);
  }
  



  private void blockingDialogComponents(Component paramComponent, List<Component> paramList)
  {
    String str = paramComponent.getName();
    if ((str != null) && (str.startsWith("BlockingDialog"))) {
      paramList.add(paramComponent);
    }
    if ((paramComponent instanceof Container)) {
      for (Component localComponent : ((Container)paramComponent).getComponents())
        blockingDialogComponents(localComponent, paramList);
    }
  }
  
  private List<Component> blockingDialogComponents(Component paramComponent) {
    ArrayList localArrayList = new ArrayList();
    blockingDialogComponents(paramComponent, localArrayList);
    return localArrayList;
  }
  



  private void injectBlockingDialogComponents(Component paramComponent)
  {
    ResourceMap localResourceMap1 = getTask().getResourceMap();
    if (localResourceMap1 != null) {
      localResourceMap1.injectComponents(paramComponent);
    }
    ApplicationAction localApplicationAction = getAction();
    if (localApplicationAction != null) {
      ResourceMap localResourceMap2 = localApplicationAction.getResourceMap();
      String str = localApplicationAction.getName();
      for (Component localComponent : blockingDialogComponents(paramComponent)) {
        localComponent.setName(str + "." + localComponent.getName());
      }
      localResourceMap2.injectComponents(paramComponent);
    }
  }
  


















  private JDialog createBlockingDialog()
  {
    JOptionPane localJOptionPane = new JOptionPane();
    


    if (getTask().getUserCanCancel()) {
      localObject1 = new JButton();
      ((JButton)localObject1).setName("BlockingDialog.cancelButton");
      localObject2 = new ActionListener() {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
          getTask().cancel(true);
        }
      };
      ((JButton)localObject1).addActionListener((ActionListener)localObject2);
      localJOptionPane.setOptions(new Object[] { localObject1 });
    }
    else {
      localJOptionPane.setOptions(new Object[0]);
    }
    


    Object localObject1 = (Component)getTarget();
    Object localObject2 = getTask().getTitle();
    Object localObject3 = localObject2 == null ? "BlockingDialog" : localObject2;
    final JDialog localJDialog = localJOptionPane.createDialog((Component)localObject1, localObject3);
    localJDialog.setModal(true);
    localJDialog.setName("BlockingDialog");
    localJDialog.setDefaultCloseOperation(0);
    WindowAdapter local2 = new WindowAdapter() {
      public void windowClosing(WindowEvent paramAnonymousWindowEvent) {
        if (getTask().getUserCanCancel()) {
          getTask().cancel(true);
          localJDialog.setVisible(false);
        }
      }
    };
    localJDialog.addWindowListener(local2);
    localJOptionPane.setName("BlockingDialog.optionPane");
    injectBlockingDialogComponents(localJDialog);
    


    recreateOptionPaneMessage(localJOptionPane);
    localJDialog.pack();
    return localJDialog;
  }
  





  private void recreateOptionPaneMessage(JOptionPane paramJOptionPane)
  {
    Object localObject = paramJOptionPane.getMessage();
    if ((localObject instanceof String)) {
      Font localFont = paramJOptionPane.getFont();
      final JTextArea localJTextArea = new JTextArea((String)localObject);
      localJTextArea.setFont(localFont);
      int i = localJTextArea.getFontMetrics(localFont).getHeight();
      Insets localInsets = new Insets(0, 0, i, 24);
      localJTextArea.setMargin(localInsets);
      localJTextArea.setEditable(false);
      localJTextArea.setWrapStyleWord(true);
      localJTextArea.setBackground(paramJOptionPane.getBackground());
      JPanel localJPanel = new JPanel(new BorderLayout());
      localJPanel.add(localJTextArea, "Center");
      final JProgressBar localJProgressBar = new JProgressBar();
      localJProgressBar.setName("BlockingDialog.progressBar");
      localJProgressBar.setIndeterminate(true);
      PropertyChangeListener local3 = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent paramAnonymousPropertyChangeEvent) {
          if ("progress".equals(paramAnonymousPropertyChangeEvent.getPropertyName())) {
            localJProgressBar.setIndeterminate(false);
            localJProgressBar.setValue(((Integer)paramAnonymousPropertyChangeEvent.getNewValue()).intValue());
            DefaultInputBlocker.this.updateStatusBarString(localJProgressBar);
          }
          else if ("message".equals(paramAnonymousPropertyChangeEvent.getPropertyName())) {
            localJTextArea.setText((String)paramAnonymousPropertyChangeEvent.getNewValue());
          }
        }
      };
      getTask().addPropertyChangeListener(local3);
      localJPanel.add(localJProgressBar, "South");
      injectBlockingDialogComponents(localJPanel);
      paramJOptionPane.setMessage(localJPanel);
    }
  }
  
  private void updateStatusBarString(JProgressBar paramJProgressBar) {
    if (!paramJProgressBar.isStringPainted()) {
      return;
    }
    





    String str1 = "progressBarStringFormat";
    if (paramJProgressBar.getClientProperty(str1) == null) {
      paramJProgressBar.putClientProperty(str1, paramJProgressBar.getString());
    }
    String str2 = (String)paramJProgressBar.getClientProperty(str1);
    if (paramJProgressBar.getValue() <= 0) {
      paramJProgressBar.setString("");
    }
    else if (str2 == null) {
      paramJProgressBar.setString(null);
    }
    else {
      double d = paramJProgressBar.getValue() / 100.0D;
      long l1 = getTask().getExecutionDuration(TimeUnit.SECONDS);
      long l2 = l1 / 60L;
      long l3 = (0.5D + l1 / d) - l1;
      long l4 = l3 / 60L;
      String str3 = String.format(str2, new Object[] { Long.valueOf(l2), Long.valueOf(l1 - l2 * 60L), Long.valueOf(l4), Long.valueOf(l3 - l4 * 60L) });
      
      paramJProgressBar.setString(str3);
    }
  }
  
  private void showBusyGlassPane(boolean paramBoolean)
  {
    RootPaneContainer localRootPaneContainer = null;
    Object localObject = (Component)getTarget();
    while (localObject != null) {
      if ((localObject instanceof RootPaneContainer)) {
        localRootPaneContainer = (RootPaneContainer)localObject;
        break;
      }
      localObject = ((Component)localObject).getParent();
    }
    if (localRootPaneContainer != null) { JMenuBar localJMenuBar;
      if (paramBoolean) {
        localJMenuBar = localRootPaneContainer.getRootPane().getJMenuBar();
        if (localJMenuBar != null) {
          localJMenuBar.putClientProperty(this, Boolean.valueOf(localJMenuBar.isEnabled()));
          localJMenuBar.setEnabled(false);
        }
        BusyGlassPane localBusyGlassPane = new BusyGlassPane();
        InputVerifier local4 = new InputVerifier() {
          public boolean verify(JComponent paramAnonymousJComponent) {
            return !paramAnonymousJComponent.isVisible();
          }
        };
        localBusyGlassPane.setInputVerifier(local4);
        Component localComponent2 = localRootPaneContainer.getGlassPane();
        localRootPaneContainer.getRootPane().putClientProperty(this, localComponent2);
        localRootPaneContainer.setGlassPane(localBusyGlassPane);
        localBusyGlassPane.setVisible(true);
        localBusyGlassPane.revalidate();
      }
      else {
        localJMenuBar = localRootPaneContainer.getRootPane().getJMenuBar();
        if (localJMenuBar != null) {
          boolean bool = ((Boolean)localJMenuBar.getClientProperty(this)).booleanValue();
          localJMenuBar.putClientProperty(this, null);
          localJMenuBar.setEnabled(bool);
        }
        Component localComponent1 = (Component)localRootPaneContainer.getRootPane().getClientProperty(this);
        localRootPaneContainer.getRootPane().putClientProperty(this, null);
        if (!localComponent1.isVisible()) {
          localRootPaneContainer.getGlassPane().setVisible(false);
        }
        localRootPaneContainer.setGlassPane(localComponent1);
      }
    }
  }
  
  private static class BusyGlassPane
    extends JPanel
  {
    BusyGlassPane()
    {
      super(false);
      setVisible(false);
      setOpaque(false);
      setCursor(Cursor.getPredefinedCursor(3));
      MouseInputAdapter local1 = new MouseInputAdapter() {};
      addMouseMotionListener(local1);
      addMouseListener(local1);
    }
  }
  





  private int blockingDialogDelay()
  {
    Integer localInteger = null;
    String str1 = "BlockingDialogTimer.delay";
    ApplicationAction localApplicationAction = getAction();
    if (localApplicationAction != null) {
      localResourceMap = localApplicationAction.getResourceMap();
      String str2 = localApplicationAction.getName();
      localInteger = localResourceMap.getInteger(str2 + "." + str1);
    }
    ResourceMap localResourceMap = getTask().getResourceMap();
    if ((localInteger == null) && (localResourceMap != null)) {
      localInteger = localResourceMap.getInteger(str1);
    }
    return localInteger == null ? 0 : localInteger.intValue();
  }
  
  private void showBlockingDialog(boolean paramBoolean) { Object localObject;
    if (paramBoolean) {
      if (modalDialog != null) {
        localObject = String.format("unexpected InputBlocker state [%s] %s", new Object[] { Boolean.valueOf(paramBoolean), this });
        logger.warning((String)localObject);
        modalDialog.dispose();
      }
      modalDialog = createBlockingDialog();
      localObject = new ActionListener() {
        public void actionPerformed(ActionEvent paramAnonymousActionEvent) {
          if (modalDialog != null) {
            modalDialog.setVisible(true);
          }
        }
      };
      Timer localTimer = new Timer(blockingDialogDelay(), (ActionListener)localObject);
      localTimer.setRepeats(false);
      localTimer.start();

    }
    else if (modalDialog != null) {
      modalDialog.dispose();
      modalDialog = null;
    }
    else {
      localObject = String.format("unexpected InputBlocker state [%s] %s", new Object[] { Boolean.valueOf(paramBoolean), this });
      logger.warning((String)localObject);
    }
  }
  
  protected void block()
  {
    switch (6.$SwitchMap$org$jdesktop$application$Task$BlockingScope[getScope().ordinal()]) {
    case 1: 
      setActionTargetBlocked(true);
      break;
    case 2: 
      setComponentTargetBlocked(true);
      break;
    case 3: 
    case 4: 
      showBusyGlassPane(true);
      showBlockingDialog(true);
    }
  }
  
  protected void unblock()
  {
    switch (6.$SwitchMap$org$jdesktop$application$Task$BlockingScope[getScope().ordinal()]) {
    case 1: 
      setActionTargetBlocked(false);
      break;
    case 2: 
      setComponentTargetBlocked(false);
      break;
    case 3: 
    case 4: 
      showBusyGlassPane(false);
      showBlockingDialog(false);
    }
  }
}
