import java.util.logging.Level;
import java.util.logging.Logger;
import sss.classloadertest.myclassloader.MyClassLoader;

public class ClassLoaderTest {
	public static void main(String args[]) {
		try {
			Thread.currentThread().interrupt();
			//printing ClassLoader of this class
			System.out.println("ClassLoaderTest.class.getClassLoader(): "
					+ ClassLoaderTest.class.getClassLoader());

			//trying to explicitly load String class using the Application class loader
			Class.forName("java.lang.String", true
					,  ClassLoaderTest.class.getClassLoader());
			System.out.println("String class loaded using other loader.");

			//trying to explicitly load String class using the MyClassLoader
			Class.forName("java.lang.String", true
					, new MyClassLoader(String.class.getClassLoader()));
			System.out.println("String class loaded using other loader.");

			//trying to explicitly load this class again using Extension class loader
			Class.forName("ClassLoaderTest", true 
					,  ClassLoaderTest.class.getClassLoader().getParent());
			System.out.println("ClassLoaderTest class loaded using other loader");
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ClassLoaderTest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
