package application;

import java.util.Properties;

public class ConnectionString {

	private StringBuilder connectionStringBuilder = new StringBuilder();
	private Properties properties = new Properties();

	public ConnectionString(String jdbcName, String host, int port, String database) {
		connectionStringBuilder.append("jdbc:" + jdbcName + "://" + host + ":" + port + "/" + database + "?");
	}

	public void addCredential(String username, String password) {
		properties.setProperty("user", username);
		properties.setProperty("password", password);
	}

	@Override
	public String toString() {
		String propertyString = properties.toString().replaceAll("[{]|[}]", "");
		propertyString = propertyString.replaceAll(", ", "&");
		return connectionStringBuilder.toString() + propertyString;
	}
}