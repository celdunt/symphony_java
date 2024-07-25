package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.listview.Link;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ArticlesWindow {

    private final Stage window = new Stage();

    public ArticlesWindow() throws IOException {
        initScene();
        defineGraphic();
    }

    public ArticlesWindow(List<Link> blinks, List<Link> elinks, List<Link> olinks) throws IOException {
        initScene();
        defineGraphic();
        ArticlesController.isAdditionArticle.set(true);
        ArticlesController.elinks = elinks;
        ArticlesController.blinks = blinks;
        ArticlesController.olinks = olinks;
    }


    private void initScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/article-window-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 400, 300);



        window.setTitle("Темы");
        window.setScene(scene);
    }

    private void defineGraphic() {
        InputStream urlStream = Symphony.class.getResourceAsStream("main-window/articles.png");
        if (urlStream == null) {
            throw new RuntimeException("Resource not found: main-window/articles.png");
        }

        window.getIcons().add(new Image(urlStream));
    }

    public Stage stage() {
        return window;
    }

}
