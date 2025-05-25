package application;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

public class UyariOlustur {
	
	private static UyariDAO uyariDAO = new UyariDAO();
	
	private static OlcumDAO olcumDAO = new OlcumDAO();
	
	private static HastaDAO hastaDAO = new HastaDAO();
	
	public static void setUyari(String hasta_tc) {
		
		Date tarih = Date.valueOf(LocalDateTime.now().toLocalDate());
		
		List<Olcum> hastaOlcumleri = olcumDAO.getHastaTarihOlcumleri(hasta_tc, tarih);
		
		if(!hastaOlcumleri.isEmpty()) {
			if(hastaOlcumleri.size() < 3) {
				uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"Ölçüm Yetersiz Uyarısı", "Hastanın günlük kan şekeri ölçüm\nsayısı yetersiz (<3). Durum izlenmelidir."), tarih);
			}else {
				
				double ort = hastaDAO.getHasta(hasta_tc).getGunOrtalama(tarih);
				
				if(ort < 70.0) {
					uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"Acil Uyarı", "Hastanın günlük kan şekeri ölçüm\nsayısı yetersiz (<3). Durum izlenmelidir."), tarih);
				}else if(ort >= 70 && ort <= 110) {
					uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"Uyarı Yok", "Kan şekeri seviyesi normal\naralıkta. Hiçbir işlem gerekmez."), tarih);
				}else if(ort >= 111 && ort <= 150) {
					uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"Takip Uyarısı", "Hastanın kan şekeri 111-150 mg/dL\narasında. Durum izlenmeli."), tarih);
				}else if(ort >= 151 && ort <= 200) {
					uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"İzleme Uyarısı", "Hastanın kan şekeri 151-200 mg/dL\narasında. Diyabet kontrolü gereklidir."), tarih);
				}else {
					uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"Acil Müdahale Uyarısı", "Hastanın kan şekeri 200 mg/dL'nin üzerinde.\nHiperglisemi durumu.\n"
							+ "Acil müdahale gerekebilir."), tarih);
				}
			}
		}
		else {
			uyariDAO.uyariEkle(new Uyari(hasta_tc, tarih,"Ölçüm Eksik Uyarısı", "Hasta gün boyunca kan şekeri ölçümü\nyapmamıştır. Acil takip önerilir."), tarih);
		}
	}
}
