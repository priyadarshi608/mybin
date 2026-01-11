import java.util.List;
import java.util.Map;
import java.util.Queue;


class GameBoard {
	private Dice dice;
	private Queue<Player> nextTurn;
	private List<Snake> snakes;
	private List<Ladder> ladders;
	private Map<String,Integer> playersCurrentPosition;
	int boardSize;

	GameBoard(Dice dice, Queue<Player> nextTurn, List<Snake> snakes, List<Ladder> ladders,Map<String,Integer> playersCurrentPosition,int boardSize) {
		this.dice = dice;
		this.nextTurn = nextTurn;
		this.snakes = snakes;
		this.ladders = ladders;
		this.playersCurrentPosition = playersCurrentPosition;
		this.boardSize = boardSize;
	}

	private int checkSnakeBite(Player player, int nextPosition, int nextCell) {
		for (Snake snake : snakes) {
			if (snake.startPoint == nextCell) {
				nextPosition = snake.endPoint;
			}
		}
		if(nextPosition != nextCell) {
			System.out.println(player.getPlayerName() + " Bitten by Snake present at: "+ nextCell);
		}

		return nextPosition;
	}

	private int checkLadderJump(Player player, int nextPosition, int nextCell) {
		for (Ladder ladder : ladders) {
			if (ladder.startPoint == nextCell) {
				nextPosition = ladder.endPoint;
			}
		}
		if(nextPosition != nextCell) {
			System.out.println(player.getPlayerName() + " Got ladder present at: "+ nextCell);
		}

		return nextPosition;
	}

	private void checkWin(Player player, int nextPosition) {
		if (nextPosition == boardSize) {
			System.out.println(player.getPlayerName() + " won the game");
		} else {
			playersCurrentPosition.put(player.getPlayerName(),nextPosition);
			System.out.println(player.getPlayerName() + " is at position "+ nextPosition);
			nextTurn.offer(player);
		}
	}

	void startGame(){
		while(nextTurn.size()>1) {
			Player player = nextTurn.poll();
			int currentPosition = playersCurrentPosition.get(player.getPlayerName());
			int diceValue = dice.rollDice();
			int nextCell = currentPosition + diceValue;

			if (nextCell > boardSize) {
				nextTurn.offer(player);
			} else if (nextCell == boardSize) {
				System.out.println( player.getPlayerName() + " won the game");
			} else {
				int nextPosition = nextCell;
				nextPosition = checkSnakeBite(player, nextPosition, nextCell);
				nextPosition = checkLadderJump(player, nextPosition, nextCell);
				checkWin(player, nextPosition);
			}
		}
	}
}
