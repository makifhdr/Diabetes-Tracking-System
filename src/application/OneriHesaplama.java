package application;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class OneriHesaplama {
	
	private HastaDAO hastaDAO = new HastaDAO();
	
	public double getInsulinOnerisi(String hasta_tc, Date tarih) {
		
		double ort = hastaDAO.getHasta(hasta_tc).getGunOrtalama( tarih);
		
		if(ort <= 110) {
			return 0.0;
		}
		else if(ort >= 111 && ort <= 150) {
			return 1.0;
		}
		else if (ort >= 151 && ort <= 200) {
			return 2.0;
		}
		else {
			return 3.0;
		}
	}
	
	public static Oneri getOneri(List<Olcum> olcumler, List<Belirti> belirtiler) {
		
		double toplam = 0.0;
		
		for(Olcum olcum : olcumler) {
			toplam += olcum.getMiktar();
		}
		
		double olcumMiktar = toplam/(double)olcumler.size();
		
		List<String> belirtiList = new ArrayList<>();
		
		for(Belirti belirti : belirtiler) {
			belirtiList.add(belirti.getBelirti_adi().toUpperCase());
		}
		
		if(olcumMiktar < 70.0) {
			if(belirtiList.contains("Nöropati".toUpperCase()) && belirtiList.contains("Polifaji".toUpperCase()) && belirtiList.contains("Yorgunluk".toUpperCase())) {
				return new Oneri(Diyet_Turu.DENGELI_BESLENME, Egzersiz_Turu.YOK);
			}else {
				return null;
			}
		}else if(olcumMiktar >= 70.0 && olcumMiktar < 110.0) {
			if(belirtiList.contains("Yorgunluk".toUpperCase()) && belirtiList.contains("Kilo Kaybı".toUpperCase())) {
				return new Oneri(Diyet_Turu.AZ_SEKERLI, Egzersiz_Turu.YURUYUS);
			}
			if(belirtiList.contains("Polifaji".toUpperCase()) && belirtiList.contains("Polidipsi".toUpperCase())) {
				return new Oneri(Diyet_Turu.DENGELI_BESLENME, Egzersiz_Turu.YURUYUS);
			}else {
				return null;
			}
		}else if(olcumMiktar >= 110.0 && olcumMiktar < 180.0) {
			if(belirtiList.contains("Bulanık Görme".toUpperCase()) && belirtiList.contains("Nöropati".toUpperCase())) {
				return new Oneri(Diyet_Turu.AZ_SEKERLI, Egzersiz_Turu.KLINIK);
			}
			if(belirtiList.contains("Poliüri".toUpperCase()) && belirtiList.contains("Polidipsi".toUpperCase())) {
				return new Oneri(Diyet_Turu.SEKERSIZ, Egzersiz_Turu.KLINIK);
			}
			if(belirtiList.contains("Yorgunluk".toUpperCase()) && belirtiList.contains("Nöropati".toUpperCase()) && belirtiList.contains("Bulanık Görme".toUpperCase())) {
				return new Oneri(Diyet_Turu.AZ_SEKERLI, Egzersiz_Turu.YURUYUS);
			}else {
				return null;
			}
		}else {
			if(belirtiList.contains("Yaraların Yavaş İyileşmesi".toUpperCase()) && belirtiList.contains("Polifaji".toUpperCase()) && belirtiList.contains("Polidipsi".toUpperCase())) {
				return new Oneri(Diyet_Turu.SEKERSIZ, Egzersiz_Turu.KLINIK);
			}
			if(belirtiList.contains("Yaraların Yavaş İyileşmesi".toUpperCase()) && belirtiList.contains("Kilo Kaybı".toUpperCase())) {
				return new Oneri(Diyet_Turu.SEKERSIZ, Egzersiz_Turu.YURUYUS);
			}else {
				return null;
			}
		}
	}
}
