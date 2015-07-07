package application.controller;

import application.Constants;
import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.generator.BooleanGenerator;
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
import java.time.Duration;
import java.time.Instant;
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
    private Button previewButton;

    @FXML
    private Button generateButton;

    @FXML
    private Button visualizeDatabaseButton;

    @FXML
    private Button visualizeTablesButton;

    @FXML
    private ProgressBar progressBar;

    private Stage primaryStage;
    private Stage connectionStage;

    private ObservableList<ColumnInfo> columnInfoList = null;
    private ObservableList<ColumnInfo> tableInfoList = FXCollections.observableArrayList();
    private List<ColumnInfo> selectedColumnInfoList = new ArrayList<>();
    private ObservableList<ObservableList<Object>> previewObservableList = FXCollections.observableArrayList();

    private final FXMLLoader stringLoader = new FXMLLoader();
    private final FXMLLoader integerLoader = new FXMLLoader();
    private final FXMLLoader dateLoader = new FXMLLoader();
    private final FXMLLoader booleanLoader = new FXMLLoader();
    private final FXMLLoader binaryLoader = new FXMLLoader();
    private final FXMLLoader tableSettingsLoader = new FXMLLoader();
    private final FXMLLoader databaseSettingsLoader = new FXMLLoader();
    private AnchorPane stringGeneratorSubScene;
    private AnchorPane numberGeneratorSubScene;
    private AnchorPane dateGeneratorSubScene;
    private AnchorPane booleanGeneratorSubScene;
    private AnchorPane binaryGeneratorSubScene;
    private AnchorPane tableSettingsSubScene;
    private ScrollPane databaseSettingsSubScene;
    private StringGeneratorController stringGeneratorController;
    private NumberGeneratorController numberGeneratorController;
    private DateGeneratorController dateGeneratorController;
    private BooleanController booleanGeneratorController;
    private BinaryGeneratorController binaryGeneratorController;
    private TableGenerationSettingsController tableGenerationSettingsController;
    private DatabaseSettingsController databaseSettingsController;

    private ConnectionController connectionController;

    private Image tableIcon;
    private Image primaryKeyIcon;
    private Image foreignKeyIcon;
    private Image primaryForeignKeyIcon;

    private String lastGeneratorType;
    private Generator lastActiveGenerator;
    private TableGenerationSettings lastSelectedTableGenerationSettings;

    public boolean blockAll = false;
    private boolean noLoad = false;

    @FXML
    private void initialize() {

        tableIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/table.png"));
        primaryKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/primaryKey.png"));
        foreignKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/foreignKey.png"));
        primaryForeignKeyIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/primaryForeignKey.png"));

        stringLoader.setLocation(DbGen.class.getResource("view/StringGenerator.fxml"));
        integerLoader.setLocation(DbGen.class.getResource("view/NumberGenerator.fxml"));
        dateLoader.setLocation(DbGen.class.getResource("view/DateGenerator.fxml"));
        booleanLoader.setLocation(DbGen.class.getResource("view/BooleanGenerator.fxml"));
        binaryLoader.setLocation(DbGen.class.getResource("view/BinaryGenerator.fxml"));
        tableSettingsLoader.setLocation(DbGen.class.getResource("view/TableGenerationSettings.fxml"));
        databaseSettingsLoader.setLocation(DbGen.class.getResource("view/DatabaseSettings.fxml"));

        try {
            stringGeneratorSubScene = stringLoader.load();
            numberGeneratorSubScene = integerLoader.load();
            dateGeneratorSubScene = dateLoader.load();
            booleanGeneratorSubScene = booleanLoader.load();
            binaryGeneratorSubScene = binaryLoader.load();
            tableSettingsSubScene = tableSettingsLoader.load();
            databaseSettingsSubScene = databaseSettingsLoader.load();
            stringGeneratorController = stringLoader.getController();
            numberGeneratorController = integerLoader.getController();
            numberGeneratorController.setMainController(this);
            dateGeneratorController = dateLoader.getController();
            booleanGeneratorController = booleanLoader.getController();
            binaryGeneratorController = binaryLoader.getController();
            tableGenerationSettingsController = tableSettingsLoader.getController();
            databaseSettingsController = databaseSettingsLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(Stage primaryStage, Stage connectionStage, ConnectionController connectionController){
        this.primaryStage = primaryStage;
        this.connectionStage = connectionStage;
        this.connectionController = connectionController;
        getTableInfoData();
        fillTableInfoTreeTableView();
        setupDatabaseSettings();
        addTreeTableViewListeners();
        addTableViewSynchronizationWithColumnInfo();
        addPreviewDataListener();
        addGenerateDataListener();
        addSaveProjectListener();
        addLoadProjectListener();
        addRefreshMetadataListener();
        addVisualizeDatabaseListener();
        addVisualizeTablesCapacity();

        columnInfoTreeTableView.getSelectionModel().clearSelection();
        columnInfoTreeTableView.getSelectionModel().select(0);
    }

    private void getTableInfoData(){

        File f = new File(Constants.SaveLocation);
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

        TreeItem<ColumnInfo> databaseRoot = new TreeItem<>();
        ColumnInfo rootColumnInfo = new ColumnInfo(JDBC_Repository.getInstance().getConnectionInfo().getDatabase(), false);
        rootColumnInfo.setDatabaseRootFlag(true);
        databaseRoot.setValue(rootColumnInfo);
        databaseRoot.setExpanded(true);
        for (TreeItem<ColumnInfo> root : roots.values()) {
            databaseRoot.getChildren().add(root);
        }

        TreeTableColumn<ColumnInfo, String> columnName = new TreeTableColumn<>("Column Name");
        TreeTableColumn<ColumnInfo, String> columnType = new TreeTableColumn<>("Column Type");
        columnName.setPrefWidth(250);
        columnType.setPrefWidth(250);
        columnInfoTreeTableView.getColumns().clear();
        columnInfoTreeTableView.getColumns().add(columnName);
        columnInfoTreeTableView.getColumns().add(columnType);

        columnName.setCellValueFactory(p -> {
            ColumnInfo columnInfo = p.getValue().getValue();
            if (columnInfo.getIsTableRoot() || columnInfo.getDatabaseRootFlag()) {
                // in case of database root this is database name
                return new ReadOnlyStringWrapper(columnInfo.getTableName());
            } else {
                return new ReadOnlyStringWrapper(columnInfo.getColumnName());
            }
        });
        columnType.setCellValueFactory(p -> {
            ColumnInfo columnInfo = p.getValue().getValue();
            if(columnInfo.getIsTableRoot() || columnInfo.getDatabaseRootFlag()){
                return new ReadOnlyStringWrapper("");
            }
            else{
                return new ReadOnlyStringWrapper(columnInfo.getColumnType() + " [" + columnInfo.getColumnSize() + "]");
            }
        });
        columnInfoTreeTableView.setRoot(databaseRoot);
        columnInfoTreeTableView.setShowRoot(true);

        columnInfoTreeTableView.getSelectionModel().clearSelection();
        columnInfoTreeTableView.getSelectionModel().select(0);

        databaseSettingsController.init(tableInfoList);
    }

    private void setupDatabaseSettings(){

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
                if (newValue != null) {
                    if (newValue.getValue().getIsTableRoot()) {
                        if (lastSelectedTableGenerationSettings != null) {
                            tableGenerationSettingsController.unbindValues(lastSelectedTableGenerationSettings);
                        }
                        lastSelectedTableGenerationSettings = newValue.getValue().getTableGenerationSettings();
                        tableGenerationSettingsController.bindValues(lastSelectedTableGenerationSettings);
                        mainBorderPane.setCenter(tableSettingsSubScene);
                        return;
                    }
                    if (newValue.getValue().getDatabaseRootFlag()) {
                        mainBorderPane.setCenter(databaseSettingsSubScene);
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
                            case "BIT":
                                booleanGeneratorController.unbindValues(lastActiveGenerator);
                                lastActiveGenerator = null;
                                break;
                            case "BINARY":
                                binaryGeneratorController.unbindValues(lastActiveGenerator);
                                lastActiveGenerator = null;
                                break;
                            default:
                                break;
                        }
                    }

                    switch (type) {
                        case "CHAR":
                        case "NCHAR":
                        case "VARCHAR":
                        case "NVARCHAR":
                        case "LONGVARCHAR":
                        case "LONGNVARCHAR":
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
                            break;
                        case "BIT":
                            lastGeneratorType = "BIT";
                            lastActiveGenerator = newValue.getValue().getGenerator();
                            booleanGeneratorController.bindValues(newValue.getValue().getGenerator());
                            mainBorderPane.setCenter(booleanGeneratorSubScene);
                            break;
                        case "BINARY":
                        case "VARBINARY":
                        case "LONGVARBINARY":
                        case "BLOB":
                            lastGeneratorType = "BINARY";
                            lastActiveGenerator = newValue.getValue().getGenerator();
                            binaryGeneratorController.bindValues(newValue.getValue().getGenerator(), type);
                            mainBorderPane.setCenter(binaryGeneratorSubScene);
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

                    if (newValue.getValue().getDatabaseRootFlag()) {
                        tableView.getItems().clear();
                        tableView.getColumns().clear();
                        return;
                    }
                    // in case table root is selected
                    if ((oldValue.getValue().getDatabaseRootFlag() && newValue.getValue().getIsTableRoot())
                            || (newValue.getValue().getIsTableRoot() && !oldValue.getParent().equals(newValue))) {
                        tableView.getItems().clear();
                        tableView.getColumns().clear();

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
                    else if (oldValue.getValue().getDatabaseRootFlag()
                            || (!oldValue.getParent().equals(newValue.getParent()) && !newValue.getValue().getIsTableRoot() && !newValue.getParent().equals(oldValue))) {
                        tableView.getItems().clear();
                        tableView.getColumns().clear();


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
            if (!blockAll && columnInfoList != null && !columnInfoList.isEmpty()) {
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
                noLoad = false;
                connectionController.setConnectionList();
                connectionStage.showAndWait();
                if(!noLoad){
                    getTableInfoData();
                    fillTableInfoTreeTableView();
                }
            }
        });
        connectionStage.setOnCloseRequest(event -> {
            noLoad = true;
        });
    }

    private void addRefreshMetadataListener(){
        refreshMetadata.setOnAction(event -> {
            if (!blockAll) {
                DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
                try {
                    //column info part
                    ObservableList<ColumnInfo> newColumnInfoList = dt.getColumnInfoObservableList(null, null, null, new String[]{"TABLE"});
                    // get set of existing column info hash values
                    Set<String> oldColumnInfoHashSet = columnInfoList.stream()
                            .map(ColumnInfo::getHash)
                            .collect(Collectors.toSet());
                    //add new columns
                    for (ColumnInfo columnInfo : newColumnInfoList) {
                        if (!oldColumnInfoHashSet.contains(columnInfo.getHash())) {
                            columnInfoList.add(columnInfo);
                        }
                    }

                    // get set of new column info hash values
                    Set<String> newColumnInfoHashSet = newColumnInfoList.stream()
                            .map(ColumnInfo::getHash)
                            .collect(Collectors.toSet());
                    // remove unused values
                    List<ColumnInfo> filteredColumnInfoList = columnInfoList.stream().filter(columnInfo -> newColumnInfoHashSet
                            .contains(columnInfo.getHash()))
                            .collect(Collectors.toList());
                    columnInfoList = FXCollections.observableList(filteredColumnInfoList);

                    //JSON.createJSONforColumnInfo(filteredColumnInfoList, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), false);

                    // table info part
                    // get set of existing tableInfo names
                    Set<String> oldTableInfoNames = tableInfoList.stream()
                            .map(ColumnInfo::getTableName)
                            .collect(Collectors.toSet());
                    // add new values
                    for (ColumnInfo columnInfo : newColumnInfoList) {
                        if (!oldTableInfoNames.contains(columnInfo.getTableName())) {
                            oldTableInfoNames.add(columnInfo.getTableName());
                            tableInfoList.add(new ColumnInfo(columnInfo.getTableName(), true));
                        }
                    }
                    // remove old values
                    // get set of new values
                    Set<String> newTableInfoNames = newColumnInfoList.stream()
                            .map(ColumnInfo::getTableName)
                            .collect(Collectors.toSet());
                    // remove unused values
                    List<ColumnInfo> filteredTableInfoList = tableInfoList.stream().filter(columnInfo -> newTableInfoNames
                            .contains(columnInfo.getTableName()))
                            .collect(Collectors.toList());
                    tableInfoList = FXCollections.observableList(filteredTableInfoList);

                    //JSON.createJSONforColumnInfo(filteredTableInfoList, JDBC_Repository.getInstance().getConnectionInfo().getSaveName(), true);

                    System.out.println("refresh of data in progress");
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

    private void addVisualizeTablesCapacity(){
        visualizeTablesButton.setOnAction(event -> {
            if(!blockAll){
                FXMLLoader chartLoader = new FXMLLoader();
                chartLoader.setLocation(DbGen.class.getResource("view/TableVisualizer.fxml"));
                DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
                try {
                    Map<String, Integer> rowCount = dt.getRowCount();

                    ScrollPane tableVisualizerScrollPane = chartLoader.load();
                    Stage tableVisualizerStage = new Stage();
                    tableVisualizerStage.setScene(new Scene(tableVisualizerScrollPane));
                    tableVisualizerStage.setTitle("Tables usage visualizer");
                    tableVisualizerStage.initModality(Modality.APPLICATION_MODAL);
                    tableVisualizerStage.initOwner(primaryStage);

                    TableVisualizerController controller = chartLoader.getController();


                    tableVisualizerStage.show();
                    controller.init(rowCount);
                }catch (SQLException | IOException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private String prepareData(int numberOfDataToGenerate, boolean preview) {
        String returnValue = null;
        if (selectedColumnInfoList.size() != 0) {

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

                        // removing duplicate values
                        primaryKeyList = primaryKeyList.stream().collect(Collectors.toSet())
                                .stream().collect(Collectors.toList());

                        // break generation of data if reference are not satisfied
                        if (primaryKeyList.isEmpty() && !preview && !columnInfo.getNullable()) {
                            return primaryKeyColumn.getTableName();
                        }

                        jdbc_repository.addCollectionToMap(hash, new ArrayList<>());
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
                            jdbc_repository.insertIntoCollection(hash, object);
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
                        ColumnInfo foreignKeyColumnInfo = columnInfoList.stream()
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
                            ColumnInfo primaryKeyColumn = columnInfoList.stream().filter(column -> column.getHash().equals(primaryKeyHash)).findFirst().get();
                            List<ColumnInfo> columnInfoList = new ArrayList<>();
                            columnInfoList.add(primaryKeyColumn);
                            DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
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
                            jdbc_repository.addCollectionToMap(regularForeignKeysList.get(i), objectList);
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
                            jdbc_repository.addCollectionToMap(regularKeysList.get(i).getHash(), objectList);
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
                        jdbc_repository.addCollectionToMap(hash, new ArrayList<>(uniqueObjects));
                        finishedColumn.add(columnInfo.getHash());
                        continue;
                    }

                    // unique key
                    if(!columnInfo.getAutoIncrement() && !columnInfo.getIsPrimaryKey() && columnInfo.getIsUniqueKey() && !columnInfo.getIsCompositeUniqueKey()){

                        List<ColumnInfo> columnInfoList = new ArrayList<>();
                        columnInfoList.add(columnInfo);
                        DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
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

                        jdbc_repository.addCollectionToMap(hash, new ArrayList<>(uniqueObjects));
                        finishedColumn.add(columnInfo.getHash());
                        System.out.println("Unique column generated " + columnInfo.getColumnName() + " " + columnInfo.getTableName());
                        continue;
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
                    jdbc_repository.getConnectionInfo();
                }
            }
        }
        return returnValue;
    }

    private void addPreviewDataListener(){
        previewButton.setOnAction(event -> {
            if(!blockAll){

                selectedColumnInfoList.clear();
                TreeItem<ColumnInfo> selectedColumnInfoTreeItem = columnInfoTreeTableView.getSelectionModel().getSelectedItem();

                if(selectedColumnInfoTreeItem.getValue().getDatabaseRootFlag()){
                    return;
                }

                // if root is not selected use data from root
                if (!selectedColumnInfoTreeItem.getValue().getIsTableRoot()) {
                    selectedColumnInfoTreeItem = selectedColumnInfoTreeItem.getParent();
                }

                if(!selectedColumnInfoTreeItem.getValue().getTableGenerationSettings().getAllowGeneration()) {
                    Alert generationNotAllowed = new Alert(Alert.AlertType.WARNING);
                    generationNotAllowed.setTitle("Preparation warning");
                    generationNotAllowed.setContentText("Generation of data for this table is not selected!!");
                    generationNotAllowed.show();
                    return;
                }

                selectedColumnInfoList.addAll(selectedColumnInfoTreeItem.getChildren().stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList()));

                prepareData(100, true);
                transformData();

                tableView.setItems(previewObservableList);

            }
        });
    }

    private void transformData() {
        previewObservableList.clear();
        tableView.getItems().clear();
        // remapping data!!!
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
            if (blockAll) {
                return;
            }

            tableView.getItems().clear();
            tableView.getColumns().clear();

            Instant start = Instant.now();


            ObservableList<TreeItem<ColumnInfo>> tableInfoList = columnInfoTreeTableView.getRoot().getChildren();

            //remove tables that skips data generation
            ObservableList<TreeItem<ColumnInfo>> tableInfoListWithoutIgnored = FXCollections.observableArrayList();
            for (TreeItem<ColumnInfo> columnInfoTreeItem : tableInfoList) {
                if (columnInfoTreeItem.getValue().getTableGenerationSettings().getAllowGeneration()) {
                    tableInfoListWithoutIgnored.add(columnInfoTreeItem);
                }
            }

            if (tableInfoListWithoutIgnored.size() == 0) {
                Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                referenceMissing.setTitle("Preparation warning");
                referenceMissing.setContentText("Not possible to generate data!!!\nNone of the table is not selected to generate!");
                referenceMissing.show();
                return;
            }

            Deque<String> availTableNames = new ArrayDeque<>();
            availTableNames.addAll(tableInfoListWithoutIgnored.stream()
                    .map(TreeItem::getValue)
                    .map(ColumnInfo::getTableName)
                    .collect(Collectors.toList()));


            // this block checks are all reference available
            List<String> availableHashes = columnInfoList.stream()
                    .filter(columnInfo -> availTableNames.contains(columnInfo.getTableName()))
                    .map(ColumnInfo::getHash)
                    .collect(Collectors.toList());
            boolean generationPossible = true;
            Set<String> missingTables = new HashSet<>();
            DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
            Map<String, Integer> rowCount = new HashMap<>();

            try {
                rowCount = dt.getRowCount();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            for(String hash : availableHashes){
                if(DatabaseTools.foreignKeyMap.containsKey(hash)){
                    ForeignKey foreignKey = DatabaseTools.foreignKeyMap.get(hash);
                    String primaryKeyTableName = foreignKey.getPrimaryKey().getTableName();
                    System.out.println(primaryKeyTableName);
                    if(!availTableNames.contains(primaryKeyTableName) && rowCount.get(primaryKeyTableName) == 0 ){
                        generationPossible = false;
                        missingTables.add(primaryKeyTableName);
                    }
                }
            }

            if(!generationPossible){
                StringBuilder missingNames = new StringBuilder();
                missingTables.forEach(name -> {
                    missingNames.append(name);
                    missingNames.append(' ');
                });
                Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                referenceMissing.setTitle("Preparation warning");
                referenceMissing.setContentText("Not possible to generate data!!!\n" +
                      "Missing tables: " + missingNames.toString());
                referenceMissing.show();
                return;
            }
            ///////

            Integer tableCount = availTableNames.size();
            progressBar.setProgress(0);

            List<String> previousHashes = new ArrayList<>();

            boolean getNextValueFromTableName = true;
            String resultOfOperation = null;
            while (availTableNames.size() != 0) {
                // final is necessary because value is used in lambda
                final String tableName;
                if (getNextValueFromTableName) {
                    tableName = availTableNames.getFirst();
                } else {
                    tableName = resultOfOperation;
                }
                long avail = tableInfoListWithoutIgnored.stream()
                        .filter(columnInfo -> columnInfo.getValue().getTableName().equals(tableName))
                        .count();

                if (avail == 0) {
                    Alert referenceMissing = new Alert(Alert.AlertType.WARNING);
                    referenceMissing.setTitle("Preparation warning");
                    referenceMissing.setContentText("Not possible to generate data!!!\nMissing table in selected list " + tableName);
                    referenceMissing.show();
                    return;
                }

                TreeItem<ColumnInfo> currentTableInfo = tableInfoListWithoutIgnored.stream()
                        .filter(columnInfo -> columnInfo.getValue().getTableName().equals(tableName))
                        .findFirst().get();
                selectedColumnInfoList.clear();

                if (!previousHashes.isEmpty()) {
                    // rewriting content of collection so that garbage collection can collect old one
                    previousHashes.stream().forEach(s ->
                                    JDBC_Repository.getInstance().addCollectionToMap(s, new ArrayList<>())
                    );
                    //System.gc();
                }

                previousHashes.clear();
                previousHashes = currentTableInfo.getChildren().stream()
                        .map(TreeItem::getValue)
                        .map(ColumnInfo::getHash)
                        .collect(Collectors.toList());

                selectedColumnInfoList.addAll(currentTableInfo.getChildren().stream()
                        .map(TreeItem::getValue)
                        .collect(Collectors.toList()));

                String numberOfDataToGenerateString = currentTableInfo.getValue().getTableGenerationSettings().getNumberOfDataToGenerateString();
                int numberOfDataToGenerate = Integer.parseInt(numberOfDataToGenerateString);

                String result = prepareData(numberOfDataToGenerate, false);
                System.out.println("preparation done");
                // if previous operation is successful
                if (result == null) {
                    availTableNames.remove(tableName);
                    getNextValueFromTableName = true;

                    // generate data into database
                    transformData();
                    System.out.println("transform done");
                    try {
                        System.out.println("Filling table: " + tableName);
                        System.out.println("Remaining table " + availTableNames.size());
                        dt.generateData(previewObservableList, selectedColumnInfoList);
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                        //Alert alert = new Alert(Alert.AlertType.ERROR);
                        //alert.setContentText(e.getMessage());
                        //alert.showAndWait();
                    }

                } else {
                    System.out.println("First resolving dependency for " + result);
                    resultOfOperation = result;
                    getNextValueFromTableName = false;
                    // TODO remove first value to last place, optionally
                }

                progressBar.setProgress(tableCount.doubleValue() / availTableNames.size());
            }

            Instant end = Instant.now();
            System.out.println("Final time: " + Duration.between(start, end));

            progressBar.setProgress(1);

            int index = columnInfoTreeTableView.getSelectionModel().getSelectedIndex();
            columnInfoTreeTableView.getSelectionModel().clearSelection();
            columnInfoTreeTableView.getSelectionModel().select(index);

        });
    }
}
