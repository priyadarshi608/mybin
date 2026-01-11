import java.util.Comparator;

public class MyComp implements Comparator<Integer> {
	public int compare (Integer t1, Integer t2) {
		return ((t2 - t1) > 0) ? 1 : ((t2 - t1) < 0) ? -1 : 0;
	}
}
