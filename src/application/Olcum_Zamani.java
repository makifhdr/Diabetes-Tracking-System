package application;

public enum Olcum_Zamani {
	SABAH("Sabah", 7, 8),
	OGLE("Öğle", 12, 13),
	IKINDI("Ikindi", 15, 16),
	AKSAM("Akşam", 18, 19),
	GECE("Gece", 22, 23),
	GECERSIZ("Geçersiz", 0, 24);
	
	private final String etiket;
    private final int baslangicSaat;
    private final int bitisSaat;
	
	Olcum_Zamani(String etiket, int baslangicSaat, int bitisSaat){
		this.etiket = etiket;
		this.baslangicSaat = baslangicSaat;
		this.bitisSaat = bitisSaat;
	}
	
	public String getEtiket() {
		return this.etiket;
	}
	
	@Override
	public String toString() {
		return this.etiket;
	}
	
	public static Olcum_Zamani fromEtiket(String etiket) {
        for (Olcum_Zamani olcum_zamani : Olcum_Zamani.values()) {
            if (olcum_zamani.getEtiket().equalsIgnoreCase(etiket)) {
                return olcum_zamani;
            }
        }
        throw new IllegalArgumentException("Geçersiz etiket: " + etiket);
    }
	
	public static Olcum_Zamani fromTimestamp(int hour) {

        for (Olcum_Zamani oz : Olcum_Zamani.values()) {
            if (oz != GECERSIZ && hour >= oz.baslangicSaat && hour < oz.bitisSaat) {
                return oz;
            }
        }
        return GECERSIZ;
    }
}
