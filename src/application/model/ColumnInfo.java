package application.model;

import application.generator.Generator;
import application.generator.StringGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ColumnInfo {

    private final StringProperty tableName;
    private final StringProperty columnName;
    private final StringProperty columnType;
    private final StringProperty columnSize;

    public Generator generator = null;

    public ColumnInfo() {
        tableName = new SimpleStringProperty();
        columnName = new SimpleStringProperty();
        columnType = new SimpleStringProperty();
        columnSize = new SimpleStringProperty();
        generator = new StringGenerator();
    }

    public ColumnInfo(String tableName, String columnName, String columnType, String columnSize) {
        this.tableName = new SimpleStringProperty(tableName);
        this.columnName = new SimpleStringProperty(columnName);
        this.columnType = new SimpleStringProperty(columnType);
        this.columnSize = new SimpleStringProperty(columnSize);
        generator = new StringGenerator();

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

}
