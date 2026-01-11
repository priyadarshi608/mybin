package pl.art.lach.mateusz.javaopenchess.core.players.implementation;

import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;

























public class NetworkPlayer
  extends ComputerPlayer
{
  public NetworkPlayer()
  {
    playerType = PlayerType.NETWORK_USER;
  }
  





  public NetworkPlayer(String name, String color)
  {
    super(name, color);
    playerType = PlayerType.NETWORK_USER;
  }
  





  public NetworkPlayer(String name, Colors color)
  {
    super(name, color);
    playerType = PlayerType.NETWORK_USER;
  }
  

  public Piece getPromotionPiece(Chessboard chessboard)
  {
    return null;
  }
}
