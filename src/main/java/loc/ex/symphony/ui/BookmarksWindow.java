package loc.ex.symphony.ui;

import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;

import java.io.IOException;

public class BookmarksWindow {

    public static void initAndShow() throws IOException {
        Stage window = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/bookmarks-window_design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 300, 450);

        window.setTitle("Bookmark's");
        window.setScene(scene);

        window.show();
    }

}
