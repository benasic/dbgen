package application.service;

import application.DatabaseTools;
import application.Repository;
import application.generator.BooleanGenerator;
import application.generator.Generator;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import application.utils.ObjectCollection;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class GenerateService extends Service<Void> {

    private List<TreeItem<ColumnInfo>> tableList;
    private Repository repository = Repository.getInstance();

    public void setTableNameList(List<TreeItem<ColumnInfo>> tableList){
        this.tableList = tableList;
    }



    @Override
    protected Task<Void> createTask() {

        Deque<String> tableNames = new ArrayDeque<>();
        tableNames.addAll(tableList.stream()
                .map(TreeItem::getValue)
                .map(ColumnInfo::getTableName)
                .collect(Collectors.toList()));

        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                Instant start = Instant.now();
                List<String> previousHashes = new ArrayList<>();

                boolean getNextValueFromTableName = true;
                String resultOfOperation = null;
                int count = tableNames.size();
                while (tableNames.size() != 0) {
                    // final is necessary because value is used in lambda
                    final String tableName;
                    if (getNextValueFromTableName) {
                        tableName = tableNames.getFirst();
                    } else {
                        tableName = resultOfOperation;
                    }


                    List<ColumnInfo> columnInfoListForGeneration = tableList.stream()
                            .filter(tableTreeItem -> tableTreeItem.getValue().getTableName().equals(tableName))
                            .findFirst().get().getChildren().stream()
                            .map(TreeItem::getValue)
                            .collect(Collectors.toList());

                    if (!previousHashes.isEmpty()) {
                        // rewriting content of collection so that garbage collection can collect old one
                        previousHashes.stream().forEach(s ->
                                        Repository.getInstance().addCollectionToMap(s, new ArrayList<>())
                        );
                    }

                    previousHashes.clear();
                    previousHashes = columnInfoListForGeneration.stream()
                            .map(ColumnInfo::getHash)
                            .collect(Collectors.toList());


                    String numberOfDataToGenerateString = tableList.stream()
                            .filter(tableTreeItem -> tableTreeItem.getValue().getTableName().equals(tableName))
                            .findFirst().get().getValue().getTableGenerationSettings().getNumberOfDataToGenerateString();
                    int numberOfDataToGenerate = Integer.parseInt(numberOfDataToGenerateString);

                    String result = prepareData(columnInfoListForGeneration, numberOfDataToGenerate, false);
                    System.out.println("preparation done");
                    // if previous operation is successful
                    if (result == null) {
                        tableNames.remove(tableName);
                        getNextValueFromTableName = true;

                        // generate data into database
                        List<List<Object>> SQLRowsList = transformData(columnInfoListForGeneration);
                        System.out.println("transform done");
                        DatabaseTools dt = new DatabaseTools(Repository.getInstance().getConnectionInfo().getConnectionString());
                        try {
                            System.out.println("Filling table: " + tableName);
                            System.out.println("Remaining table " + tableNames.size());
                            dt.generateData(SQLRowsList, columnInfoListForGeneration);
                        } catch (SQLException e) {
                            System.err.println(e.getMessage());
                        }
                    } else {
                        System.out.println("First resolving dependency for " + result);
                        resultOfOperation = result;
                        getNextValueFromTableName = false;
                        // TODO remove first value to last place, optionally
                    }

                    updateProgress(count - tableNames.size(), count);
                    //updateMessage("");
                }

                Instant end = Instant.now();
                System.out.println("Generation time: " + Duration.between(start, end));

                return null;
            }
        };
    }

    private String prepareData(List<ColumnInfo> columnInfoListForGeneration ,int numberOfDataToGenerate, boolean preview) {
        String returnValue = null;



        if (columnInfoListForGeneration.size() != 0) {

            // calling generator for each of selected item
            // TODO add different collection type support

            Set<String> finishedColumn = new HashSet<>();
            for (ColumnInfo columnInfo : columnInfoListForGeneration) {

                // skip multiple generation of columns
                if (finishedColumn.contains(columnInfo.getHash())) {
                    continue;
                }

                String hash = columnInfo.getHash();
                Generator generator = columnInfo.getGenerator();
                boolean referenceGeneration = false;

                // case one

                if (columnInfo.getIsForeignKey() && !columnInfo.getIsCompositeForeignKey() && !columnInfo.getIsCompositePrimaryKey()) {
                    // fetch all referenced primary key data
                    referenceGeneration = true;
                    ForeignKey foreignKey = DatabaseTools.foreignKeyMap.get(columnInfo.getHash());
                    String primaryKeyHash = foreignKey.getPrimaryKey().getHash();
                    ColumnInfo primaryKeyColumn = repository.getColumnInfoList().stream().filter(column -> column.getHash().equals(primaryKeyHash)).findFirst().get();
                    List<ColumnInfo> columnInfoList = new ArrayList<>();
                    columnInfoList.add(primaryKeyColumn);
                    DatabaseTools dt = new DatabaseTools(Repository.getInstance().getConnectionInfo().getConnectionString());
                    List<Object[]> primaryKeyReferenceList;
                    List<Object> primaryKeyList;

                    try {
                        primaryKeyReferenceList = dt.fetchData(columnInfoList);
                        primaryKeyList = primaryKeyReferenceList.stream().map(objects -> objects[0]).collect(Collectors.toList());

                        // removing duplicate values
                        primaryKeyList = primaryKeyList.stream().collect(Collectors.toSet())
                                .stream().collect(Collectors.toList());

                        // break generation of data if reference are not satisfied
                        if (primaryKeyList.isEmpty() && !preview && !columnInfo.getNullable()) {
                            return primaryKeyColumn.getTableName();
                        }

                        repository.addCollectionToMap(hash, new ArrayList<>());
                        // pick random value
                        Random random = new Random();
                        random.setSeed(System.currentTimeMillis());
                        for (int i = 0; i < numberOfDataToGenerate; i++) {
                            Object object = null;
                            if(preview){
                                object = "Reference";
                            }
                            // TODO this else if will be changed to some advanced functionality
                            else if(columnInfo.getNullable()){
                                object = null;
                            }
                            else{
                                object = primaryKeyList.get(random.nextInt(primaryKeyList.size()));
                            }
                            repository.insertIntoCollection(hash, object);
                            if(i % 10000 == 0){
                                System.out.println("Trenutno " + i);
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    finishedColumn.add(columnInfo.getHash());
                }

                // case two

                else if (columnInfo.getIsPrimaryKey() && columnInfo.getIsCompositePrimaryKey()) {
                    // determine is all composite key value reference to regular foreign keys
                    boolean regularForeignKeys = true;
                    List<String> regularForeignKeysList = new LinkedList<>();
                    List<ColumnInfo> regularKeysList = new LinkedList<>();
                    for (String primaryKeyHash : columnInfo.getCompositePrimaryKeySet()) {

                        // stream through all composite key
                        ColumnInfo foreignKeyColumnInfo = repository.getColumnInfoList().stream()
                                .filter(column -> column.getHash().equals(primaryKeyHash))
                                .findFirst().get();

                        if (foreignKeyColumnInfo.getIsForeignKey() && !foreignKeyColumnInfo.getIsCompositeForeignKey()) {
                            regularForeignKeysList.add(foreignKeyColumnInfo.getHash());
                        } else if (foreignKeyColumnInfo.getIsForeignKey() && foreignKeyColumnInfo.getIsCompositeForeignKey()) {
                            regularForeignKeys = false;
                        }
                        // composite primary key contains regular column
                        else if (!foreignKeyColumnInfo.getIsForeignKey()) {
                            regularKeysList.add(foreignKeyColumnInfo);
                        }
                    }
                    // subcase one: all composite keys are foreign column or regular column

                    if (regularForeignKeys) {
                        // steps: fetch data and generate random pair of object that are not equal
                        List<List<Object>> fetchedData = new LinkedList<>();

                        for (String regularForeignKey : regularForeignKeysList) {
                            ForeignKey foreignKey = DatabaseTools.foreignKeyMap.get(regularForeignKey);
                            String primaryKeyHash = foreignKey.getPrimaryKey().getHash();
                            ColumnInfo primaryKeyColumn = repository.getColumnInfoList().stream().filter(column -> column.getHash().equals(primaryKeyHash)).findFirst().get();
                            List<ColumnInfo> columnInfoList = new ArrayList<>();
                            columnInfoList.add(primaryKeyColumn);
                            DatabaseTools dt = new DatabaseTools(Repository.getInstance().getConnectionInfo().getConnectionString());
                            List<Object[]> primaryKeyReferenceList;
                            List<Object> primaryKeyList;
                            Set<Object> primaryKeySet;
                            try {
                                primaryKeyReferenceList = dt.fetchData(columnInfoList);
                                primaryKeyList = primaryKeyReferenceList.stream().map(objects -> objects[0]).collect(Collectors.toList());

                                // removing duplicate values
                                primaryKeySet = primaryKeyList.stream().collect(Collectors.toSet());
                                primaryKeyList = primaryKeySet.stream().collect(Collectors.toList());

                                // break generation of data if reference are not satisfied
                                if (primaryKeySet.isEmpty() && !preview) {
                                    return primaryKeyColumn.getTableName();
                                }
                                fetchedData.add(primaryKeyList);

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            finishedColumn.add(regularForeignKey);
                        }

                        for (ColumnInfo regularKey : regularKeysList) {
                            // these values don't need to have maximum number of data
                            //  because they are under composite primary key
                            Set<Object> regularValuesSet = new HashSet<>();
                            generator = regularKey.getGenerator();
                            generator.initiateGenerator();
                            // TODO find better solution for this if part
                            if(generator instanceof BooleanGenerator){
                                ((BooleanGenerator)generator).setDataNumber(numberOfDataToGenerate);
                            }

                            for (int i = 0; i < numberOfDataToGenerate; i++) {
                                regularValuesSet.add(generator.generate());
                            }
                            fetchedData.add(new ArrayList<>(regularValuesSet));

                            finishedColumn.add(regularKey.getHash());
                        }

                        // generate unique pairs
                        Set<ObjectCollection> objects = new HashSet<>();
                        // list for preview support
                        List<ObjectCollection> objectsListPreview = new ArrayList<>();

                        int size = regularForeignKeysList.size() + regularKeysList.size();
                        Random random = new Random();
                        random.setSeed(System.currentTimeMillis());
                        int iteration = 0;
                        while (objects.size() < numberOfDataToGenerate && objectsListPreview.isEmpty()
                                || objectsListPreview.size() < numberOfDataToGenerate && objects.isEmpty()) {
                            ObjectCollection objectCollection = new ObjectCollection(size);
                            for (int i = 0; i < size; i++) {
                                if(!preview){
                                    objectCollection.objects[i] = fetchedData.get(i).get(random.nextInt(fetchedData.get(i).size()));
                                }
                                else{
                                    objectCollection.objects[i] = "Reference";
                                }
                            }
                            if(!preview){
                                objects.add(objectCollection);
                            }
                            else{
                                objectsListPreview.add(objectCollection);
                            }
                            iteration++;
                            // this part is for debugging to see if there is not enough data for unique pairs
                            if(!preview && iteration % numberOfDataToGenerate == 0){
                                System.out.println("Generating unique pair: current size" + objects.size() + " requested size " + numberOfDataToGenerate);
                            }
                        }

                        // return pairs into regular format
                        for (int i = 0; i < regularForeignKeysList.size(); i++) {
                            final int finalI = i;
                            List<Object> objectList;
                            if(!preview){
                                objectList = objects.stream()
                                        .map(objectCollection -> objectCollection.objects[finalI])
                                        .collect(Collectors.toList());
                            }
                            else{
                                objectList = objectsListPreview.stream()
                                        .map(objectCollection -> objectCollection.objects[finalI])
                                        .collect(Collectors.toList());
                            }
                            repository.addCollectionToMap(regularForeignKeysList.get(i), objectList);
                        }

                        for (int i = 0; i < regularKeysList.size(); i++) {
                            final int finalI = i + regularForeignKeysList.size();
                            List<Object> objectList;
                            if(!preview){
                                objectList = objects.stream()
                                        .map(objectCollection -> objectCollection.objects[finalI])
                                        .collect(Collectors.toList());
                            }
                            else{
                                objectList = objectsListPreview.stream()
                                        .map(objectCollection -> objectCollection.objects[finalI])
                                        .collect(Collectors.toList());
                            }
                            repository.addCollectionToMap(regularKeysList.get(i).getHash(), objectList);
                        }


                    } else {
                        // for now
                        Alert generationNotAllowed = new Alert(Alert.AlertType.ERROR);
                        generationNotAllowed.setTitle("Preparation error");
                        generationNotAllowed.setContentText("Composite primary to composite foreign key not yet implemented!!\nBreaking preparation!!");
                        generationNotAllowed.show();
                        return returnValue;
                    }
                }
                // skip normal generation of data for composite or referenced columns
                if (finishedColumn.contains(hash)) {
                    continue;
                }
                if (!referenceGeneration) {

                    if(!columnInfo.getAutoIncrement()){
                        generator.initiateGenerator();
                        if(generator instanceof BooleanGenerator){
                            ((BooleanGenerator)generator).setDataNumber(numberOfDataToGenerate);
                        }
                    }

                    // part for unique primary key, will be moved up
                    if(!columnInfo.getAutoIncrement() && columnInfo.getIsPrimaryKey() && !columnInfo.getIsCompositePrimaryKey()){
                        Set<Object> uniqueObjects = new HashSet<>();
                        while(uniqueObjects.size() < numberOfDataToGenerate){
                            uniqueObjects.add(generator.generate());
                        }
                        repository.addCollectionToMap(hash, new ArrayList<>(uniqueObjects));
                        finishedColumn.add(columnInfo.getHash());
                        continue;
                    }

                    // unique key
                    if(!columnInfo.getAutoIncrement() && !columnInfo.getIsPrimaryKey() && columnInfo.getIsUniqueKey() && !columnInfo.getIsCompositeUniqueKey()){

                        List<ColumnInfo> columnInfoList = new ArrayList<>();
                        columnInfoList.add(columnInfo);
                        DatabaseTools dt = new DatabaseTools(Repository.getInstance().getConnectionInfo().getConnectionString());
                        List<Object[]> keyReferenceList;
                        Set<Object> keyList = new HashSet<>();

                        try {
                            keyReferenceList = dt.fetchData(columnInfoList);
                            keyList = keyReferenceList.stream().map(objects -> objects[0]).collect(Collectors.toSet());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // this block is for preventing infinity loop
                        int maxTry = numberOfDataToGenerate * 5;
                        int count = 0;

                        Set<Object> uniqueObjects = new HashSet<>();
                        while(uniqueObjects.size() < numberOfDataToGenerate){
                            Object value = generator.generate();
                            if(!keyList.contains(value)){
                                uniqueObjects.add(value);
                            }
                            if(++count == maxTry){
                                break;
                            }
                        }

                        //for preview
                        if(preview && uniqueObjects.size() < 100){
                            while(uniqueObjects.size() < 100){
                                uniqueObjects.add(generator.generate());
                            }
                        }

                        repository.addCollectionToMap(hash, new ArrayList<>(uniqueObjects));
                        finishedColumn.add(columnInfo.getHash());
                        System.out.println("Unique column generated " + columnInfo.getColumnName() + " " + columnInfo.getTableName());
                        continue;
                    }


                    repository.addCollectionToMap(hash, new ArrayList<>());
                    for (int i = 0; i < numberOfDataToGenerate; i++) {
                        if (columnInfo.getAutoIncrement()) {
                            repository.insertIntoCollection(hash, "Auto generated in DB");
                        } else {
                            repository.insertIntoCollection(hash, generator.generate());
                        }
                        finishedColumn.add(columnInfo.getHash());
                    }
                    repository.getConnectionInfo();
                }
            }
        }
        return returnValue;
    }

    private List<List<Object>> transformData(List<ColumnInfo> columnInfoListForGeneration) {

        List<List<Object>> SQLRowsList = new ArrayList<>();

        // remapping data!!!
        for(int i = 0; i < columnInfoListForGeneration.size(); i++){
            String hash = columnInfoListForGeneration.get(i).getHash();
            Iterator<Object> objectIterator = Repository.getInstance().returnCollectionIterator(hash);
            int counter = SQLRowsList.size();
            int previewObservableListSize = SQLRowsList.size();
            while(objectIterator.hasNext()) {
                // append to existing collection
                if (i > 0) {
                    SQLRowsList.get(previewObservableListSize - counter).add(objectIterator.next());
                    counter--;
                }
                // case when need to recreate entire collection
                else if (i == 0) {
                    List<Object> objectList = new ArrayList<>(columnInfoListForGeneration.size());
                    objectList.add(objectIterator.next());
                    SQLRowsList.add(objectList);
                }
            }
        }

        return SQLRowsList;
    }

}

