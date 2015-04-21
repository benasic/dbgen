package application.model;

import application.Utils;
import application.generator.Generator;
import javafx.beans.property.*;

public class ColumnInfo {

    private final StringProperty tableName;
    private final StringProperty columnName;
    private final StringProperty columnType;
    private final StringProperty columnSize;
    private final IntegerProperty sqlType;


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
