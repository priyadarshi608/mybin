import java.rmi.*;
import java.rmi.registry.*;

public class RmiClient {
	static public void main(String args[]) {
		ReceiveMessageInterface rmiServer;
		Registry registry;
		String serverAddress = "localhost";
		String serverPort = "3232";
		String text = "Hii server";
		System.out.println("sending "+text+" to "+serverAddress+":"+serverPort);
		try{
			// get the registry
			registry=LocateRegistry.getRegistry(
					serverAddress,
					(new Integer(serverPort)).intValue()
					);
			// look up the remote object
			rmiServer=
					(ReceiveMessageInterface)(registry.lookup("rmiServer"));
			// call the remote method
			rmiServer.receiveMessage(text);
		}
		catch(RemoteException e){
			e.printStackTrace();
		}
		catch(NotBoundException e){
			e.printStackTrace();
		}
	}
}
