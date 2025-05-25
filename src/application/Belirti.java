package application;

public class Belirti {
	private Hasta hasta;
	private String belirti_adi;
	
	public Belirti(Hasta hasta, String belirti_adi) {
		this.hasta = hasta;
		this.belirti_adi = belirti_adi;
	}

	public Hasta getHasta() {
		return hasta;
	}

	public String getBelirti_adi() {
		return belirti_adi;
	}
}
