package application;

public class Egzersiz {
	private Hasta hasta;
	private TarihAraligi tarihAraligi;
	private Egzersiz_Turu egzersiz_turu;
	
	public Egzersiz(Hasta hasta, TarihAraligi tarihAraligi, Egzersiz_Turu egzersiz_turu) {
		this.hasta = hasta;
		this.tarihAraligi = tarihAraligi;
		this.egzersiz_turu = egzersiz_turu;
	}
	
	public Hasta getHasta() {
		return this.hasta;
	}
	
	public TarihAraligi getTarihAraligi() {
		return this.tarihAraligi;
	}
	
	public Egzersiz_Turu getEgzersiz_Turu() {
		return this.egzersiz_turu;
	}
		
}
