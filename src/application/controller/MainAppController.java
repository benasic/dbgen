package application.controller;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;





import com.sun.javafx.scene.traversal.SubSceneTraversalEngine;



import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import application.DatabaseTools;
import application.JDBC_Repository;
import application.DbGen;
import application.model.ColumnInfo;

public class MainAppController {
    @FXML
    private TreeTableView<ColumnInfo> columnInfoTreeTableView;
    
    @FXML
    private BorderPane mainBorderPane;
    
    private ObservableList<ColumnInfo> columnInfoList = null;
    
    private SubScene currentGenerator = null;
    
    @FXML
    private void initialize() {


    }
    
    public void init(){
        getTableInfoData();
        fillTableInfoTreeTableView();
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
            TreeItem<ColumnInfo> tempRoot = null;
            if(roots.keySet().contains(columnInfo.getTableName())){
                tempRoot = roots.get(columnInfo.getTableName());
                tempRoot.getChildren().add(new TreeItem<>(columnInfo));
            }
            else{
                ImageView tableIcon = new ImageView (new Image(getClass().getResourceAsStream("../view/tables1.png")));
                tempRoot = new TreeItem<ColumnInfo>(new ColumnInfo(columnInfo.getTableName(), "", "", ""), tableIcon);
                tempRoot.setExpanded(false);
                tempRoot.getChildren().add(new TreeItem<ColumnInfo>(columnInfo));
                roots.put(columnInfo.getTableName(), tempRoot);
            }
        }
        
        TreeItem<ColumnInfo> mainRoot = new TreeItem<ColumnInfo>(new ColumnInfo("root", "", "", ""));
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
        
        columnName.setCellValueFactory(new Callback<CellDataFeatures<ColumnInfo, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<ColumnInfo, String> p) {
                ColumnInfo columnInfo = p.getValue().getValue();
                if(columnInfo.getColumnSize().equals("")){
                    return new ReadOnlyStringWrapper(columnInfo.getTableName());
                }
                else{
                    return new ReadOnlyStringWrapper(columnInfo.getColumnName());
                }
            }
        });
        columnType.setCellValueFactory(new Callback<CellDataFeatures<ColumnInfo, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<ColumnInfo, String> p) {
                ColumnInfo columnInfo = p.getValue().getValue();
                if(columnInfo.getColumnSize().equals("")){
                    return new ReadOnlyStringWrapper("");
                }
                else{
                    return new ReadOnlyStringWrapper(columnInfo.getColumnType() + " [" + columnInfo.getColumnSize() + "]");
                }
            }
        });
        columnInfoTreeTableView.setRoot(mainRoot);
        columnInfoTreeTableView.setShowRoot(false);
        
        columnInfoTreeTableView.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                Alert testConectionAlert = new Alert(AlertType.INFORMATION);
                testConectionAlert.setTitle("Test");
                switch (newValue.getValue().getColumnType()) {
                case "VARCHAR":
                    testConectionAlert.setContentText("String type selected");
                    testConectionAlert.show();
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(DbGen.class.getResource("view/StringGenerator.fxml"));
                    AnchorPane stringGeneratorSubScene = null;
                    try {
                        stringGeneratorSubScene = (AnchorPane) loader.load();
                        //currentGenerator = new SubScene(mainBorderPane,400,400);
                        mainBorderPane.setCenter(stringGeneratorSubScene);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "INTEGER":
                    testConectionAlert.setContentText("Integer type selected");
                    testConectionAlert.show();
                    break;
                default:
                    break;
                }

        });
        
    }

}
