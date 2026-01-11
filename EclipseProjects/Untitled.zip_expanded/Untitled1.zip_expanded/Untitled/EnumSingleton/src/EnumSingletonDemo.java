import sss.enumsingleton.mysingleton.EnumSingleton;

public class EnumSingletonDemo {
	public static void main(String[] args) {
		EnumSingleton.INSTANCE.sayHello();
		EnumSingleton.INSTANCE.sayBye();
	}
}
