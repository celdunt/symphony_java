package loc.ex.symphony;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import loc.ex.symphony.file.BookSerializer;
import loc.ex.symphony.file.StartupParameters;
import loc.ex.symphony.file.TranslateHelperSerializer;
import loc.ex.symphony.listview.NotesStorage;
import loc.ex.symphony.listview.ParallelsLinksStorage;
import loc.ex.symphony.listview.TranslateHelperStorage;
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
                TranslateHelperStorage.update();
                MainController controller = fxmlLoader.getController();
                StartupParameters startupParameters = new StartupParameters(
                        controller.bookTabPane.getSelectionModel().getSelectedIndex(),
                        controller.bibleTab.isSelected()?controller.bibleListView.getSelectionModel().getSelectedIndex():
                                controller.ellenTab.isSelected()? controller.ellenListView.getSelectionModel().getSelectedIndex():
                                        controller.otherListView.getSelectionModel().getSelectedIndex(),
                        controller.chapterListView.getSelectionModel().getSelectedIndex()
                );
                startupParameters.save();
                BookSerializer.save(controller.bibleListView.getItems());
                BookSerializer.save(controller.ellenListView.getItems());
                BookSerializer.save(controller.otherListView.getItems());
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