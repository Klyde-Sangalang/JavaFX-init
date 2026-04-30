
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext; 
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class VBoxDemo extends Application {
	
	public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
    	StackPane root = new StackPane();
   
    	VBox vbox = new VBox();
    	
    	Canvas canvas = new Canvas(350,400);
    	GraphicsContext gc = canvas.getGraphicsContext2D();
    	
    	Image bg = new Image(getClass().getResource("background.jpg").toExternalForm());
    	gc.drawImage(bg, 0, 0);
    	
    	vbox.setAlignment(Pos.CENTER);
    	vbox.setSpacing(8);
    	
    	Button b1 = new Button("New Game");
    	Button b2 = new Button("Load Game");
    	
    	vbox.getChildren().add(b1);
    	vbox.getChildren().add(b2);
    	
    	root.getChildren().addAll(canvas,vbox);
    	
    	Scene scene = new Scene(root,350,400);
    	stage.setScene(scene);
        stage.setTitle("GUI Example");
        stage.show();
    }
}