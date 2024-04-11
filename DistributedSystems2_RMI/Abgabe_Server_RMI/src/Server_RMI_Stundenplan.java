/**
*@author DomG RMI Distributed Systems
*@version 2.0
*/


import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Scanner;


public class Server_RMI_Stundenplan extends UnicastRemoteObject implements I_RMI_Stundenplan{

	
	public static ArrayList<Vorlesung> alleVorlesungen = new ArrayList<Vorlesung>();
	
	
	public void fuegeVorlesungHinzu(Vorlesung neueV) throws RemoteException{
		
		
		/**
		 * fügt vorlesung dem statischen objekt zu
		 */
		init();
		alleVorlesungen.add(neueV);
		
		try{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("zwischenspeicher.dat"));
		out.writeObject(alleVorlesungen);
		out.close();
		}catch (Exception e){
		System.out.println("Vorlesung konnte nicht zwischengespeichert werden");
		}
		
		/*
		System.out.println("Sie haben beenden gewählt\nFolgendes wird in datei geschrieben:\n");
		
		String stringAlleVorlesungen ="";
		String separator="\n";
		for (int i = 0; i < alleVorlesungen.size(); i++) {
			
			Object tmp=	alleVorlesungen.get(i);
			
			Vorlesung tmpVorlesung=(Vorlesung)tmp;
			String stringVorlesung =tmpVorlesung.toString();
			System.out.println("stringVorlesung: "+stringVorlesung);
			stringAlleVorlesungen =stringAlleVorlesungen.concat(stringVorlesung.concat(separator));
		}
		
		
File datei =new File("./zwischenspeicher.txt");
FileWriter writer=null;
try {
writer = new FileWriter(datei);
} catch (IOException e) {
System.out.println("Kann nicht auf Datei zugreifen");
}
try {
	writer.write(stringAlleVorlesungen);
	writer.write("ende\n");
} catch (IOException e) {
System.out.println("Kann nicht auf Datei schreiben");
}
try {
writer.close();
} catch (IOException e) {
// TODO Auto-generated catch block
System.out.println("Kann nicht den FileWriter schliessen");
}
*/

	}

	public Server_RMI_Stundenplan() throws RemoteException{
		super();
	}
	/**
	 * Arrayliste erzeugen für object das dann zurück gesendet werden kann
	 */
	public ArrayList<Vorlesung> holeAlleVorlesungen() throws RemoteException{
		
		for(int i=0; i< alleVorlesungen.size();i++){
			Vorlesung tmp=alleVorlesungen.get(i);
			tmp.setId(i);	
			
		}
		return alleVorlesungen;
	}
	

	public void schliesseProgramm() throws RemoteException{
		
		//I_RMI_Stundenplan Datenbank=(I_RMI_Stundenplan)Naming.lookup("localhost/Vorlesungsservice");
		//alleVorlesungen=Datenbank.holeAlleVorlesungen();
		
		System.out.println("Sie haben beenden gewählt\nFolgendes wird in datei geschrieben:\n");
		
		try{
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("zwischenspeicher.dat"));
			out.writeObject(alleVorlesungen);
			out.close();
			}catch (Exception e){
			System.out.println("Vorlesung konnte nicht zwischengespeichert werden");
			}
		
		
		
		
		/*
				String stringAlleVorlesungen ="";
				String separator="\n";
				for (int i = 0; i < alleVorlesungen.size(); i++) {
					
					Object tmp=	alleVorlesungen.get(i);
					
					Vorlesung tmpVorlesung=(Vorlesung)tmp;
					String stringVorlesung =tmpVorlesung.toString();
					System.out.println("stringVorlesung: "+stringVorlesung);
					stringAlleVorlesungen =stringAlleVorlesungen.concat(stringVorlesung.concat(separator));
				}
				
				
		File datei =new File("./zwischenspeicher.txt");
		FileWriter writer=null;
		try {
		writer = new FileWriter(datei);
		} catch (IOException e) {
		System.out.println("Kann nicht auf Datei zugreifen");
		}
		try {
			writer.write(stringAlleVorlesungen);
			writer.write("ende\n");
		} catch (IOException e) {
		System.out.println("Kann nicht auf Datei schreiben");
		}
		try {
		writer.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("Kann nicht den FileWriter schliessen");
		}
	
		*/
		
		System.out.println("\t\tServer wurde durch den Client soeben geschlossen\n");
		System.exit(1);
	}
	
	public String[] wievielFreizeitHabeIch()throws RemoteException{
		
	init();
		
		int restMo=10;
		int restDi=10;
		int restMi=10;
		int restDo=10;
		int restFr=10;
		
		for(int i=0; i< alleVorlesungen.size();i++){
			String tmpTag="";
			Vorlesung v=alleVorlesungen.get(i);		
			tmpTag=v.getWochentag();
			
			if(tmpTag.equalsIgnoreCase("Montag")){
				restMo--;
			}
			if(tmpTag.equalsIgnoreCase("Dienstag")){
				restDi--;
			}
			if(tmpTag.equalsIgnoreCase("Mittwoch")){
				restMi--;
			}
			if(tmpTag.equalsIgnoreCase("Donnerstag")){
				restDo--;
			}
			if(tmpTag.equalsIgnoreCase("Freitag")){
				restFr--;
			}	
		}
		
		String[] strArr_tage = new String[5];
		strArr_tage = new String[50];
						
		strArr_tage[0]="Es sind noch "+restMo+" Stunden übrig am Montag";
		strArr_tage[1]="Es sind noch "+restDi+" Stunden übrig am Dienstag";
		strArr_tage[2]="Es sind noch "+restMi+" Stunden übrig am Mittwoch";
		strArr_tage[3]="Es sind noch "+restDo+" Stunden übrig am Donnerstag";
		strArr_tage[4]="Es sind noch "+restFr+" Stunden übrig am Freitag";
		
		return strArr_tage;	
	}
		
	/**
	 * init liest aus datei zwischenspeicher.dat und übergibt werte an das object 
	 */
	public void init() throws RemoteException  {
		System.out.println("Init Methode gestartet:\n");
		
		//Server_RMI_Stundenplan objStdPlan = new Server_RMI_Stundenplan();
		try{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(
				"zwischenspeicher.dat"));
		
		alleVorlesungen= (ArrayList<Vorlesung>) in.readObject();
		System.out.println("Datei erfolgreich eingelesen");
		in.close();
		}catch(FileNotFoundException e){
			System.out.println("Kann nicht auf Datei zugreifen");
		}
		catch(Exception f)
		{
		//	System.out.println("Datei von init() noch leer.");
			}
		
		for(int i=0; i<alleVorlesungen.size();i++){
			
			Vorlesung tmp = alleVorlesungen.get(i);
			tmp.setId(i);
		//Vorlesung tmp = (Vorlesung) in.readObject();
		
		System.out.println(tmp.getId());
		System.out.println(tmp.getTitelVorlesung());
		System.out.println(tmp.getWochentag());
		System.out.println(tmp.getUhrzeitBeginn());
		}
		/*
		try {
		FileReader datei =new FileReader("./zwischenspeicher.txt");
		BufferedReader reader= new BufferedReader(datei);	
		
		String stringAlleVorlesungen ="";
		String separator="\n";
		for (int i = 0; i < reader.size(); i++) {
		*/
			/**
			 * hier übergebe ich der ArrayList<Vorlesung> wieder die einzelnen Vorlesungen unter position i gespeichert.
			 */
		/*
			alleVorlesungen.get(i)=reader;
		String tmpstring =reader.readLine();
			System.out.println(tmpstring);
			
		}
		
		} catch (FileNotFoundException) {
		System.out.println("Kann nicht auf Datei zugreifen");
		}	*/
		
	}

	/**
**brauch ich nicht weil schon registry object erzeugt nämlich RMI_object_server
		**Server_RMI_Stundenplan stundenplan_obj= new Server_RMI_Stundenplan();
**		Server_RMI_Stundenplan stundenplan_obj= new Server_RMI_Stundenplan();
	 * @throws RemoteException 

*/




public static void main(String[] args) throws RemoteException {
	
	Scanner eingabe = new Scanner(System.in);
	
	String serverrun = "ja"; //beenden soll server exit(1); machen
	
	Server_RMI_Stundenplan objStdPlan = new Server_RMI_Stundenplan();
	
	objStdPlan.init();
	
	
	System.out.println("Serverschleife gestartet startet!");
	while(!(serverrun.equalsIgnoreCase("nein")))
	{
			
		
	System.out.println("soll der Server weiter laufen (nein beendet)?");
	serverrun=eingabe.next();
	
	
}//schleife serverrun ende
	eingabe.close();
}//main ende
}//class ende
