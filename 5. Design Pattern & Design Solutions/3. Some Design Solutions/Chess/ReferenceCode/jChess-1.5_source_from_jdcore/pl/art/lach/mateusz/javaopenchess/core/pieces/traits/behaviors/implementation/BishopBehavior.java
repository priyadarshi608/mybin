package pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation;

import java.util.HashSet;
import java.util.Set;
import pl.art.lach.mateusz.javaopenchess.core.Square;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;



















public class BishopBehavior
  extends LongRangePieceBehavior
{
  public BishopBehavior(Piece piece)
  {
    super(piece);
  }
  





  public Set<Square> getSquaresInRange()
  {
    Set<Square> list = new HashSet();
    
    list.addAll(getMovesForDirection(-1, -1));
    list.addAll(getMovesForDirection(-1, 1));
    list.addAll(getMovesForDirection(1, -1));
    list.addAll(getMovesForDirection(1, 1));
    
    return list;
  }
}
