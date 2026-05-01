import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import components.MenuButton;

public class App extends Application {
    private Stage primaryStage;
    private Pane mainLayout;
    private Scene scene1;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mainLayout = new Pane();
        scene1 = new Scene(mainLayout, 960, 540);
        scene1.getStylesheets().add("style.css");

        showMenuView();

        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    private void showMenuView() {
        mainLayout.getChildren().clear();

        Image backgroundImage = new Image("file:assets/backgrounds/scene1.gif");
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setRotate(270);
        backgroundView.setLayoutX(210);
        backgroundView.setLayoutY(-210);

        Label title = new Label("Space Shooter");
        title.getStyleClass().add("title");
        Font.loadFont("file:assets/fonts/Jersey10-Regular.ttf", 36);
        title.setFont(Font.font("Jersey10", 36));

        HBox titleBox = new HBox();
        titleBox.setPrefWidth(960);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setLayoutY(50);
        titleBox.getChildren().add(title);

        ImageView startButton = MenuButton.create("file:assets/buttons/ICONS - 25.png", 240, 170, () -> {
            GameStage gameStage = new GameStage();
            gameStage.setStage(primaryStage);
        });

        ImageView aboutButton = MenuButton.create("file:assets/buttons/ICONS - 36.png", 240, 170, () -> {
            showAboutView();
        });

        ImageView exitButton = MenuButton.create("file:assets/buttons/ICONS - 30.png", 240, 170, () -> {
            Platform.exit();
        });

        VBox centerButtons = new VBox(-100);
        centerButtons.setLayoutX((960 - 240) / 2);
        centerButtons.setLayoutY(200);
        centerButtons.setStyle("-fx-padding: 0; -fx-alignment: center; -fx-width: 240; -fx-height: 170;");
        centerButtons.getChildren().addAll(startButton, aboutButton, exitButton);

        ImageView instructionsButton = MenuButton.create("file:assets/buttons/Instructions .png", 50, 50, () -> {
            showInstructionsView();
        });
        instructionsButton.setLayoutX(14);
        instructionsButton.setLayoutY(14);

        mainLayout.getChildren().addAll(backgroundView, titleBox, centerButtons, instructionsButton);
    }

    private void showAboutView() {
        mainLayout.getChildren().clear();

        VBox aboutContent = new VBox(15);
        aboutContent.setPrefSize(960, 540);
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

        membersBox.getChildren().forEach(node ->
            ((Label)node).setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;"
            )
        );

        Button backButton = new Button("Back to Menu");
        backButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-color: #ff6b6b;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> showMenuView());

        aboutContent.getChildren().addAll(
            aboutTitle,
            description,
            membersTitle,
            membersBox,
            backButton
        );

        mainLayout.getChildren().add(aboutContent);
    }

    private void showInstructionsView() {
        mainLayout.getChildren().clear();

        VBox instructionsContent = new VBox(15);
        instructionsContent.setPrefSize(960, 540);
        instructionsContent.setAlignment(Pos.CENTER);
        instructionsContent.setStyle(
            "-fx-background-color: linear-gradient(to bottom,#111827,#1e293b);" +
            "-fx-padding: 30;"
        );

        Label instructionsTitle = new Label("HOW TO PLAY");
        instructionsTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #38bdf8;"
        );

        VBox instructionsList = new VBox(10);
        instructionsList.setAlignment(Pos.CENTER_LEFT);
        instructionsList.setStyle(
            "-fx-background-color: #0f172a;" +
            "-fx-padding: 20;" +
            "-fx-background-radius: 10;"
        );

        String[] instructions = {
            "1. Use arrow keys or WASD to move your spaceship around",
            "2. Press SPACE to fire projectiles at enemies",
            "3. Avoid enemy fire and survive the countdown timer",
            "4. Destroy enemies to earn time bonuses (+6 seconds per kill)",
            "5. Every 50 enemies defeated triggers a boss battle",
            "6. Defeat the boss to progress and earn more time",
            "7. Game ends when the timer reaches 00:00"
        };

        for (String instruction : instructions) {
            Label label = new Label(instruction);
            label.setStyle(
                "-fx-text-fill: white;" +
                "-fx-font-size: 13px;" +
                "-fx-wrap-text: true;"
            );
            instructionsList.getChildren().add(label);
        }

        Button backButton = new Button("Back to Menu");
        backButton.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-background-color: #ff6b6b;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 5;"
        );
        backButton.setOnAction(e -> showMenuView());

        instructionsContent.getChildren().addAll(
            instructionsTitle,
            instructionsList,
            backButton
        );

        mainLayout.getChildren().add(instructionsContent);
    }

    public static void setMainMenu(Stage stage) {
        App app = new App();
        try {
            app.start(stage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
