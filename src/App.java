import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import components.MenuButton;

public class App extends Application {
    public void start(Stage primaryStage) {
        Pane mainLayout = new Pane();
        Scene scene1 = new Scene(mainLayout, 960, 540);

        Label title = new Label("Space Shooter");
        title.getStyleClass().add("title");
        title.setLayoutX(380);
        title.setLayoutY(50);

        // Register the custom font globally
        Font.loadFont("file:assets/fonts/Jersey10-Regular.ttf", 36);

        // Apply the font explicitly to the title label
        title.setFont(Font.font("Jersey10", 36));

        Image backgroundImage = new Image("file:assets/backgrounds/scene1.gif");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setRotate(270);
        backgroundView.setLayoutX(210);
        backgroundView.setLayoutY(-210);

        // Buttons
        ImageView startButton = MenuButton.create("file:assets/buttons/ICONS - 25.png", 240, 170, () -> {
            GameStage gameStage = new GameStage();
            gameStage.setStage(primaryStage);
        });

        ImageView aboutButton = MenuButton.create("file:assets/buttons/ICONS - 36.png", 240, 170, () -> {
             Stage aboutStage = new Stage();

        VBox aboutContent = new VBox(15);
        aboutContent.setAlignment(Pos.CENTER);
        aboutContent.setStyle(
            "-fx-background-color: linear-gradient(to bottom,#111827,#1e293b);" +
            "-fx-padding: 30;"
        );

        Label aboutTitle = new Label("ABOUT THE GAME");
        aboutTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #38bdf8;"
        );

        Label description = new Label(
            "A Space Shooter is a Java-coded game where players\n" +
            "fire ammunition, eliminate enemies, and defeat bosses.\n" +
            "Each enemy defeated rewards the player with points."
        );

        description.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: white;" +
            "-fx-background-color: #1e3a5f;" +
            "-fx-padding: 15;" +
            "-fx-background-radius: 10;"
        );

        Label membersTitle = new Label("PROJECT MEMBERS");
        membersTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #facc15;"
        );

        VBox membersBox = new VBox(5);
        membersBox.setAlignment(Pos.CENTER);
        membersBox.setStyle(
            "-fx-background-color: #0f172a;" +
            "-fx-padding:15;" +
            "-fx-background-radius:10;"
        );

        membersBox.getChildren().addAll(
            new Label("• Marlenne Angella Fortin"),
            new Label("• Ivan Ray V. Isanan"),
            new Label("• Margarette Jorge"),
            new Label("• Keirsten David"),
            new Label("• Lorie Jane O. Mahinay"),
            new Label("• Daena Alenrae D. Amata")
        );

        // Make member names white
        membersBox.getChildren().forEach(node ->
            ((Label)node).setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;"
            )
        );

        aboutContent.getChildren().addAll(
            title,
            description,
            membersTitle,
            membersBox
        );

        Scene aboutScene = new Scene(aboutContent, 500, 450);

        aboutStage.setScene(aboutScene);
        aboutStage.setTitle("About");
        aboutStage.show();
        });

        ImageView exitButton = MenuButton.create("file:assets/buttons/ICONS - 30.png", 240, 170, () -> {
            Platform.exit();
        });

        VBox centerButtons = new VBox(-100);
        centerButtons.setLayoutX((scene1.getWidth() - 240) / 2);
        centerButtons.setLayoutY(200);
        centerButtons.setStyle("-fx-padding: 0; -fx-alignment: center; -fx-width: 240; -fx-height: 170;");
        centerButtons.getChildren().addAll(startButton, aboutButton, exitButton);

        ImageView instructionsButton = MenuButton.create("file:assets/buttons/Instructions .png", 50, 50, () -> {
            Stage instructionsStage = new Stage();
            VBox instructionsContent = new VBox(10);
            instructionsContent.setAlignment(Pos.CENTER);
            instructionsContent.getChildren().addAll(
                new Label("How to Play"),
                new Label("1. Use arrow keys to move."),
                new Label("2. Press space to shoot."),
                new Label("3. Avoid enemy fire and survive!"),
                new Label("4. Collect power-ups to gain advantages.")
            );
            Scene instructionsScene = new Scene(instructionsContent, 400, 300);
            instructionsStage.setScene(instructionsScene);
            instructionsStage.setTitle("Instructions");
            instructionsStage.show();
        });
        instructionsButton.setLayoutX(14);
        instructionsButton.setLayoutY(14);

        scene1.getStylesheets().add("style.css");

        mainLayout.getChildren().addAll(backgroundView, title, centerButtons, instructionsButton);

        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    public static void setMainMenu(Stage stage) {
        Pane mainLayout = new Pane();
        Scene scene1 = new Scene(mainLayout, 960, 540);

        Label title = new Label("Space Shooter");
        title.getStyleClass().add("title");
        title.setLayoutX(380);
        title.setLayoutY(50);

        // Register the custom font globally
        Font.loadFont("file:assets/fonts/Jersey10-Regular.ttf", 36);

        // Apply the font explicitly to the title label
        title.setFont(Font.font("Jersey10", 36));

        Image backgroundImage = new Image("file:assets/backgrounds/scene1.gif");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setRotate(270);
        backgroundView.setLayoutX(210);
        backgroundView.setLayoutY(-210);

        // Buttons
        ImageView startButton = MenuButton.create("file:assets/buttons/ICONS - 25.png", 240, 170, () -> {
            GameStage gameStage = new GameStage();
            gameStage.setStage(stage);
        });

        ImageView aboutButton = MenuButton.create("file:assets/buttons/ICONS - 36.png", 240, 170, () -> {
            Stage aboutStage = new Stage();
            VBox aboutContent = new VBox(10);
            aboutContent.setAlignment(Pos.CENTER);
            aboutContent.getChildren().addAll(
                new Label("About the Game"),
                new Label("Project Members:"),
                new Label("- Member 1"),
                new Label("- Member 2"),
                new Label("- Member 3")
            );
            Scene aboutScene = new Scene(aboutContent, 400, 300);
            aboutStage.setScene(aboutScene);
            aboutStage.setTitle("About");
            aboutStage.show();
        });

        ImageView exitButton = MenuButton.create("file:assets/buttons/ICONS - 30.png", 240, 170, () -> {
            Platform.exit();
        });

        VBox centerButtons = new VBox(-100);
        centerButtons.setLayoutX((scene1.getWidth() - 240) / 2);
        centerButtons.setLayoutY(200);
        centerButtons.setStyle("-fx-padding: 0; -fx-alignment: center; -fx-width: 240; -fx-height: 170;");
        centerButtons.getChildren().addAll(startButton, aboutButton, exitButton);

        ImageView instructionsButton = MenuButton.create("file:assets/buttons/Instructions .png", 50, 50, () -> {
            Stage instructionsStage = new Stage();
            VBox instructionsContent = new VBox(10);
            instructionsContent.setAlignment(Pos.CENTER);
            instructionsContent.getChildren().addAll(
                new Label("How to Play"),
                new Label("1. Use arrow keys to move."),
                new Label("2. Press space to shoot."),
                new Label("3. Avoid enemy fire and survive!"),
                new Label("4. Collect power-ups to gain advantages.")
            );
            Scene instructionsScene = new Scene(instructionsContent, 400, 300);
            instructionsStage.setScene(instructionsScene);
            instructionsStage.setTitle("Instructions");
            instructionsStage.show();
        });
        instructionsButton.setLayoutX(14);
        instructionsButton.setLayoutY(14);

        scene1.getStylesheets().add("style.css");

        mainLayout.getChildren().addAll(backgroundView, title, centerButtons, instructionsButton);

        stage.setScene(scene1);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
