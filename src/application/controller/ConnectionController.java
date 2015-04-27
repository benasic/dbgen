package application.controller;

import application.DatabaseTools;
import application.DbGen;
import application.JDBC_Repository;
import application.model.ConnectionInfo;
import application.model.ConnectionParameters;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ConnectionController {
    @FXML
    private ChoiceBox<ConnectionInfo> connectionChoiceBox;

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

    @FXML
    private void initialize() {
        parametersTable.setEditable(true);
        connectionChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    unbindConnectInfo(oldValue);
                    bindConnectInfo(newValue);
                    JDBC_Repository.getInstance().setConnectionInfo(newValue);
                });
        keyColumn.setCellValueFactory(cellData -> cellData.getValue().getKeyProperty());
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
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getValueProperty());

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
                    ((Stage) retrieveMetadataButton.getScene().getWindow()).close();
                    ;
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
        currentParameters = connectInfo.getParameters();
        parametersTable.setItems(currentParameters);
        hostName.textProperty().bindBidirectional(connectInfo.getHostProperty());
        port.textProperty().bindBidirectional(connectInfo.getPortProperty());
        database.textProperty().bindBidirectional(connectInfo.getDatabaseProperty());

    }

    private void unbindConnectInfo(ConnectionInfo connectInfo) {
        if (connectInfo != null) {
            hostName.textProperty().unbindBidirectional(connectInfo.getHostProperty());
            port.textProperty().unbindBidirectional(connectInfo.getPortProperty());
            database.textProperty().unbindBidirectional(connectInfo.getDatabaseProperty());
        }
    }

    public void setMainApp(DbGen mainApp) {
        // Add observable list data to the choice box
        connectionChoiceBox.setItems(mainApp.getConnectInfoData());
        connectionChoiceBox.getSelectionModel().selectFirst();
    }
}
