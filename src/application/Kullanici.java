package application;

import java.sql.Date;
import java.util.List;

import javafx.scene.image.Image;

abstract class Kullanici {
	protected String tc_kimlik_no;
	private String sifre;
	private String ad;
	private String soyad;
	private Date dogumTarihi;
	private String e_posta;
	private String rol;
	private Image profil_resmi;
	private Cinsiyet cinsiyet;
	
	public Kullanici( String tc_kimlik_no,
	String sifre,
	String ad,
	String soyad,
	Date dogumTarihi,
	String e_posta,
	String rol,
	Image profil_resmi,
	Cinsiyet cinsiyet) {
		this.tc_kimlik_no = tc_kimlik_no;
		this.sifre = sifre;
		this.ad = ad;
		this.soyad = soyad;
		this.dogumTarihi = dogumTarihi;
		this.e_posta = e_posta;
		this.rol = rol;
		this.profil_resmi = profil_resmi;
		this.cinsiyet = cinsiyet;
	}
	
	public String getTc_kimlik_no() {
		return tc_kimlik_no;
	}
	public String getSifre() {
		return sifre;
	}
	public String getAd() {
		return ad;
	}
	public String getSoyad() {
		return soyad;
	}
	public Date getDogumTarihi() {
		return dogumTarihi;
	}
	public String getE_posta() {
		return e_posta;
	}
	public String getRol() {
		return rol;
	}
	public Image getProfil_resmi() {
		return profil_resmi;
	}	
	public Cinsiyet getCinsiyet() {
		return cinsiyet;
	}
}

class Hasta extends Kullanici {
	
	private OlcumDAO olcumDAO = new OlcumDAO();
	
	private DiyetDAO diyetDAO = new DiyetDAO();
	
	private EgzersizDAO egzersizDAO = new EgzersizDAO();
	
	public Hasta(String tc_kimlik_no,
			String sifre,
			String ad,
			String soyad,
			Date dogumTarihi,
			String e_posta,
			Image profil_resmi,
			Cinsiyet cinsiyet) {
		super(tc_kimlik_no, sifre, ad, soyad, dogumTarihi, e_posta, "hasta", profil_resmi,cinsiyet);
	}
	
	public double getGunOrtalama(Date tarih) {
		List<Olcum> olcumler = olcumDAO.getHastaTarihOlcumleri(this.tc_kimlik_no, tarih);
		
		if(!olcumler.isEmpty()) {
			double toplam = 0.0;
			
			for(Olcum olcum : olcumler) {
				if(!olcum.getOlcum_Zamani().equals(Olcum_Zamani.GECERSIZ)) {
					toplam += olcum.getMiktar();
				}
			}
			
			return toplam/(double)olcumler.size();
		}
		else {
			return 0.0;
		}
	}
	
	public Diyet getCurrentDiyet(Date tarih) {
		return diyetDAO.getCurrentDiyet(tc_kimlik_no, tarih);
	}
	
	public Egzersiz getCurrentEgzersiz(Date tarih) {
		return egzersizDAO.getCurrentEgzersiz(tc_kimlik_no, tarih);
	}
}

class Doktor extends Kullanici{
	
	public Doktor(String tc_kimlik_no,
			String sifre,
			String ad,
			String soyad,
			Date dogumTarihi,
			String e_posta,
			Image profil_resmi,
			Cinsiyet cinsiyet) {
		super(tc_kimlik_no, sifre, ad, soyad, dogumTarihi, e_posta, "doktor", profil_resmi,cinsiyet);
	}
}
