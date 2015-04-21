package application.controller;

import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.generator.Generator;
import application.model.ColumnInfo;
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
    private AnchorPane stringGeneratorSubScene;
    private AnchorPane integerGeneratorSubScene;
    private StringGeneratorController stringGeneratorController;
    private IntegerGeneratorController integerGeneratorController;
    private Image tableIcon;

    @FXML
    private void initialize() {
        tableIcon = new Image(DbGen.class.getResourceAsStream("resources/icons/tables1.png"));
        stringLoader.setLocation(DbGen.class.getResource("view/StringGenerator.fxml"));
        integerLoader.setLocation(DbGen.class.getResource("view/IntegerGenerator.fxml"));
        try {
            stringGeneratorSubScene = stringLoader.load();
            integerGeneratorSubScene = integerLoader.load();
            stringGeneratorController = stringLoader.getController();
            integerGeneratorController = integerLoader.getController();
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
    }

    private void getTableInfoData(){
        DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getConnectionInfo().getConnectionString());
        try {
            columnInfoList = dt.GetTables(null, null, null, new String[] {"TABLE"});
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void fillTableInfoTreeTableView(){

        Map<String, TreeItem<ColumnInfo>> roots = new HashMap<>();
        for (ColumnInfo columnInfo : columnInfoList) {
            TreeItem<ColumnInfo> tempRoot;
            if(roots.keySet().contains(columnInfo.getTableName())){
                tempRoot = roots.get(columnInfo.getTableName());
            }
            else{
                tempRoot = new TreeItem<>(new ColumnInfo(columnInfo.getTableName(), "", "", ""), new ImageView (tableIcon));
                tempRoot.setExpanded(false);
                roots.put(columnInfo.getTableName(), tempRoot);
            }
            tempRoot.getChildren().add(new TreeItem<>(columnInfo));
        }

        TreeItem<ColumnInfo> mainRoot = new TreeItem<>(new ColumnInfo("root", "", "", ""));
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
            if (columnInfo.getColumnSize().equals("")) {
                return new ReadOnlyStringWrapper(columnInfo.getTableName());
            } else {
                return new ReadOnlyStringWrapper(columnInfo.getColumnName());
            }
        });
        columnType.setCellValueFactory(p -> {
            ColumnInfo columnInfo = p.getValue().getValue();
            if(columnInfo.getColumnSize().equals("")){
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
                switch (newValue.getValue().getColumnType()) {
                    case "VARCHAR":
                        stringGeneratorController.setGenerator(newValue.getValue().getGenerator());
                        mainBorderPane.setCenter(stringGeneratorSubScene);
                        break;
                    case "INTEGER":
                        integerGeneratorController.setGenerator(newValue.getValue().getGenerator());
                        mainBorderPane.setCenter(integerGeneratorSubScene);
                        break;
                    default:
                        break;
                }
        });
    }

    private void addTableViewSynchronizationWithColumnInfo() {
        columnInfoTreeTableView.getSelectionModel().selectedItemProperty()
            .addListener(((observable, oldValue, newValue) -> {
                // in case root is selected
                if (newValue.getValue().getColumnName().equals("")) {
                    tableView.getColumns().clear();
                    tableView.getItems().clear();
                    for (TreeItem<ColumnInfo> columnInfoTreeItem : newValue.getChildren()) {
                        int column = newValue.getChildren().indexOf(columnInfoTreeItem);
                        TableColumn<ObservableList<Object>,String> objectStringTableColumn = new TableColumn<>(columnInfoTreeItem.getValue().getColumnName());
                        objectStringTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObservableList<Object>, String> param) -> new ReadOnlyStringWrapper(param.getValue().get(column).toString()));
                        tableView.getColumns().add(objectStringTableColumn);
                    }
                }
                // in case internal element is selected, but only if it
                // has different root element then previous one
                else if (oldValue == null || !newValue.getParent().equals(oldValue.getParent())) {
                    tableView.getColumns().clear();
                    tableView.getItems().clear();
                    TreeItem<ColumnInfo> rootColumnInfoTreeItem = newValue.getParent();
                    for (TreeItem<ColumnInfo> columnInfoTreeItem : rootColumnInfoTreeItem.getChildren()) {
                        int column = rootColumnInfoTreeItem.getChildren().indexOf(columnInfoTreeItem);
                        TableColumn<ObservableList<Object>,String> objectStringTableColumn = new TableColumn<>(columnInfoTreeItem.getValue().getColumnName());
                        objectStringTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<ObservableList<Object>, String> param) -> new ReadOnlyStringWrapper(param.getValue().get(column).toString()));
                        tableView.getColumns().add(objectStringTableColumn);
                    }
                }
            }));
    }

    private void addPrepareDataListener(){
        prepareButton.setOnAction(event -> {
            TreeItem<ColumnInfo> selectedColumnInfoTreeItem = columnInfoTreeTableView.getSelectionModel().getSelectedItem();
            if (selectedColumnInfoTreeItem != null) {
                selectedColumnInfoList.clear();
                // if root is selected
                if (selectedColumnInfoTreeItem.getValue().getColumnName().equals("")) {
                    selectedColumnInfoList.addAll(selectedColumnInfoTreeItem.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList()));
                }
                // if leaf is selected
                else {
                    TreeItem<ColumnInfo> columnInfoTreeItem = selectedColumnInfoTreeItem.getParent();
                    selectedColumnInfoList.addAll(columnInfoTreeItem.getChildren().stream().map(TreeItem::getValue).collect(Collectors.toList()));
                }

                // calling generator for each of selected item
                // TODO for now 100 times, must be replaced with user option for each table
                // TODO add different collection type support
                JDBC_Repository jdbc_repository = JDBC_Repository.getInstance();

                for (ColumnInfo columnInfo : selectedColumnInfoList) {
                    String hash = columnInfo.getHash();
                    Generator generator = columnInfo.getGenerator();
                    jdbc_repository.addCollectionToMap(hash, new ArrayList<>());
                    for (int i = 0; i < 100000; i++) {
                        jdbc_repository.insertIntoCollection(hash, generator.generate());
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
