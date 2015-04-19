package application.controller;

import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.model.ColumnInfo;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import java.util.HashMap;
import java.util.Map;

public class MainAppController {

    @FXML
    private TreeTableView<ColumnInfo> columnInfoTreeTableView;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<String> tableView;

    @FXML
    private Button previewButton;

    @FXML
    private Button generateButton;

    private ObservableList<ColumnInfo> columnInfoList = null;

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
    }

    private void getTableInfoData(){
        DatabaseTools dt = new DatabaseTools(JDBC_Repository.getInstance().getconnectionInfo().getConnectionString());
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
                    for (TreeItem<ColumnInfo> columnInfoTreeItem : newValue.getChildren()) {
                        tableView.getColumns().add(new TableColumn<>(columnInfoTreeItem.getValue().getColumnName()));
                    }
                }
                // in case internal element is selected, but only if it
                // has different root element then previous one
                else if (oldValue == null || !newValue.getParent().equals(oldValue.getParent())) {
                    tableView.getColumns().clear();
                    TreeItem<ColumnInfo> rootColumnInfoTreeItem = newValue.getParent();
                    for (TreeItem<ColumnInfo> columnInfoTreeItem : rootColumnInfoTreeItem.getChildren()) {
                        tableView.getColumns().add(new TableColumn<>(columnInfoTreeItem.getValue().getColumnName()));
                    }
                }
            }));
    }
}
