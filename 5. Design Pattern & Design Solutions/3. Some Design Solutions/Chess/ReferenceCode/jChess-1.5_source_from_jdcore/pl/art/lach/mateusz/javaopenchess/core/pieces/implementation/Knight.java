package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.KnightBehavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;




















public class Knight
  extends Piece
{
  public Knight(Chessboard chessboard, Player player)
  {
    super(chessboard, player);
    value = 3;
    symbol = "N";
    addBehavior(new KnightBehavior(this));
  }
}
