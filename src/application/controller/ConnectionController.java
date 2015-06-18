package application.controller;

import application.Constants;
import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.model.ConnectionInfo;
import application.model.ConnectionParameters;
import application.utils.JSON;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ConnectionController {
    @FXML
    private ChoiceBox<ConnectionInfo> connectionChoiceBox;

    @FXML
    private TextField saveName;

    @FXML
    private TextField hostName;

    @FXML
    private TextField port;

    @FXML
    private TextField database;

    @FXML
    private TextField newKey;

    @FXML
    private TextField newValue;

    @FXML
    private TableView<ConnectionParameters> parametersTable;

    @FXML
    private Button newParameterButton;

    @FXML
    private Button deleteParameterButton;

    @FXML
    private Button testConnectionButton;

    @FXML
    private Button retrieveMetadataButton;

    @FXML
    private TableColumn<ConnectionParameters, String> keyColumn;

    @FXML
    private TableColumn<ConnectionParameters, String> valueColumn;

    private ObservableList<ConnectionParameters> currentParameters;
    //private ConnectionInfo currentConnectionInfo;
    //TODO vidjeti da li je pametno drzati u repozitoriju

    private DbGen mainApp;

    @FXML
    private void initialize() {
        parametersTable.setEditable(true);
        connectionChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    unbindConnectInfo(oldValue);
                    bindConnectInfo(newValue);
                    JDBC_Repository.getInstance().setConnectionInfo(newValue);
                });
        keyColumn.setCellValueFactory(cellData -> cellData.getValue().keyProperty());
        keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        keyColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<ConnectionParameters, String>>() {
                    @Override
                    public void handle(CellEditEvent<ConnectionParameters, String> t) {

                        ((ConnectionParameters) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setKey(t.getNewValue());
                    }
                }
        );
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        valueColumn.setOnEditCommit(
                new EventHandler<CellEditEvent<ConnectionParameters, String>>() {
                    @Override
                    public void handle(CellEditEvent<ConnectionParameters, String> t) {

                        ((ConnectionParameters) t.getTableView().getItems().get(
                                t.getTablePosition().getRow())
                        ).setValue(t.getNewValue());
                    }
                }
        );
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        newParameterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (!newKey.getText().equals("") && !newValue.getText().equals("")) {
                    currentParameters.add(new ConnectionParameters(
                            newKey.getText(),
                            newValue.getText()
                    ));
                    newKey.clear();
                    newValue.clear();
                }
            }
        });
        deleteParameterButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                ConnectionParameters parametar = parametersTable.getSelectionModel().getSelectedItem();
                if (parametar != null) {
                    currentParameters.remove(parametar);
                }
            }
        });
        testConnectionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                ConnectionInfo connectionInfo = JDBC_Repository.getInstance().getConnectionInfo();
                DatabaseTools db = new DatabaseTools(connectionInfo.getConnectionString());
                Alert testConectionAlert;
                try {
                    String testConnectionResult = db.testConnection();
                    testConectionAlert = new Alert(AlertType.INFORMATION);
                    testConectionAlert.setTitle("Connection test");
                    testConectionAlert.setContentText(testConnectionResult);
                    testConectionAlert.showAndWait();
                } catch (SQLException e2) {
                    testConectionAlert = new Alert(AlertType.ERROR);
                    testConectionAlert.setTitle("Connection test");
                    testConectionAlert.setContentText(e2.getMessage());
                    testConectionAlert.showAndWait();
                }

            }
        });
        retrieveMetadataButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ConnectionInfo connectionInfo = JDBC_Repository.getInstance().getConnectionInfo();
                DatabaseTools db = new DatabaseTools(connectionInfo.getConnectionString());
                try {
                    db.testConnection();
                    JSON.createJSONforConnectionInfo(connectionInfo, connectionInfo.getSaveName());
                    mainApp.earlyExit = false;
                    ((Stage) retrieveMetadataButton.getScene().getWindow()).hide();
                } catch (SQLException e1) {
                    Alert retrieveMetadataAlert = new Alert(AlertType.ERROR);
                    retrieveMetadataAlert.setTitle("Connection test");
                    retrieveMetadataAlert.setContentText(e1.getMessage());
                    retrieveMetadataAlert.showAndWait();
                }
            }
        });


    }

    private void bindConnectInfo(ConnectionInfo connectInfo) {
        if (connectInfo != null) {
            currentParameters = connectInfo.getParameters();
            parametersTable.setItems(currentParameters);
            hostName.textProperty().bindBidirectional(connectInfo.hostProperty());
            port.textProperty().bindBidirectional(connectInfo.portProperty());
            database.textProperty().bindBidirectional(connectInfo.databaseProperty());
            saveName.textProperty().bindBidirectional(connectInfo.saveNameProperty());
        }

    }

    private void unbindConnectInfo(ConnectionInfo connectInfo) {
        if (connectInfo != null) {
            hostName.textProperty().unbindBidirectional(connectInfo.hostProperty());
            port.textProperty().unbindBidirectional(connectInfo.portProperty());
            database.textProperty().unbindBidirectional(connectInfo.databaseProperty());
            saveName.textProperty().unbindBidirectional(connectInfo.saveNameProperty());
        }
    }

    public void setMainApp(DbGen mainApp) {
        this.mainApp = mainApp;
        // Add observable list data to the choice box
        connectionChoiceBox.setItems(mainApp.getConnectInfoData());
        connectionChoiceBox.getSelectionModel().selectFirst();

        File f = new File(Constants.SaveLoation);
        Set<String> names = new HashSet<>(Arrays.asList(f.list()));
        names = names.stream().filter(s -> s.contains("_connection.json")).collect(Collectors.toSet());
        for (String name : names){
            ConnectionInfo connectionInfo = JSON.createJavaObjectsforConnectionInfo(name.replace("_connection.json", ""));
            connectionChoiceBox.getItems().add(connectionInfo);
        }

    }

    public void setConnectionList(){
        connectionChoiceBox.getItems().clear();
        connectionChoiceBox.setItems(mainApp.getConnectInfoData());

        File f = new File(Constants.SaveLoation);
        Set<String> names = new HashSet<>(Arrays.asList(f.list()));
        names = names.stream().filter(s -> s.contains("_connection.json")).collect(Collectors.toSet());
        for (String name : names){
            ConnectionInfo connectionInfo = JSON.createJavaObjectsforConnectionInfo(name.replace("_connection.json", ""));
            connectionChoiceBox.getItems().add(connectionInfo);
        }
    }
}
