package application;

public enum Diyet_Turu {
	DENGELI_BESLENME("Dengeli Beslenme", "Dengeli Beslenme Diyeti; Diyabetli bireylerin yaşam tarzına uygun, dengeli ve sürdürülebilir bir diyet yaklaşımıdır. "
			+ "Tüm besin gruplarından yeterli miktarda alınır; porsiyon kontrolü, mevsimsel taze ürünler ve su tüketimi temel unsurlardır."),
	AZ_SEKERLI("Az Şekerli Diyet", "Az Şekerli Diyet; Şekerli gıdalar sınırlanır, kompleks karbonhidratlara öncelik verilir. Lifli gıdalar ve düşük glisemik indeksli besinler tercih edilir."),
	SEKERSIZ("Şekersiz Diyet", "Şekersiz Diyet; Rafine şeker ve şeker katkılı tüm ürünler tamamen dışlanır. Hiperglisemi riski taşıyan bireylerde önerilir."),
	YOK("Yok","Yok");
	
	private final String etiket;
	private final String aciklama;
	
	Diyet_Turu(String etiket, String aciklama){
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
	
	public static Diyet_Turu fromEtiket(String etiket) {
        for (Diyet_Turu diyet_turu : Diyet_Turu.values()) {
            if (diyet_turu.getEtiket().equalsIgnoreCase(etiket)) {
                return diyet_turu;
            }
        }
        throw new IllegalArgumentException("Geçersiz etiket: " + etiket);
    }
}
