package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiyetDAO {
	
	private HastaDAO hastaDAO = new HastaDAO();
	
	public int getDiyetId(String hasta_tc, Date tarih) {
		
		String sql = "SELECT d.*, LOWER(d.tarih_araligi) AS start_date, UPPER(d.tarih_araligi) AS end_date FROM diyet d WHERE d.hasta_tc = ? AND ?::date <@ d.tarih_araligi;";
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
	
	public void hastaDiyetEkle(Diyet diyet) throws Exception {
		String sql = "INSERT INTO diyet (hasta_tc, diyet_turu, tarih_araligi) VALUES (?, ?::diyet_turu, ?::daterange)";
		
		try (Connection conn = VTBaglantisi.connect();
			 PreparedStatement ps = conn.prepareStatement(sql))	{
	    	conn.setAutoCommit(false);
	    	
	    	ps.setString(1, diyet.getHasta().getTc_kimlik_no());
	    	ps.setString(2, diyet.getDiyet_Turu().toString());
	    	ps.setString(3, "[" + diyet.getTarihAraligi().getBaslangic().toString() + "," + diyet.getTarihAraligi().getBitis().toString() + ")");
	    	
	    	ps.executeUpdate();
	    	
	    	conn.commit();
	    	
	    	System.out.println("Diyet başarıyla eklendi");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void hastaDiyetTakipEkle(String hasta_tc, Date tarih, boolean uygulandi) {
		
		String takipSQL = "INSERT INTO diyet_takip (hasta_tc, tarih, diyet_id, uygulandi) VALUES (?, ?, ?, ?)";
		
		try (Connection conn = VTBaglantisi.connect();
			 PreparedStatement takipPS = conn.prepareStatement(takipSQL))	{
	    	conn.setAutoCommit(false);
	    	
	    	takipPS.setString(1, hasta_tc);
	    	takipPS.setDate(2, tarih);
	    	takipPS.setInt(3, getDiyetId(hasta_tc, tarih));
	    	takipPS.setBoolean(4, uygulandi);
	    	
	    	takipPS.executeUpdate();
	    	
	    	conn.commit();
	    	
	    	System.out.println("Diyet takip başarıyla eklendi");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void hastaDiyetGuncelle(String hasta_tc, Date tarih, boolean uygulandi) throws SQLException {
		if(checkDiyetTakip(hasta_tc, tarih)) {
		
		String sql = "UPDATE diyet_takip SET uygulandi = ? WHERE diyet_id = ? AND tarih = ?";
		
		try (Connection conn = VTBaglantisi.connect();
				 PreparedStatement ps = conn.prepareStatement(sql))	{
		    	conn.setAutoCommit(false);
		    	
		    	ps.setBoolean(1, uygulandi);
		    	ps.setInt(2, getDiyetId(hasta_tc, tarih));
		    	ps.setDate(3, tarih);
		    	
		    	ps.executeUpdate();
		    	
		    	conn.commit();
		    	
		    	System.out.println("Diyet başarıyla güncellendi");
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		}
		else {
			hastaDiyetTakipEkle(hasta_tc, tarih, uygulandi);
		}
	}
	
	public List<Diyet> getHastaDiyetler(String hasta_tc) {
		List<Diyet> diyetList = new ArrayList<>();
		
		String sql = "SELECT d.*, LOWER(tarih_araligi) AS start_date, UPPER(tarih_araligi) AS end_date FROM diyet d WHERE d.hasta_tc = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Diyet diyet = new Diyet(hastaDAO.getHasta(hasta_tc), new TarihAraligi(rs.getDate("start_date"), 
						rs.getDate("end_date")), Diyet_Turu.fromEtiket(rs.getString("diyet_turu")));
				diyetList.add(diyet);
			}
			
			return diyetList;
		} catch (SQLException e) {
			e.printStackTrace();
			return diyetList;
		}
	}
	
	public Diyet getCurrentDiyet(String hasta_tc, Date tarih) {
		Diyet diyet = null;
		
		String sql = "SELECT d.*, LOWER(tarih_araligi) AS start_date, UPPER(tarih_araligi) AS end_date FROM diyet d WHERE d.hasta_tc = ? AND ?::date <@ d.tarih_araligi;";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
			
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				diyet = new Diyet(hastaDAO.getHasta(hasta_tc), new TarihAraligi(rs.getDate("start_date"), rs.getDate("end_date")), Diyet_Turu.fromEtiket(rs.getString("diyet_turu")));
			}
			
			return diyet;
		} catch (SQLException e) {
			return diyet;
		}
	}
	
	public boolean checkDiyetTakip(String hasta_tc, Date tarih) {
		
		String sql = "SELECT d.* FROM diyet_takip d WHERE d.hasta_tc = ? AND d.tarih = ?;";
		
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
	
	public Boolean checkDiyetUygulandiTakip(String hasta_tc, Date tarih) {
		
		String sql = "SELECT d.* FROM diyet_takip d WHERE d.hasta_tc = ? AND d.tarih = ?;";
		
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
