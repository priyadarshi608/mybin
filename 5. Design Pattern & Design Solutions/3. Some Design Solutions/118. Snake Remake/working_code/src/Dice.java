import java.util.Random;

public class Dice {
    private int numberOfDice;

    Dice(int numberOfDice) {
        this.numberOfDice = numberOfDice;
    }

    public int getNumberOfDice() {
		return numberOfDice;
	}

	public void setNumberOfDice(int numberOfDice) {
		this.numberOfDice = numberOfDice;
	}

	public int rollDice(){
		Random r = new Random();
		int low = numberOfDice;
		int high = numberOfDice * 6 + 1; // adding 1 as nextInt(n) returns a random value between 0 (inclusive) and n (exclusive)
		int result = r.nextInt(high-low) + low;
        return result;
    }
}
