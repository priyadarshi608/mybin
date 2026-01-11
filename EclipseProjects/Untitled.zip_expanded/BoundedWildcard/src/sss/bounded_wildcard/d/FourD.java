package sss.bounded_wildcard.d;

//Four-dimensional coordinates.
public class FourD extends ThreeD {
	public int t;
	public FourD(int a, int b, int c, int d) {
		super(a, b, c);
		t = d;
	}
}
