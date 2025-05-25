package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BelirtiDAO {

	private HastaDAO hastaDAO = new HastaDAO();
	
	public void hastaBelirtiEkle(String hasta_tc, Belirti belirti) {
		String sql = "INSERT INTO belirti (hasta_tc, belirti_adi) VALUES (?, ?)";
		
		try (Connection conn = VTBaglantisi.connect();
			 PreparedStatement ps = conn.prepareStatement(sql)) {
        	conn.setAutoCommit(false);
        	
        	ps.setString(1, hasta_tc);
        	ps.setString(2, belirti.getBelirti_adi());
        	
        	ps.executeUpdate();
        	
        	conn.commit();
        	
        	System.out.println("Belirti başarıyla eklendi");
        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	
	public List<Belirti> getHastaBelirtiler(String hasta_tc) {
		List<Belirti> belirti = new ArrayList<>();
		
		String sql = "SELECT b.* FROM belirti b WHERE hasta_tc = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
				
			ps.setString(1, hasta_tc);
				
			ResultSet rs = ps.executeQuery();
				
			while(rs.next()) {
				belirti.add(new Belirti(hastaDAO.getHasta(hasta_tc), rs.getString("belirti_adi")));
			}
				
			return belirti;
				
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return belirti;
	}
}
