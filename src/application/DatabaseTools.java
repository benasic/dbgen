package application;

import application.generator.*;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import application.model.helper.JDBC_Table;
import application.model.helper.PrimaryKey;
import application.model.helper.UniqueKey;
import application.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DatabaseTools {
    private Connection DBConnection;
    private String connectionString;
    private DatabaseMetaData metadata;
    private static final Map<Integer, String> jdbcTypeNames = new HashMap<>();
    private static final List<JDBC_Table> tableList = new ArrayList<>();
    public static final Map<String, PrimaryKey> primaryKeyMap = new LinkedHashMap<>();
    public static final Map<String, ForeignKey> foreignKeyMap = new LinkedHashMap<>();
    public static final Map<String, UniqueKey> uniqueKeyMap = new LinkedHashMap<>();

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

            String tableScheme = result.getString("TABLE_SCHEM");
            if(tableScheme != null && tableScheme.equals("sys")){
                continue;
            }
            String tableName = result.getString("TABLE_NAME");
            if(tableScheme != null && tableName.equals("sysdiagrams")){
                continue;
            }

            JDBC_Table jdbc_table = new JDBC_Table();
            jdbc_table.setName(tableName);
            jdbc_table.setSchema(tableScheme);
            tableList.add(jdbc_table);
            //System.out.println(result.getString("TABLE_NAME") + " " + result.getString("TABLE_CAT") + " " + result.getString("TABLE_SCHEM"));
        }
    }

    private Set<String> getPrimaryKeyHashSet(String tableName){
        return primaryKeyMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getTableName().equals(tableName))
                .map(PrimaryKey::getHash)
                .collect(Collectors.toSet());
    }

    private Set<String> getForeignKeyHashSet(String tableName){
        return foreignKeyMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getTableName().equals(tableName))
                .map(ForeignKey::getHash)
                .collect(Collectors.toSet());
    }

    private Set<String> getUniqueKeyHashSet(String tableName){
        return uniqueKeyMap.entrySet().stream()
                .map(Map.Entry::getValue)
                .filter(entry -> entry.getTableName().equals(tableName))
                .map(UniqueKey::getHash)
                .collect(Collectors.toSet());
    }

    private Set<String> getCompositePrimaryKeySet(Set<String> hashSet, String hash){
        Set<String> stringHashSet = new HashSet<>();
        // composite key requires more then one column
        if(hashSet.size() > 1 && hashSet.contains(hash)){
            stringHashSet.addAll(hashSet);
        }
        return stringHashSet;
    }

    // TODO napravit tablicu za testiranje najkompliciranijeg slucaja
    // TODO dva kompozitna strana kljuca i jedan obicni strani kljuc
    private Set<String> getCompositeForeignKeySet(Set<String> hashSet, String hash){
        Set<String> stringHashSet = new HashSet<>();
        // first check minimum size for composite key
        // second check is hash inside set
        if(hashSet.size() > 1 && hashSet.contains(hash)){
            //third check is there any key with sequence number > 1
            boolean candidateExist = false;
            Set<ForeignKey> candidateSet = new HashSet<>();
            for(String tempHash : hashSet){
                if(foreignKeyMap.get(tempHash).getSequenceNumber() > 1){
                    candidateExist = true;
                    candidateSet.add(foreignKeyMap.get(tempHash));
                    break;
                }
            }
            if(candidateExist){
                // fourth map candidates to correct map
                Map<String, Set<String>> stringSetMap = new HashMap<>();
                for(ForeignKey foreignKey : candidateSet){
                    String key = foreignKey.getPrimaryKey().getTableName();
                    // make set for mapping
                    if(!stringSetMap.containsKey(key)){
                        stringSetMap.put(key, new HashSet<>());
                    }
                    stringSetMap.get(key).add(foreignKey.getHash());
                }
                // fifth resolve candidate with sequence number one
                // only foreign key with primary key table name that exist in map can enter in set
                // set will eliminate duplicate
                for(String tempForeignKeyHash : hashSet){
                    ForeignKey foreignKey = foreignKeyMap.get(tempForeignKeyHash);
                    String tableNameKey = foreignKey.getPrimaryKey().getTableName();
                    if(stringSetMap.containsKey(tableNameKey)){
                        stringSetMap.get(tableNameKey).add(tempForeignKeyHash);
                    }
                }
                // sixth get correct set
                for(Set<String> finalForeignKeyHashSet : stringSetMap.values()){
                    if(finalForeignKeyHashSet.contains(hash)){
                        stringHashSet.addAll(finalForeignKeyHashSet);
                        break;
                    }
                }
            }
        }
        return stringHashSet;
    }

    // TODO generirati slucaj kao i za strane kljuceve
    // provjeriti kod
    private Set<String> getCompositeUniqueKeySet(Set<String> hashSet, String hash) {
        Set<String> stringHashSet = new HashSet<>();
        if(hashSet.size() > 1 && hashSet.contains(hash)){
            boolean candidateExist = false;
            Set<UniqueKey> candidateSet = new HashSet<>();
            for(String tempHash : hashSet){
                if(uniqueKeyMap.get(tempHash).getSequenceNumber() > 1){
                    candidateExist = true;
                    candidateSet.add(uniqueKeyMap.get(tempHash));
                    break;
                }
            }

            if(candidateExist){
                String keyName = uniqueKeyMap.get(hash).getName();
                // pick hashes that have same name as queried hash
                for(UniqueKey uniqueKey : candidateSet){
                    if(uniqueKey.getName().equals(keyName)){
                        stringHashSet.add(uniqueKey.getHash());
                    }
                }
                // is same key are added, add rest of key with same name
                if(!stringHashSet.isEmpty()){
                    for(String uniqueKeyHash : hashSet){
                        UniqueKey uniqueKey = uniqueKeyMap.get(uniqueKeyHash);
                        if(uniqueKey.getName() == null){
                            continue;
                        }
                        if(uniqueKey.getName().equals(keyName)){
                            stringHashSet.add(uniqueKey.getHash());
                        }
                    }
                }
            }
        }
        return stringHashSet;
    }

    private void fetchPrimaryKeys(String catalog, String schema) throws SQLException {

        primaryKeyMap.clear();
        for(JDBC_Table table : tableList){
            ResultSet resultSet = metadata.getPrimaryKeys(catalog, table.getSchema(), table.getName());
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
        for (JDBC_Table table : tableList){
            ResultSet resultForeignKeys = metadata.getImportedKeys(catalog, table.getSchema(), table.getName());
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
        for (JDBC_Table table : tableList){
            ResultSet resultUniqueKeys = metadata.getIndexInfo(catalog, table.getSchema(), table.getName(), unique, approximate);
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

    public Map<String, Integer> getRowCount() throws SQLException {
        Map<String, Integer> rowCountMap = new HashMap<>();
        OpenConnection();
        for(JDBC_Table table : tableList){
            Statement statement = DBConnection.createStatement();

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("SELECT COUNT(*) FROM ");
            if(Repository.getInstance().getConnectionInfo().getJDBCName().equals(JDBC_Constants.Name.SQLSERVER)){
                stringBuilder.append(table.getSchema() + "." + table.getName());
            }
            else{
                stringBuilder.append(table.getName());
            }

            ResultSet resultSet = statement.executeQuery(stringBuilder.toString());
            resultSet.next();

            if(Repository.getInstance().getConnectionInfo().getJDBCName().equals(JDBC_Constants.Name.SQLSERVER)){
                //rowCountMap.put(table.getSchema() + "." + table.getName(), resultSet.getInt(1));
                rowCountMap.put(table.getName(), resultSet.getInt(1));
            }
            else{
                rowCountMap.put(table.getName(), resultSet.getInt(1));
            }
        }

        CloseConnection();

        Comparator<Map.Entry<String, Integer>> byValue = (entry1, entry2) -> entry1.getValue().compareTo(
                entry2.getValue());

        // must be linked map for correct sort
        Map<String, Integer> result = new LinkedHashMap<>();
        Stream<Map.Entry<String, Integer>> st = rowCountMap.entrySet().stream();

        st.sorted(byValue)
                .forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
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

        for (JDBC_Table tableName : tableList) {

            Set<String> primaryKeyHashSet = getPrimaryKeyHashSet(tableName.getName());
            Set<String> foreignKeyHashSet = getForeignKeyHashSet(tableName.getName());
            Set<String> uniqueKeyHashSet = getUniqueKeyHashSet(tableName.getName());

            ResultSet resultColumns = metadata.getColumns(catalog, tableName.getSchema(), tableName.getName(), null);
            while (resultColumns.next()) {
                ColumnInfo columnInfo = new ColumnInfo();

                columnInfo.setTableName(tableName.getName());
                columnInfo.setColumnName(resultColumns.getString("COLUMN_NAME"));
                columnInfo.setColumnType(jdbcTypeNames.get(resultColumns.getInt("DATA_TYPE")));
                columnInfo.setColumnSize(resultColumns.getString("COLUMN_SIZE"));
                columnInfo.setSqlType(resultColumns.getInt("DATA_TYPE"));

                columnInfo.setSchema(tableName.getSchema());

                // JDBC implementation dependable
                columnInfo.setDatabaseType(resultColumns.getString("TYPE_NAME"));

                // MySQL blob fix
                if(resultColumns.getString("TYPE_NAME").equals("BLOB")){
                    columnInfo.setColumnType("BLOB");
                    columnInfo.setColumnSize("");
                    columnInfo.setSqlType(Types.BLOB);
                }

                // if column is nullable
                columnInfo.setNullable(resultColumns.getInt("NULLABLE") == 1);

                if(Repository.getInstance().getConnectionInfo().getJDBCName().equals(JDBC_Constants.Name.SQLSERVER)){
                    columnInfo.setAutogenerated(resultColumns.getInt("SS_IS_COMPUTED") == 1);
                }

                // if column is auto incremented
                // this field can return empty string which means that values can not be determined
                // TODO that can cause potentials problem with some database
                columnInfo.setAutoIncrement(resultColumns.getString("IS_AUTOINCREMENT").equals("YES"));

                columnInfo.setOrdinalPosition(resultColumns.getInt("ORDINAL_POSITION"));
                columnInfo.setColumnDefaultValue(resultColumns.getString("COLUMN_DEF"));

                // primary key check
                columnInfo.setIsPrimaryKey(primaryKeyHashSet.contains(columnInfo.getHash()));
                boolean isCompositePrimaryKey = !getCompositePrimaryKeySet(primaryKeyHashSet, columnInfo.getHash()).isEmpty();
                if(isCompositePrimaryKey){
                    columnInfo.setCompositePrimaryKeySet(getCompositePrimaryKeySet(primaryKeyHashSet, columnInfo.getHash()));
                }
                columnInfo.setIsCompositePrimaryKey(isCompositePrimaryKey);

                // foreign key check
                columnInfo.setIsForeignKey(foreignKeyHashSet.contains(columnInfo.getHash()));
                boolean isCompositeForeignKey = !getCompositeForeignKeySet(foreignKeyHashSet, columnInfo.getHash()).isEmpty();
                if(isCompositeForeignKey){
                    columnInfo.setCompositeForeignKeySet(getCompositeForeignKeySet(foreignKeyHashSet, columnInfo.getHash()));
                }
                columnInfo.setIsCompositeForeignKey(isCompositeForeignKey);

                // unique key check
                columnInfo.setIsUniqueKey(uniqueKeyHashSet.contains(columnInfo.getHash()));
                boolean isCompositeUniqueKey = !getCompositeUniqueKeySet(uniqueKeyHashSet, columnInfo.getHash()).isEmpty();
                if(isCompositeUniqueKey){
                    columnInfo.setCompositeUniqueKeySet(getCompositeUniqueKeySet(uniqueKeyHashSet, columnInfo.getHash()));
                }
                columnInfo.setIsCompositeUniqueKey(isCompositeUniqueKey);

                switch (columnInfo.getColumnType()){
                    case "VARCHAR":
                    case "NVARCHAR":
                    case "CHAR":
                    case "NCHAR":
                    case "LONGVARCHAR":
                    case "LONGNVARCHAR":
                        StringGenerator stringGenerator = new StringGenerator();
                        stringGenerator.setAppropriateGeneratorContext(columnInfo.getColumnName(), columnInfo.getColumnSize());
                        columnInfo.setGenerator(stringGenerator);
                        break;
                    case "INTEGER":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.INTEGER));
                        break;
                    case "SMALLINT":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.SMALLINT));
                        break;
                    case "TINYINT":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.TINYINT));
                        break;
                    case "BIGINT":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.BIGINT));
                        break;
                    case "REAL":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.REAL));
                        break;
                    case "FLOAT":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.FLOAT));
                        break;
                    case "DOUBLE":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.DOUBLE));
                        break;
                    case "DECIMAL":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.DECIMAL));
                        break;
                    case "NUMERIC":
                        columnInfo.setGenerator(new NumberGenerator(NumberType.NUMERIC));
                        break;
                    case "TIME":
                        columnInfo.setGenerator(new DateGenerator(DateType.TIME));
                        break;
                    case "DATE":
                        columnInfo.setGenerator(new DateGenerator(DateType.DATE));
                        break;
                    case "TIMESTAMP":
                        columnInfo.setGenerator(new DateGenerator(DateType.TIMESTAMP));
                        break;
                    case "BIT":
                        columnInfo.setGenerator(new BooleanGenerator());
                        break;
                    case "BINARY":
                    case "VARBINARY":
                    case "LONGVARBINARY":
                    case "BLOB":
                        columnInfo.setGenerator(new BinaryGenerator());
                        break;

                }

                columnInfoCollection.add(columnInfo);
            }
        }

        CloseConnection();

        return columnInfoCollection;
    }

    public void generateData(List<List<Object>> observableLists, List<ColumnInfo> columnInfos) throws SQLException {
        if (!columnInfos.isEmpty()) {
            Set<Integer> skippableColumns = new HashSet<>();

            for(ColumnInfo columnInfo : columnInfos){
                if(columnInfo.getAutoIncrement() || columnInfo.getAutogenerated()){
                    skippableColumns.add(columnInfo.getOrdinalPosition());
                }
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("insert into ");
            if(Repository.getInstance().getConnectionInfo().getJDBCName().equals(JDBC_Constants.Name.SQLSERVER)){
                stringBuilder.append(columnInfos.get(0).getSchema() + "." + columnInfos.get(0).getTableName());
            }
            else{
                stringBuilder.append(columnInfos.get(0).getTableName());
            }
            stringBuilder.append(" (");

            Iterator<ColumnInfo> columnInfoIterator = columnInfos.listIterator();
            while (columnInfoIterator.hasNext()){
                ColumnInfo columnInfo = columnInfoIterator.next();

                if(!skippableColumns.contains(columnInfo.getOrdinalPosition())) {
                    if(Repository.getInstance().getConnectionInfo().getJDBCName().equals(JDBC_Constants.Name.SQLSERVER)){
                        stringBuilder.append('"' + columnInfo.getColumnName() + '"');
                    }
                    else{
                        stringBuilder.append(columnInfo.getColumnName());
                    }
                    if (columnInfoIterator.hasNext()) {
                        stringBuilder.append(", ");
                    }
                }
            }
            stringBuilder.append(") values (");
            columnInfoIterator = columnInfos.listIterator();
            while (columnInfoIterator.hasNext()){
                ColumnInfo columnInfo = columnInfoIterator.next();

                if(!skippableColumns.contains(columnInfo.getOrdinalPosition())) {
                    stringBuilder.append("?");
                    if(columnInfoIterator.hasNext()){
                        stringBuilder.append(", ");
                    }
                }
            }
            stringBuilder.append(")");
            String sql = stringBuilder.toString();
            OpenConnection();
            final int batchSize = 10000;
            int count = 0;

            int regularSize = columnInfos.size();

            PreparedStatement ps = DBConnection.prepareStatement(sql);
            for(List<Object> observableList : observableLists){

                if(observableList.size() == regularSize) {
                    // j value is used as help for object that needs to be skipped
                    for (int i = 0, j = 1; i < columnInfos.size(); i++) {
                        if (!skippableColumns.contains(i + 1) && observableList.size() == regularSize) {
                            ps.setObject(j++, observableList.get(i), columnInfos.get(i).getSqlType());
                        }
                    }
                    ps.addBatch();
                }
                if(++count % batchSize == 0) {
                    ps.executeBatch();
                    ps.clearBatch();
                    System.out.println("execute batch (entry count): " + count);
                }
            }
            ps.executeBatch();
            ps.close();
            CloseConnection();
        }
    }

    public List<Object[]> fetchData(List<ColumnInfo> columnInfoList) throws SQLException {
        List<Object[]> fetchedDataList = new ArrayList<>();
        int inputSize = columnInfoList.size();
        if(!columnInfoList.isEmpty()){
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("select ");
            Iterator<ColumnInfo> columnInfoIterator = columnInfoList.listIterator();
            while (columnInfoIterator.hasNext()){
                ColumnInfo columnInfo = columnInfoIterator.next();
                stringBuilder.append(columnInfo.getColumnName());
                if (columnInfoIterator.hasNext()) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append(" from ");
            if(Repository.getInstance().getConnectionInfo().getJDBCName().equals(JDBC_Constants.Name.SQLSERVER)){
                stringBuilder.append(columnInfoList.get(0).getSchema() + "." +columnInfoList.get(0).getTableName());
            }
            else{
                stringBuilder.append(columnInfoList.get(0).getTableName());
            }
            String sql = stringBuilder.toString();

            OpenConnection();

            Statement statement = DBConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while ((resultSet.next())){
                Object[] objects = new Object[inputSize];
                for(int i = 1; i <= inputSize; i++){
                    objects[i-1] = resultSet.getObject(i);
                }
                fetchedDataList.add(objects);
            }

            CloseConnection();
        }
        return fetchedDataList;
    }
}
