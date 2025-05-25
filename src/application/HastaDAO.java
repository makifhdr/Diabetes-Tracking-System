package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HastaDAO {
	
	public void hastaEkle(Hasta hasta, Doktor doktor) throws Exception {
        String hastaSQL = "INSERT INTO hasta (tc_kimlik_no, doktor_tc) VALUES (?, ?)";;
        
        String kullaniciSQL = "INSERT INTO kullanici (tc_kimlik_no, sifre, ad, soyad, dogum_tarihi, eposta, rol, profil_resmi, cinsiyet) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'hasta'::rol, ?, ?::cinsiyet)";

        try (Connection conn = VTBaglantisi.connect();
        		PreparedStatement psKullanici = conn.prepareStatement(kullaniciSQL);
                PreparedStatement psHasta = conn.prepareStatement(hastaSQL)){
        	
            conn.setAutoCommit(false);
            psKullanici.setString(1, hasta.getTc_kimlik_no());
            psKullanici.setString(2, AESCryptoUtil.encrypt(hasta.getSifre()) );
            psKullanici.setString(3, hasta.getAd());
            psKullanici.setString(4, hasta.getSoyad());
            psKullanici.setDate(5, hasta.getDogumTarihi());
            psKullanici.setString(6, hasta.getE_posta());
            psKullanici.setString(7, ImageAndBase64Op.imageToBase64(hasta.getProfil_resmi()));
            psKullanici.setString(8, hasta.getCinsiyet().toString());
            	
            psKullanici.executeUpdate();

            psHasta.setString(1, hasta.getTc_kimlik_no());
            psHasta.setString(2, doktor.getTc_kimlik_no());

            psHasta.executeUpdate();
                
            conn.commit();
                System.out.println("Hasta başarıyla eklendi.");
            }
        }
    
	
	public boolean girisYap(String tc_kimlik_no, String sifre) {
		String sql = "SELECT sifre FROM kullanici WHERE tc_kimlik_no = ? AND rol = 'hasta'";
		
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
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public Hasta getHasta(String hasta_tc) {
		Hasta hasta = null;
		
		String sql = "SELECT k.* FROM kullanici k WHERE k.tc_kimlik_no = ? AND rol = 'hasta';";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql)){
			
			ps.setString(1, hasta_tc);

			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				hasta = new Hasta(rs.getString("tc_kimlik_no"),rs.getString("sifre"),rs.getString("ad"),rs.getString("soyad"),
					rs.getDate("dogum_tarihi"),rs.getString("eposta"),ImageAndBase64Op.base64ToImage(rs.getString("profil_resmi")),
					Cinsiyet.valueOf(rs.getString("cinsiyet")));
			}
			
			return hasta;
		} catch (SQLException e) {
			e.printStackTrace();
			return hasta;	
		}	
	}
}
