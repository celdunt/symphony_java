package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;

import java.io.IOException;
import java.io.InputStream;

public class ArticlesWindow {

    private final Stage window = new Stage();

    public ArticlesWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/article-window-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        InputStream urlStream = Symphony.class.getResourceAsStream("buttons/to-article.png");
        if (urlStream == null) {
            throw new RuntimeException("Resource not found: buttons/to-article.png");
        }

        window.getIcons().add(new Image(urlStream));

        window.setTitle("Темы");
        window.setScene(scene);
    }


    public Stage stage() {
        return window;
    }

}
