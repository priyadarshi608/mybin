import java.util.*;
class MyClass3 {
	private int val;
	MyClass3(int v) { val = v; }
	int getVal() { return val; }
}
class MyComp implements Comparator<MyClass3> {
	@Override
	public int compare(MyClass3 o1, MyClass3 o2) {
		return (o1.getVal() - o2.getVal() > 0) ? 1 : (o1.getVal() - o2.getVal() < 0) ? -1 : 0;
	}
}
class ComparatorDemo {
	public static void main(String args[])
	{
		ArrayList<MyClass3> al = new ArrayList<MyClass3>();
		al.add(new MyClass3(1));
		al.add(new MyClass3(4));
		al.add(new MyClass3(2));
		al.add(new MyClass3(9));
		al.add(new MyClass3(3));
		al.add(new MyClass3(7));

		Collections.sort(al, new MyComp());
		for(MyClass3 myClass3 : al)
			System.out.println(myClass3.getVal());
	}
}
