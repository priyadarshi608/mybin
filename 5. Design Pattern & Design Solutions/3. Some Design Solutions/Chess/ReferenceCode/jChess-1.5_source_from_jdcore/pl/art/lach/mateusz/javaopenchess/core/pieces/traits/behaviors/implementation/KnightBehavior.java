package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.Behavior;


















public class KnightBehavior
  extends Behavior
{
  public KnightBehavior(Piece piece)
  {
    super(piece);
  }
  

















  public Set<Square> getSquaresInRange()
  {
    Set<Square> list = new HashSet();
    Square[][] squares = piece.getChessboard().getSquares();
    
    int pozX = piece.getSquare().getPozX();
    int pozY = piece.getSquare().getPozY();
    
    int[][] squaresInRange = { { pozX - 2, pozY + 1 }, { pozX - 1, pozY + 2 }, { pozX + 1, pozY + 2 }, { pozX + 2, pozY + 1 }, { pozX + 2, pozY - 1 }, { pozX + 1, pozY - 2 }, { pozX - 1, pozY - 2 }, { pozX - 2, pozY - 1 } };
    









    for (int[] squareCoordinates : squaresInRange)
    {
      int x = squareCoordinates[0];
      int y = squareCoordinates[1];
      if (!piece.isOut(x, y))
      {
        list.add(squares[x][y]);
      }
    }
    return list;
  }
}
