package application.controller;

import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.generator.Generator;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import application.utils.ObjectCollection;
import application.utils.XML;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MainAppController {

    @FXML
    private TreeTableView<ColumnInfo> columnInfoTreeTableView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<ObservableList<Object>> tableView;

    @FXML
    private Button saveButton;

    @FXML
    private Button prepareButton;

    @FXML
    private Button previewButton;

    @FXML
    private Button generateButton;

    private ObservableList<ColumnInfo> columnInfoList = null;
    private List<ColumnInfo> selectedColumnInfoList = new ArrayList<>();
    private ObservableList<ObservableList<Object>> previewObservableList = FXCollections.observableArrayList();

    private final FXMLLoader stringLoader = new FXMLLoader();
    private final FXMLLoader integerLoader = new FXMLLoader();
    private final FXMLLoader dateLoader = new FXMLLoader();
    private AnchorPane stringGeneratorSubScene;
    private AnchorPane numberGeneratorSubScene;
    private AnchorPane dateGeneratorSubScene;
    private StringGeneratorController stringGeneratorController;
    private NumberGeneratorController numberGeneratorController;
    private DateGeneratorController dateGeneratorController;
    private Image tableIcon;
    private Image primaryKeyIcon;
    private Image foreignKeyIcon;
    private Image primaryForeignKeyIcon;

    private int index = 0;

    private String lastGeneratorType;
    private Generator lastActiveGenerator;

    @FXML
    private void initialize() {

        tableIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/table.png"));
        primaryKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/primaryKey.png"));
        foreignKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/foreignKey.png"));
        primaryForeignKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/primaryForeignKey.png"));

        stringLoader.setLocation(DbGen.class.getResource("view/StringGenerator.fxml"));
        integerLoader.setLocation(DbGen.class.getResource("view/NumberGenerator.fxml"));
        dateLoader.setLocation(DbGen.class.getResource("view/DateGenerator.fxml"));

        try {
            stringGeneratorSubScene = stringLoader.load();
            numberGeneratorSubScene = integerLoader.load();
            dateGeneratorSubScene = dateLoader.load();
            stringGeneratorController = stringLoader.getController();
            numberGeneratorController = integerLoader.getController();
            dateGeneratorController = dateLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(){
        getTableInfoData();
        fillTableInfoTreeTableView();
        addTableViewSynchronizationWithColumnInfo();
        addPrepareDataListener();
        addPreviewDataListener();
        addGenerateDataListener();
        addSaveProjectListener();
    }

    private void getTableInfoData(){
        DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
        try {
            columnInfoList = dt.getColumnInfoObservableList(null, null, null, new String[]{"TABLE"});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void fillTableInfoTreeTableView(){

        Map<String, TreeItem<ColumnInfo>> roots = new HashMap<>();

        for (ColumnInfo columnInfo : columnInfoList) {

            // setting root elements
            TreeItem<ColumnInfo> tempRoot;
            // find temp root
            if(roots.keySet().contains(columnInfo.getTableName())){
                tempRoot = roots.get(columnInfo.getTableName());
            }
            // if root doesn't exits crate new
            else{
                tempRoot = new TreeItem<>(new ColumnInfo(columnInfo.getTableName(), true), new ImageView (tableIcon));
                tempRoot.setExpanded(true);
                roots.put(columnInfo.getTableName(), tempRoot);
            }
            // setting child
            if(columnInfo.getIsPrimaryKey() && columnInfo.getIsForeignKey()){
                tempRoot.getChildren().add(new TreeItem<>(columnInfo, new ImageView(primaryForeignKeyIcon)));
            }
            else if(columnInfo.getIsPrimaryKey()){
                tempRoot.getChildren().add(new TreeItem<>(columnInfo, new ImageView(primaryKeyIcon)));
            }
            else if (columnInfo.getIsForeignKey()) {
                tempRoot.getChildren().add(new TreeItem<>(columnInfo, new ImageView(foreignKeyIcon)));
            } else {
                tempRoot.getChildren().add(new TreeItem<>(columnInfo));
            }

        }

        TreeItem<ColumnInfo> mainRoot = new TreeItem<>(new ColumnInfo("root", false));
        mainRoot.setExpanded(true);
        for (TreeItem<ColumnInfo> root : roots.values()) {
            mainRoot.getChildren().add(root);
        }

        TreeTableColumn<ColumnInfo, String> columnName = new TreeTableColumn<>("Column Name");
        TreeTableColumn<ColumnInfo, String> columnType = new TreeTableColumn<>("Column Type");
        columnName.setPrefWidth(150);
        columnType.setPrefWidth(150);
        columnInfoTreeTableView.getColumns().add(columnName);
        columnInfoTreeTableView.getColumns().add(columnType);

        columnName.setCellValueFactory(p -> {
            ColumnInfo columnInfo = p.getValue().getValue();
            if (columnInfo.getIsRoot()) {
                return new ReadOnlyStringWrapper(columnInfo.getTableName());
            } else {
                return new ReadOnlyStringWrapper(columnInfo.getColumnName());
            }
        });
        columnType.setCellValueFactory(p -> {
            ColumnInfo columnInfo = p.getValue().getValue();
            if(columnInfo.getIsRoot()){
                return new ReadOnlyStringWrapper("");
            }
            else{
                return new ReadOnlyStringWrapper(columnInfo.getColumnType() + " [" + columnInfo.getColumnSize() + "]");
            }
        });
        columnInfoTreeTableView.setRoot(mainRoot);
        columnInfoTreeTableView.setShowRoot(false);

        columnInfoTreeTableView.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                String type = newValue.getValue().getColumnType();
                if(type == null){
                    System.err.println("Invalid type");
                    return;
                }
                // unbind old values
                if(lastActiveGenerator != null){
                    switch (lastGeneratorType){
                        case "VARCHAR":
                            stringGeneratorController.unbindValues(lastActiveGenerator);
                            lastActiveGenerator = null;
                            break;
                        case "INTEGER":
                            numberGeneratorController.unbindValues(lastActiveGenerator);
                            lastActiveGenerator = null;
                            break;
                        case "TIMESTAMP":
                            dateGeneratorController.unbindValues(lastActiveGenerator);
                            lastActiveGenerator = null;
                            break;
                        default:
                            break;
                    }
                }

                switch (type) {
                    case "VARCHAR":
                        lastGeneratorType = "VARCHAR";
                        lastActiveGenerator = newValue.getValue().getGenerator();
                        stringGeneratorController.setGenerator(newValue.getValue().getGenerator());
                        mainBorderPane.setCenter(stringGeneratorSubScene);
                        break;
                    case "INTEGER":
                        lastGeneratorType = "INTEGER";
                        lastActiveGenerator = newValue.getValue().getGenerator();
                        numberGeneratorController.setGenerator(newValue.getValue().getGenerator());
                        mainBorderPane.setCenter(numberGeneratorSubScene);
                        break;
                    case "TIMESTAMP":
                        lastGeneratorType = "TIMESTAMP";
                        lastActiveGenerator = newValue.getValue().getGenerator();
                        dateGeneratorController.setGenerator(newValue.getValue().getGenerator());
                        mainBorderPane.setCenter(dateGeneratorSubScene);
                    default:
                        break;
                }
        });
    }

    private void addTableViewSynchronizationWithColumnInfo() {
        columnInfoTreeTableView.getSelectionModel().selectedItemProperty()
            .addListener(((observable, oldValue, newValue) -> {

                // in case root is selected
                if ((oldValue == null && newValue.getValue().getIsRoot())
                        || (oldValue != null && newValue.getValue().getIsRoot() && !oldValue.getParent().equals(newValue))) {
                    tableView.getColumns().clear();
                    tableView.getItems().clear();

                    TableColumn<ObservableList<Object>, Object> indexColumn = new TableColumn<>("#");
                    indexColumn.setPrefWidth(40);
                    // TODO this line will only work correctly if row is unique
                    indexColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(param.getValue()) + 1));
                    indexColumn.setSortable(false);
                    tableView.getColumns().add(indexColumn);

                    for (TreeItem<ColumnInfo> columnInfoTreeItem : newValue.getChildren()) {
                        int column = newValue.getChildren().indexOf(columnInfoTreeItem);
                        TableColumn<ObservableList<Object>, String> objectStringTableColumn = new TableColumn<>(columnInfoTreeItem.getValue().getColumnName());
                        objectStringTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObservableList<Object>, String> param) -> new ReadOnlyStringWrapper(param.getValue().get(column).toString()));
                        tableView.getColumns().add(objectStringTableColumn);
                    }
                }
                // in case internal element is selected, but only if it
                // has different root element then previous one
                else if (oldValue == null || (!oldValue.getParent().equals(newValue.getParent()) && !newValue.getValue().getIsRoot() && !newValue.getParent().equals(oldValue))) {
                    tableView.getColumns().clear();
                    tableView.getItems().clear();

                    TableColumn<ObservableList<Object>, Object> indexColumn = new TableColumn<>("#");
                    indexColumn.setPrefWidth(40);
                    // TODO this line will only work correctly if row is unique
                    indexColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(tableView.getItems().indexOf(param.getValue()) + 1));
                    indexColumn.setSortable(false);
                    tableView.getColumns().add(indexColumn);

                    TreeItem<ColumnInfo> rootColumnInfoTreeItem = newValue.getParent();
                    for (TreeItem<ColumnInfo> columnInfoTreeItem : rootColumnInfoTreeItem.getChildren()) {
                        int column = rootColumnInfoTreeItem.getChildren().indexOf(columnInfoTreeItem);
                        TableColumn<ObservableList<Object>, String> objectStringTableColumn = new TableColumn<>(columnInfoTreeItem.getValue().getColumnName());
                        objectStringTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObservableList<Object>, String> param) -> new ReadOnlyStringWrapper(param.getValue().get(column).toString()));
                        tableView.getColumns().add(objectStringTableColumn);
                    }
                }
            }));
    }

    private void addSaveProjectListener() {
        saveButton.setOnAction(event -> {
            if(columnInfoList != null && !columnInfoList.isEmpty()){
                List<ColumnInfo> columnInfos = columnInfoList.stream().collect(Collectors.toList());
                XML.createXML(columnInfos);
            }
        });
    }

    private void addPrepareDataListener(){
        prepareButton.setOnAction(event -> {
            TreeItem<ColumnInfo> selectedColumnInfoTreeItem = columnInfoTreeTableView.getSelectionModel().getSelectedItem();
            if (selectedColumnInfoTreeItem != null) {
                selectedColumnInfoList.clear();
                // if root is selected
                if (selectedColumnInfoTreeItem.getValue().getIsRoot()) {
                    selectedColumnInfoList.addAll(selectedColumnInfoTreeItem.getChildren().stream()
                            .map(TreeItem::getValue)
                            .collect(Collectors.toList()));
                }
                // if leaf is selected
                else {
                    TreeItem<ColumnInfo> columnInfoTreeItem = selectedColumnInfoTreeItem.getParent();
                    selectedColumnInfoList.addAll(columnInfoTreeItem.getChildren().stream()
                            .map(TreeItem::getValue)
                            .collect(Collectors.toList()));
                }

                // calling generator for each of selected item
                // TODO add different collection type support
                JDBC_Repository jdbc_repository = JDBC_Repository.getInstance();


                Set<String> finishedColumn = new HashSet<>();
                for (ColumnInfo columnInfo : selectedColumnInfoList) {

                    // skip multiple generation of columns
                    if(finishedColumn.contains(columnInfo.getHash())){
                        continue;
                    }

                    String hash = columnInfo.getHash();
                    Generator generator = columnInfo.getGenerator();
                    boolean referenceGeneration = false;

                    // case one

                    if(columnInfo.getIsForeignKey() && !columnInfo.getIsCompositeForeignKey() && !columnInfo.getIsCompositePrimaryKey()){
                        // fetch all referenced primary key data
                        referenceGeneration = true;
                        ForeignKey foreignKey = DatabaseTools.foreignKeyMap.get(columnInfo.getHash());
                        String primaryKeyHash = foreignKey.getPrimaryKey().getHash();
                        ColumnInfo primaryKeyColumn = columnInfoList.stream().filter(column -> column.getHash().equals(primaryKeyHash)).findFirst().get();
                        List<ColumnInfo> columnInfoList = new ArrayList<>();
                        columnInfoList.add(primaryKeyColumn);
                        DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
                        List<Object[]> primaryKeyReferenceList;
                        List<Object> primaryKeyList;
                        try {
                            primaryKeyReferenceList = dt.fetchData(columnInfoList);
                            primaryKeyList = primaryKeyReferenceList.stream().map(objects -> objects[0]).collect(Collectors.toList());

                            // break generation of data if reference are not satisfied
                            if(primaryKeyList.isEmpty()){
                                Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                                referenceMissing.setTitle("Preparation warning");
                                referenceMissing.setContentText("First generate data for reference table: " + primaryKeyColumn.getTableName());
                                referenceMissing.showAndWait();
                                return;
                            }

                            jdbc_repository.addCollectionToMap(hash, new ArrayList<>());
                            // pick random value
                            Random random = new Random();
                            random.setSeed(System.currentTimeMillis());
                            for(int i = 0; i < 1000; i++){
                                Object object = primaryKeyList.get(random.nextInt(1000));
                                jdbc_repository.insertIntoCollection(hash, object);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        finishedColumn.add(columnInfo.getHash());
                    }

                    // case two

                    else if(columnInfo.getIsPrimaryKey() && columnInfo.getIsCompositePrimaryKey()){
                        // determine is all composite key value reference to regular foreign keys
                        boolean regularForeignKeys = true;
                        boolean allKeysAreForeign = true;
                        List<String> regularForeignKeysSet = new LinkedList<>();
                        for(String primaryKeyHash : columnInfo.getCompositePrimaryKeySet()){
                            // stream through all composite key
                            ColumnInfo foreignKeyColumnInfo = columnInfoList.stream()
                                    .filter(column -> column.getHash().equals(primaryKeyHash))
                                    .findFirst().get();
                            if(foreignKeyColumnInfo.getIsForeignKey() && !foreignKeyColumnInfo.getIsCompositeForeignKey()){
                                regularForeignKeysSet.add(foreignKeyColumnInfo.getHash());
                            }
                            else if(foreignKeyColumnInfo.getIsForeignKey() && foreignKeyColumnInfo.getIsCompositeForeignKey()){
                                regularForeignKeys = false;
                            }
                            else if(!foreignKeyColumnInfo.getIsForeignKey()){
                                allKeysAreForeign = false;
                            }
                        }
                        // subcase one: all composite keys are regular foreign

                        if(regularForeignKeys && allKeysAreForeign){
                            // steps: fetch data and generate random pair of object that are not equal
                            List<List<Object>> fetchedData = new LinkedList<>();
                            for(String regularForeignKey : regularForeignKeysSet){
                                ForeignKey foreignKey = DatabaseTools.foreignKeyMap.get(regularForeignKey);
                                String primaryKeyHash = foreignKey.getPrimaryKey().getHash();
                                ColumnInfo primaryKeyColumn = columnInfoList.stream().filter(column -> column.getHash().equals(primaryKeyHash)).findFirst().get();
                                List<ColumnInfo> columnInfoList = new ArrayList<>();
                                columnInfoList.add(primaryKeyColumn);
                                DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
                                List<Object[]> primaryKeyReferenceList;
                                List<Object> primaryKeyList;
                                try {
                                    primaryKeyReferenceList = dt.fetchData(columnInfoList);
                                    primaryKeyList = primaryKeyReferenceList.stream().map(objects -> objects[0]).collect(Collectors.toList());

                                    // break generation of data if reference are not satisfied
                                    if(primaryKeyList.isEmpty()){
                                        Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                                        referenceMissing.setTitle("Preparation warning");
                                        referenceMissing.setContentText("First generate data for reference table: " + primaryKeyColumn.getTableName());
                                        referenceMissing.showAndWait();
                                        return;
                                    }

                                    fetchedData.add(primaryKeyList);

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }

                                finishedColumn.add(regularForeignKey);
                            }

                            // generate unique pairs
                            Set<ObjectCollection> objects = new HashSet<>();
                            int size = regularForeignKeysSet.size();
                            Random random = new Random();
                            random.setSeed(System.currentTimeMillis());
                            while(objects.size() < 1000){
                                ObjectCollection objectCollection = new ObjectCollection(size);
                                for(int i = 0; i < size; i++){
                                    objectCollection.objects[i] = fetchedData.get(i).get(random.nextInt(1000));
                                }
                                objects.add(objectCollection);
                            }

                            // return pairs into regular format
                            for(int i = 0;  i < regularForeignKeysSet.size(); i++){
                                final int finalI = i;
                                List<Object> objectList = objects.stream()
                                        .map(objectCollection -> objectCollection.objects[finalI])
                                        .collect(Collectors.toList());
                                jdbc_repository.addCollectionToMap(regularForeignKeysSet.get(i), objectList);
                            }
                        }
                        else{
                            // for now
                            return;
                        }
                    }
                    // skip normal generation of data for composite or referenced columns
                    if(finishedColumn.contains(hash)){
                        continue;
                    }
                    if(!referenceGeneration){
                        jdbc_repository.addCollectionToMap(hash, new ArrayList<>());
                        for (int i = 0; i < 1000; i++) {
                            if(columnInfo.getAutoIncrement()){
                                jdbc_repository.insertIntoCollection(hash, "Auto generated in DB");
                            }
                            else{
                                jdbc_repository.insertIntoCollection(hash, generator.generate());
                            }
                            finishedColumn.add(columnInfo.getHash());
                        }
                    }
                }
            }
        });
    }

    private void addPreviewDataListener(){
        previewButton.setOnAction(event -> {
            if(selectedColumnInfoList.size() != 0){
                previewObservableList.clear();
                tableView.getItems().clear();
                for(int i = 0; i < selectedColumnInfoList.size(); i++){
                    String hash = selectedColumnInfoList.get(i).getHash();
                    Iterator<Object> objectIterator = JDBC_Repository.getInstance().returnCollectionIterator(hash);
                    int counter = previewObservableList.size();
                    int previewObservableListSize = previewObservableList.size();
                    while(objectIterator.hasNext()) {
                        // append to existing collection
                        if (i > 0) {
                            previewObservableList.get(previewObservableListSize - counter).add(objectIterator.next());
                            counter--;
                        }
                        // case when need to recreate entire collection
                        else if (i == 0) {
                            previewObservableList.add(FXCollections.observableArrayList(objectIterator.next()));
                        }
                    }
                }

                tableView.setItems(previewObservableList);

            }
        });
    }

    private void addGenerateDataListener(){
        generateButton.setOnAction(event -> {
            if(previewObservableList.isEmpty()){
                return;
            }

            DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
            try {
                dt.generateData(previewObservableList, selectedColumnInfoList);
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });
    }
}
