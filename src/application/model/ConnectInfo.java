package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectInfo {
	
	private final StringProperty connectionName;
	private final StringProperty host;
	
	public ConnectInfo(String connectionName, String host){
		this.connectionName = new SimpleStringProperty(connectionName);
		this.host = new SimpleStringProperty(host);
	}
	
	public String getConnectionName(){
		return connectionName.get();
	}
	
	public void setConnectionName(String connectionName){
		this.connectionName.set(connectionName);
	}
	
	public StringProperty getConnectionNameProperty(){
		return connectionName;
	}
	
	public String getHost(){
		return host.get();
	}
	
	public void setHost(String host){
		this.host.set(host);
	}
	
	public StringProperty getHostProperty(){
		return host;
	}
	
	@Override
	public String toString() {
		return connectionName.get();
	}
	
	
}
