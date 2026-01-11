package pl.art.lach.mateusz.javaopenchess.core.ai;

import pl.art.lach.mateusz.javaopenchess.core.Game;
import pl.art.lach.mateusz.javaopenchess.core.moves.Move;

public abstract interface AI
{
  public abstract Move getMove(Game paramGame, Move paramMove);
}
