package sss.enumsingleton.mysingleton;

public enum EnumSingleton implements MySingleton {
	INSTANCE;
	
	public void sayHello() {
		System.out.println("Hellod");
	}
	public void sayBye() {
		System.out.println("Bye");
	}
}
