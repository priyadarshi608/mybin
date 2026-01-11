package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;






















abstract class LongRangePieceBehavior
  extends Behavior
{
  protected static final int DIRECTION_LEFT = -1;
  protected static final int DIRECTION_RIGHT = 1;
  protected static final int DIRECTION_UP = -1;
  protected static final int DIRECTION_BOTTOM = 1;
  protected static final int DIRECTION_NILL = 0;
  
  public LongRangePieceBehavior(Piece piece)
  {
    super(piece);
  }
  







  protected Set<Square> getMovesForDirection(int moveX, int moveY)
  {
    Set<Square> list = new HashSet();
    
    int h = piece.getSquare().getPozX(); for (int i = piece.getSquare().getPozY(); !piece.isOut(h, i); i += moveY)
    {
      if ((piece.getSquare().getPozX() != h) || (piece.getSquare().getPozY() != i))
      {


        Square sq = piece.getChessboard().getSquare(h, i);
        if ((null != sq.getPiece()) && (piece.getPlayer() == sq.getPiece().getPlayer()))
          break;
        list.add(piece.getChessboard().getSquare(h, i));
        
        if (piece.otherOwner(h, i)) {
          break;
        }
      }
      h += moveX;
    }
    


















    return list;
  }
}
