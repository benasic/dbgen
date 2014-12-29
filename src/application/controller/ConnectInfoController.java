package application.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import application.MainApp;
import application.model.ConnectInfo;

public class ConnectInfoController {
	@FXML
	private ChoiceBox<ConnectInfo> connectionChoiceBox;

	@FXML
	private TextField hostName;

	private MainApp mainApp;

	@FXML
	private void initialize() {
		connectionChoiceBox.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> {
					unbindHostName(oldValue);
					bindHostName(newValue);
				});
	}

	private void bindHostName(ConnectInfo connectInfo) {
		hostName.textProperty().bindBidirectional(connectInfo.getHostProperty());
	}

	private void unbindHostName(ConnectInfo connectInfo) {
		if (connectInfo != null) {
			hostName.textProperty().unbindBidirectional(connectInfo.getHostProperty());
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		// Add observable list data to the choice box
		connectionChoiceBox.setItems(mainApp.getConnectInfoData());
		connectionChoiceBox.getSelectionModel().selectFirst();
	}
}
