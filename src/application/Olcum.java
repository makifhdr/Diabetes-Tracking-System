package application;

import java.sql.Date;

public class Olcum {
	private Hasta hasta;
	private Date tarih;
	private Double miktar;
	private Olcum_Zamani olcum_zamani;
	
	public Olcum(Hasta hasta, Date tarih, Double miktar, Olcum_Zamani olcum_Zamani) {
		this.hasta = hasta;
		this.miktar = miktar;
		this.olcum_zamani = olcum_Zamani;
		this.tarih = tarih;
	}

	public Hasta getHasta() {
		return this.hasta;
	}

	public Double getMiktar() {
		return this.miktar;
	}

	public Olcum_Zamani getOlcum_Zamani() {
		return this.olcum_zamani;
	}
	
	public Date getTarih() {
		return this.tarih;
	}
}
