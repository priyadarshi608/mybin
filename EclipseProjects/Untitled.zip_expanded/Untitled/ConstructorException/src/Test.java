class Test {
	static Test obj; // stores a "partially constructed" object
	String myName;
	Test(String myName) {
		Test.obj = this;
		this.myName = myName;
		throw new RuntimeException();
	}
	public static void main(String[] args) {
		Test obj = null;
		try {
			obj = new Test("thisObject");
		} catch (RuntimeException e) {
			
			// the address assigned to static variable will be printed.
			System.out.println("" + Test.obj);
			
			// this won't work because the object is not created.
			System.out.println(obj.myName);
		}
		
		// the address assigned to static variable will be printed.
		System.out.println("" + Test.obj);

		// this won't work because the object is not created.
		System.out.println(obj.myName);
	}
}
