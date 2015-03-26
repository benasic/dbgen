package application;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import application.model.ColumnInfo;

public class DatabaseTools {
    private Connection DBConnection;
    private String connectionString;
    private DatabaseMetaData metadata;
    private Map<Integer, String> jdbcTypeNames = new HashMap<Integer, String>();

    public DatabaseTools(String connectionString) {
        this.connectionString = connectionString;
        getAllJdbcTypeNames();
    }
    
    private void OpenConenction() throws SQLException {
            DBConnection = DriverManager.getConnection(connectionString);
    }
    
    private void CloseConnection() throws SQLException{
            DBConnection.close();
            System.out.println("Connection closed");
    }
    
    private void getAllJdbcTypeNames() {
        for (Field field : Types.class.getFields()) {
            try {
                jdbcTypeNames.put((Integer)field.get(null), field.getName());
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String TestConnection() throws SQLException {
        OpenConenction();
        CloseConnection();
        return "Connection sucessfull";
    }

    public void retrieveMetadata() throws SQLException {
        OpenConenction();
        
        metadata = DBConnection.getMetaData();
        String productName = metadata.getDatabaseProductName();
        String productVersion = metadata.getDatabaseProductVersion();
        System.out.println(productName + " " + productVersion);

        CloseConnection();
    }

    public ObservableList<ColumnInfo> GetTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
            throws SQLException {
        OpenConenction();
        
        List<String> tableNames = new ArrayList<String>();
        ObservableList<ColumnInfo> columnInfoCollection = FXCollections.observableArrayList();
        
        metadata = DBConnection.getMetaData();
        ResultSet result = metadata.getTables(catalog, schemaPattern, tableNamePattern, types);
        
        while (result.next()) {
            tableNames.add(result.getString("TABLE_NAME"));
        }
        
        for (String tableName : tableNames) {
            result = metadata.getColumns(catalog, schemaPattern, tableName, null);
            while (result.next()) {
                ColumnInfo columnInfo = new ColumnInfo();
                columnInfo.setTableName(tableName);
                columnInfo.setColumnName(result.getString("COLUMN_NAME"));
                columnInfo.setColumnType(jdbcTypeNames.get(Integer.parseInt(result.getString("DATA_TYPE"))));
                columnInfo.setColumnSize(result.getString("COLUMN_SIZE"));
                columnInfoCollection.add(columnInfo);
            }
        }
        
        CloseConnection();
        
        return columnInfoCollection;
    }

}
