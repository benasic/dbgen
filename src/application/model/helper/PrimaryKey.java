package application.model.helper;

import application.utils.Utils;

public class PrimaryKey {

    private String name;
    private String columnName;
    private String tableName;
    private int sequenceNumber;
    private String hash;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
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
