package application.controller;

import java.sql.SQLException;

import sun.security.pkcs11.Secmod.DbMode;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import application.DatabaseTools;
import application.MainApp;
import application.model.ConnectionInfo;
import application.model.ConnectionParametars;

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
	private TableView<ConnectionParametars> parametarsTable;
	
	@FXML
	private Button newParametarButton;
	
	@FXML
	private Button deleteParametarButton;
	
	@FXML
	private Button testConnectionButton;
	
	@FXML
	private Button retrieveMetadataButton;
	
	@FXML
	private TableColumn<ConnectionParametars, String> keyColumn;
	
	@FXML
	private TableColumn<ConnectionParametars, String> valueColumn;

	private MainApp mainApp;
	
	private ObservableList<ConnectionParametars> currentParametars;
	private ConnectionInfo currentConnectionInfo;
	
	@FXML
	private void initialize() {
		parametarsTable.setEditable(true);
		connectionChoiceBox.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					unbindConnectInfo(oldValue);
					bindConnectInfo(newValue);
					currentConnectionInfo = newValue;
				});
		keyColumn.setCellValueFactory(cellData -> cellData.getValue().getkeyProperty());
		keyColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		keyColumn.setOnEditCommit(
			    new EventHandler<CellEditEvent<ConnectionParametars, String>>() {
			        @Override
			        public void handle(CellEditEvent<ConnectionParametars, String> t) {

			            ((ConnectionParametars) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setKey(t.getNewValue());
			        }
			    }
			);
		valueColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		valueColumn.setOnEditCommit(
			    new EventHandler<CellEditEvent<ConnectionParametars, String>>() {
			        @Override
			        public void handle(CellEditEvent<ConnectionParametars, String> t) {

			            ((ConnectionParametars) t.getTableView().getItems().get(
			                t.getTablePosition().getRow())
			                ).setValue(t.getNewValue());
			        }
			    }
			);
		valueColumn.setCellValueFactory(cellData -> cellData.getValue().getValueProperty());
		
		newParametarButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override 
		    public void handle(ActionEvent e) {
		    	if(!newKey.getText().equals("") && !newValue.getText().equals("")){
		    		currentParametars.add(new ConnectionParametars(
				        	newKey.getText(),
				            newValue.getText()
				        ));
				        newKey.clear();
				        newValue.clear();
		    	}
		    }
		});
		deleteParametarButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override
		    public void handle(ActionEvent e) {
		    	ConnectionParametars parametar = parametarsTable.getSelectionModel().getSelectedItem();
		    	if(parametar != null){
		    		currentParametars.remove(parametar);
		    	} 
		    }
		});
		testConnectionButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				DatabaseTools db = new DatabaseTools(currentConnectionInfo.getConnectionString());
				try {
					db.TestConnection();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		retrieveMetadataButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DatabaseTools db = new DatabaseTools(currentConnectionInfo.getConnectionString());
				try {
					db.retrieveMetadata();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
			}
		});
	}

	private void bindConnectInfo(ConnectionInfo connectInfo) {
		currentParametars = connectInfo.getParametars();
		parametarsTable.setItems(currentParametars);
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

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		// Add observable list data to the choice box
		connectionChoiceBox.setItems(mainApp.getConnectInfoData());
		connectionChoiceBox.getSelectionModel().selectFirst();
	}
}
