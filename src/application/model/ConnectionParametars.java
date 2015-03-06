package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectionParametars {
	
	private final StringProperty key;
	private final StringProperty value;
	
	public ConnectionParametars(String key, String value){
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
	}
	
	public String getKey(){
		return key.get();
	}
	
	public void setKey(String key){
		this.key.set(key);
	}
	
	public StringProperty getkeyProperty(){
		return key;
	}
	
	public String getValue(){
		return value.get();
	}
	
	public void setValue(String value){
		this.value.set(value);
	}
	
	public StringProperty getValueProperty(){
		return value;
	}
}
