package org.jdesktop.application;

import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JRootPane;










public class FrameView
  extends View
{
  private static final Logger logger = Logger.getLogger(FrameView.class.getName());
  private JFrame frame = null;
  
  public FrameView(Application paramApplication) {
    super(paramApplication);
  }
  














  public JFrame getFrame()
  {
    if (frame == null) {
      String str = getContext().getResourceMap().getString("Application.title", new Object[0]);
      frame = new JFrame(str);
      frame.setName("mainFrame");
    }
    return frame;
  }
  





















  public void setFrame(JFrame paramJFrame)
  {
    if (paramJFrame == null) {
      throw new IllegalArgumentException("null JFrame");
    }
    if (frame != null) {
      throw new IllegalStateException("frame already set");
    }
    frame = paramJFrame;
    firePropertyChange("frame", null, frame);
  }
  
  public JRootPane getRootPane() {
    return getFrame().getRootPane();
  }
}
