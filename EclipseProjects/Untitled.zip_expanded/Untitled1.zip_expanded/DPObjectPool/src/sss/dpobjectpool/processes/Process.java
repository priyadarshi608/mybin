package sss.dpobjectpool.processes;

public class Process {  
	private long processNo;  

	public Process(long processNo)  {  
		this.processNo = processNo;  
		// .........  
		System.out.println("Object with process no. " + processNo + " was created");  
	}  

	public long getProcessNo() {  
		return processNo;  
	}  
}
