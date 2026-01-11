package pl.art.lach.mateusz.javaopenchess.core.players;

import java.io.Serializable;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.pieces.Piece;

public abstract interface Player
  extends Serializable
{
  public static final String CPU_NAME = "CPU";
  
  public abstract Colors getColor();
  
  public abstract String getName();
  
  public abstract PlayerType getPlayerType();
  
  public abstract boolean isGoDown();
  
  public abstract void setGoDown(boolean paramBoolean);
  
  public abstract void setName(String paramString);
  
  public abstract void setType(PlayerType paramPlayerType);
  
  public abstract Piece getPromotionPiece(Chessboard paramChessboard);
}
