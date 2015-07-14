package application;

import application.model.ColumnInfo;
import application.model.ConnectionInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Repository {

    private static Repository repository = null;
    private ConnectionInfo connectionInfo = null;
        private ObservableList<ColumnInfo> columnInfoList = FXCollections.observableArrayList();
    private Map<String, Collection<Object>> columnInfoMap = new HashMap<>();

    public static Repository getInstance(){

        if(repository == null){
            repository = new Repository();
        }
        return repository;
    }

    public ObservableList<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(ObservableList<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
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
