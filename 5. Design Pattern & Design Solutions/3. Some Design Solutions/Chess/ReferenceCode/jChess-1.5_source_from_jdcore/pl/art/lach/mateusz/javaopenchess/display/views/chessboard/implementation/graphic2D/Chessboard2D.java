package pl.art.lach.mateusz.javaopenchess.display.views.chessboard.implementation.graphic2D;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.display.views.chessboard.ChessboardView;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;















public class Chessboard2D
  extends ChessboardView
{
  private static final Logger LOG = Logger.getLogger(Chessboard2D.class);
  
  protected Pieces2D pieces2D = Pieces2D.getInstance();
  
  private static final String[] LETTERS = { "a", "b", "c", "d", "e", "f", "g", "h" };
  


  public Chessboard2D(Chessboard chessboard)
  {
    init(chessboard);
  }
  
  protected final void init(Chessboard chessboard)
  {
    setChessboard(chessboard);
    
    setVisible(true);
    setSize(480, 480);
    setLocation(new Point(0, 0));
    setDoubleBuffered(true);
    
    drawLabels((int)squareHeight);
    




    resizeChessboard(480);
  }
  

  public void unselect()
  {
    repaint();
  }
  

  public int getChessboardWidht()
  {
    return getChessboardWidht(false);
  }
  

  public int getChessboardHeight()
  {
    return getChessboardHeight(false);
  }
  


  public int getChessboardWidht(boolean includeLables)
  {
    return getHeight();
  }
  


  public int getChessboardHeight(boolean includeLabels)
  {
    if (getChessboard().getSettings().isRenderLabels())
    {
      return image.getHeight(null) + getUpDownLabel().getHeight(null);
    }
    return image.getHeight(null);
  }
  

  public int getSquareHeight()
  {
    int result = (int)squareHeight;
    return result;
  }
  

  public Square getSquare(int clickedX, int clickedY)
  {
    if ((clickedX > getChessboardHeight()) || (clickedY > getChessboardWidht()))
    {
      LOG.debug("click out of chessboard.");
      return null;
    }
    if (getChessboard().getSettings().isRenderLabels())
    {
      clickedX -= getUpDownLabel().getHeight(null);
      clickedY -= getUpDownLabel().getHeight(null);
    }
    double squareX = clickedX / squareHeight;
    double squareY = clickedY / squareHeight;
    
    if (squareX > (int)squareX)
    {
      squareX = (int)squareX + 1;
    }
    if (squareY > (int)squareY)
    {
      squareY = (int)squareY + 1;
    }
    
    LOG.debug("square_x: " + squareX + " square_y: " + squareY);
    Square result = null;
    try
    {
      result = getChessboard().getSquare((int)squareX - 1, (int)squareY - 1);
      if (getChessboard().getSettings().isUpsideDown())
      {
        int x = transposePosition(result.getPozX());
        int y = transposePosition(result.getPozY());
        result = getChessboard().getSquare(x, y);
      }
    }
    catch (ArrayIndexOutOfBoundsException exc)
    {
      LOG.error("!!Array out of bounds when getting Square with Chessboard.getSquare(int,int) : " + exc.getMessage());
      return null;
    }
    return result;
  }
  

  public void paintComponent(Graphics g)
  {
    Graphics2D g2d = (Graphics2D)g;
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    Point topLeftPoint = getTopLeftPoint();
    Square[][] squares = getChessboard().getSquares();
    if (getChessboard().getSettings().isRenderLabels())
    {
      drawLabels();
      g2d.drawImage(getUpDownLabel(), 0, 0, null);
      g2d.drawImage(getUpDownLabel(), 0, image.getHeight(null) + y, null);
      g2d.drawImage(leftRightLabel, 0, 0, null);
      g2d.drawImage(leftRightLabel, image.getHeight(null) + x, 0, null);
    }
    g2d.drawImage(image, x, y, null);
    drawPieces(squares, g2d);
    
    Square activeSquare = getChessboard().getActiveSquare();
    
    if (null != activeSquare)
    {
      drawActiveSquare(activeSquare, g2d, topLeftPoint, squares);
      drawLegalMoves(g2d, topLeftPoint);
    }
  }
  

  public Point getTopLeftPoint()
  {
    if (getChessboard().getSettings().isRenderLabels())
    {
      return new Point(topLeft.x + getUpDownLabel().getHeight(null), topLeft.y + getUpDownLabel().getHeight(null));
    }
    return topLeft;
  }
  

  public final void resizeChessboard(int height)
  {
    if (0 != height)
    {
      BufferedImage resized = new BufferedImage(height, height, 3);
      Graphics g = resized.createGraphics();
      g.drawImage(ChessboardView.orgImage, 0, 0, height, height, null);
      g.dispose();
      if (!getChessboard().getSettings().isRenderLabels())
      {



        height += 2 * getUpDownLabel().getHeight(null);
      }
      image = resized.getScaledInstance(height, height, 0);
      
      resized = new BufferedImage(height, height, 3);
      g = resized.createGraphics();
      g.drawImage(image, 0, 0, height, height, null);
      g.dispose();
      
      squareHeight = (height / 8);
      if (getChessboard().getSettings().isRenderLabels())
      {



        height += 2 * getUpDownLabel().getHeight(null);
      }
      setSize(height, height);
      
      resized = new BufferedImage((int)squareHeight, (int)squareHeight, 3);
      g = resized.createGraphics();
      g.drawImage(ChessboardView.orgAbleSquare, 0, 0, (int)squareHeight, (int)squareHeight, null);
      g.dispose();
      ChessboardView.ableSquare = resized.getScaledInstance((int)squareHeight, (int)squareHeight, 0);
      
      resized = new BufferedImage((int)squareHeight, (int)squareHeight, 3);
      g = resized.createGraphics();
      g.drawImage(ChessboardView.orgSelSquare, 0, 0, (int)squareHeight, (int)squareHeight, null);
      g.dispose();
      ChessboardView.selSquare = resized.getScaledInstance((int)squareHeight, (int)squareHeight, 0);
      pieces2D.resize(getSquareHeight());
      drawLabels();
    }
  }
  
  protected void drawLabels()
  {
    drawLabels((int)squareHeight);
  }
  
  protected final void drawLabels(int squareHeight)
  {
    int minLabelHeight = 20;
    int labelHeight = (int)Math.ceil(squareHeight / 4);
    labelHeight = labelHeight < minLabelHeight ? minLabelHeight : labelHeight;
    int labelWidth = (int)Math.ceil(squareHeight * 8 + 2 * labelHeight);
    BufferedImage uDL = new BufferedImage(labelWidth + minLabelHeight, labelHeight, 5);
    



    Graphics2D graph2D = uDL.createGraphics();
    graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graph2D.setColor(Color.white);
    
    graph2D.fillRect(0, 0, labelWidth + minLabelHeight, labelHeight);
    graph2D.setColor(Color.black);
    graph2D.setFont(new Font("Arial", 1, 12));
    int addX = squareHeight / 2;
    
    if (getChessboard().getSettings().isRenderLabels())
    {
      addX += labelHeight;
    }
    
    if (!getChessboard().getSettings().isUpsideDown())
    {
      for (int i = 1; i <= LETTERS.length; i++)
      {
        graph2D.drawString(LETTERS[(i - 1)], squareHeight * (i - 1) + addX, 10 + labelHeight / 3);
      }
    }
    else
    {
      int j = 1;
      for (int i = LETTERS.length; i > 0; j++)
      {
        graph2D.drawString(LETTERS[(i - 1)], squareHeight * (j - 1) + addX, 10 + labelHeight / 3);i--;
      }
    }
    graph2D.dispose();
    setUpDownLabel(uDL);
    
    uDL = new BufferedImage(labelHeight, labelWidth + minLabelHeight, 5);
    graph2D = uDL.createGraphics();
    graph2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graph2D.setColor(Color.white);
    
    graph2D.fillRect(0, 0, labelHeight, labelWidth + minLabelHeight);
    graph2D.setColor(Color.black);
    graph2D.setFont(new Font("Arial", 1, 12));
    
    if (getChessboard().getSettings().isUpsideDown())
    {
      for (int i = 1; i <= 8; i++)
      {
        graph2D.drawString(Integer.toString(i), 3 + labelHeight / 3, squareHeight * (i - 1) + addX);
      }
    }
    else
    {
      int j = 1;
      for (int i = 8; i > 0; j++)
      {
        graph2D.drawString(Integer.toString(i), 3 + labelHeight / 3, squareHeight * (j - 1) + addX);i--;
      }
    }
    graph2D.dispose();
    leftRightLabel = uDL;
  }
  
  private void drawLegalMoves(Graphics2D g2d, Point topLeftPoint)
  {
    Iterator it;
    if (getChessboard().getSettings().isDisplayLegalMovesEnabled())
    {
      Set<Square> moves = getChessboard().getMoves();
      if (null != moves)
      {
        for (it = moves.iterator(); it.hasNext();)
        {
          Square sq = (Square)it.next();
          int ableSquarePosX = sq.getPozX();
          int ableSquarePosY = sq.getPozY();
          if (getChessboard().getSettings().isUpsideDown())
          {
            ableSquarePosX = transposePosition(ableSquarePosX);
            ableSquarePosY = transposePosition(ableSquarePosY);
          }
          g2d.drawImage(ableSquare, ableSquarePosX * (int)squareHeight + x, ableSquarePosY * (int)squareHeight + y, null);
        }
      }
    }
  }
  



  private void drawActiveSquare(Square activeSquare, Graphics2D g2d, Point topLeftPoint, Square[][] squares)
  {
    int activeSquareX = activeSquare.getPozX();
    int activeSquareY = activeSquare.getPozY();
    if (getChessboard().getSettings().isUpsideDown())
    {
      activeSquareX = transposePosition(activeSquareX);
      activeSquareY = transposePosition(activeSquareY);
    }
    
    g2d.drawImage(selSquare, activeSquareX * (int)squareHeight + x, activeSquareY * (int)squareHeight + y, null);
    




    Square tmpSquare = squares[activeSquare.getPozX()][activeSquare.getPozY()];
    
    if (null != piece)
    {
      Set<Square> moves = tmpSquare.getPiece().getAllMoves();
      getChessboard().setMoves(moves);
    }
  }
  
  private void drawPieces(Square[][] squares, Graphics2D g2d)
  {
    for (int i = 0; i < 8; i++)
    {
      for (int y = 0; y < 8; y++)
      {
        if (squares[i][y].getPiece() != null)
        {
          int drawPosI = i;
          int drawPosY = y;
          if (getChessboard().getSettings().isUpsideDown())
          {
            drawPosI = transposePosition(drawPosI);
            drawPosY = transposePosition(drawPosY);
          }
          Piece piece = squares[i][y].getPiece();
          Image pieceImage = pieces2D.getImage(piece.getPlayer().getColor(), piece);
          Pieces2D.draw(this, squares[i][y].getPiece(), drawPosI, drawPosY, g2d, pieceImage);
        }
      }
    }
  }
}
