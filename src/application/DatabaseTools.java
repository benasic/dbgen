package application;

import application.generator.IntegerGenerator;
import application.generator.StringGenerator;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import application.model.helper.PrimaryKey;
import application.model.helper.UniqueKey;
import application.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseTools {
    private Connection DBConnection;
    private String connectionString;
    private DatabaseMetaData metadata;
    private static final Map<Integer, String> jdbcTypeNames = new HashMap<>();
    private static final List<String> tableList = new ArrayList<>();
    private static final Map<String, PrimaryKey> primaryKeyMap = new LinkedHashMap<>();
    private static final Map<String, ForeignKey> foreignKeyMap = new LinkedHashMap<>();
    private static final Map<String, UniqueKey> uniqueKeyMap = new LinkedHashMap<>();

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
        if(!jdbcTypeNames.isEmpty()){
            return;
        }
        for (Field field : Types.class.getFields()) {
            try {
                jdbcTypeNames.put((Integer) field.get(null), field.getName());
            } catch (IllegalArgumentException | IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void fillTableList(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {

        tableList.clear();

        ResultSet result = metadata.getTables(catalog, schemaPattern, tableNamePattern, types);
        while (result.next()) {
            tableList.add(result.getString("TABLE_NAME"));
        }
    }

    private Set<String> getPrimaryKeyHashSet(String tableName){
        return primaryKeyMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(entry1 -> entry1.getTableName().equals(tableName))
                .map(PrimaryKey::getHash)
                .collect(Collectors.toSet());
    }

    private Set<String> getForeignKeyHashSet(String tableName){
        return foreignKeyMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(entry1 -> entry1.getTableName().equals(tableName))
                .map(ForeignKey::getHash)
                .collect(Collectors.toSet());
    }

    private Set<String> getUniqueKeyHashSet(String tableName){
        return uniqueKeyMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(entry1 -> entry1.getTableName().equals(tableName))
                .map(UniqueKey::getHash)
                .collect(Collectors.toSet());
    }

    private void fetchPrimaryKeys(String catalog, String schema) throws SQLException {

        primaryKeyMap.clear();
        for(String table : tableList){
            ResultSet resultSet = metadata.getPrimaryKeys(catalog, schema, table);
            while (resultSet.next()) {
                PrimaryKey primaryKey = new PrimaryKey();
                primaryKey.setName(resultSet.getString("PK_NAME"));
                primaryKey.setColumnName(resultSet.getString("COLUMN_NAME"));
                primaryKey.setTableName(resultSet.getString("TABLE_NAME"));
                primaryKey.setSequenceNumber(resultSet.getInt("KEY_SEQ"));
                primaryKeyMap.put(primaryKey.getHash(), primaryKey);
            }
        }
    }

    private void fetchForeignKeys(String catalog, String schema) throws SQLException{

        foreignKeyMap.clear();
        for (String table : tableList){
            ResultSet resultForeignKeys = metadata.getImportedKeys(catalog, schema, table);
            while( resultForeignKeys.next()) {
                ForeignKey foreignKey = new ForeignKey();
                foreignKey.setName(resultForeignKeys.getString("FK_NAME"));
                foreignKey.setColumnName(resultForeignKeys.getString("FKCOLUMN_NAME"));
                foreignKey.setTableName(resultForeignKeys.getString("FKTABLE_NAME"));
                foreignKey.setSequenceNumber(resultForeignKeys.getInt("KEY_SEQ"));

                String primaryKeyHash = Utils.generateSHA256String(resultForeignKeys.getString("PKTABLE_NAME") + resultForeignKeys.getString("PKCOLUMN_NAME"));
                foreignKey.setPrimaryKey(primaryKeyMap.get(primaryKeyHash));

                foreignKeyMap.put(foreignKey.getHash(), foreignKey);
            }
        }
    }

    private void fetchUniqueKeys(String catalog, String schema, boolean unique, boolean approximate) throws SQLException {

        uniqueKeyMap.clear();
        for (String table : tableList){
            ResultSet resultUniqueKeys = metadata.getIndexInfo(catalog, schema, table, unique, approximate);
            while (resultUniqueKeys.next()){
                UniqueKey uniqueKey = new UniqueKey();
                uniqueKey.setName(resultUniqueKeys.getString("INDEX_NAME"));
                uniqueKey.setTableName(resultUniqueKeys.getString("TABLE_NAME"));
                uniqueKey.setColumnName(resultUniqueKeys.getString("COLUMN_NAME"));
                uniqueKey.setSequenceNumber(resultUniqueKeys.getInt("ORDINAL_POSITION"));
                uniqueKey.setCardinality(resultUniqueKeys.getLong("CARDINALITY"));
                uniqueKey.setType(resultUniqueKeys.getShort("TYPE"));
                uniqueKeyMap.put(uniqueKey.getHash(), uniqueKey);
            }
        }
    }

    public String testConnection() throws SQLException {
        OpenConnection();
        CloseConnection();
        return "Connection successful";
    }

    public ObservableList<ColumnInfo> getColumnInfoObservableList(String catalog, String schemaPattern, String tableNamePattern, String[] types)
            throws SQLException {

        OpenConnection();
        ObservableList<ColumnInfo> columnInfoCollection = FXCollections.observableArrayList();

        metadata = DBConnection.getMetaData();

        fillTableList(catalog, schemaPattern, tableNamePattern, types);
        fetchPrimaryKeys(catalog, schemaPattern);
        fetchForeignKeys(catalog, schemaPattern);
        fetchUniqueKeys(catalog, schemaPattern, true, true);

        for (String tableName : tableList) {

            Set<String> primaryKeyHashSet = getPrimaryKeyHashSet(tableName);
            boolean isMultipleColumnPrimaryKey = primaryKeyHashSet.size() > 1;

            ResultSet resultColumns = metadata.getColumns(catalog, schemaPattern, tableName, null);
            while (resultColumns.next()) {
                ColumnInfo columnInfo = new ColumnInfo();

                columnInfo.setTableName(tableName);
                columnInfo.setColumnName(resultColumns.getString("COLUMN_NAME"));
                columnInfo.setColumnType(jdbcTypeNames.get(resultColumns.getInt("DATA_TYPE")));
                columnInfo.setColumnSize(resultColumns.getString("COLUMN_SIZE"));
                columnInfo.setSqlType(resultColumns.getInt("DATA_TYPE"));

                // JDBC implementation dependable
                columnInfo.setDatabaseType(resultColumns.getString("TYPE_NAME"));

                // if column is nullable
                columnInfo.setNullable(resultColumns.getInt("NULLABLE") == 1);

                // if column is auto incremented
                // this field can return empty string which means that values can not be determined
                // TODO that can cause potentials problem with some database
                columnInfo.setAutoIncrement(resultColumns.getString("IS_AUTOINCREMENT").equals("YES"));

                columnInfo.setOrdinalPosition(resultColumns.getInt("ORDINAL_POSITION"));

                // primary key check
                columnInfo.setIsPrimaryKey(primaryKeyHashSet.contains(columnInfo.getHash()));
                columnInfo.setIsMultipleColumnPrimaryKey(isMultipleColumnPrimaryKey);


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
