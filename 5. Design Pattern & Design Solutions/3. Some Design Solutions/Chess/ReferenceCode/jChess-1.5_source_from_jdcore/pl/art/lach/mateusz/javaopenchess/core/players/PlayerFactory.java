package pl.art.lach.mateusz.javaopenchess.core.players;

import pl.art.lach.mateusz.javaopenchess.core.Colors;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.ComputerPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.HumanPlayer;
import pl.art.lach.mateusz.javaopenchess.core.players.implementation.NetworkPlayer;

















public class PlayerFactory
{
  public PlayerFactory() {}
  
  public static Player getInstance(String name, Colors color, PlayerType playerType)
  {
    Player player = null;
    switch (1.$SwitchMap$pl$art$lach$mateusz$javaopenchess$core$players$PlayerType[playerType.ordinal()])
    {
    case 1: 
      player = new HumanPlayer(name, color);
      break;
    case 2: 
      player = new NetworkPlayer(name, color);
      break;
    case 3: 
      player = new ComputerPlayer(name, color);
      player.setName("CPU");
      break;
    default: 
      player = new HumanPlayer(name, color);
    }
    
    return player;
  }
  
  public static Player getInstance(String name, String color, PlayerType playerType)
  {
    return getInstance(name, Colors.valueOf(color.toUpperCase()), playerType);
  }
}
