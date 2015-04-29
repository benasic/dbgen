package application.model;

import application.utils.Utils;
import application.generator.Generator;
import javafx.beans.property.*;

public class ColumnInfo {

    private final StringProperty tableName;
    private final StringProperty columnName;
    private final StringProperty columnType;
    private final StringProperty columnSize;
    private final IntegerProperty sqlType;

    private StringProperty databaseType;
    private BooleanProperty nullable;
    private BooleanProperty autoIncrement;
    private IntegerProperty ordinalPosition;

    private String hash;
    private Generator generator;

    public ColumnInfo() {
        tableName = new SimpleStringProperty();
        columnName = new SimpleStringProperty();
        columnType = new SimpleStringProperty();
        columnSize = new SimpleStringProperty();
        sqlType = new SimpleIntegerProperty();
    }

    public ColumnInfo(String tableName, String columnName, String columnType, String columnSize) {
        this.tableName = new SimpleStringProperty(tableName);
        this.columnName = new SimpleStringProperty(columnName);
        this.columnType = new SimpleStringProperty(columnType);
        this.columnSize = new SimpleStringProperty(columnSize);
        this.sqlType = new SimpleIntegerProperty();
    }

    public Generator getGenerator() {
        return generator;
    }

    public void setGenerator(Generator generator) {
        this.generator = generator;
    }

    // Table Name
    public String getTableName() {
        return tableName.get();
    }

    public void setTableName(String tableName) {
        this.tableName.set(tableName);
    }

    public StringProperty getTableNameProperty() {
        return tableName;
    }

    // Column Name
    public String getColumnName() {
        return columnName.get();
    }

    public void setColumnName(String columnName) {
        this.columnName.set(columnName);
    }

    public StringProperty getColumnNameProperty() {
        return columnName;
    }

    // Column Type
    public String getColumnType() {
        return columnType.get();
    }

    public void setColumnType(String columnType) {
        this.columnType.set(columnType);
    }

    public StringProperty getColumnTypeProperty() {
        return columnType;
    }

    // Column Size
    public String getColumnSize() {
        return columnSize.get();
    }

    public void setColumnSize(String columnSize) {
        this.columnSize.set(columnSize);
    }

    public StringProperty getColumnSizeProperty() {
        return columnSize;
    }

    // SQL Type

    public Integer getSqlType(){
        return  sqlType.get();
    }

    public void setSqlType(int sqlType){
        this.sqlType.set(sqlType);
    }

    // Database Type

    public String getDatabaseType() {
        return databaseType.get();
    }

    public StringProperty databaseTypeProperty() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        if(this.databaseType != null){
            this.databaseType = new ReadOnlyStringWrapper(databaseType);
        }
    }

    // Nullable

    public boolean getNullable() {
        return nullable.get();
    }

    public BooleanProperty nullableProperty() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        if(this.nullable != null){
            this.nullable = new ReadOnlyBooleanWrapper(nullable);
        }
    }

    // AutoIncrement

    public boolean getAutoIncrement() {
        return autoIncrement.get();
    }

    public BooleanProperty autoIncrementProperty() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        if(this.autoIncrement != null){
            this.autoIncrement = new ReadOnlyBooleanWrapper(autoIncrement);
        }
    }

    // Ordinar Position

    public int getOrdinalPosition() {
        return ordinalPosition.get();
    }

    public IntegerProperty ordinalPositionProperty() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        if(this.ordinalPosition != null){
            this.ordinalPosition = new ReadOnlyIntegerWrapper(ordinalPosition);
        }
    }

    // Hash

    public String getHash(){
        if(hash == null){
            generateHash();
        }
        return hash;
    }

    private void generateHash(){
        hash = Utils.generateSHA256String(getTableName() + getColumnName());
    }

}
