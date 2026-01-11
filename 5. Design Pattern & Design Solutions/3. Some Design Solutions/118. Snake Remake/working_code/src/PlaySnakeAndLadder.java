import java.util.*;

public class PlaySnakeAndLadder {
    public static void main(String[] args) {
        Dice dice = new Dice(1);
        Player p1 = new Player("Alberts",1);
        Player p2 = new Player("Pintoss",2);
        Queue<Player> allPlayers = new LinkedList<>();
        allPlayers.offer(p1);
        allPlayers.offer(p2);
        Snake snake1 = new Snake(10,2);
        Snake snake2 = new Snake(99,12);
        List<Snake> snakes = new ArrayList<>();
        snakes.add(snake1);
        snakes.add(snake2);
        Ladder ladder1 = new Ladder(5,25);
        Ladder ladder2 = new Ladder(40,89);
//        Ladder ladder3 = new Ladder(10,25);
        List<Ladder> ladders = new ArrayList<>();
        ladders.add(ladder1);
        ladders.add(ladder2);
        Map<String,Integer> playersCurrentPosition = new HashMap<>();
        playersCurrentPosition.put("Alberts",0);
        playersCurrentPosition.put("Pintoss",0);
        GameBoard gb=new GameBoard(dice,allPlayers,snakes,ladders,playersCurrentPosition,100);
        gb.startGame();
    }
}
