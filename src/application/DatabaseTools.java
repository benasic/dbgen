package application;

import application.generator.IntegerGenerator;
import application.generator.StringGenerator;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import application.model.helper.PrimaryKey;
import application.utils.Utils;
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
    private List<String> tableList = new ArrayList<>();

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

    public String testConnection() throws SQLException {
        OpenConnection();
        CloseConnection();
        return "Connection successful";
    }

    private void fillTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {

        ResultSet result = metadata.getTables(catalog, schemaPattern, tableNamePattern, types);
        tableList.clear();
        while (result.next()) {
            tableList.add(result.getString("TABLE_NAME"));
        }
    }

    public ObservableList<ColumnInfo> getColumnInfoObservableList(String catalog, String schemaPattern, String tableNamePattern, String[] types)
            throws SQLException {

        OpenConnection();
        ObservableList<ColumnInfo> columnInfoCollection = FXCollections.observableArrayList();

        metadata = DBConnection.getMetaData();
        fillTableList(catalog, schemaPattern, tableNamePattern, types);

        for (String tableName : tableList) {

            ResultSet resultPrimaryKeys = metadata.getPrimaryKeys(catalog, schemaPattern, tableName);
            Map<String, PrimaryKey> primaryKeyMap = new HashMap<>();
            while (resultPrimaryKeys.next()) {
                PrimaryKey primaryKey = new PrimaryKey();
                primaryKey.setName(resultPrimaryKeys.getString("PK_NAME"));
                primaryKey.setColumnName(resultPrimaryKeys.getString("COLUMN_NAME"));
                primaryKey.setTableName(resultPrimaryKeys.getString("TABLE_NAME"));
                primaryKey.setSequenceNumber(resultPrimaryKeys.getInt("KEY_SEQ"));
                primaryKeyMap.put(primaryKey.getHash(), primaryKey);
            }

            ResultSet resultForeignKeys = metadata.getImportedKeys(catalog, schemaPattern, tableName);
            Map<String, ForeignKey> foreignKeyMap = new HashMap<>();
            while( resultForeignKeys.next()){
                ForeignKey foreignKey = new ForeignKey();
                foreignKey.setName(resultForeignKeys.getString("FK_NAME"));
                foreignKey.setColumnName(resultForeignKeys.getString("FKCOLUMN_NAME"));
                foreignKey.setTableName(resultForeignKeys.getString("FKTABLE_NAME"));
                foreignKey.setSequenceNumber(resultForeignKeys.getInt("KEY_SEQ"));

                String primaryKeyHash = Utils.generateSHA256String(resultForeignKeys.getString("PKTABLE_NAME") + resultForeignKeys.getString("PKTABLE_NAME"));
                foreignKey.setPrimaryKey(primaryKeyMap.get(primaryKeyHash));

                foreignKeyMap.put(foreignKey.getHash(), foreignKey);
            }

            ResultSet resultColumns = metadata.getColumns(catalog, schemaPattern, tableName, null);
            while (resultColumns.next()) {
                ColumnInfo columnInfo = new ColumnInfo();
                // set table name
                columnInfo.setTableName(tableName);
                columnInfo.setColumnName(resultColumns.getString("COLUMN_NAME"));
                columnInfo.setColumnType(jdbcTypeNames.get(Integer.parseInt(resultColumns.getString("DATA_TYPE"))));
                columnInfo.setColumnSize(resultColumns.getString("COLUMN_SIZE"));
                columnInfo.setSqlType(Integer.parseInt(resultColumns.getString("DATA_TYPE")));
                System.out.println(resultColumns.getString("DATA_TYPE") + " " + resultColumns.getString("TYPE_NAME"));

                // JDBC implementation dependable
                columnInfo.setDatabaseType(resultColumns.getString("TYPE_NAME"));

                // if column is nullable
                columnInfo.setNullable(Integer.parseInt(resultColumns.getString("NULLABLE")) == 1);

                // if column is auto incremented
                // this field can return empty string which means that values can not be determined
                // TODO that can cause potentials problem with some database
                columnInfo.setAutoIncrement(resultColumns.getString("IS_AUTOINCREMENT").equals("YES"));

                columnInfo.setOrdinalPosition(Integer.parseInt(resultColumns.getString("ORDINAL_POSITION")));

                System.out.println(resultColumns.getString("IS_AUTOINCREMENT") + " " + resultColumns.getString("COLUMN_NAME") + " " + resultColumns.getString("ORDINAL_POSITION"));

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
