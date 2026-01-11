package pl.art.lach.mateusz.javaopenchess.core.players.implementation;

import pl.art.lach.mateusz.javaopenchess.JChessApp;
import pl.art.lach.mateusz.javaopenchess.JChessView;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;
import pl.art.lach.mateusz.javaopenchess.core.pieces.PieceFactory;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;






















public class HumanPlayer
  extends ComputerPlayer
{
  public HumanPlayer()
  {
    playerType = PlayerType.LOCAL_USER;
  }
  





  public HumanPlayer(String name, String color)
  {
    super(name, Colors.valueOf(color.toUpperCase()));
    playerType = PlayerType.LOCAL_USER;
  }
  





  public HumanPlayer(String name, Colors color)
  {
    super(name, color);
    playerType = PlayerType.LOCAL_USER;
  }
  
  public Piece getPromotionPiece(Chessboard chessboard)
  {
    String colorSymbol = color.getSymbolAsString().toUpperCase();
    String newPiece = JChessApp.getJavaChessView().showPawnPromotionBox(colorSymbol);
    return PieceFactory.getPiece(chessboard, colorSymbol, newPiece, this);
  }
}
