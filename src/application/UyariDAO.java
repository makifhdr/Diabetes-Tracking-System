package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UyariDAO {
	
	private boolean checkUyari(String hasta_tc, Date tarih) {
		String sql = "SELECT u.* FROM uyari u WHERE u.hasta_tc = ? AND u.tarih = ?;";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				return true;
			}else {
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void uyariEkle(Uyari uyari, Date tarih) {
		String sql = "INSERT INTO uyari (hasta_tc, tarih, uyari_tipi, uyari_mesaji) VALUES (?, ?, ?, ?)";
		
		if(!checkUyari(uyari.getHastaTC(), tarih)) {
		
			try (Connection conn = VTBaglantisi.connect();
				 PreparedStatement ps = conn.prepareStatement(sql))	{
		    	conn.setAutoCommit(false);
		    	
		    	ps.setString(1, uyari.getHastaTC());
		    	ps.setDate(2, uyari.getTarih());
		    	ps.setString(3, uyari.getUyariTipi());
		    	ps.setString(4, uyari.getUyariMesaji());
		    	
		    	ps.executeUpdate();
		    	
		    	conn.commit();
		    	
		    	System.out.println("Uyarı başarıyla eklendi");
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		}
	}
	
	public Uyari getCurrentUyari(String hasta_tc, Date tarih) {
		Uyari uyari = null;
		
		String sql = "SELECT u.* FROM uyari u WHERE u.hasta_tc = ? AND u.tarih = ?;";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				uyari = new Uyari(hasta_tc, rs.getDate("tarih"), rs.getString("uyari_tipi"), rs.getString("uyari_mesaji"));
			}
			
			return uyari;
		} catch (SQLException e) {
			return uyari;
		}
	}
	
	public List<Uyari> getUyarilar(String hasta_tc){
		List<Uyari> uyariList = new ArrayList<>();
		
		String sql = "SELECT u.* FROM uyari u WHERE u.hasta_tc = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Uyari uyari = new Uyari(hasta_tc, rs.getDate("tarih"), 
						rs.getString("uyari_tipi"), rs.getString("uyari_mesaji"));
				uyariList.add(uyari);
			}
			
			return uyariList;
		} catch (SQLException e) {
			e.printStackTrace();
			return uyariList;
		}
	}
}
