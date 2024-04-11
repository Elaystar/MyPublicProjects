/**
*@author Dominik Gierczak Martikelnr. 1132980 �bung2 RMI Distributed Systems Hoja
*@version 2.0
*/
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;



import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;



public class RMI_Client_Stundenplan{
	
	private static LocalTime timeNow =LocalTime.now();
	private static ArrayList<Vorlesung> alleVorlesungen = new ArrayList<Vorlesung>();
	

	public static void main(String[] args) throws Exception{
				
	
		/**
		 * hier erzeuge ich ein object f�r die registry
		 */
		I_RMI_Stundenplan Datenbank=(I_RMI_Stundenplan)Naming.lookup("localhost/Vorlesungsservice");
		
		Vorlesung input = new Vorlesung();
		
		//werte hinzuf�gen testen
		//neueV.setTitelVorlesung("Mathe");
		//neueV.setWochentag("Montag");
		//neueV.setUhrzeitBeginn(timeNow);
		
		//vorlesung in datenbank hinzuf�gen
		//Datenbank.fuegeVorlesungHinzu(neueV);
		
		/*
		neueV.getId();
		neueV.getTitelVorlesung();
		neueV.getWochentag();
		*/
		//vorlesung in datenbank hinzuf�gen
		//Datenbank.fuegeVorlesungHinzu(neueV);
	
		
		//--------------------------------------------------ab�ndern und einbinden alles was oben
		

		Scanner eingabe = new Scanner(System.in);
		String programmlaeuft="ja";
		String wahl="";
		System.out.println("Pr�fwerte von arrayList alleVorlesungen: "+alleVorlesungen+"\n\n");
		System.out.println("\t\tWillkommen zur Stundenplanverwaltung f�r Clients");
		
		Datenbank.init();
		
		while(!(programmlaeuft.equalsIgnoreCase("nein"))){
			/*
			 *System.out.println("Test schleife");
			 */
			System.out.println("\n\t\t--------------------------------------------\n");
			System.out.println("\n\t\tBitte treffen Sie Ihre Auswahl (1/2/3/4):");
			System.out.println("\n\t\t\t1) F�ge Vorlesung hinzu");
			System.out.println("\n\t\t\t2) Hole alle Vorlesungen");
			System.out.println("\n\t\t\t3) Wie viel Freizeit habe ich");
			System.out.println("\n\t\t\t4) Schliesse Programm");
			System.out.println("\n\t\t--------------------------------------------\n");
			wahl=eingabe.next();
			/*
			 * Men�auswahl auf client was getan werden soll
			 */
			switch(wahl){
			
				case "1": 			System.out.println("\t\tSie haben "+wahl+") f�ge Vorlesung hinzu gew�hlt\n");
									
				
									System.out.println("\t\tFolgende Vorlesung stehen bereits in der Datenbank:\n\n");
									Datenbank.init();
									//vorlesung in datenbank hinzuf�gen
									System.out.println("\n\t\tJetzt k�nnen Sie eine weitere Vorlesung hinzuf�gen\n\n");
			
				String tmpwert="";
				while(true){
					//Vorlesung tmp = new Vorlesung(); // Objekt vom Typ Vorlesung
					//werte hinzuf�gen
					/*input.setTitelVorlesung("default");
					input.setWochentag("default");
					input.setUhrzeitBeginn(timeNow);
					*/
					
					System.out.println("Bitte Titel der Vorlesung eingeben: ");
					tmpwert=eingabe.next();
					input.setTitelVorlesung(tmpwert);
					
					System.out.println("Bitte Wochentag dieser Vorlesung eingeben: ");
					tmpwert=eingabe.next();
					input.setWochentag(tmpwert);
										
					System.out.println("Bitte Uhrzeit des Beginns dieser Vorlesung eingeben (Format ist STD:MIN ): ");
					tmpwert=eingabe.next();
					LocalTime zeitLT = LocalTime.parse(tmpwert);				
					System.out.println("Die Uhrzeit wurde automatisch in localtime umgewandelt: "+zeitLT);
					input.setUhrzeitBeginn(zeitLT);
					
					
					//braucht man nicht hier
					//alleVorlesungen.add(neueV);	
					//vorlesung in datenbank hinzuf�gen
					Datenbank.fuegeVorlesungHinzu(input);
										
					System.out.println("M�chten Sie noch eine Vorlesung eingeben? (ja/nein)\n");
					String weiter;
					weiter=eingabe.next();
					if(weiter.equalsIgnoreCase("nein"))
						break;
				}//ende while			
				break;
				
				case "2": 			System.out.println("\t\tSie haben "+wahl+") Hole alle Vorlesungen  gew�hlt\n");
									
									//System.out.println("\t\tSie haben "+wahl+") Ausgabe init():\n");
									Datenbank.init();
									
									System.out.println("\t\tAusgabe aller bisher abgelegten Vorlesungen:\n");
									alleVorlesungen=Datenbank.holeAlleVorlesungen();	
									
									for (int i = 0; i < alleVorlesungen.size(); i++) {
										Vorlesung v=alleVorlesungen.get(i);
									
									System.out.println(v.getId());
									System.out.println(v.getTitelVorlesung());
									System.out.println(v.getWochentag());
									System.out.println(v.getUhrzeitBeginn());
									}
								
				break;
				
				case "3": 			System.out.println("\t\tSie haben "+wahl+") Wieviel Zeit habe ich gew�hlt\n");
									Datenbank.init();
									String[] freizeit = new String[5];
									freizeit = new String[50];
									freizeit=Datenbank.wievielFreizeitHabeIch();
									System.out.println(freizeit[0]);
									System.out.println(freizeit[1]);
									System.out.println(freizeit[2]);
									System.out.println(freizeit[3]);
									System.out.println(freizeit[4]);

				break;
				
				case "4": 			System.out.println("\t\tSie haben "+wahl+") Schliesse Programm gew�hlt\n");
									System.out.println("\t\tServer wird nun geschlossen\n");
									Datenbank.schliesseProgramm();
									System.out.println("\tServer geschlossen!\n");
									System.out.println("\t\tClient wird geschlossen.\nAuf Wiedersehen\n");
									System.exit(1);
				break;
				
				default:	System.out.println("\t\tFalsche Eingabe, bitte noch einmal 1-4 eingeben:\n");
			}//ende switch
				
		
			
			/*
			 * abfrage ob �ussere Schleife noch laufen soll oder programm beendet wird (wenn man wahl=4 trifft soll an server serverprogrun="nein" gesendet werden und clientprogrun="nein" auf client aus schleife springen und beenden
			 */
		//if(programmlaeuft.equalsIgnoreCase("nein"))  hier anders gel�st in der while schleife
		//break;
			//System.out.println("\t\tM�chten Sie im Clientprogramm fortfahren? (ja/nein)\n");
			//programmlaeuft=eingabe.next();
		}//ende while programm auf client
		System.out.println("Pr�fwerte von arrayList alleVorlesungen: "+alleVorlesungen+"\n\n");

		eingabe.close();
		System.exit(1);
		Datenbank.schliesseProgramm();
		System.out.println("programm auf beiden seiten beendet (zus�tzlich)!");
		
	}//ende main

	
	

}//ende class

