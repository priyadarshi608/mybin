import java.util.*;
class MyClass1 {
	private int val;
	MyClass1(int v) { val = v; }
	int getVal() { return val; }
}
public class MethodRefDemo {
	// A compare() method compatible with the one defined by Comparator<T>.
	static int compareMC(MyClass1 a, MyClass1 b) {
		return a.getVal() - b.getVal();
	}
	public static void main(String args[])
	{
		ArrayList<MyClass1> al = new ArrayList<MyClass1>();
		al.add(new MyClass1(1));
		al.add(new MyClass1(4));
		al.add(new MyClass1(2));
		al.add(new MyClass1(9));
		al.add(new MyClass1(3));
		al.add(new MyClass1(7));

		Collections.sort(al, MethodRefDemo::compareMC);
		for(MyClass1 myClass1 : al)
			System.out.println(myClass1.getVal());
	}
}
