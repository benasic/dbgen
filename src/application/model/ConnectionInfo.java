package application.model;

import application.JDBC_Constants;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ConnectionInfo {

    private StringProperty saveName;

	private StringProperty connectionName;
	private StringProperty host;
	private StringProperty port;
	private StringProperty database;

    private String JDBCName;
	private String connectionString;

	private ObservableList<ConnectionParameters> parameters = FXCollections.observableArrayList();

    public void setParameters(List<ConnectionParameters> parameters){
        this.parameters = FXCollections.observableArrayList(parameters);
    }

    public ConnectionInfo(){
        connectionName = new SimpleStringProperty();
        host = new SimpleStringProperty();
        port = new SimpleStringProperty();
        database = new SimpleStringProperty();
        saveName = new SimpleStringProperty();
    }

	public ConnectionInfo(String connectionName, String JDBCName, String host, String port, String database){
		this.connectionName = new SimpleStringProperty(connectionName);
		this.JDBCName = JDBCName;
		this.host = new SimpleStringProperty(host);
		this.port = new SimpleStringProperty(port);
		this.database = new SimpleStringProperty(database);
        this.saveName = new SimpleStringProperty("template");
	}

    // Save Name

    public String getSaveName() {
        return saveName.get();
    }

    public StringProperty saveNameProperty() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName.set(saveName);
    }

	//Conenction name
	public String getConnectionName(){
		return connectionName.get();
	}

	public void setConnectionName(String connectionName){
		this.connectionName.set(connectionName);
	}

	public StringProperty connectionNameProperty(){
		return connectionName;
	}

	//Host
	public String getHost(){
		return host.get();
	}

	public void setHost(String host){
		this.host.set(host);
	}

	public StringProperty hostProperty(){
		return host;
	}

	//Port
	public String getPort(){
		return port.get();
	}

	public void setPort(String port){
		this.port.set(port);
	}

	public StringProperty portProperty(){
		return port;
	}

	//Database
	public String getDatabase(){
		return database.get();
	}

	public void setDatabase(String database){
		this.database.set(database);
	}

	public StringProperty databaseProperty(){
		return database;
	}

	//Parametars
	public ObservableList<ConnectionParameters> getParameters(){
		return parameters;
	}

	public void addParametar(String key, String value){
		parameters.add(new ConnectionParameters(key, value));
	}

	//Connection string
	private void generateConnectionString(){
		if(JDBCName.equals(JDBC_Constants.Name.SQLSERVER)){
		    generateSQLServerConnectionString();
		} else {
		    generateReguralConnectionString();
		}

	}

	private void generateSQLServerConnectionString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jdbc:" + JDBCName + "://" + host.get() + ":" + port.get() + ";databaseName="
                + database.get());
        if (parameters.size() != 0) {
            stringBuilder.append(";");
            for (ConnectionParameters parametar : parameters) {
                stringBuilder.append(parametar.getKey() + "=" + parametar.getValue());
                if (parameters.indexOf(parametar) != parameters.size() - 1) {
                    stringBuilder.append(";");
                }
            }
        }
        connectionString = stringBuilder.toString();
	}

    private void generateReguralConnectionString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("jdbc:" + JDBCName + "://" + host.get() + ":" + port.get() + "/" + database.get());
        if (parameters.size() != 0) {
            stringBuilder.append("?");
            for (ConnectionParameters parametar : parameters) {
                stringBuilder.append(parametar.getKey() + "=" + parametar.getValue());
                if (parameters.indexOf(parametar) != parameters.size() - 1) {
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
		return connectionName.get() + " - " + saveName.get();
	}

    public String getJDBCName() {
        return JDBCName;
    }

    public void setJDBCName(String JDBCName) {
        this.JDBCName = JDBCName;
    }

}
