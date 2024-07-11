package loc.ex.symphony.ui;

import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Objects;

public class BookmarksWindow {

    private final Stage window = new Stage();

    public BookmarksWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/bookmark-window-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        InputStream urlStream = Symphony.class.getResourceAsStream("buttons/to-bookmark.png");
        if (urlStream == null) {
            throw new RuntimeException("Resource not found: buttons/to-bookmark.png");
        }

        window.getIcons().add(new Image(urlStream));

        window.setTitle("Закладки");
        window.setScene(scene);
    }


    public Stage stage() {
        return window;
    }
}
