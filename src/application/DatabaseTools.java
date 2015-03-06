package application;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTools {
	private Connection DBConnection;
	private String connectionString;
	private DatabaseMetaData metadata;

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
	
	public void retrieveMetadata() throws SQLException{
		ConnectToDB();
		metadata = DBConnection.getMetaData();
		int    majorVersion   = metadata.getDatabaseMajorVersion();
		int    minorVersion   = metadata.getDatabaseMinorVersion();

		String productName    = metadata.getDatabaseProductName();
		String productVersion = metadata.getDatabaseProductVersion();
		System.out.println(majorVersion + " " + minorVersion + " " + productName + " " + productVersion);
		
		String   catalog          = null;
		String   schemaPattern    = null;
		String   tableNamePattern = null;
		String[] types            = new String[1];
		types[0] = "TABLE";

		ResultSet result = metadata.getTables(
		    catalog, schemaPattern, tableNamePattern, types );

		while(result.next()) {
		    System.out.println(result.getString(3));
		}
		
		try {
			DBConnection.close();
			System.out.println("Connection closed");
		} catch (NullPointerException e) {
			System.err.println(e.getMessage());
		}
		
	}

}
