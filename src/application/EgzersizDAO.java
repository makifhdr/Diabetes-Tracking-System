package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EgzersizDAO {

	private HastaDAO hastaDAO = new HastaDAO();
	
	public int getEgzersizId(String hasta_tc, Date tarih) {
		
		String sql = "SELECT e.*, LOWER(e.tarih_araligi) AS start_date, UPPER(e.tarih_araligi) AS end_date FROM egzersiz e WHERE e.hasta_tc = ? AND ?::date <@ e.tarih_araligi;";
		
		int id = 0;
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				id = rs.getInt("id");
			}
			
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
			return id;
		}
	}
	
	public void hastaEgzersizEkle(Egzersiz egzersiz) throws Exception {
		String sql = "INSERT INTO egzersiz (hasta_tc, egzersiz_turu, tarih_araligi) VALUES (?, ?::egzersiz_turu, ?::daterange)";
		
		if(!egzersiz.getEgzersiz_Turu().equals(Egzersiz_Turu.YOK)) {
			try (Connection conn = VTBaglantisi.connect();
					 PreparedStatement ps = conn.prepareStatement(sql)) {
		        	conn.setAutoCommit(false);
		        	
		        	ps.setString(1, egzersiz.getHasta().getTc_kimlik_no());
		        	ps.setString(2, egzersiz.getEgzersiz_Turu().toString());
		        	ps.setString(3, "[" + egzersiz.getTarihAraligi().getBaslangic().toString() + "," + egzersiz.getTarihAraligi().getBitis().toString() + ")");
		        	
		        	ps.executeUpdate();
		        	
		        	conn.commit();
		        	
		        	System.out.println("Egzersiz başarıyla eklendi");
		        }
		}
	}
	
	public void hastaEgzersizTakipEkle(String hasta_tc, Date tarih, boolean uygulandi) {
		
		String takipSQL = "INSERT INTO egzersiz_takip (hasta_tc, tarih, egzersiz_id, uygulandi) VALUES (?, ?, ?, ?)";
		
		try (Connection conn = VTBaglantisi.connect();
			 PreparedStatement takipPS = conn.prepareStatement(takipSQL))	{
	    	conn.setAutoCommit(false);
	    	
	    	takipPS.setString(1, hasta_tc);
	    	takipPS.setDate(2, tarih);
	    	takipPS.setInt(3, getEgzersizId(hasta_tc, tarih));
	    	takipPS.setBoolean(4, uygulandi);
	    	
	    	takipPS.executeUpdate();
	    	
	    	conn.commit();
	    	
	    	System.out.println("Egzersiz takip başarıyla eklendi");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void hastaEgzersizGuncelle(String hasta_tc, Date tarih, boolean uygulandi) throws SQLException {
		if(checkEgzersizTakip(hasta_tc, tarih)) {
		
		String sql = "UPDATE egzersiz_takip SET uygulandi = ? WHERE egzersiz_id = ?";
		
		try (Connection conn = VTBaglantisi.connect();
				 PreparedStatement ps = conn.prepareStatement(sql))	{
		    	conn.setAutoCommit(false);
		    	
		    	ps.setBoolean(1, uygulandi);
		    	ps.setInt(2, getEgzersizId(hasta_tc, tarih));
		    	
		    	ps.executeUpdate();
		    	
		    	conn.commit();
		    	
		    	System.out.println("Egzersiz takip başarıyla güncellendi");
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		else {
			hastaEgzersizTakipEkle(hasta_tc, tarih, uygulandi);
		}
	}
	
	public List<Egzersiz> getHastaEgzersizler(String hasta_tc) {
		List<Egzersiz> egzersizList = new ArrayList<>();
		
		String sql = "SELECT e.*, LOWER(e.tarih_araligi) AS start_date, UPPER(e.tarih_araligi) AS end_date FROM egzersiz e WHERE e.hasta_tc = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Egzersiz egzersiz = new Egzersiz(hastaDAO.getHasta(hasta_tc), new TarihAraligi(rs.getDate("start_date"), 
						rs.getDate("end_date")), Egzersiz_Turu.fromEtiket(rs.getString("egzersiz_turu")));
				egzersizList.add(egzersiz);
			}
			
			return egzersizList;
		} catch (SQLException e) {
			return egzersizList;
		}
	}

	public Egzersiz getCurrentEgzersiz(String hasta_tc, Date tarih) {
		Egzersiz egzersiz = null;
		
		String sql = "SELECT e.*, LOWER(e.tarih_araligi) AS start_date, UPPER(e.tarih_araligi) AS end_date FROM egzersiz e WHERE e.hasta_tc = ? AND ?::date <@ e.tarih_araligi;";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				egzersiz = new Egzersiz(hastaDAO.getHasta(hasta_tc), new TarihAraligi(rs.getDate("start_date"), 
						rs.getDate("end_date")), Egzersiz_Turu.fromEtiket(rs.getString("egzersiz_turu")));
			}
			
			return egzersiz;
		} catch (SQLException e) {
			return egzersiz;
		}
	}
	
	public boolean checkEgzersizTakip(String hasta_tc, Date tarih) {
		
		String sql = "SELECT e.* FROM egzersiz_takip e WHERE e.hasta_tc = ? AND e.tarih = ?;";
		
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
	
	public boolean checkEgzersizUygulandiTakip(String hasta_tc, Date tarih) {
		
		String sql = "SELECT e.* FROM egzersiz_takip e WHERE e.hasta_tc = ? AND e.tarih = ?;";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next() && rs.getBoolean("uygulandi")) {
				return true;
			}else {
				return false;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
}
