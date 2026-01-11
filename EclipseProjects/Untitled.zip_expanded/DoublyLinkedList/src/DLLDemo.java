import java.io.PrintWriter;

import sss.dll.list.*;

public class DLLDemo {
	public static void main(String args[]) {
		PrintWriter printWriter = new PrintWriter(System.out, true);
		List<Integer> dllInteger = new DoublyLinkedList<Integer>();
		dllInteger.add(10);
		dllInteger.add(20);
		dllInteger.add(30);
		dllInteger.add(3, 40);
		printWriter.println(dllInteger.size());
//		Iterator<Integer> itr = dllInteger.getDLLIterator(0);
		Iterator<Integer> itr = dllInteger.getDLLIterator();
		printWriter.println(itr.next());
		printWriter.println(itr.next());
		printWriter.println(itr.next());
		printWriter.println(itr.next());
		System.out.println("1111111111111111111111111111111111");
		printWriter.println(itr.prev());
		printWriter.println(itr.prev());
		System.out.println("2222222222222222222222222222222222");
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.println(itr.next());
		printWriter.println(itr.prev());
		printWriter.close();
	}
}
