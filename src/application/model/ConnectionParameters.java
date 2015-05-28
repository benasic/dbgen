package application.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConnectionParameters {

	private StringProperty key;
	private StringProperty value;

	public ConnectionParameters(){
        this.key = new SimpleStringProperty();
        this.value = new SimpleStringProperty();
    }

	public ConnectionParameters(String key, String value){
		this.key = new SimpleStringProperty(key);
		this.value = new SimpleStringProperty(value);
	}

	public String getKey(){
		return key.get();
	}

	public void setKey(String key){
		this.key.set(key);
	}

	public StringProperty keyProperty(){
		return key;
	}

	public String getValue(){
		return value.get();
	}

	public void setValue(String value){
		this.value.set(value);
	}

	public StringProperty valueProperty(){
		return value;
	}
}
