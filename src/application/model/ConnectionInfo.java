package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ConnectionInfo {
	
	private final StringProperty connectionName;
	private final StringProperty host;
	private final StringProperty port;
	private final StringProperty database;
	
	private String JDBCName;
	private String connectionString;
	
	private final ObservableList<ConnectionParametars> parametars;
	
	public ConnectionInfo(String connectionName, String JDBCName, String host, String port, String database){
		this.connectionName = new SimpleStringProperty(connectionName);
		this.JDBCName = JDBCName;
		this.host = new SimpleStringProperty(host);
		this.port = new SimpleStringProperty(port);
		this.database = new SimpleStringProperty(database);
		parametars = FXCollections.observableArrayList();
	}
	
	//Conenction name
	public String getConnectionName(){
		return connectionName.get();
	}
	
	public void setConnectionName(String connectionName){
		this.connectionName.set(connectionName);
	}
	
	public StringProperty getConnectionNameProperty(){
		return connectionName;
	}
	
	//Host
	public String getHost(){
		return host.get();
	}
	
	public void setHost(String host){
		this.host.set(host);
	}
	
	public StringProperty getHostProperty(){
		return host;
	}
	
	//Port
	public String getPort(){
		return port.get();
	}
	
	public void setPort(String port){
		this.port.set(port);
	}
	
	public StringProperty getPortProperty(){
		return port;
	}
	
	//Database
	public String getDatabase(){
		return database.get();
	}
	
	public void setDatabase(String database){
		this.database.set(database);
	}
	
	public StringProperty getDatabaseProperty(){
		return database;
	}
	
	//Parametars
	public ObservableList<ConnectionParametars> getParametars(){
		return parametars;
	}
	
	public void addParametar(String key, String value){
		parametars.add(new ConnectionParametars(key, value));
	}
	
	//Connection string
	private void generateConnectionString(){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("jdbc:" + JDBCName + "://" + host.get() + ":" + port.get() + "/" + database.get());
		if(parametars.size() != 0){
			stringBuilder.append("?");
			for(ConnectionParametars parametar : parametars){
				stringBuilder.append(parametar.getKey() + "=" + parametar.getValue());
				if(parametars.indexOf(parametar) != parametars.size() - 1){
					stringBuilder.append("&");
				}
			}
		}
		connectionString = stringBuilder.toString();
	}
	
	public String getConnectionString(){
		generateConnectionString();
		return connectionString;
	}
	
	@Override
	public String toString() {
		return connectionName.get();
	}
	
	
}
