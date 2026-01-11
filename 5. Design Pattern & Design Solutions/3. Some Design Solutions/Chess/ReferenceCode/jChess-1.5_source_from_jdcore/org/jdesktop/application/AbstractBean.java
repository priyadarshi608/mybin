package org.jdesktop.application;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;













public class AbstractBean
{
  private final PropertyChangeSupport pcs;
  
  public AbstractBean()
  {
    pcs = new EDTPropertyChangeSupport(this);
  }
  












  public void addPropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    pcs.addPropertyChangeListener(paramPropertyChangeListener);
  }
  









  public void removePropertyChangeListener(PropertyChangeListener paramPropertyChangeListener)
  {
    pcs.removePropertyChangeListener(paramPropertyChangeListener);
  }
  













  public void addPropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    pcs.addPropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  













  public synchronized void removePropertyChangeListener(String paramString, PropertyChangeListener paramPropertyChangeListener)
  {
    pcs.removePropertyChangeListener(paramString, paramPropertyChangeListener);
  }
  





  public PropertyChangeListener[] getPropertyChangeListeners()
  {
    return pcs.getPropertyChangeListeners();
  }
  











  protected void firePropertyChange(String paramString, Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 != null) && (paramObject2 != null) && (paramObject1.equals(paramObject2))) {
      return;
    }
    pcs.firePropertyChange(paramString, paramObject1, paramObject2);
  }
  











  protected void firePropertyChange(PropertyChangeEvent paramPropertyChangeEvent)
  {
    pcs.firePropertyChange(paramPropertyChangeEvent);
  }
  
  private static class EDTPropertyChangeSupport
    extends PropertyChangeSupport {
    EDTPropertyChangeSupport(Object paramObject) { super(); }
    
    public void firePropertyChange(final PropertyChangeEvent paramPropertyChangeEvent) {
      if (SwingUtilities.isEventDispatchThread()) {
        super.firePropertyChange(paramPropertyChangeEvent);
      }
      else {
        Runnable local1 = new Runnable() {
          public void run() {
            firePropertyChange(paramPropertyChangeEvent);
          }
        };
        SwingUtilities.invokeLater(local1);
      }
    }
  }
}
