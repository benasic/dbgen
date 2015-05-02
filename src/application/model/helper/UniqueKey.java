package application.model.helper;

import application.utils.Utils;

public class UniqueKey {

    private String name;
    private String columnName;
    private String tableName;
    private int sequenceNumber;
    private IndexType type;
    private long cardinality;
    private String hash;

    public enum IndexType{
        tableIndexStatistic,
        tableIndexClustered,
        tableIndexHashed,
        tableIndexOther
    }


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

    public long getCardinality() {
        return cardinality;
    }

    public void setCardinality(long cardinality) {
        this.cardinality = cardinality;
    }

    public String getHash(){
        if(hash == null){
            generateHash();
        }
        return hash;
    }

    public IndexType getType() {
        return type;
    }

    public void setType(short type) {
        switch (type){
            case 0 :
                this.type = IndexType.tableIndexStatistic;
                break;
            case 1 :
                this.type = IndexType.tableIndexClustered;
                break;
            case 2 :
                this.type = IndexType.tableIndexHashed;
                break;
            case 3 :
                this.type = IndexType.tableIndexOther;
                break;
            default:
                break;
        }
    }

    private void generateHash(){
        hash = Utils.generateSHA256String(getTableName() + getColumnName());
    }
}
