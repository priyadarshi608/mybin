package MyPackage;

public class ObjectInSameClass {
	private int a;
	int b;
	protected int c;
	public int d;
	public ObjectInSameClass(int a, int b, int c, int d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	void myMeth(ObjectInSameClass myA) {
//		ObjectInSameClass myA = new ObjectInSameClass(3, 2, 6, 5);
		System.out.println(myA.a);
		System.out.println(myA.b);
		System.out.println(myA.c);
		System.out.println(myA.d);
	}
}
