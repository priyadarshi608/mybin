package pl.art.lach.mateusz.javaopenchess.core;

import pl.art.lach.mateusz.javaopenchess.core.players.Player;
import pl.art.lach.mateusz.javaopenchess.core.players.PlayerType;
import pl.art.lach.mateusz.javaopenchess.network.Chat;
import pl.art.lach.mateusz.javaopenchess.utils.GameModes;
import pl.art.lach.mateusz.javaopenchess.utils.GameTypes;
import pl.art.lach.mateusz.javaopenchess.utils.Settings;



















public class GameFactory
{
  public GameFactory() {}
  
  public static Game instance(GameModes gameMode, GameTypes gameType, String whiteName, String blackName, PlayerType whiteType, PlayerType blackType, boolean setPieces4newGame)
  {
    Game game = new Game();
    Settings sett = game.getSettings();
    Player whitePlayer = sett.getPlayerWhite();
    Player blackPlayer = sett.getPlayerBlack();
    sett.setGameMode(GameModes.NEW_GAME);
    blackPlayer.setName(whiteName);
    whitePlayer.setName(blackName);
    whitePlayer.setType(whiteType);
    blackPlayer.setType(blackType);
    sett.setGameType(GameTypes.LOCAL);
    if (setPieces4newGame)
    {
      game.getChessboard().setPieces4NewGame(whitePlayer, blackPlayer);
    }
    game.setActivePlayer(whitePlayer);
    return game;
  }
  

  public static Game instance(GameModes gameMode, GameTypes gameType, String whiteName, String blackName, PlayerType whiteType, PlayerType blackType)
  {
    return instance(gameMode, gameType, whiteName, blackName, whiteType, blackType, true);
  }
  


  public static Game instance(GameModes gameMode, GameTypes gameType, String whiteName, String blackName, PlayerType whiteType, PlayerType blackType, boolean setPieces4newGame, boolean chatEnabled)
  {
    Game game = instance(gameMode, gameType, whiteName, blackName, whiteType, blackType);
    game.getChat().setEnabled(chatEnabled);
    return game;
  }
  

  public static Game instance(GameModes gameMode, GameTypes gameType, Player whitePlayer, Player blackPlayer)
  {
    return instance(gameMode, gameType, whitePlayer
    
      .getName(), blackPlayer
      .getName(), whitePlayer
      .getPlayerType(), blackPlayer
      .getPlayerType());
  }
}
