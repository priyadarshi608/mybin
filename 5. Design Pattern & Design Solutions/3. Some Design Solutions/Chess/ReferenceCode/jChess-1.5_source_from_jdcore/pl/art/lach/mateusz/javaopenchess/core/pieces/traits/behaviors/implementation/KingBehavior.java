package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.King;
import pl.art.lach.mateusz.javaopenchess.core.pieces.implementation.Rook;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;

















public class KingBehavior
  extends Behavior
{
  public KingBehavior(King piece)
  {
    super(piece);
  }
  
















  public Set<Square> getSquaresInRange()
  {
    Set<Square> list = new HashSet();
    

    King king = (King)piece;
    
    for (int i = king.getSquare().getPozX() - 1; i <= king.getSquare().getPozX() + 1; i++)
    {
      for (int y = king.getSquare().getPozY() - 1; y <= king.getSquare().getPozY() + 1; y++)
      {
        if (!king.isOut(i, y))
        {
          Square sq = king.getChessboard().getSquare(i, y);
          if (king.getSquare() != sq)
          {


            if ((null == sq.getPiece()) || (sq.getPiece().getPlayer() != piece.getPlayer()))
            {
              list.add(sq);
            }
          }
        }
      }
    }
    if (!king.getWasMotioned())
    {
      if ((king.getChessboard().getSquares()[0][king.getSquare().getPozY()].getPiece() != null) && 
        (king.getChessboard().getSquares()[0][king.getSquare().getPozY()].getPiece().getName().equals("Rook")))
      {
        boolean canCastling = true;
        
        Rook rook = (Rook)king.getChessboard().getSquare(0, king.getSquare().getPozY()).getPiece();
        if (!rook.getWasMotioned())
        {
          for (int i = king.getSquare().getPozX() - 1; i > 0; i--)
          {
            if (king.getChessboard().getSquare(i, king.getSquare().getPozY()).getPiece() != null)
            {
              canCastling = false;
              break;
            }
          }
          Square sq = king.getChessboard().getSquare(king.getSquare().getPozX() - 2, king.getSquare().getPozY());
          Square sq1 = king.getChessboard().getSquare(king.getSquare().getPozX() - 1, king.getSquare().getPozY());
          
          if (canCastling)
          {
            list.add(sq);
          }
        }
      }
      if ((king.getChessboard().getSquares()[7][king.getSquare().getPozY()].getPiece() != null) && 
        (king.getChessboard().getSquares()[7][king.getSquare().getPozY()].getPiece().getName().equals("Rook")))
      {
        boolean canCastling = true;
        Rook rook = (Rook)king.getChessboard().getSquares()[7][king.getSquare().getPozY()].getPiece();
        if (!rook.getWasMotioned())
        {
          for (int i = king.getSquare().getPozX() + 1; i < 7; i++)
          {
            if (king.getChessboard().getSquares()[i][king.getSquare().getPozY()].getPiece() != null)
            {
              canCastling = false;
              break;
            }
          }
          Square sq = king.getChessboard().getSquares()[(king.getSquare().getPozX() + 2)][king.getSquare().getPozY()];
          Square sq1 = king.getChessboard().getSquares()[(king.getSquare().getPozX() + 1)][king.getSquare().getPozY()];
          if (canCastling)
          {
            list.add(sq);
          }
        }
      }
    }
    return list;
  }
  

  public Set<Square> getLegalMoves()
  {
    Set<Square> list = super.getLegalMoves();
    Set<Square> result = new HashSet();
    for (Square sq : list)
    {
      if (((King)piece).isSafe(sq))
      {
        result.add(sq);
      }
    }
    return result;
  }
}
