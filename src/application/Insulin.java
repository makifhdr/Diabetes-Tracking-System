package application;

import java.sql.Date;

public class Insulin {
	private String hastaTC;
	private Date tarih;
	private double miktar;

	public Insulin(String hastaTC, Date tarih, double miktar) {
		this.hastaTC = hastaTC;
		this.tarih = tarih;
		this.miktar = miktar;
	}

	public String getHastaTC() {
		return hastaTC;
	}

	public Date getTarih() {
		return tarih;
	}

	public double getMiktar() {
		return miktar;
	}
	
	
}
