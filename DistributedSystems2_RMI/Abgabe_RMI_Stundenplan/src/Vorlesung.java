/**
*@author DomG RMI Distributed Systems
*@version 2.0
*/
import java.io.Serializable;
import java.time.LocalTime;

public class Vorlesung implements Serializable{
		private String id;
		private String TitelVorlesung;
		private String Wochentag;
		private LocalTime UhrzeitBeginn;
		
		
		public String getId(){
			return id;
		}
		public void setId(int id){
			this.id=""+id;
		}
		
		public java.time.LocalTime getUhrzeitBeginn(){
			return UhrzeitBeginn;
		}
		
		public void setUhrzeitBeginn(LocalTime UhrzeitBeginn){
			this.UhrzeitBeginn=UhrzeitBeginn;
		}
		
		public String getTitelVorlesung(){
			return TitelVorlesung;
		}
		public void setTitelVorlesung(String TitelVorlesung){
			this.TitelVorlesung=TitelVorlesung;
		}
		
		
		public String getWochentag(){
			return Wochentag;
		}
		public void setWochentag(String Wochentag){
			this.Wochentag=Wochentag;
		}
		
}
