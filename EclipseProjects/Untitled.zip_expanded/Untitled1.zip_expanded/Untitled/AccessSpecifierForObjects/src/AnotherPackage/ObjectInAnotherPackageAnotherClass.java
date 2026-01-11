package AnotherPackage;

import MyPackage.ObjectInSameClass;

public class ObjectInAnotherPackageAnotherClass {
	void myMeth() {
		ObjectInSameClass myA = new ObjectInSameClass(3, 2, 6, 5);
		System.out.println(myA.a);
		System.out.println(myA.b);
		System.out.println(myA.c);
		System.out.println(myA.d);
	}
}
class AnotherPackageSubclassTest extends ObjectInSameClass {

	AnotherPackageSubclassTest(int a, int b, int c, int d) {
		super(a, b, c, d);
		// TODO Auto-generated constructor stub
	}
	void myMeth() {
		ObjectInSameClass myA = new ObjectInSameClass(3, 2, 6, 5);
		System.out.println(this.a);
		System.out.println(myA.a);
		System.out.println(this.b);
		System.out.println(myA.b);

		/*
		 * **************************** V V I *********************************
		 */
		// Protected variables of the SuperClass with this keyword is visible 
		// here but protected variable of the SuperClass of a particular object
		// is not visible here.
		System.out.println(this.c);
		System.out.println(myA.c);

		System.out.println(this.d);
		System.out.println(myA.d);
	}
}
