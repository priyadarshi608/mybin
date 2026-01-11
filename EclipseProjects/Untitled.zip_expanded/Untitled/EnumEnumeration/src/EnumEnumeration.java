import java.util.Enumeration;
import java.util.Vector;

enum Apple {
	Jonathan, GoldenDel, RedDel, Winesap, Cortland
}
class EnumDemo4 {
	public static void main(String args[])
	{
		// Use of Enumeration
		Enumeration<String> days;
		Vector<String> dayNames = new Vector<>();
		dayNames.add("Sunday");
		dayNames.add("Monday");
		dayNames.add("Tuesday");
		dayNames.add("Wednesday");
		dayNames.add("Thursday");
		dayNames.add("Friday");
		dayNames.add("Saturday");
		days = dayNames.elements();
		while (days.hasMoreElements()){
			System.out.println(days.nextElement()); 
		}
		
		// Use of methods of Enum
		try {
			Apple apElement1 = Apple.valueOf("RedDel");
			System.out.println("Apple element 1:" + apElement1);
		} catch (IllegalArgumentException e) {
			System.out.println("No such enum constant.");
		}
		try {
			Apple apElement2 = Apple.valueOf("Red");
			System.out.println("Apple element 1:" + apElement2);
		} catch (IllegalArgumentException e) {
			System.out.println("No such enum constant.");
		}

		Apple apArray[] = Apple.values();
		// Obtain all ordinal values using ordinal().
		System.out.println("Here are all apple constants" +
				" and their ordinal values: ");
		for(Apple a : apArray)
			System.out.println(a + " " + a.ordinal());

		Apple ap, ap2, ap3;
		ap = Apple.RedDel;
		ap2 = Apple.GoldenDel;
		ap3 = Apple.RedDel;
		System.out.println();
		// Demonstrate compareTo() and equals()
		if(ap.compareTo(ap2) < 0)
			System.out.println(ap + " comes before " + ap2);
		if(ap.compareTo(ap2) > 0)
			System.out.println(ap2 + " comes before " + ap);
		if(ap.compareTo(ap3) == 0)
			System.out.println(ap + " equals " + ap3);
		System.out.println();
		if(ap.equals(ap2))
			System.out.println("Error!");
		if(ap.equals(ap3))
			System.out.println(ap + " equals " + ap3);
		if(ap == ap3)
			System.out.println(ap + " == " + ap3);
	}
}
