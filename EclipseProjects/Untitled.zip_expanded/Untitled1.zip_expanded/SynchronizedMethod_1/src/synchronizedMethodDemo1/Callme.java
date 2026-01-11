/**
 * 
 */
package synchronizedMethodDemo1;

/**
 * @author SONU
 *
 */
public class Callme {

	/**
	 * Default constructor
	 */
	public Callme() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * call method which prints the message.
	 */
	synchronized void call(String msg) {
		System.out.print("[" + msg);
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e) {
			System.out.println("Interrupted");
		}
		System.out.println("]");
	}
}
