package org.jdesktop.swingworker;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import javax.swing.SwingUtilities;

public final class SwingPropertyChangeSupport
  extends PropertyChangeSupport
{
  static final long serialVersionUID = 7162625831330845068L;
  private final boolean notifyOnEDT;
  
  public SwingPropertyChangeSupport(Object paramObject)
  {
    this(paramObject, false);
  }
  
  public SwingPropertyChangeSupport(Object paramObject, boolean paramBoolean)
  {
    super(paramObject);
    notifyOnEDT = paramBoolean;
  }
  
  public void firePropertyChange(final PropertyChangeEvent paramPropertyChangeEvent)
  {
    if (paramPropertyChangeEvent == null) {
      throw new NullPointerException();
    }
    if ((!isNotifyOnEDT()) || (SwingUtilities.isEventDispatchThread())) {
      super.firePropertyChange(paramPropertyChangeEvent);
    } else {
      SwingUtilities.invokeLater(new Runnable()
      {
        public void run()
        {
          firePropertyChange(paramPropertyChangeEvent);
        }
      });
    }
  }
  
  public final boolean isNotifyOnEDT()
  {
    return notifyOnEDT;
  }
}
