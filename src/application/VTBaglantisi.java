package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class VTBaglantisi {
	private static final String URL = "jdbc:postgresql://localhost:5432/diyabet_takip?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
	private static final String USER = "postgres";
	private static final String PASSWORD = "Makifhidir61*";
	
	public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
