import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
/*Author : AnkitMittal  Copyright- contents must not be reproduced in any form*/
public class DeSerializeEmployee {
	public static void main(String[] args){
		try{
			InputStream fin=new FileInputStream("ser.txt");
			ObjectInput oin=new ObjectInputStream(fin);

			System.out.println("DeSerialization process has started, displaying deSerialized employee objects...");
			//while( oin.readObject()!=null );
			Employee emp;
			while( (emp=(Employee)oin.readObject())!=null ){
				System.out.println(emp);
			}
			fin.close();
			oin.close();
		}catch(IOException | ClassNotFoundException e){
			//e.printStackTrace();
		}
		System.out.println("Object DeSerialization completed.");
	}
}
