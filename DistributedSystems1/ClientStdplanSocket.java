/**
*@author DomG Distributed Systems 
*@version 2.0
*/
import java.util.ArrayList;
import java.util.Scanner;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


public class ClientStdplanSocket extends Vorlesung {
	/**
	 * //private da nur in der klasse sichtbar und nicht in classen des packages oder fremden classen
	 */
	private static ArrayList<Vorlesung> alleVorlesungen = new ArrayList<Vorlesung>(); 
	
	public static void main(String[] args) {
		
		
		Scanner eingabe = new Scanner(System.in);
		String programmlaeuft="ja";
		String wahl="";
		System.out.println(alleVorlesungen);
		System.out.println("\t\tWillkommen zur Stundenplanverwaltung für Clients");
		
		while(!(programmlaeuft.equalsIgnoreCase("nein"))){
			/*
			 *System.out.println("Test schleife");
			 */
			System.out.println("\n\t\t--------------------------------------------\n");
			System.out.println("\n\t\tBitte treffen Sie Ihre Auswahl (1/2/3/4):");
			System.out.println("\n\t\t\t1) Füge Vorlesung hinzu");
			System.out.println("\n\t\t\t2) Hole alle Vorlesungen");
			System.out.println("\n\t\t\t3) Wie viel Freizeit habe ich");
			System.out.println("\n\t\t\t4) Schliesse Programm");
			System.out.println("\n\t\t--------------------------------------------\n");
			wahl=eingabe.next();
			/*
			 * Menüauswahl auf client was getan werden soll
			 */
			switch(wahl){
			
				case "1": 			System.out.println("\t\tSie haben "+wahl+") Füge Vorlesung hinzu gewählt\n");
									eingabeVorlesungen();
									printAll();
									String printall =printAll();
									schreibeStringDatei(printAll());
									fuegeVorlesungHinzu();
									System.out.println(alleVorlesungen);
								
				break;
				case "2": 			System.out.println("\t\tSie haben "+wahl+") Hole alle Vorlesungen  gewählt\n");
									holeAlleVorlesungen();
									System.out.println(alleVorlesungen);							
				break;
				case "3": 			System.out.println("\t\tSie haben "+wahl+") Wieviel Zeit habe ich gewählt\n");
				break;
				case "4": 			System.out.println("\t\tSie haben "+wahl+") Schliesse Programm gewählt\n");
									schliesseProgramm();
									System.exit(1);
				break;
				default:	System.out.println("\t\tFalsche Eingabe, bitte noch einmal 1-4 eingeben:\n");
			}//ende switch
				
			
			
			/*
			 * abfrage ob äussere Schleife noch laufen soll oder programm beendet wird (wenn man wahl=4 trifft soll an server serverprogrun="nein" gesendet werden und clientprogrun="nein" auf client aus schleife springen und beenden
			 */
		System.out.println("\t\tMöchten Sie im Clientprogramm fortfahren? (ja/nein)\n");
		programmlaeuft=eingabe.next();
		//if(programmlaeuft.equalsIgnoreCase("nein"))  hier anders gelöst in der while schleife
		//break;
		}//ende while programm auf client
		/*
		 * Testausgabe Schleife beendet
		 */
		System.out.println("Test schleife beendet");		
	eingabe.close();
	
	}//ende main
	
	
	public static void eingabeVorlesungen()
	{	
		System.out.println("\nHier fuegen Sie eine Vorlesung hinzu\n");
		Scanner eingabe = new Scanner(System.in);
		while(true){
			Vorlesung tmp = new Vorlesung(); // Objekt vom Typ Vorlesung
		
			System.out.println("Bitte Titel der Vorlesung eingeben: ");
			tmp.titelVorlesung= eingabe.next();
			System.out.println("Bitte Wochentag dieser Vorlesung eingeben: ");
			tmp.wochentag= eingabe.next();
			System.out.println("Bitte Uhrzeit des Beginns dieser Vorlesung eingeben: ");
			tmp.uhrzeitBeginn= eingabe.next();
		
			alleVorlesungen.add(tmp);
		
			System.out.println("Möchten Sie noch eine Vorlesung eingeben? (ja/nein)\n");
			String weiter;
			weiter=eingabe.next();
			if(weiter.equalsIgnoreCase("nein"))
				break;
		}//ende while
	}//ende eingabeVorlesungen()
	
	/*
	 * fügt auf dem server vorlesungen hinzu
	 */
	public static void fuegeVorlesungHinzu(){
		try {
			Socket socket=new Socket("localhost",1065);
			OutputStream os=socket.getOutputStream();
			OutputStreamWriter osw=new OutputStreamWriter(os);
			PrintWriter printW=new PrintWriter(osw);
			
			String datei = "clientdatei.txt";
			File file = new File(datei);
			
			Scanner scanner = null;
			try{
				scanner = new Scanner(file);
				String tmpOfFile="";
				//printW.println("Nachricht vom Client:"); // würde sonst in der serverdatei.txt mitgespeichert werden wegen printW
				while (scanner.hasNext()){
					tmpOfFile=scanner.nextLine(); //einlesen in tmpOfFile
					printW.println(tmpOfFile); //übertragung von tmpOfFile
				}//ende while
				//printW.println("Daten vom Client vollständig übertragen. \nEnde der Übertragung vom Client!\n"); // würde sonst in der serverdatei.txt mitgespeichert werden wegen printW
		}catch(FileNotFoundException e){
		System.out.println("Datei nicht gefunden");
		}
			printW.flush();
		//ende try	
		} catch (IOException e) {
			System.out.println("Keine Server-Verbindung!");
			
		}//ende catch
	}//ende fuegeVorlesungHinzu
	
	
public static void schreibeStringDatei(String input){
		File a =new File("./clientdatei.txt");
		FileWriter fw=null;
		try {
		fw = new FileWriter(a);
		} catch (IOException e) {
		System.out.println("Kann nicht auf Datei zugreifen");
		}
		try {
		fw.write(input);
		} catch (IOException e) {
		System.out.println("Kann nicht auf Datei schreiben");
		}
		try {
		fw.close();
		} catch (IOException e) {
		// TODO Auto-generated catch block
		System.out.println("Kann nicht den FileWriter schliessen");
		}
	}//ende schreibeStringDatei()

public static String printAll()
{
	String stringAlleVorlesungen ="";
	String separator=";";
	for (int i = 0; i < alleVorlesungen.size(); i++) {
		
		Object tmp=	alleVorlesungen.get(i);
		
		Vorlesung tmpVorlesung=(Vorlesung)tmp;
		String stringVorlesung =tmpVorlesung.toString();
		System.out.println("stringVorlesung: "+stringVorlesung);
		stringAlleVorlesungen =stringAlleVorlesungen.concat(stringVorlesung.concat(separator));
	}	
//System.out.println("\n!!String aller Vorlesungen: \n"+stringAlleVorlesungen); //zum testen 
		
return stringAlleVorlesungen;	
}//ende printAll()

public static String holeAlleVorlesungen(){
	String stringVomServer="";
	String separator=";";
	try{
//Daten vom server empfangen

	System.out.println("Verbindung zum Server wird aufgebaut");
	Socket socket = new Socket("localhost", 1065);
	// erzeuge kommunikationsendpunkt um daten zu lesen
	BufferedReader bufRead = new BufferedReader(new InputStreamReader(
	socket.getInputStream()));
	System.out.println("Verbindungsstatus: OK\n");
	String temp1;
	while ((temp1=bufRead.readLine())!= null) {

		System.out.println(temp1+"\n");
		stringVomServer =stringVomServer.concat(temp1.concat(separator));
	}
	} catch (IOException e) {
		System.out.println("Keine Client-Verbindung!");
		
	}//ende catch holeAlleVorl
	return stringVomServer;
}//ende holeAlleVorl
	
public static void schliesseProgramm(){
	try {
		Socket socket=new Socket("localhost",1065);
		OutputStream os=socket.getOutputStream();
		OutputStreamWriter osw=new OutputStreamWriter(os);
		PrintWriter printW=new PrintWriter(osw);
	
		String serverprogrun = "beenden";
		printW.println(serverprogrun); //übertragung damit server beendet
		printW.flush();
		printW.close();
		socket.close();	
	
//ende try	
	} catch (IOException e) {
		System.out.println("Keine Verbindung zum Server!");
		System.exit(1);
	}//ende catch
}


}//ende class client
