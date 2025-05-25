package application;

public class Oneri {
	
	private Diyet_Turu diyetTuru;
	private Egzersiz_Turu egzersizTuru;
	
	public Oneri(Diyet_Turu diyetTuru, Egzersiz_Turu egzersizTuru) {
		this.diyetTuru = diyetTuru;
		this.egzersizTuru = egzersizTuru;
	}

	public Diyet_Turu getDiyetTuru() {
		return diyetTuru;
	}

	public Egzersiz_Turu getEgzersizTuru() {
		return egzersizTuru;
	}
	
	@Override
	public String toString() {
		return "Diyet: " + this.diyetTuru.toString() + "\nEgzersiz: " + this.egzersizTuru.toString();
	}
}
