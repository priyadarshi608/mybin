package testing;

import java.lang.annotation.*;
import java.lang.reflect.*;
@Retention(RetentionPolicy.RUNTIME)
@interface MyAnno {
	String str();
	int val();
}
class AnnotationDemo {
	// myMeth now has two arguments.
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@MyAnno(str = "Two Parameters", val = 19)
	public static void myMeth(String str, int i)
	{
		AnnotationDemo ob = new AnnotationDemo();
		try {
			Class c = ob.getClass();
			// Here, the parameter types are specified.
			Method m = c.getMethod("myMeth", String.class, int.class);
			MyAnno anno = m.getAnnotation(MyAnno.class);
			System.out.println(anno.str() + " " + anno.val());
		} catch (NoSuchMethodException exc) {
			System.out.println("Method Not Found.");
		}
	}
	public static void main(String args[]) {
		myMeth("test", 10);
	}
}
