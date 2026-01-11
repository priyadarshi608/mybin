import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/*Author : AnkitMittal  Copyright- contents must not be reproduced in any form*/
class Employee implements Externalizable {

	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;

	public Employee(){}  //This constructor is called during deSerializaition process, as we have implemented Externalizable. 

	public Employee(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + "]";
	}


	/*
	 * define how Serialization process will write objects.  
	 */
	@Override
	public void writeExternal(ObjectOutput oo) throws IOException {
		System.out.println("in writeExternal()");
		oo.writeInt(id);
		oo.writeObject(name);
	}


	/*
	 * define how deSerialization process will read objects.  
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException,  ClassNotFoundException {
		System.out.println("in readExternal()");
		this.id=in.readInt();
		this.name=(String)in.readObject();
	}

}
