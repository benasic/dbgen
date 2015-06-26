package application.model;

import application.generator.Generator;
import application.generator.TableGenerationSettings;
import application.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

import java.util.Set;

public class ColumnInfo {

    private StringProperty tableName;
    private StringProperty columnName;
    private StringProperty columnType;
    private StringProperty columnSize;
    private IntegerProperty sqlType;

    private String schema;

    // helper for determining root element
    private BooleanProperty isRoot = new SimpleBooleanProperty();

    private StringProperty databaseType = new SimpleStringProperty();
    private BooleanProperty nullable = new SimpleBooleanProperty();
    private BooleanProperty autoIncrement = new SimpleBooleanProperty();
    private IntegerProperty ordinalPosition = new SimpleIntegerProperty();
    private StringProperty columnDefaultValue = new SimpleStringProperty();
    private BooleanProperty isPrimaryKey = new SimpleBooleanProperty();
    private BooleanProperty isCompositePrimaryKey = new SimpleBooleanProperty();
    private BooleanProperty isForeignKey = new SimpleBooleanProperty();
    private BooleanProperty isCompositeForeignKey = new SimpleBooleanProperty();
    private BooleanProperty isUniqueKey = new SimpleBooleanProperty();
    private BooleanProperty isCompositeUniqueKey = new SimpleBooleanProperty();

    private final ObservableSet<String> compositePrimaryKeySet = FXCollections.observableSet();
    private final ObservableSet<String> compositeForeignKeySet = FXCollections.observableSet();
    private final ObservableSet<String> compositeUniqueKeySet = FXCollections.observableSet();

    private String hash;
    private Generator generator;

    private TableGenerationSettings tableGenerationSettings;

    public ColumnInfo() {
        tableName = new SimpleStringProperty();
        columnName = new SimpleStringProperty();
        columnType = new SimpleStringProperty();
        columnSize = new SimpleStringProperty();
        sqlType = new SimpleIntegerProperty();
    }

    public ColumnInfo(String tableName, boolean root) {
        this.tableName = new SimpleStringProperty(tableName);
        this.columnName = new SimpleStringProperty();
        this.columnType = new SimpleStringProperty();
        this.columnSize = new SimpleStringProperty();
        this.sqlType = new SimpleIntegerProperty();
        tableGenerationSettings = new TableGenerationSettings();
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
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

    @JsonIgnore
    public StringProperty getColumnSizeProperty() {
        return columnSize;
    }

    // SQL Type

    public Integer getSqlType(){
        return  sqlType.get();
    }

    @JsonIgnore
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
        this.isRoot.set(isRoot);

    }

    // Database Type

    public String getDatabaseType() {
        return databaseType.get();
    }

    public StringProperty databaseTypeProperty() {
        return databaseType;
    }

    public void setDatabaseType(String databaseType) {
        this.databaseType.set(databaseType);
    }

    // Nullable

    public boolean getNullable() {
        return nullable.get();
    }

    public BooleanProperty nullableProperty() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable.set(nullable);
    }

    // AutoIncrement

    public boolean getAutoIncrement() {
        return autoIncrement.get();
    }

    public BooleanProperty autoIncrementProperty() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement.set(autoIncrement);
    }

    // Ordinar Position

    public int getOrdinalPosition() {
        return ordinalPosition.get();
    }

    public IntegerProperty ordinalPositionProperty() {
        return ordinalPosition;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition.set(ordinalPosition);
    }

    // Column Default Value

    public String getColumnDefaultValue() {
        return columnDefaultValue.get();
    }

    public StringProperty columnDefaultValueProperty() {
        return columnDefaultValue;
    }

    public void setColumnDefaultValue(String columnDefaultValue) {
        this.columnDefaultValue.set(columnDefaultValue);
    }

    // Is Primary Key

    public boolean getIsPrimaryKey() {
        return isPrimaryKey.get();
    }

    public BooleanProperty isPrimaryKeyProperty() {
        return isPrimaryKey;
    }

    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey.set(isPrimaryKey);
    }

    // Is multiple Column Primary Key

    public boolean getIsCompositePrimaryKey() {
        return isCompositePrimaryKey.get();
    }

    public BooleanProperty isCompositePrimaryKeyProperty() {
        return isCompositePrimaryKey;
    }

    public void setIsCompositePrimaryKey(boolean isCompositePrimaryKey) {
        this.isCompositePrimaryKey.set(isCompositePrimaryKey);
    }

    // Is Foreign Key

    public boolean getIsForeignKey() {
        return isForeignKey.get();
    }

    public BooleanProperty isForeignKeyProperty() {
        return isForeignKey;
    }

    public void setIsForeignKey(boolean isForeignKey) {
        this.isForeignKey.set(isForeignKey);
    }

    // Is Composite Foreign Key

    public boolean getIsCompositeForeignKey() {
        return isCompositeForeignKey.get();
    }

    public BooleanProperty isCompositeForeignKeyProperty() {
        return isCompositeForeignKey;
    }

    public void setIsCompositeForeignKey(boolean isCompositeForeignKey) {
        this.isCompositeForeignKey.set(isCompositeForeignKey);
    }

    // Is Unique Key

    public boolean getIsUniqueKey() {
        return isUniqueKey.get();
    }

    public BooleanProperty isUniqueKeyProperty() {
        return isUniqueKey;
    }

    public void setIsUniqueKey(boolean isUniqueKey) {
        this.isUniqueKey.set(isUniqueKey);
    }

    // Is Composite Unique Key

    public boolean getIsCompositeUniqueKey() {
        return isCompositeUniqueKey.get();
    }

    public BooleanProperty isCompositeUniqueKeyProperty() {
        return isCompositeUniqueKey;
    }

    public void setIsCompositeUniqueKey(boolean isCompositeUniqueKey) {
        this.isCompositeUniqueKey.set(isCompositeUniqueKey);
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

    // Table Generation Settings

    public TableGenerationSettings getTableGenerationSettings() {
        return tableGenerationSettings;
    }

    public void setTableGenerationSettings(TableGenerationSettings tableGenerationSettings) {
        this.tableGenerationSettings = tableGenerationSettings;
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

    // Schema

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

}
