package MyPackage;

class Test1 extends ObjectInSameClass {
	public Test1(int a, int b, int c, int d) {
		super(a, b, c, d);
	}

	public static void myMeth1() {
		ObjectInSameClass myA = new ObjectInSameClass(3, 2, 6, 5);
		System.out.println(myA.a);
		System.out.println(myA.b);
		System.out.println(myA.c);
		System.out.println(myA.d);
	}
}
class Test {
	public static void myMeth() {
		ObjectInSameClass myA = new ObjectInSameClass(3, 2, 6, 5);
		System.out.println(myA.a);
		System.out.println(myA.b);
		System.out.println(myA.c);
		System.out.println(myA.d);
	}
}
