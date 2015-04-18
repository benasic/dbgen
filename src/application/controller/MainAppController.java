package application.controller;

import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.model.ColumnInfo;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MainAppController {
    @FXML
    private TreeTableView<ColumnInfo> columnInfoTreeTableView;
    
    @FXML
    private BorderPane mainBorderPane;
    
    private ObservableList<ColumnInfo> columnInfoList = null;

    
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
            @Override
            public ObservableValue<String> call(CellDataFeatures<ColumnInfo, String> p) {
                ColumnInfo columnInfo = p.getValue().getValue();
                if (columnInfo.getColumnSize().equals("")) {
                    return new ReadOnlyStringWrapper(columnInfo.getTableName());
                } else {
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
                FXMLLoader loader = new FXMLLoader();
                AnchorPane stringGeneratorSubScene = null;
                switch (newValue.getValue().getColumnType()) {
                    case "VARCHAR":
                        loader.setLocation(DbGen.class.getResource("view/StringGenerator.fxml"));
                        try {
                            stringGeneratorSubScene = loader.load();
                            StringGeneratorController stringGeneratorController = loader.getController();
                            stringGeneratorController.setGenerator(newValue.getValue().getGenerator());
                            mainBorderPane.setCenter(stringGeneratorSubScene);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case "INTEGER":
                        loader.setLocation(DbGen.class.getResource("view/IntegerGenerator.fxml"));
                        try {
                            stringGeneratorSubScene = loader.load();
                            IntegerGeneratorController integerGeneratorController = loader.getController();
                            integerGeneratorController.setGenerator(newValue.getValue().getGenerator());
                            mainBorderPane.setCenter(stringGeneratorSubScene);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }

        });
        
    }

}
