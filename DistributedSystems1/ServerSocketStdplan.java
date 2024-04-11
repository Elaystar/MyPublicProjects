/**
 * author Dominik Gierczak
 * version 2.0
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class ServerSocketStdplan {
	
	 //ServerSocketStdplan lesen = new ServerSocketStdplan();
	private static ArrayList<Vorlesung> alleVorlesungen = new ArrayList<Vorlesung>();
	

	public static void main(String[] args) throws IOException {
		String serverrun = "ja"; //beenden soll server exit(1); machen
		while(!(serverrun.equalsIgnoreCase("beenden"))){
		
		//Alle Informationen die der Client dem Server gesendet hat werden in einer Text Datei gespeichert
				//schliesseProgramm();
		// temp hat client string
		//stdplan.schreibeStringDatei(stdplan.printAll());
		
			ServerSocket server=new ServerSocket(1065);
			Socket socket=server.accept();
			
			InputStream is=socket.getInputStream();
			InputStreamReader isr=new InputStreamReader(is);
			BufferedReader bufRead=new BufferedReader(isr);
			
			System.out.println("Client schickt:\n");
			
			String temp;
			String stringAlleVorlesungen ="";
			String separator=";";
			
			while ((temp = bufRead.readLine()) != null) {
				if(temp=="beenden"){
					serverrun="beenden";
					System.exit(1);
				}else{
				System.out.println(temp);
				stringAlleVorlesungen =stringAlleVorlesungen.concat(temp.concat(separator));
				}
			}
			
			
			//Server sendet
			
			socket = server.accept();
			PrintWriter printW = new PrintWriter(socket.getOutputStream());
			// daten werden an den client geschickt

			File file = new File("serverdatei.txt");
			Scanner scanner = null;
			try {
				//printW.println("\nDaten der serverdatei.txt auf dem Server:\n"); // würde sonst in der serverdatei.txt mitgespeichert werden wegen printW
				scanner = new Scanner(file);
				String tmp = "";
				while (scanner.hasNext()) {
					tmp = scanner.nextLine();
					printW.println(tmp);
					printW.flush();
				}
				printW.println("\nDaten vom Server vollständig vom übertragen.\n Ende der Übertragung vom Server!");
				printW.close();	
			} catch (FileNotFoundException e) {
				System.out.println("File not found!");

			}
			
			
			//zu speichern
			
				//String stringAlleVorlesungen ="";
				//String separator=";";
				
					
			
		//speichern
			String input= stringAlleVorlesungen;
			File a =new File("./serverdatei.txt");
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
	
	}// while ende vom serverläuft	
		System.exit(1);
	/*die übergabe jedes einzelnen strings jeder vorlesung müssen hier beim Server zu einem string verbunden werden
	public String printAll()
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
	System.out.println("!!String aller Vorlesungen: "+stringAlleVorlesungen);
			
	return stringAlleVorlesungen;	
	}*/
	} // main ende	
	
}