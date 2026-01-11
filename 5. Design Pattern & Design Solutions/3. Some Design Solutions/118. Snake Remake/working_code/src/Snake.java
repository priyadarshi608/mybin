
public class Snake extends Hopper {

	public Snake(int startPoint, int endPoint) throws InvalidStartEndException {
		super(startPoint, endPoint);
		if (startPoint <= endPoint) {
			throw new InvalidStartEndException("For a snake, stratPoint should be greater than endPoint.");
		}
	}

}
