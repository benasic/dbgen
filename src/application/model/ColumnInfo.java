package application.model;

import application.generator.Generator;
import application.utils.Utils;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;

public class ColumnInfo {

    private final StringProperty tableName;
    private final StringProperty columnName;
    private final StringProperty columnType;
    private final StringProperty columnSize;
    private final IntegerProperty sqlType;

    // helper for determining root element
    private BooleanProperty isRoot;

    private StringProperty databaseType;
    private BooleanProperty nullable;
    private BooleanProperty autoIncrement;
    private IntegerProperty ordinalPosition;
    private StringProperty columnDefaultValue;
    private BooleanProperty isPrimaryKey;
    private BooleanProperty isCompositePrimaryKey;
    private BooleanProperty isForeignKey;
    private BooleanProperty isCompositeForeignKey;
    private BooleanProperty isUniqueKey;
    private BooleanProperty isCompositeUniqueKey;

    private final ObservableSet<String> compositePrimaryKeySet = FXCollections.observableSet();
    private final ObservableSet<String> compositeForeignKeySet = FXCollections.observableSet();
    private final ObservableSet<String> compositeUniqueKeySet = FXCollections.observableSet();

    private String hash;
    private Generator generator;

    public ColumnInfo() {
        tableName = new SimpleStringProperty();
        columnName = new SimpleStringProperty();
        columnType = new SimpleStringProperty();
        columnSize = new SimpleStringProperty();
        sqlType = new SimpleIntegerProperty();
        setIsRoot(false);
    }

    public ColumnInfo(String tableName, boolean root) {
        this.tableName = new SimpleStringProperty(tableName);
        this.columnName = new SimpleStringProperty();
        this.columnType = new SimpleStringProperty();
        this.columnSize = new SimpleStringProperty();
        this.sqlType = new SimpleIntegerProperty();
        setIsRoot(root);
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

    public IntegerProperty getSqlTypeProperty(){
        return sqlType;
    }

    public void setSqlType(int sqlType){
        this.sqlType.set(sqlType);
    }

    // Root Flag

    public boolean getIsRoot() {
        return isRoot.get();
    }

    public BooleanProperty isRootProperty() {
        return isRoot;
    }

    public void setIsRoot(boolean isRoot) {
        if(this.isRoot == null){
            this.isRoot = new ReadOnlyBooleanWrapper(isRoot);
        }
    }

    // Database Type

    public String getDatabaseType() {
        return databaseType.get();
    }

    public StringProperty databaseTypeProperty() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        if(this.databaseType == null){
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
        if(this.nullable == null){
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
        if(this.autoIncrement == null){
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
        if(this.ordinalPosition == null){
            this.ordinalPosition = new ReadOnlyIntegerWrapper(ordinalPosition);
        }
    }

    // Column Default Value

    public String getColumnDefaultValue() {
        return columnDefaultValue.get();
    }

    public StringProperty columnDefaultValueProperty() {
        return columnDefaultValue;
    }

    public void setColumnDefaultValue(String columnDefaultValue) {
        if(this.columnDefaultValue == null){
            this.columnDefaultValue = new ReadOnlyStringWrapper(columnDefaultValue);
        }
    }

    // Is Primary Key

    public boolean getIsPrimaryKey() {
        return isPrimaryKey.get();
    }

    public BooleanProperty isPrimaryKeyProperty() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        if(this.isPrimaryKey == null){
            this.isPrimaryKey = new ReadOnlyBooleanWrapper(isPrimaryKey);
        }
    }

    // Is multiple Column Primary Key

    public boolean getIsCompositePrimaryKey() {
        return isCompositePrimaryKey.get();
    }

    public BooleanProperty isCompositePrimaryKeyProperty() {
        return isCompositePrimaryKey;
    }

    public void setIsCompositePrimaryKey(boolean isCompositePrimaryKey) {
        if(this.isCompositePrimaryKey == null){
            this.isCompositePrimaryKey = new ReadOnlyBooleanWrapper(isCompositePrimaryKey);
        }
    }

    // Is Foreign Key

    public boolean getIsForeignKey() {
        return isForeignKey.get();
    }

    public BooleanProperty isForeignKeyProperty() {
        return isForeignKey;
    }

    public void setIsForeignKey(boolean isForeignKey) {
        if(this.isForeignKey == null){
            this.isForeignKey = new ReadOnlyBooleanWrapper(isForeignKey);
        }
    }

    // Is Composite Foreign Key

    public boolean getIsCompositeForeignKey() {
        return isCompositeForeignKey.get();
    }

    public BooleanProperty isCompositeForeignKeyProperty() {
        return isCompositeForeignKey;
    }

    public void setIsCompositeForeignKey(boolean isCompositeForeignKey) {
        if(this.isCompositeForeignKey == null){
            this.isCompositeForeignKey = new ReadOnlyBooleanWrapper(isCompositeForeignKey);
        }
    }

    // Is Unique Key

    public boolean getIsUniqueKey() {
        return isUniqueKey.get();
    }

    public BooleanProperty isUniqueKeyProperty() {
        return isUniqueKey;
    }

    public void setIsUniqueKey(boolean isUniqueKey) {
        if(this.isUniqueKey == null){
            this.isUniqueKey = new ReadOnlyBooleanWrapper(isUniqueKey);
        }
    }

    // Is Composite Unique Key

    public boolean getIsCompositeUniqueKey() {
        return isCompositeUniqueKey.get();
    }

    public BooleanProperty isCompositeUniqueKeyProperty() {
        return isCompositeUniqueKey;
    }

    public void setIsCompositeUniqueKey(boolean isCompositeUniqueKey) {
        if(this.isCompositeUniqueKey == null){
            this.isCompositeUniqueKey = new ReadOnlyBooleanWrapper(isCompositeUniqueKey);
        }
    }

    // Composite Sets Part

    //Primary key Composite Set

    public ObservableSet<String> getCompositePrimaryKeySet() {
        return compositePrimaryKeySet;
    }

    public void setCompositePrimaryKeySet(Set<String> hashSet){
        compositePrimaryKeySet.clear();
        compositePrimaryKeySet.addAll(hashSet);
    }

    // Foreign Key Composite Set

    public ObservableSet<String> getCompositeForeignKeySet() {
        return compositeForeignKeySet;
    }

    public void setCompositeForeignKeySet(Set<String> hashSet){
        compositeForeignKeySet.clear();
        compositeForeignKeySet.addAll(hashSet);
    }

    // Unique Key Composite Set

    public ObservableSet<String> getCompositeUniqueKeySet() {
        return compositeUniqueKeySet;
    }

    public void setCompositeUniqueKeySet(Set<String> hashSet){
        compositeUniqueKeySet.clear();
        compositeUniqueKeySet.addAll(hashSet);
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
