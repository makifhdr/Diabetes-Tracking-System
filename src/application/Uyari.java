package application;

import java.sql.Date;

public class Uyari {
	
	private String hastaTC;
	private Date tarih;
	private String uyariTipi;
	private String uyariMesaji;
	
	public Uyari(String hastaTC, Date tarih, String uyariTipi, String uyariMesaji) {
		this.hastaTC = hastaTC;
		this.tarih = tarih;
		this.uyariTipi = uyariTipi;
		this.uyariMesaji = uyariMesaji;
	}
	
	public String getHastaTC() {
		return hastaTC;
	}

	public Date getTarih() {
		return tarih;
	}

	public String getUyariTipi() {
		return uyariTipi;
	}

	public String getUyariMesaji() {
		return uyariMesaji;
	}		
	
}
