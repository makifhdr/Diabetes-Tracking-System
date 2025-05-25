package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OlcumDAO {

	private HastaDAO hastaDAO = new HastaDAO();
	
	public boolean hastaOlcumEkle(String hasta_tc, Olcum olcum) {
		String sql = "INSERT INTO olcum (hasta_tc, tarih, olcum_degeri, olcum_zamani) VALUES (?, ?, ?, ?::olcum_saati)";
		String checkSQL = "SELECT COUNT(*) FROM olcum WHERE olcum_zamani = ?::olcum_saati AND tarih = ? AND hasta_tc = ?";
		
		try (Connection conn = VTBaglantisi.connect();
			 PreparedStatement checkPS = conn.prepareStatement(checkSQL)) {
        	
			checkPS.setString(1, olcum.getOlcum_Zamani().toString());
			checkPS.setDate(2, olcum.getTarih());
			checkPS.setString(3, olcum.getHasta().getTc_kimlik_no());
			
			ResultSet rs = checkPS.executeQuery();
			rs.next();
			
			if(rs.getInt(1) == 0 || olcum.getOlcum_Zamani().equals(Olcum_Zamani.GECERSIZ)) {
	        	try(PreparedStatement ps = conn.prepareStatement(sql)) {
	            	conn.setAutoCommit(false);
	        		
					ps.setString(1, hasta_tc);
					ps.setDate(2, olcum.getTarih());
					ps.setDouble(3, olcum.getMiktar());
					ps.setString(4, olcum.getOlcum_Zamani().toString());
					
					ps.executeUpdate();
					
					conn.commit();
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		return true;
			}else {
				return false;
			}
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}
	
	public List<Olcum> getHastaOlcumleri(String hasta_tc) {
		List<Olcum> olcumListesi = new ArrayList<>();
		
		String sql = "SELECT o.* FROM olcum o WHERE hasta_tc = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
				
			ps.setString(1, hasta_tc);
				
			ResultSet rs = ps.executeQuery();
				
			while(rs.next()) {
				olcumListesi.add(new Olcum(hastaDAO.getHasta(hasta_tc), rs.getDate("tarih"), rs.getDouble("olcum_degeri"), 
							Olcum_Zamani.fromEtiket(rs.getString("olcum_zamani"))));
				
			}
				
			return olcumListesi;
				
		} catch (SQLException e) {
			return olcumListesi;
		}
	}
	
	public List<Olcum> getHastaTarihOlcumleri(String hasta_tc, Date tarih){
		List<Olcum> olcumListesi = new ArrayList<>();
		
		String sql = "SELECT o.* FROM olcum o WHERE hasta_tc = ? AND tarih = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
				
			ps.setString(1, hasta_tc);
			ps.setDate(2, tarih);
				
			ResultSet rs = ps.executeQuery();
				
			while(rs.next()) {
				if(!Olcum_Zamani.fromEtiket(rs.getString("olcum_zamani")).equals(Olcum_Zamani.GECERSIZ)) {
					olcumListesi.add(new Olcum(hastaDAO.getHasta(hasta_tc), rs.getDate("tarih"), rs.getDouble("olcum_degeri"), Olcum_Zamani.fromEtiket(rs.getString("olcum_zamani"))));
				}
			}
				
			return olcumListesi;
				
		} catch (SQLException e) {
			return olcumListesi;
		}
	}
}
