package pl.art.lach.mateusz.javaopenchess.display.views.chessboard;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import javax.swing.JPanel;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.utils.GUI;





















public abstract class ChessboardView
  extends JPanel
{
  private static final int CENTER_POSITION = 3;
  protected static final Image orgImage = GUI.loadImage("chessboard.png");
  



  protected Image image = orgImage;
  



  protected static final Image orgSelSquare = GUI.loadImage("sel_square.png");
  



  protected static Image selSquare = orgSelSquare;
  



  protected static final Image orgAbleSquare = GUI.loadImage("able_square.png");
  



  protected static Image ableSquare = orgAbleSquare;
  

  private Image upDownLabel = null;
  
  protected Image leftRightLabel = null;
  
  protected Point topLeft = new Point(0, 0);
  

  protected float squareHeight;
  

  public static final int imgX = 5;
  

  public static final int imgY = 5;
  

  public static final int imgWidht = 480;
  

  public static final int imgHeight = 480;
  

  private Chessboard chessboard;
  


  public ChessboardView() {}
  

  public abstract Square getSquare(int paramInt1, int paramInt2);
  

  public abstract void unselect();
  

  public abstract int getChessboardWidht();
  

  public abstract int getChessboardHeight();
  

  public abstract int getChessboardWidht(boolean paramBoolean);
  

  public abstract int getChessboardHeight(boolean paramBoolean);
  

  public abstract int getSquareHeight();
  

  public abstract void resizeChessboard(int paramInt);
  

  public abstract Point getTopLeftPoint();
  

  public void update(Graphics g)
  {
    repaint();
  }
  



  public Chessboard getChessboard()
  {
    return chessboard;
  }
  



  public void setChessboard(Chessboard chessboard)
  {
    this.chessboard = chessboard;
  }
  



  public Image getUpDownLabel()
  {
    return upDownLabel;
  }
  



  public void setUpDownLabel(Image upDownLabel)
  {
    this.upDownLabel = upDownLabel;
  }
  
  public int transposePosition(int squarePosition)
  {
    return transposePosition(squarePosition, 3);
  }
  
  public int transposePosition(int squarePosition, int centerPosition)
  {
    return -(squarePosition - centerPosition) + centerPosition + 1;
  }
}
