
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameStage {

    public void setStage(Stage stage) {
 
        // Create UI elements
        Label title = new Label("Game Screen");
        Button backButton = new Button("Back to Menu");

        // Layout
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(title, backButton);

        // Scene
        Scene gameScene = new Scene(root, 700, 400);

        // Back button action (go back to menu)
        backButton.setOnAction(e -> {
            App menu = new App();
            try {
                menu.start(stage); // reload original menu
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Set new scene to stage
        stage.setScene(gameScene);
        stage.setTitle("Game Stage");
        stage.show();
    }
}