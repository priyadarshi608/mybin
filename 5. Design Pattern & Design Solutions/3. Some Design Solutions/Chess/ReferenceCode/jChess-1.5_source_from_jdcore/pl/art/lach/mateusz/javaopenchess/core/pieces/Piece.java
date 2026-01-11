package pl.art.lach.mateusz.javaopenchess.core.pieces;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;

public abstract class Piece
{
  private static final Logger LOG = Logger.getLogger(Piece.class);
  
  protected Chessboard chessboard;
  
  protected Square square;
  
  protected Player player;
  
  protected String name;
  
  protected String symbol;
  
  protected short value = 0;
  
  protected Set<Behavior> behaviors = new HashSet();
  
  public Piece(Chessboard chessboard, Player player)
  {
    this.chessboard = chessboard;
    this.player = player;
    name = getClass().getSimpleName();
  }
  




  public short getValue()
  {
    return value;
  }
  
  public final void addBehavior(Behavior behavior)
  {
    behaviors.add(behavior);
  }
  
  public final Set<Behavior> getBehaviors()
  {
    return Collections.unmodifiableSet(behaviors);
  }
  
  public void setBehaviors(Set<Behavior> behaviors)
  {
    this.behaviors = behaviors;
  }
  


  void clean() {}
  


  boolean canMove(Square square, ArrayList allmoves)
  {
    ArrayList moves = allmoves;
    for (Iterator it = moves.iterator(); it.hasNext();)
    {
      Square sq = (Square)it.next();
      if (sq == square)
      {
        return true;
      }
    }
    return false;
  }
  






  public boolean canMove(int newX, int newY)
  {
    boolean result = false;
    
    Square[][] squares = chessboard.getSquares();
    if ((!isOut(newX, newY)) && (checkPiece(newX, newY)))
    {
      if (getPlayer().getColor() == Colors.WHITE)
      {
        if (chessboard.getKingWhite().willBeSafeAfterMove(square, squares[newX][newY]))
        {
          result = true;
        }
        

      }
      else if (chessboard.getKingBlack().willBeSafeAfterMove(square, squares[newX][newY]))
      {
        result = true;
      }
    }
    
    return result;
  }
  




  public Set<Square> getAllMoves()
  {
    Set<Square> moves = new HashSet();
    for (Behavior behavior : behaviors)
    {
      moves.addAll(behavior.getLegalMoves());
    }
    return moves;
  }
  
  public Set<Square> getSquaresInRange()
  {
    Set<Square> moves = new HashSet();
    for (Behavior behavior : behaviors)
    {
      moves.addAll(behavior.getSquaresInRange());
    }
    return moves;
  }
  





  public boolean isOut(int x, int y)
  {
    return (x < 0) || (x > 7) || (y < 0) || (y > 7);
  }
  







  public boolean checkPiece(int x, int y)
  {
    if ((getChessboardgetSquarespiece != null) && 
      (getChessboard().getSquares()[x][y].getPiece().getName().equals("King")))
    {
      return false;
    }
    Piece piece = getChessboardgetSquarespiece;
    if ((piece == null) || 
      (piece.getPlayer() != getPlayer()))
    {
      return true;
    }
    return false;
  }
  





  public boolean otherOwner(int x, int y)
  {
    Square sq = getChessboard().getSquare(x, y);
    if (piece == null)
    {
      return false;
    }
    if (getPlayer() != sq.getPiece().getPlayer())
    {
      return true;
    }
    return false;
  }
  
  public String getSymbol()
  {
    return symbol;
  }
  



  public Chessboard getChessboard()
  {
    return chessboard;
  }
  



  public void setChessboard(Chessboard chessboard)
  {
    this.chessboard = chessboard;
  }
  



  public Square getSquare()
  {
    return square;
  }
  



  public void setSquare(Square square)
  {
    this.square = square;
  }
  



  public Player getPlayer()
  {
    return player;
  }
  



  public void setPlayer(Player player)
  {
    this.player = player;
  }
  



  public String getName()
  {
    return name;
  }
  



  public void setName(String name)
  {
    this.name = name;
  }
}
