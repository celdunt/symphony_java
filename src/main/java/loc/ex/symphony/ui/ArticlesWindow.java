package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;

import java.io.IOException;

public class ArticlesWindow {

    private final Stage window = new Stage();

    public ArticlesWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/article-window-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);

        window.setTitle("Article's");
        window.setScene(scene);
    }


    public Stage stage() {
        return window;
    }

}
