package application;
	
import org.opencv.core.Core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			//Load FXML resource
			BorderPane root = (BorderPane)FXMLLoader.load(getClass().getResource("JFX.fxml"));
			//Create scene
			Scene scene = new Scene(root,500,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			//Configure scene
			primaryStage.setTitle("Image segmentation");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		//Load OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		//Start JFX application
		launch(args);
	}
}
