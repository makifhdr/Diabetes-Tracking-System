package application;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class TarihAraligi {
	private Date baslangic;
	private Date bitis;
	
	public TarihAraligi(Date baslangic, Date bitis) {
		this.baslangic = baslangic;
		this.bitis = bitis;
	}

	public Date getBaslangic() {
		return baslangic;
	}

	public Date getBitis() {
		return bitis;
	}
	
	@Override
	public String toString() {
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String formatliTarihBaslangic = formatter.format(this.baslangic);
        String formatliTarihBitis = formatter.format(this.bitis);
		
		return formatliTarihBaslangic + " ile\n" + formatliTarihBitis + " arası";
	}
	
}
