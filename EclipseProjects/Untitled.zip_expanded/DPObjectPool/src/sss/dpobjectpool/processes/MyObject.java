package sss.dpobjectpool.processes;

public class MyObject {  
	private long myObjectNo;  

	public MyObject(long objectNo)  {  
		this.myObjectNo = objectNo;  
		// .........  
		System.out.println("Object with object no. " + this.myObjectNo + " was created");  
	}  

	public long getProcessNo() {  
		return myObjectNo;  
	}  
}
