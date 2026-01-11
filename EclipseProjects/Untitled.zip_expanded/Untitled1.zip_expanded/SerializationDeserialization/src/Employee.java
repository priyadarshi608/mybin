import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Employee implements java.io.Serializable {

	// ******************************V V I**************************************
	// If this serialVersionUID is not present, it won't be possible to 
	// deserialize the objects even after compatible changes in class.
	private static final long serialVersionUID = 7226698433416942369L;

	public String name;
	public String address;
	public transient int SSN;
	public int number;
	public int anotherNumber = 10;

	public void mailCheck()
	{
		System.out.println("Mailing a check to " + name + " " + address);
	}

	// ******************************V V I**************************************
	// If we will not implement this method here, the default serialization of
	// java will occur. WE ARE NOT OVERRIDING THIS METHOD, WE ARE DEFINING THEM
	// IN OUR CLASS.
	private void writeObject(ObjectOutputStream out) throws IOException {
		System.out.println("Going to serialize the object..");
		out.defaultWriteObject();
		System.out.println("Serialization complete.");
	}

	// ******************************V V I**************************************
	// If we will not implement this method here, the default deserialization of
	// java will occur. WE ARE NOT OVERRIDING THIS METHOD, WE ARE DEFINING THEM
	// IN OUR CLASS.
	private void readObject(ObjectInputStream in) 
			throws IOException, ClassNotFoundException {
		System.out.println("Going to deserialize the object..");
		in.defaultReadObject();
		System.out.println("Deserialization complete.");
	}
}
