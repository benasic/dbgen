package application.controller;

import application.Constants;
import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.generator.Generator;
import application.generator.TableGenerationSettings;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import application.utils.JSON;
import application.utils.ObjectCollection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
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
    private Button loadButton;

    @FXML
    private Button refreshMetadata;

    @FXML
    private Button prepareButton;

    @FXML
    private Button previewButton;

    @FXML
    private Button generateButton;

    @FXML
    private Button visualizeDatabaseButton;

    private Stage primaryStage;

    private ObservableList<ColumnInfo> columnInfoList = null;
    private ObservableList<ColumnInfo> tableInfoList = FXCollections.observableArrayList();
    private List<ColumnInfo> selectedColumnInfoList = new ArrayList<>();
    private ObservableList<ObservableList<Object>> previewObservableList = FXCollections.observableArrayList();

    private final FXMLLoader stringLoader = new FXMLLoader();
    private final FXMLLoader integerLoader = new FXMLLoader();
    private final FXMLLoader dateLoader = new FXMLLoader();
    private final FXMLLoader tableSettingsLoader = new FXMLLoader();
    private AnchorPane stringGeneratorSubScene;
    private AnchorPane numberGeneratorSubScene;
    private AnchorPane dateGeneratorSubScene;
    private AnchorPane tableSettingsSubScene;
    private StringGeneratorController stringGeneratorController;
    private NumberGeneratorController numberGeneratorController;
    private DateGeneratorController dateGeneratorController;
    private TableGenerationSettingsController tableGenerationSettingsController;

    private Image tableIcon;
    private Image primaryKeyIcon;
    private Image foreignKeyIcon;
    private Image primaryForeignKeyIcon;

    private String lastGeneratorType;
    private Generator lastActiveGenerator;
    private TableGenerationSettings lastSelectedTableGenerationSettings;

    public boolean blockAll = false;

    @FXML
    private void initialize() {

        tableIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/table.png"));
        primaryKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/primaryKey.png"));
        foreignKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/foreignKey.png"));
        primaryForeignKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/primaryForeignKey.png"));

        stringLoader.setLocation(DbGen.class.getResource("view/StringGenerator.fxml"));
        integerLoader.setLocation(DbGen.class.getResource("view/NumberGenerator.fxml"));
        dateLoader.setLocation(DbGen.class.getResource("view/DateGenerator.fxml"));
        tableSettingsLoader.setLocation(DbGen.class.getResource("view/TableGenerationSettings.fxml"));

        try {
            stringGeneratorSubScene = stringLoader.load();
            numberGeneratorSubScene = integerLoader.load();
            dateGeneratorSubScene = dateLoader.load();
            tableSettingsSubScene = tableSettingsLoader.load();
            stringGeneratorController = stringLoader.getController();
            numberGeneratorController = integerLoader.getController();
            numberGeneratorController.setMainController(this);
            dateGeneratorController = dateLoader.getController();
            tableGenerationSettingsController = tableSettingsLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(Stage primaryStage){
        this.primaryStage = primaryStage;
        getTableInfoData();
        fillTableInfoTreeTableView();
        addTreeTableViewListeners();
        addTableViewSynchronizationWithColumnInfo();
        addPrepareDataListener();
        addPreviewDataListener();
        addGenerateDataListener();
        addSaveProjectListener();
        addLoadProjectListener();
        addRefreshMetadataListener();
        addVisualizeDatabaseListener();
    }

    private void getTableInfoData(){

        File f = new File(Constants.SaveLoation);
        Set<String> names = new HashSet<>();
        names.addAll(Arrays.asList(f.list()));

        boolean projectExist = false;
        projectExist = names.stream().anyMatch(s -> s.equals(JDBC_Repository.getInstance().getConnectionInfo().getSaveName().concat("_columns.json")));

        DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());

        if(projectExist){
            columnInfoList = JSON.createJavaObjectsforColumnInfo(JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), false);
            tableInfoList = JSON.createJavaObjectsforColumnInfo(JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), true);
            System.out.println("ponovno učitanje");
            try{
                dt.getColumnInfoObservableList(null, null, null, new String[]{"TABLE"});
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else{

            try {
                columnInfoList = dt.getColumnInfoObservableList(null, null, null, new String[]{"TABLE"});
                List<ColumnInfo> columnInfos = columnInfoList.stream().collect(Collectors.toList());
                JSON.createJSONforColumnInfo(columnInfos, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), false);
                System.out.println("prvo učitanje");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void fillTableInfoTreeTableView(){

        boolean newData = false;
        if(tableInfoList.size() == 0){
            newData = true;
        }

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
                ColumnInfo rootColumnInfo;
                if(newData){
                    rootColumnInfo = new ColumnInfo(columnInfo.getTableName(),true);
                    tableInfoList.add(rootColumnInfo);
                }
                else{
                    rootColumnInfo = tableInfoList.stream()
                            .filter(tableInfo -> tableInfo.getTableName().equals(columnInfo.getTableName()))
                            .findFirst().get();
                }

                tempRoot = new TreeItem<>(rootColumnInfo, new ImageView (tableIcon));
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

        if(newData){
            List<ColumnInfo> tableInfRealList = tableInfoList.stream().collect(Collectors.toList());
            JSON.createJSONforColumnInfo(tableInfRealList, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), true);
        }

        TreeItem<ColumnInfo> mainRoot = new TreeItem<>(new ColumnInfo("root", false));
        mainRoot.setExpanded(true);
        for (TreeItem<ColumnInfo> root : roots.values()) {
            mainRoot.getChildren().add(root);
        }

        TreeTableColumn<ColumnInfo, String> columnName = new TreeTableColumn<>("Column Name");
        TreeTableColumn<ColumnInfo, String> columnType = new TreeTableColumn<>("Column Type");
        columnName.setPrefWidth(250);
        columnType.setPrefWidth(250);
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
    }

    private void addTreeTableViewListeners(){
        columnInfoTreeTableView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if(blockAll){
                mouseEvent.consume();
                System.out.println("Blocking event until validation is complete!");
            }
        });

        columnInfoTreeTableView.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                if(newValue != null) {
                    Boolean isRoot = newValue.getValue().getIsRoot();
                    if (isRoot) {
                        if(lastSelectedTableGenerationSettings != null){
                            tableGenerationSettingsController.unbindValues(lastSelectedTableGenerationSettings);
                        }
                        lastSelectedTableGenerationSettings = newValue.getValue().getTableGenerationSettings();
                        tableGenerationSettingsController.bindValues(lastSelectedTableGenerationSettings);
                        mainBorderPane.setCenter(tableSettingsSubScene);
                        return;
                    }
                    String type = newValue.getValue().getColumnType();
                    // unbind old values
                    if (lastActiveGenerator != null) {
                        switch (lastGeneratorType) {
                            case "TEXT":
                                stringGeneratorController.unbindValues(lastActiveGenerator);
                                lastActiveGenerator = null;
                                break;
                            case "NUMBER":
                                numberGeneratorController.unbindValues(lastActiveGenerator, lastGeneratorType);
                                lastActiveGenerator = null;
                                break;
                            case "DATE":
                                dateGeneratorController.unbindValues(lastActiveGenerator);
                                lastActiveGenerator = null;
                                break;
                            default:
                                break;
                        }
                    }

                    switch (type) {
                        case "CHAR":
                        case "VARCHAR":
                        case "LONGVARCHAR":
                            lastGeneratorType = "TEXT";
                            lastActiveGenerator = newValue.getValue().getGenerator();
                            stringGeneratorController.setGenerator(newValue.getValue().getGenerator(), type);
                            mainBorderPane.setCenter(stringGeneratorSubScene);
                            break;
                        case "INTEGER":
                        case "SMALLINT":
                        case "TINYINT":
                        case "BIGINT":
                        case "REAL":
                        case "FLOAT":
                        case "DOUBLE":
                        case "DECIMAL":
                        case "NUMERIC":
                            lastGeneratorType = "NUMBER";
                            lastActiveGenerator = newValue.getValue().getGenerator();
                            numberGeneratorController.setGenerator(newValue.getValue().getGenerator(), type);
                            mainBorderPane.setCenter(numberGeneratorSubScene);
                            break;
                        case "TIMESTAMP":
                        case "DATE":
                        case "TIME":
                            lastGeneratorType = "DATE";
                            lastActiveGenerator = newValue.getValue().getGenerator();
                            dateGeneratorController.setGenerator(newValue.getValue().getGenerator(), type);
                            mainBorderPane.setCenter(dateGeneratorSubScene);
                        default:
                            break;
                    }
                }
            });
    }

    private void addTableViewSynchronizationWithColumnInfo() {
        columnInfoTreeTableView.getSelectionModel().selectedItemProperty()
            .addListener(((observable, oldValue, newValue) -> {
                if (newValue != null) {
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
                }
            }));

    }

    private void addSaveProjectListener() {
        saveButton.setOnAction(event -> {
            if(!blockAll && columnInfoList != null && !columnInfoList.isEmpty()){
                List<ColumnInfo> columnInfoRealList = columnInfoList.stream().collect(Collectors.toList());
                JSON.createJSONforColumnInfo(columnInfoRealList, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), false);
                List<ColumnInfo> tableInfRealList = tableInfoList.stream().collect(Collectors.toList());
                JSON.createJSONforColumnInfo(tableInfRealList, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), true);
            }
        });
    }

    private void addLoadProjectListener() {
        loadButton.setOnAction(event -> {
            if(!blockAll){
                columnInfoList.clear();
                tableInfoList.clear();
                getTableInfoData();
                fillTableInfoTreeTableView();
            }
        });
    }

    private void addRefreshMetadataListener(){
        refreshMetadata.setOnAction(event -> {
            if (!blockAll) {
                DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
                try {
                    // TODO cijela funkcija prebrise podatke sto nije dobro, napraviti da radi usporedbu
                    // TODO terba popraviti da radi ispravno...... preko usporedbe hesheva
                    columnInfoList = dt.getColumnInfoObservableList(null, null, null, new String[]{"TABLE"});
                    List<ColumnInfo> columnInfos = columnInfoList.stream().collect(Collectors.toList());
                    JSON.createJSONforColumnInfo(columnInfos, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), false);
                    System.out.println("refresh podataka");
                    // TODO vidjeti sta treba s table info
                    fillTableInfoTreeTableView();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addVisualizeDatabaseListener(){
        visualizeDatabaseButton.setOnAction(event -> {
            if (!blockAll){
                FXMLLoader graphLoader = new FXMLLoader();
                graphLoader.setLocation(DbGen.class.getResource("view/DatabaseVisualizer.fxml"));
                try {
                    AnchorPane databaseVisualizerAnchorPane = graphLoader.load();
                    Stage databaseVisualizerStage = new Stage();
                    databaseVisualizerStage.setScene(new Scene(databaseVisualizerAnchorPane));
                    databaseVisualizerStage.setTitle("Database Visualizer");
                    databaseVisualizerStage.initModality(Modality.APPLICATION_MODAL);
                    databaseVisualizerStage.initOwner(primaryStage);

                    DatabaseVisualizerController controller = graphLoader.getController();
                    controller.init(columnInfoList);

                    databaseVisualizerStage.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addPrepareDataListener(){
        prepareButton.setOnAction(event -> {
            if(!blockAll) {
                prepareData();
            }
        });
    }

    private String prepareData() {
        String returnValue = null;
        TreeItem<ColumnInfo> selectedColumnInfoTreeItem = columnInfoTreeTableView.getSelectionModel().getSelectedItem();
        if (selectedColumnInfoTreeItem != null) {

            selectedColumnInfoList.clear();
            // if root is not selected use data from root
            if (!selectedColumnInfoTreeItem.getValue().getIsRoot()) {
                selectedColumnInfoTreeItem = selectedColumnInfoTreeItem.getParent();
            }

            selectedColumnInfoList.addAll(selectedColumnInfoTreeItem.getChildren().stream()
                    .map(TreeItem::getValue)
                    .collect(Collectors.toList()));

            if(!selectedColumnInfoTreeItem.getValue().getTableGenerationSettings().getAllowGeneration()){
                System.out.println("Generation of data for this table is not selected!!");
                return returnValue;
            }

            String numberOfDataToGenerateString = selectedColumnInfoTreeItem.getValue().getTableGenerationSettings().getNumberOfDataToGenerate();
            int numberOfDataToGenerate = Integer.parseInt(numberOfDataToGenerateString);

            // calling generator for each of selected item
            // TODO add different collection type support
            JDBC_Repository jdbc_repository = JDBC_Repository.getInstance();


            Set<String> finishedColumn = new HashSet<>();
            for (ColumnInfo columnInfo : selectedColumnInfoList) {

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
                        if (primaryKeyList.isEmpty()) {
                            Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                            referenceMissing.setTitle("Preparation warning");
                            referenceMissing.setContentText("First generate data for reference table: " + primaryKeyColumn.getTableName());
                            referenceMissing.showAndWait();
                            return primaryKeyColumn.getTableName();
                        }

                        jdbc_repository.addCollectionToMap(hash, new ArrayList<>());
                        // pick random value
                        Random random = new Random();
                        random.setSeed(System.currentTimeMillis());
                        for (int i = 0; i < numberOfDataToGenerate; i++) {
                            Object object = primaryKeyList.get(random.nextInt(numberOfDataToGenerate));
                            jdbc_repository.insertIntoCollection(hash, object);
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
                    boolean allKeysAreForeign = true;
                    List<String> regularForeignKeysSet = new LinkedList<>();
                    for (String primaryKeyHash : columnInfo.getCompositePrimaryKeySet()) {
                        // stream through all composite key
                        ColumnInfo foreignKeyColumnInfo = columnInfoList.stream()
                                .filter(column -> column.getHash().equals(primaryKeyHash))
                                .findFirst().get();
                        if (foreignKeyColumnInfo.getIsForeignKey() && !foreignKeyColumnInfo.getIsCompositeForeignKey()) {
                            regularForeignKeysSet.add(foreignKeyColumnInfo.getHash());
                        } else if (foreignKeyColumnInfo.getIsForeignKey() && foreignKeyColumnInfo.getIsCompositeForeignKey()) {
                            regularForeignKeys = false;
                        } else if (!foreignKeyColumnInfo.getIsForeignKey()) {
                            allKeysAreForeign = false;
                        }
                    }
                    // subcase one: all composite keys are regular foreign

                    if (regularForeignKeys && allKeysAreForeign) {
                        // steps: fetch data and generate random pair of object that are not equal
                        List<List<Object>> fetchedData = new LinkedList<>();
                        for (String regularForeignKey : regularForeignKeysSet) {
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
                                if (primaryKeyList.isEmpty()) {
                                    Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                                    referenceMissing.setTitle("Preparation warning");
                                    referenceMissing.setContentText("First generate data for reference table: " + primaryKeyColumn.getTableName());
                                    referenceMissing.showAndWait();
                                    return primaryKeyColumn.getTableName();
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
                        while (objects.size() < numberOfDataToGenerate) {
                            ObjectCollection objectCollection = new ObjectCollection(size);
                            for (int i = 0; i < size; i++) {
                                objectCollection.objects[i] = fetchedData.get(i).get(random.nextInt(fetchedData.get(i).size()));
                            }
                            objects.add(objectCollection);
                        }

                        // return pairs into regular format
                        for (int i = 0; i < regularForeignKeysSet.size(); i++) {
                            final int finalI = i;
                            List<Object> objectList = objects.stream()
                                    .map(objectCollection -> objectCollection.objects[finalI])
                                    .collect(Collectors.toList());
                            jdbc_repository.addCollectionToMap(regularForeignKeysSet.get(i), objectList);
                        }
                    } else {
                        // for now
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
                    }

                    jdbc_repository.addCollectionToMap(hash, new ArrayList<>());
                    for (int i = 0; i < numberOfDataToGenerate; i++) {
                        if (columnInfo.getAutoIncrement()) {
                            jdbc_repository.insertIntoCollection(hash, "Auto generated in DB");
                        } else {
                            jdbc_repository.insertIntoCollection(hash, generator.generate());
                        }
                        finishedColumn.add(columnInfo.getHash());
                    }
                }
            }
        }
        return returnValue;
    }

    private void addPreviewDataListener(){
        previewButton.setOnAction(event -> {
            if(!blockAll && selectedColumnInfoList.size() != 0){
                transformData();

                tableView.setItems(previewObservableList);

            }
        });
    }

    private void transformData() {
        previewObservableList.clear();
        tableView.getItems().clear();
        // remaping data!!!
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
    }

    private void addGenerateDataListener(){
        generateButton.setOnAction(event -> {
            if(blockAll){
                return;
            }

            ObservableList<TreeItem<ColumnInfo>> tableInfoList = columnInfoTreeTableView.getRoot().getChildren();

            //remove tables that skips data generation
            ObservableList<TreeItem<ColumnInfo>> tableInfoListWithoutIgnored = FXCollections.observableArrayList();
            for(TreeItem<ColumnInfo> columnInfoTreeItem : tableInfoList){
                if(columnInfoTreeItem.getValue().getTableGenerationSettings().getAllowGeneration()){
                    tableInfoListWithoutIgnored.add(columnInfoTreeItem);
                }
            }

            if(tableInfoListWithoutIgnored.size() == 0){
                return;
            }

            boolean allDataIsPossibleToGenerate = true;
            Deque<String> availTableNames = new ArrayDeque<>();
            availTableNames.addAll(tableInfoListWithoutIgnored.stream()
                    .map(TreeItem::getValue)
                    .map(ColumnInfo::getTableName)
                    .collect(Collectors.toList()));
            String tableName = availTableNames.getFirst();
            while(availTableNames.size() != 0){
                long avail = tableInfoListWithoutIgnored.stream()
                        .filter(columnInfo -> columnInfo.getValue().getTableName().equals(tableName))
                        .count();

                if(avail == 0){
                    System.err.println("Not possibe to generate data!!! Missing correct table column!");
                    allDataIsPossibleToGenerate = false;
                    return;
                }


            }

            if(!allDataIsPossibleToGenerate){
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
