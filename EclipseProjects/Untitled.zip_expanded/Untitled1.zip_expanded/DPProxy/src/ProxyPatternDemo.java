import sss.dpproxy.image.Image;
import sss.dpproxy.image.ProxyImage;

public class ProxyPatternDemo {

	public static void main(String[] args) {
		// ***************************** V V I ********************************
		// At the time of ProxyImage object creation, the heavy image is not
		// loaded from the disk. But if we create the RealImage object, the image
		// will be loaded at the time of object creation only.
		Image image = new ProxyImage("test_10mb.jpg");

		//image will be loaded from disk
		image.display(); 
		System.out.println("");

		// ********** V V I ****************
		//image will not be loaded from disk
		image.display(); 	
	}
}
