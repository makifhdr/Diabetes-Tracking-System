package application;

public class Diyet {
	private Hasta hasta;
	private TarihAraligi tarihAraligi;
	private Diyet_Turu diyet_turu;
	
	public Diyet() {
		this.hasta = null;
		this.tarihAraligi = null;
		this.diyet_turu = null;
	}
	
	public Diyet(Hasta hasta, TarihAraligi tarihAraligi, Diyet_Turu diyet_turu) {
		this.hasta = hasta;
		this.tarihAraligi = tarihAraligi;
		this.diyet_turu = diyet_turu;
	}

	public Hasta getHasta() {
		return this.hasta;
	}

	public TarihAraligi getTarihAraligi() {
		return this.tarihAraligi;
	}

	public void setTarihAraligi(TarihAraligi tarihAraligi) {
		this.tarihAraligi = tarihAraligi;
	}

	public Diyet_Turu getDiyet_Turu() {
		return this.diyet_turu;
	}
}
