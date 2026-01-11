package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.moves.Castling;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.KingBehavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;


































public class King
  extends Piece
{
  protected boolean wasMotioned = false;
  
  public King(Chessboard chessboard, Player player)
  {
    super(chessboard, player);
    value = 99;
    symbol = "K";
    addBehavior(new KingBehavior(this));
  }
  


  public boolean isChecked()
  {
    return !isSafe(square);
  }
  






  public int isCheckmatedOrStalemated()
  {
    if (getAllMoves().isEmpty())
    {
      for (int i = 0; i < 8; i++)
      {
        for (int j = 0; j < 8; j++)
        {
          Piece piece = getChessboard().getSquare(i, j).getPiece();
          if ((null != piece) && (piece.getPlayer() == getPlayer()) && (!piece.getAllMoves().isEmpty()))
          {
            return 0;
          }
        }
      }
      
      if (isChecked())
      {
        return 1;
      }
      

      return 2;
    }
    


    return 0;
  }
  





  public boolean isSafe()
  {
    return isSafe(getSquare());
  }
  




  public boolean isSafe(Square s)
  {
    Square[][] squares = chessboard.getSquares();
    for (int i = 0; i < squares.length; i++)
    {
      for (int j = 0; j < squares[i].length; j++)
      {
        Square sq = squares[i][j];
        Piece piece = sq.getPiece();
        if (piece != null)
        {
          if ((piece.getPlayer().getColor() != getPlayer().getColor()) && (piece != this))
          {
            if (piece.getSquaresInRange().contains(s))
            {
              return false;
            }
          }
        }
      }
    }
    return true;
  }
  





  public boolean willBeSafeAfterMove(Square currentSquare, Square futureSquare)
  {
    Piece tmp = piece;
    piece = piece;
    piece = null;
    
    boolean ret = false;
    if (futureSquare.getPiece().getClass() == King.class)
    {
      ret = isSafe(futureSquare);
    }
    else
    {
      ret = isSafe();
    }
    
    piece = piece;
    piece = tmp;
    
    return ret;
  }
  




  public boolean willBeSafeAfterMove(Square futureSquare)
  {
    return willBeSafeAfterMove(getSquare(), futureSquare);
  }
  



  public boolean getWasMotioned()
  {
    return wasMotioned;
  }
  



  public void setWasMotioned(boolean wasMotioned)
  {
    this.wasMotioned = wasMotioned;
  }
  
  public static Castling getCastling(Square begin, Square end)
  {
    Castling result = Castling.NONE;
    if (begin.getPozX() + 2 == end.getPozX())
    {
      result = Castling.SHORT_CASTLING;
    }
    else if (begin.getPozX() - 2 == end.getPozX())
    {
      result = Castling.LONG_CASTLING;
    }
    return result;
  }
}
