package pl.art.lach.mateusz.javaopenchess.display.windows;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;
















public class PawnPromotionWindow
  extends JDialog
  implements ActionListener
{
  JButton knightButton;
  JButton bishopButton;
  JButton rookButton;
  JButton queenButton;
  GridBagLayout gbl;
  public String result;
  GridBagConstraints gbc;
  
  public PawnPromotionWindow(Frame parent, String color)
  {
    super(parent);
    setTitle("Choose piece");
    setMinimumSize(new Dimension(520, 130));
    setSize(new Dimension(520, 130));
    setMaximumSize(new Dimension(520, 130));
    setResizable(false);
    setLayout(new GridLayout(1, 4));
    

    gbl = new GridBagLayout();
    gbc = new GridBagConstraints();
    knightButton = new JButton(new ImageIcon(GUI.loadImage("Knight-" + color + "70.png")));
    bishopButton = new JButton(new ImageIcon(GUI.loadImage("Bishop-" + color + "70.png")));
    rookButton = new JButton(new ImageIcon(GUI.loadImage("Rook-" + color + "70.png")));
    queenButton = new JButton(new ImageIcon(GUI.loadImage("Queen-" + color + "70.png")));
    result = "";
    
    knightButton.addActionListener(this);
    bishopButton.addActionListener(this);
    rookButton.addActionListener(this);
    queenButton.addActionListener(this);
    
    add(queenButton);
    add(rookButton);
    add(bishopButton);
    add(knightButton);
  }
  



  public void setColor(String color)
  {
    knightButton.setIcon(new ImageIcon(GUI.loadImage("Knight-" + color + "70.png")));
    bishopButton.setIcon(new ImageIcon(GUI.loadImage("Bishop-" + color + "70.png")));
    rookButton.setIcon(new ImageIcon(GUI.loadImage("Rook-" + color + "70.png")));
    queenButton.setIcon(new ImageIcon(GUI.loadImage("Queen-" + color + "70.png")));
  }
  



  public void actionPerformed(ActionEvent arg0)
  {
    if (arg0.getSource() == queenButton)
    {
      result = "Queen";
    }
    else if (arg0.getSource() == rookButton)
    {
      result = "Rook";
    }
    else if (arg0.getSource() == bishopButton)
    {
      result = "Bishop";
    }
    else
    {
      result = "Knight";
    }
    setVisible(false);
  }
}
