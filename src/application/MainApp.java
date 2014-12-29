package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import application.controller.ConnectInfoController;
import application.model.ConnectInfo;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private ObservableList<ConnectInfo> connectInfoData = FXCollections.observableArrayList();
	
	public MainApp() {
		generateData();
	}

	private void generateData() {
		connectInfoData.add(new ConnectInfo("Postgres", "prva"));
		connectInfoData.add(new ConnectInfo("test", "druga"));
	}
	
	public ObservableList<ConnectInfo> getConnectInfoData() {
        return connectInfoData;
    }

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("DbGen");

		initRootLayout();

		showConnectionSetup();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showConnectionSetup() {
		try {
			// Load person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/ConnectSetup.fxml"));
			AnchorPane connectionSetup = (AnchorPane) loader.load();

			// Set person overview into the center of root layout.
			rootLayout.setCenter(connectionSetup);
			
			ConnectInfoController controller = loader.getController();
	        controller.setMainApp(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	
}
