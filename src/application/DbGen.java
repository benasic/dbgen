package application;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import application.controller.ConnectionController;
import application.controller.MainAppController;
import application.model.ConnectionInfo;

public class DbGen extends Application {

    private Stage primaryStage;
    private Stage connectionStage;
    private MainAppController mainAppController = null;
    private AnchorPane mainAppLayout;
    private ObservableList<ConnectionInfo> connectionInfoData = FXCollections.observableArrayList();
    
    private boolean earlyExit = false;

    public DbGen() {
        generateData();
    }

    private void generateData() {
        ConnectionInfo postgres = new ConnectionInfo("Postgres", JDBC_Constants.Name.POSTGRES, "localhost",
                JDBC_Constants.Port.POSTGRES, "test");
        postgres.addParametar("user", "filip");
        postgres.addParametar("password", "filip");
        connectionInfoData.add(postgres);
        ConnectionInfo sqlserver = new ConnectionInfo("SQLServer", JDBC_Constants.Name.SQLSERVER, "localhost",
                JDBC_Constants.Port.SQLSERVER, "test");
        sqlserver.addParametar("user", "filip");
        sqlserver.addParametar("password", "filip");
        connectionInfoData.add(sqlserver);
    }

    public ObservableList<ConnectionInfo> getConnectInfoData() {
        return connectionInfoData;
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("DbGen");
            this.primaryStage.setMaximized(true);
            this.primaryStage.setScene(initRootLayout());
            this.primaryStage.show();

            connectionStage = new Stage();
            connectionStage.setTitle("Connection setup");
            connectionStage.initModality(Modality.APPLICATION_MODAL);
            connectionStage.initOwner(primaryStage);
            showConnectionSetup();
            connectionStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    System.out.println("Program exit");
                    earlyExit = true;
                    Platform.exit();
                }
            });
            connectionStage.showAndWait();
            
            
            if(!earlyExit){
                mainAppController.init();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Initializes the root layout.
     * 
     * @throws IOException
     */
    public Scene initRootLayout() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(DbGen.class.getResource("view/MainAppLayout.fxml"));
        mainAppLayout = (AnchorPane) loader.load();
        mainAppController = loader.getController();
        return new Scene(mainAppLayout);
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showConnectionSetup() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(DbGen.class.getResource("view/ConnectSetup.fxml"));
            AnchorPane connectionSetup = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            AnchorPane.setTopAnchor(connectionSetup, 10.0);
            AnchorPane.setBottomAnchor(connectionSetup, 15.0);
            connectionStage.setScene(new Scene(connectionSetup));

            ConnectionController controller = loader.getController();
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
