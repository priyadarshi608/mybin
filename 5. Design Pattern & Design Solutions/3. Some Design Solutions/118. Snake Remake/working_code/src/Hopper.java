import java.util.HashSet;
import java.util.Set;

public class Hopper {
    int startPoint;
    int endPoint;
    private static Set<Integer> startPointSet = new HashSet<Integer>();

    public Hopper(int startPoint, int endPoint) throws InvalidStartEndException {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        if (isStartPointAvailable()) {
            Hopper.startPointSet.add(this.startPoint);
        } else {
        	throw new InvalidStartEndException("This startPoint " + this.startPoint + " is already occupied.");
        }
    }
    
    private boolean isStartPointAvailable() {
        if (Hopper.startPointSet.contains(this.startPoint)) {
        	return false;
        } else {
        	return true;
        }
    }
}
