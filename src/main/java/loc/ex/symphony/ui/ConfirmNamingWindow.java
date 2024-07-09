package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.listview.Article;

import java.io.IOException;

public class ConfirmNamingWindow {

    private final Stage window = new Stage();

    public ConfirmNamingWindow(Article article) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/define-name-article-window.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 300, 100);

        ((ConfirmNamingController)fxmlLoader.getController()).article = article;

        window.setTitle("Ввод имени темы");
        window.setScene(scene);
    }


    public Stage stage() {
        return window;
    }

}
