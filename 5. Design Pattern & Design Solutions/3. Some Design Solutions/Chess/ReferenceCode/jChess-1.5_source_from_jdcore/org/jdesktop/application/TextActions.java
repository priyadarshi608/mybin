package org.jdesktop.application;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;

























class TextActions
  extends AbstractBean
{
  private final ApplicationContext context;
  private final CaretListener textComponentCaretListener;
  private final PropertyChangeListener textComponentPCL;
  private final String markerActionKey = "TextActions.markerAction";
  private final javax.swing.Action markerAction;
  private boolean copyEnabled = false;
  private boolean cutEnabled = false;
  private boolean pasteEnabled = false;
  private boolean deleteEnabled = false;
  
  public TextActions(ApplicationContext paramApplicationContext) {
    context = paramApplicationContext;
    markerAction = new AbstractAction() {
      public void actionPerformed(ActionEvent paramAnonymousActionEvent) {}
    };
    textComponentCaretListener = new TextComponentCaretListener(null);
    textComponentPCL = new TextComponentPCL(null);
    getClipboard().addFlavorListener(new ClipboardListener(null));
  }
  
  private ApplicationContext getContext() {
    return context;
  }
  
  private JComponent getFocusOwner() {
    return getContext().getFocusOwner();
  }
  
  private Clipboard getClipboard() {
    return getContext().getClipboard();
  }
  

  void updateFocusOwner(JComponent paramJComponent1, JComponent paramJComponent2)
  {
    JTextComponent localJTextComponent;
    if ((paramJComponent1 instanceof JTextComponent)) {
      localJTextComponent = (JTextComponent)paramJComponent1;
      localJTextComponent.removeCaretListener(textComponentCaretListener);
      localJTextComponent.removePropertyChangeListener(textComponentPCL);
    }
    if ((paramJComponent2 instanceof JTextComponent)) {
      localJTextComponent = (JTextComponent)paramJComponent2;
      maybeInstallTextActions(localJTextComponent);
      updateTextActions(localJTextComponent);
      localJTextComponent.addCaretListener(textComponentCaretListener);
      localJTextComponent.addPropertyChangeListener(textComponentPCL);
    }
    else if (paramJComponent2 == null) {
      setCopyEnabled(false);
      setCutEnabled(false);
      setPasteEnabled(false);
      setDeleteEnabled(false);
    }
  }
  
  private final class ClipboardListener implements FlavorListener { private ClipboardListener() {}
    
    public void flavorsChanged(FlavorEvent paramFlavorEvent) { JComponent localJComponent = TextActions.this.getFocusOwner();
      if ((localJComponent instanceof JTextComponent))
        TextActions.this.updateTextActions((JTextComponent)localJComponent);
    }
  }
  
  private final class TextComponentCaretListener implements CaretListener {
    private TextComponentCaretListener() {}
    
    public void caretUpdate(CaretEvent paramCaretEvent) { TextActions.this.updateTextActions((JTextComponent)paramCaretEvent.getSource()); }
  }
  
  private final class TextComponentPCL implements PropertyChangeListener {
    private TextComponentPCL() {}
    
    public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) { String str = paramPropertyChangeEvent.getPropertyName();
      if ((str == null) || ("editable".equals(str))) {
        TextActions.this.updateTextActions((JTextComponent)paramPropertyChangeEvent.getSource());
      }
    }
  }
  
  private void updateTextActions(JTextComponent paramJTextComponent) {
    Caret localCaret = paramJTextComponent.getCaret();
    boolean bool1 = localCaret.getDot() != localCaret.getMark();
    boolean bool2 = paramJTextComponent.isEditable();
    boolean bool3 = getClipboard().isDataFlavorAvailable(DataFlavor.stringFlavor);
    setCopyEnabled(bool1);
    setCutEnabled((bool2) && (bool1));
    setDeleteEnabled((bool2) && (bool1));
    setPasteEnabled((bool2) && (bool3));
  }
  
  private void maybeInstallTextActions(JTextComponent paramJTextComponent)
  {
    ActionMap localActionMap = paramJTextComponent.getActionMap();
    if (localActionMap.get("TextActions.markerAction") == null) {
      localActionMap.put("TextActions.markerAction", markerAction);
      ApplicationActionMap localApplicationActionMap = getContext().getActionMap(getClass(), this);
      for (Object localObject : localApplicationActionMap.keys()) {
        localActionMap.put(localObject, localApplicationActionMap.get(localObject));
      }
    }
  }
  


  private int getCurrentEventModifiers()
  {
    int i = 0;
    AWTEvent localAWTEvent = EventQueue.getCurrentEvent();
    if ((localAWTEvent instanceof InputEvent)) {
      i = ((InputEvent)localAWTEvent).getModifiers();
    }
    else if ((localAWTEvent instanceof ActionEvent)) {
      i = ((ActionEvent)localAWTEvent).getModifiers();
    }
    return i;
  }
  
  private void invokeTextAction(JTextComponent paramJTextComponent, String paramString) {
    ActionMap localActionMap = paramJTextComponent.getActionMap().getParent();
    long l = EventQueue.getMostRecentEventTime();
    int i = getCurrentEventModifiers();
    ActionEvent localActionEvent = new ActionEvent(paramJTextComponent, 1001, paramString, l, i);
    
    localActionMap.get(paramString).actionPerformed(localActionEvent);
  }
  
  @Action(enabledProperty="cutEnabled")
  public void cut(ActionEvent paramActionEvent) {
    Object localObject = paramActionEvent.getSource();
    if ((localObject instanceof JTextComponent)) {
      invokeTextAction((JTextComponent)localObject, "cut");
    }
  }
  
  public boolean isCutEnabled() { return cutEnabled; }
  
  public void setCutEnabled(boolean paramBoolean) {
    boolean bool = cutEnabled;
    cutEnabled = paramBoolean;
    firePropertyChange("cutEnabled", Boolean.valueOf(bool), Boolean.valueOf(cutEnabled));
  }
  
  @Action(enabledProperty="copyEnabled")
  public void copy(ActionEvent paramActionEvent) {
    Object localObject = paramActionEvent.getSource();
    if ((localObject instanceof JTextComponent)) {
      invokeTextAction((JTextComponent)localObject, "copy");
    }
  }
  
  public boolean isCopyEnabled() { return copyEnabled; }
  
  public void setCopyEnabled(boolean paramBoolean) {
    boolean bool = copyEnabled;
    copyEnabled = paramBoolean;
    firePropertyChange("copyEnabled", Boolean.valueOf(bool), Boolean.valueOf(copyEnabled));
  }
  
  @Action(enabledProperty="pasteEnabled")
  public void paste(ActionEvent paramActionEvent) {
    Object localObject = paramActionEvent.getSource();
    if ((localObject instanceof JTextComponent)) {
      invokeTextAction((JTextComponent)localObject, "paste");
    }
  }
  
  public boolean isPasteEnabled() { return pasteEnabled; }
  
  public void setPasteEnabled(boolean paramBoolean) {
    boolean bool = pasteEnabled;
    pasteEnabled = paramBoolean;
    firePropertyChange("pasteEnabled", Boolean.valueOf(bool), Boolean.valueOf(pasteEnabled));
  }
  
  @Action(enabledProperty="deleteEnabled")
  public void delete(ActionEvent paramActionEvent) {
    Object localObject = paramActionEvent.getSource();
    if ((localObject instanceof JTextComponent))
    {






      invokeTextAction((JTextComponent)localObject, "delete-next");
    }
  }
  
  public boolean isDeleteEnabled() { return deleteEnabled; }
  
  public void setDeleteEnabled(boolean paramBoolean) {
    boolean bool = deleteEnabled;
    deleteEnabled = paramBoolean;
    firePropertyChange("deleteEnabled", Boolean.valueOf(bool), Boolean.valueOf(deleteEnabled));
  }
}
