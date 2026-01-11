package pl.art.lach.mateusz.javaopenchess.core.pieces.implementation;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.traits.behaviors.implementation.BishopBehavior;
import pl.art.lach.mateusz.javaopenchess.core.players.Player;



































public class Bishop
  extends Piece
{
  public Bishop(Chessboard chessboard, Player player)
  {
    super(chessboard, player);
    value = 3;
    symbol = "B";
    addBehavior(new BishopBehavior(this));
  }
}
