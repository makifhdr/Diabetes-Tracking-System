package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoktorDAO {
		
	public List<Hasta> getDoktorHastalari(String doktor_tc) {
		List<Hasta> doktorHastalari = new ArrayList<>();
		
		String sql = "SELECT k.* FROM hasta h JOIN kullanici k ON h.tc_kimlik_no = k.tc_kimlik_no WHERE h.doktor_tc = ?;";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);){
			
			ps.setString(1, doktor_tc);

			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				doktorHastalari.add(new Hasta(rs.getString("tc_kimlik_no"),rs.getString("sifre"),rs.getString("ad"),rs.getString("soyad"),
					rs.getDate("dogum_tarihi"),rs.getString("eposta"),ImageAndBase64Op.base64ToImage(rs.getString("profil_resmi")),
					Cinsiyet.valueOf(rs.getString("cinsiyet"))));
			}
			
			return doktorHastalari;
		} catch (SQLException e) {			
			return doktorHastalari;
		}
	}
	
	public Doktor getDoktor(String doktor_tc) {
		Doktor doktor = null;
		
		String sql = "SELECT k.* FROM kullanici k WHERE k.tc_kimlik_no = ? AND rol = 'doktor';";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);){
			
			ps.setString(1, doktor_tc);

			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				doktor = new Doktor(rs.getString("tc_kimlik_no"),rs.getString("sifre"),rs.getString("ad"),rs.getString("soyad"),
					rs.getDate("dogum_tarihi"),rs.getString("eposta"),ImageAndBase64Op.base64ToImage(rs.getString("profil_resmi")),Cinsiyet.valueOf(rs.getString("cinsiyet")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return doktor;		
	}
	
	public boolean girisYap(String tc_kimlik_no, String sifre) {
		String sql = "SELECT sifre FROM kullanici WHERE tc_kimlik_no = ? AND rol = 'doktor'";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement stmt = conn.prepareStatement(sql)){
			
		    stmt.setString(1, tc_kimlik_no);
		    		    
		    ResultSet rs = stmt.executeQuery();
		    
		    if (rs.next()) {
		        String veritabanindakiSifre = rs.getString("sifre");
		        String cozulmusSifre = AESCryptoUtil.decrypt(veritabanindakiSifre);
		        return cozulmusSifre.equals(sifre);
		    } else {
		        return false;
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void doktorEkle(Doktor doktor) throws Exception {
        String doktorSQL = "INSERT INTO doktor (tc_kimlik_no) VALUES (?)";
        
        String kullaniciSQL = "INSERT INTO kullanici (tc_kimlik_no, sifre, ad, soyad, dogum_tarihi, eposta, rol, profil_resmi, cinsiyet) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'doktor'::rol, ?, ?::cinsiyet)";

        try (Connection conn = VTBaglantisi.connect();
        		PreparedStatement psKullanici = conn.prepareStatement(kullaniciSQL);
                PreparedStatement psDoktor = conn.prepareStatement(doktorSQL)){
        	
            conn.setAutoCommit(false);
            psKullanici.setString(1, doktor.getTc_kimlik_no());
            psKullanici.setString(2, AESCryptoUtil.encrypt(doktor.getSifre()) );
            psKullanici.setString(3, doktor.getAd());
            psKullanici.setString(4, doktor.getSoyad());
            psKullanici.setDate(5, doktor.getDogumTarihi());
            psKullanici.setString(6, doktor.getE_posta());
            psKullanici.setString(7, ImageAndBase64Op.imageToBase64(doktor.getProfil_resmi()));
            psKullanici.setString(8, doktor.getCinsiyet().toString());
            	
            psKullanici.executeUpdate();
            	
            psDoktor.setString(1, doktor.getTc_kimlik_no());
            
            psDoktor.executeUpdate();
                
            conn.commit();
                System.out.println("Doktor başarıyla eklendi.");
            }
        }
}
