package pl.art.lach.mateusz.javaopenchess.display.views.chessboard;

import java.awt.event.MouseListener;
import pl.art.lach.mateusz.javaopenchess.core.Chessboard;
import pl.art.lach.mateusz.javaopenchess.core.Square;

public abstract interface ChessboardMouseView
  extends MouseListener
{
  public abstract Square getSquare(int paramInt1, int paramInt2);
  
  public abstract void draw(Chessboard paramChessboard);
}
