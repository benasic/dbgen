package application;

import application.model.ConnectionInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JDBC_Repository {

    private static JDBC_Repository repository = null;
    private ConnectionInfo connectionInfo = null;
    private Map<String, Collection<Object>> columnInfoMap = new HashMap<>();

    public static JDBC_Repository getInstance(){

        if(repository == null){
            repository = new JDBC_Repository();
        }
        return repository;
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo){
        this.connectionInfo = connectionInfo;
    }

    public ConnectionInfo getConnectionInfo(){
        return connectionInfo;
    }

    public void addCollectionToMap(String hash, Collection<Object> collection){
        columnInfoMap.put(hash, collection);
    }

    public void insertIntoCollection(String hash, Object object){
        columnInfoMap.get(hash).add(object);
    }

    public Iterator<Object> returnCollectionIterator(String hash){
        return columnInfoMap.get(hash).iterator();
    }

    public Collection<Object> getCollection(String hash){
        return columnInfoMap.get(hash);
    }
}
