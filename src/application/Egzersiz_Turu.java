package application;

public enum Egzersiz_Turu {
	YURUYUS("Yürüyüş", "Yürüyüş Egzersizi; Hafif tempolu, günlük yapılabilecek bir egzersizdir."),
	BISIKLET("Bisiklet", "Bisiklet Egzersizi; Alt vücut kaslarını çalıştırır ve dış mekanda veya sabit bisikletle uygulanabilir."),
	KLINIK("Klinik", "Klinik Egzersiz; Doktor tarafından verilen belirli hareketleri içeren planlı egzersizlerdir. Stresi azaltılması ve hareket kabiliyetinin artırılması amaçlanır."),
	YOK("Yok", "Yok");
	
	private final String etiket;
	private final String aciklama;
	
	Egzersiz_Turu(String etiket, String aciklama){
		this.etiket = etiket;
		this.aciklama = aciklama;
	}
	
	public String getEtiket() {
		return this.etiket;
	}
	
	public String getAciklama() {
		return this.aciklama;
	}
	
	@Override
	public String toString() {
		return this.etiket;
	}
	
	public static Egzersiz_Turu fromEtiket(String etiket) {
        for (Egzersiz_Turu egzersiz_turu : Egzersiz_Turu.values()) {
            if (egzersiz_turu.getEtiket().equalsIgnoreCase(etiket)) {
                return egzersiz_turu;
            }
        }
        throw new IllegalArgumentException("Geçersiz etiket: " + etiket);
    }
}
