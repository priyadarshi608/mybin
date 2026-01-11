import java.io.Serializable;

/*
 * The best way to prevent the cloning of singleton class is not to implement
 * Cloneable interface because clone() method in object class is protected so
 * it won't be possible for the other classes to access this method from outside
 * of this class if we don't make its access specifier public by implementing 
 * Cloneable interface. Here we have implemented Cloneable interface just to be verbose.
 */
class Singleton implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;
	private static Singleton instance;

	private int value;

	/* Private Constructor to prevent its instantiation directly.*/
	private Singleton() throws SingletonAlreadyInitilizedException {
		 /* This private constructor will throw SingletonAlreadyInitializedException,
		 * if you try to access the private constructor of this class using the 
		 * techniques of reflection.
		 */
		if (instance != null)
			throw new SingletonAlreadyInitilizedException();
	}

	public static Singleton getInstance() {

		/* Lazy initialization, creating object on first use */
		if (instance == null) {
			synchronized (Singleton.class) {
				if (instance == null) {
					instance = new Singleton();
				}
			}
		}

		return instance;
	}

	protected Object readResolve() {
		return getInstance();
	}

	/* Restrict cloning of object */
	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

	public void display() {
		System.out.println("Hurray! I am display from Singleton!");
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
}

class SingletonAlreadyInitilizedException extends RuntimeException {
	public SingletonAlreadyInitilizedException() {
		super("Singleton already initialized.");
	}
}
