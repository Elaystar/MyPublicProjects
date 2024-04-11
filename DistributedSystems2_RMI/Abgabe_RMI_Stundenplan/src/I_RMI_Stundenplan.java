/**
*@author Dominik Gierczak Martikelnr. 1132980 �bung2 RMI Distributed Systems Hoja
*@version 2.0
*/


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface I_RMI_Stundenplan extends Remote {
	
	public ArrayList<Vorlesung> holeAlleVorlesungen() throws RemoteException;
	
	public void fuegeVorlesungHinzu(Vorlesung neueV) throws RemoteException;

	public String[] wievielFreizeitHabeIch()throws RemoteException;
	
	public void schliesseProgramm() throws RemoteException;
	
	public void init() throws RemoteException;
	
}

