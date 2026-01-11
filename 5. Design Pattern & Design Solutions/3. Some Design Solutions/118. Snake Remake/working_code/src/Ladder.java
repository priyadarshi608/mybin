
public class Ladder extends Hopper {

	public Ladder(int startPoint, int endPoint) throws InvalidStartEndException {
		super(startPoint, endPoint);
		if (startPoint >= endPoint) {
			throw new InvalidStartEndException("For a snake, endPoint should be greater than startPoint.");
		}
	}

}
