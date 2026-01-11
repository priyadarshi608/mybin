import java.util.ArrayList;
import java.util.Collections;

class MyClass2 implements Comparable<MyClass2>{
	private int val;
	MyClass2(int v) { val = v; }
	int getVal() { return val; }
	@Override
	public int compareTo(MyClass2 myClass2) {
		return (val - myClass2.val) > 0 ? 1 : (val - myClass2.val) < 0 ? -1 : 0;
	}
}
public class ComparableDemo {
	public static void main(String args[])
	{
		ArrayList<MyClass2> al = new ArrayList<MyClass2>();
		al.add(new MyClass2(1));
		al.add(new MyClass2(4));
		al.add(new MyClass2(2));
		al.add(new MyClass2(9));
		al.add(new MyClass2(3));
		al.add(new MyClass2(7));

		Collections.sort(al);
		for(MyClass2 myClass2 : al)
			System.out.println(myClass2.getVal());
	}
}
