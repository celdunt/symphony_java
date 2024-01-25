package loc.ex.symphony.ui;

import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;

import java.io.IOException;

public class BookmarksWindow {

    private final Stage window = new Stage();

    public BookmarksWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/bookmarks-window_design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 600, 400);

        window.setTitle("Bookmark's");
        window.setScene(scene);
    }

    public void Show() {
        window.show();
    }

    public Stage GetStage() {
        return window;
    }
}
