package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseTools {
	private Connection DBConnection;
	private String connectionString;

	public DatabaseTools(String connectionString) {
		this.connectionString = connectionString;
	}

	private void ConnectToDB() throws SQLException {
		DBConnection = DriverManager.getConnection(connectionString);
	}

	public String TestConnection() throws SQLException {

		ConnectToDB();
		try {
			DBConnection.close();
			System.out.println("Connection closed");
		} catch (NullPointerException e) {
			System.err.println(e.getMessage());
		}
		return "Connection sucessfull";
	}

}
