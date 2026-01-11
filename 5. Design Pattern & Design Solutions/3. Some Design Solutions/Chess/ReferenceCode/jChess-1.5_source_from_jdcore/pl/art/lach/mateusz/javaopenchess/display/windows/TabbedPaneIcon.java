package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.Icon;



















































































































































































































class TabbedPaneIcon
  implements Icon
{
  private int x_pos;
  private int y_pos;
  private int width;
  private int height;
  private Icon fileIcon;
  
  public TabbedPaneIcon(Icon fileIcon)
  {
    this.fileIcon = fileIcon;
    width = 16;
    height = 16;
  }
  

  public void paintIcon(Component c, Graphics g, int x, int y)
  {
    x_pos = x;
    y_pos = y;
    
    Color col = g.getColor();
    
    g.setColor(Color.black);
    int yP = y + 2;
    g.drawLine(x + 3, yP + 3, x + 10, yP + 10);
    g.drawLine(x + 3, yP + 4, x + 9, yP + 10);
    g.drawLine(x + 4, yP + 3, x + 10, yP + 9);
    g.drawLine(x + 10, yP + 3, x + 3, yP + 10);
    g.drawLine(x + 10, yP + 4, x + 4, yP + 10);
    g.drawLine(x + 9, yP + 3, x + 3, yP + 9);
    g.setColor(col);
    if (fileIcon != null)
    {
      fileIcon.paintIcon(c, g, x + width, yP);
    }
  }
  

  public int getIconWidth()
  {
    return width + (fileIcon != null ? fileIcon.getIconWidth() : 0);
  }
  

  public int getIconHeight()
  {
    return height;
  }
  
  public Rectangle getBounds()
  {
    return new Rectangle(x_pos, y_pos, width, height);
  }
}
