package application;

import application.model.ConnectionInfo;

public class JDBC_Repository {

    private static JDBC_Repository repository = null;
    private ConnectionInfo connectionInfo = null;
    
    public static JDBC_Repository getInstance(){
        
        if(repository == null){
            repository = new JDBC_Repository();
        }
        return repository;
    }
    
    public void setconnectionInfo(ConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo;
    }
    
    public ConnectionInfo getconnectionInfo(){
        return connectionInfo;
    }
}
