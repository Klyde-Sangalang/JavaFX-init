package components;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public interface MenuButton {
    // Reusable method to create a menu button with an image
    static ImageView create(String imagePath, double width, double height, Runnable onClick) {
        Image icon = new Image(imagePath);
        ImageView button = new ImageView(icon);
        button.setFitWidth(width);
        button.setFitHeight(height);
        button.setOnMouseClicked(event -> onClick.run());
        return button;
    }
}
