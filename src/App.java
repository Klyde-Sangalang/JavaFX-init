
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Label;


public class App extends Application {
    public void start(Stage primaryStage) {
        primaryStage.setTitle("TEST");
        Label label = new Label();
        label.setText("KLYDE SO POGI");
        Scene scene = new Scene(label, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
    
}
