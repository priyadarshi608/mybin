package av_heap;

public class Pair implements Comparable<Pair>{
	
	public int a;
	public int b;
	
	Pair(int a, int b) {
		super();
		this.a = a;
		this.b = b;
	}

	@Override
	public int compareTo(Pair anotherPair) {
		return this.a - anotherPair.a;
	}

}