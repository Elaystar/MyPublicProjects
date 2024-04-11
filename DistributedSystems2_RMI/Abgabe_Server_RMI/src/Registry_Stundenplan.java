/**
*@author DomG RMI Distributed Systems
*@version 2.0
*/


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Registry_Stundenplan {
	

	public static void main(String[] args) throws Exception {
		
		
		LocateRegistry.createRegistry(1099);
		
		Server_RMI_Stundenplan RMIobjectServer=new Server_RMI_Stundenplan();
		
		Naming.rebind("localhost/Vorlesungsservice", RMIobjectServer);
		System.out.println("Objekt an registry erfolgreich gebunden");
	
	}
}
