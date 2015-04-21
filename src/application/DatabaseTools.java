package application;

import application.generator.IntegerGenerator;
import application.generator.StringGenerator;
import application.model.ColumnInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class DatabaseTools {
    private Connection DBConnection;
    private String connectionString;
    private DatabaseMetaData metadata;
    private Map<Integer, String> jdbcTypeNames = new HashMap<>();

    public DatabaseTools(String connectionString) {
        this.connectionString = connectionString;
        getAllJdbcTypeNames();
    }

    private void OpenConnection() throws SQLException {
            DBConnection = DriverManager.getConnection(connectionString);
    }

    private void CloseConnection() throws SQLException{
            DBConnection.close();
            System.out.println("Connection closed");
    }

    private void getAllJdbcTypeNames() {
        for (Field field : Types.class.getFields()) {
            try {
                jdbcTypeNames.put((Integer) field.get(null), field.getName());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String TestConnection() throws SQLException {
        OpenConnection();
        CloseConnection();
        return "Connection successful";
    }

    public void retrieveMetadata() throws SQLException {
        OpenConnection();

        metadata = DBConnection.getMetaData();
        String productName = metadata.getDatabaseProductName();
        String productVersion = metadata.getDatabaseProductVersion();
        System.out.println(productName + " " + productVersion);

        CloseConnection();
    }

    public ObservableList<ColumnInfo> GetTables(String catalog, String schemaPattern, String tableNamePattern, String[] types)
            throws SQLException {
        OpenConnection();

        List<String> tableNames = new ArrayList<>();
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
                columnInfo.setSqlType(Integer.parseInt(result.getString("DATA_TYPE")));
                switch (columnInfo.getColumnType()){
                    case "VARCHAR":
                        columnInfo.setGenerator(new StringGenerator());
                        break;
                    case "INTEGER":
                        columnInfo.setGenerator(new IntegerGenerator());
                }

                columnInfoCollection.add(columnInfo);
            }
        }

        CloseConnection();

        return columnInfoCollection;
    }

    public void generateData(ObservableList<ObservableList<Object>> observableLists, List<ColumnInfo> columnInfos) throws SQLException {
        if (!columnInfos.isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("insert into ");
            stringBuilder.append(columnInfos.get(0).getTableName());
            stringBuilder.append(" (");

            Iterator<ColumnInfo> columnInfoIterator = columnInfos.listIterator();
            while (columnInfoIterator.hasNext()){
                stringBuilder.append(columnInfoIterator.next().getColumnName());
                if(columnInfoIterator.hasNext()){
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(") values (");
            columnInfoIterator = columnInfos.listIterator();
            while (columnInfoIterator.hasNext()){
                columnInfoIterator.next();
                stringBuilder.append("?");
                if(columnInfoIterator.hasNext()){
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(")");
            String sql = stringBuilder.toString();
            OpenConnection();
            final int batchSize = 1000;
            int count = 0;

            PreparedStatement ps = DBConnection.prepareStatement(sql);
            for(ObservableList<Object> observableList : observableLists){
                for(int i = 0; i < columnInfos.size(); i++){
                    ps.setObject(i + 1, observableList.get(i),columnInfos.get(i).getSqlType());
                }
                ps.addBatch();
                if(++count % batchSize == 0) {
                    ps.executeBatch();
                }
            }
            ps.executeBatch();
            ps.close();
            CloseConnection();
        }
    }

}
