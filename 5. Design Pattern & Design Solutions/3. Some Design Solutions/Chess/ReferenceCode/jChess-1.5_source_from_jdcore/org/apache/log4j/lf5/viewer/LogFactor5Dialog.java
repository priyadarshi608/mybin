package org.apache.log4j.lf5.viewer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.Window;
import javax.swing.JDialog;
import javax.swing.JFrame;




























public abstract class LogFactor5Dialog
  extends JDialog
{
  protected static final Font DISPLAY_FONT = new Font("Arial", 1, 12);
  









  protected LogFactor5Dialog(JFrame jframe, String message, boolean modal)
  {
    super(jframe, message, modal);
  }
  


  public void show()
  {
    pack();
    minimumSizeDialog(this, 200, 100);
    centerWindow(this);
    super.show();
  }
  






  protected void centerWindow(Window win)
  {
    Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
    

    if (width < getSizewidth) {
      win.setSize(width, getSizeheight);
    }
    
    if (height < getSizeheight) {
      win.setSize(getSizewidth, height);
    }
    

    int x = (width - getSizewidth) / 2;
    int y = (height - getSizeheight) / 2;
    win.setLocation(x, y);
  }
  
  protected void wrapStringOnPanel(String message, Container container)
  {
    GridBagConstraints c = getDefaultConstraints();
    gridwidth = 0;
    
    insets = new Insets(0, 0, 0, 0);
    GridBagLayout gbLayout = (GridBagLayout)container.getLayout();
    

    while (message.length() > 0) {
      int newLineIndex = message.indexOf('\n');
      String line;
      if (newLineIndex >= 0) {
        String line = message.substring(0, newLineIndex);
        message = message.substring(newLineIndex + 1);
      } else {
        line = message;
        message = "";
      }
      Label label = new Label(line);
      label.setFont(DISPLAY_FONT);
      gbLayout.setConstraints(label, c);
      container.add(label);
    }
  }
  
  protected GridBagConstraints getDefaultConstraints() {
    GridBagConstraints constraints = new GridBagConstraints();
    weightx = 1.0D;
    weighty = 1.0D;
    gridheight = 1;
    
    insets = new Insets(4, 4, 4, 4);
    
    fill = 0;
    
    anchor = 17;
    
    return constraints;
  }
  


  protected void minimumSizeDialog(Component component, int minWidth, int minHeight)
  {
    if (getSizewidth < minWidth) {
      component.setSize(minWidth, getSizeheight);
    }
    if (getSizeheight < minHeight) {
      component.setSize(getSizewidth, minHeight);
    }
  }
}
