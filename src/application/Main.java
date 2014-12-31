package application;

import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
//		ConnectionString connectionString = new ConnectionString(JDBC_Constants.Name.POSTGRES, "localhost",
//				JDBC_Constants.Port.POSTGRES, "test");
//		connectionString.addCredential("filip", "filip");
//		DatabaseTools db = new DatabaseTools(connectionString.toString());
//		try {
//			db.TestConnection();
//		} catch (SQLException e) {
//			System.out.println(e.getMessage());
//		}

	}
}
