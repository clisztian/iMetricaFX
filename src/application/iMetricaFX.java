package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * 
 * iMetricaFX 
 * 
 * @author Christian D. Blakely (clisztian@gmail.com)
 *
 */
public class iMetricaFX extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("iMetricaMDFA.fxml"));
			Parent root = (Parent)loader.load();
			iMetricaFXController myController = loader.getController();
			myController.setStage(primaryStage);		
	        primaryStage.setTitle("iMetricaFX");
	        Scene primaryScene = new Scene(root);
	        primaryScene.getStylesheets().add("css/timeserieschart.css");
	        primaryStage.setScene(primaryScene);
	        
	        
	        myController.initiateMDFABase();
	        myController.initiateCanvas();
	        myController.setToggleGroups();
	        
	        primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
