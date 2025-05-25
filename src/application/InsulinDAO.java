package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InsulinDAO {
	
	public void insulinEkle(Insulin insulin) {
		
		String sql = "INSERT INTO insulin (hasta_tc, tarih, miktar) VALUES (?, ?, ?)";
		
		try (Connection conn = VTBaglantisi.connect();
				PreparedStatement ps = conn.prepareStatement(sql))	{
		    conn.setAutoCommit(false);
		    	
		    ps.setString(1, insulin.getHastaTC());
		    ps.setDate(2, insulin.getTarih());
		    ps.setDouble(3, insulin.getMiktar());
		    	
		    ps.executeUpdate();
		    	
		    conn.commit();
		    	
		    System.out.println("Insulin başarıyla eklendi");
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	}
	
	public List<Insulin> getInsulinler(String hasta_tc){
		List<Insulin> insulinList = new ArrayList<>();
		
		String sql = "SELECT i.* FROM insulin i WHERE i.hasta_tc = ?";
		
		try(Connection conn = VTBaglantisi.connect();
			PreparedStatement ps = conn.prepareStatement(sql);	){
			
			ps.setString(1, hasta_tc);
			
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				Insulin insulin = new Insulin(hasta_tc, rs.getDate("tarih"), 
						rs.getDouble("miktar"));
				insulinList.add(insulin);
			}
			
			return insulinList;
		} catch (SQLException e) {
			e.printStackTrace();
			return insulinList;
		}
	}
}
