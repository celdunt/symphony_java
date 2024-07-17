package loc.ex.symphony;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import loc.ex.symphony.listview.NotesStorage;
import loc.ex.symphony.listview.ParallelsLinksStorage;
import loc.ex.symphony.ui.MainController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Objects;

public class Symphony extends Application {
    public static Stage window;
    public static Scene scene;

    @Override
    public void start(Stage window) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/main-window_design.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        Symphony.window = window;
        Symphony.scene = scene;

        MainController.currentWindowWidth.set(window.getWidth());
        MainController.currentWindowHeight.set(window.getHeight());

        window.widthProperty().addListener(lis -> {
            MainController.currentWindowWidth.set(window.getWidth());
            MainController.currentWindowHeight.set(window.getHeight());
        });

        InputStream urlStream = Symphony.class.getResourceAsStream("ico.png");
        if (urlStream == null) {
            throw new RuntimeException("Resource not found: ico.png");
        }

        window.getIcons().add(new Image(urlStream));

        window.setOnCloseRequest(request -> {
            try {
                NotesStorage.update();
                ParallelsLinksStorage.update();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        window.setTitle("Симфония");
        window.setScene(scene);
        window.show();
    }

    public static void main(String[] args) {
        launch();
    }
}