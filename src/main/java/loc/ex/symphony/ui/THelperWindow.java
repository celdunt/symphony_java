package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.listview.Note;
import loc.ex.symphony.listview.TranslateHelper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

public class THelperWindow {

    private final Stage window = new Stage();

    public THelperWindow(String title, TranslateHelper thelper) throws IOException, URISyntaxException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/thelper-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 300, 300);

        ((THelperController)fxmlLoader.getController()).thelper = thelper.open();
        ((THelperController)fxmlLoader.getController()).initTextAreaHandler();

        window.setOnCloseRequest(request -> {
            thelper.close();
        });

        InputStream urlStream = Symphony.class.getResourceAsStream("buttons/to-thelp.png");
        if (urlStream == null) {
            throw new RuntimeException("Resource not found: buttons/to-thelp.png");
        }

        window.getIcons().add(new Image(urlStream));

        window.initModality(Modality.NONE);
        window.initOwner(null);
        window.setAlwaysOnTop(true);

        window.setTitle(title);
        window.setScene(scene);
    }


    public Stage stage() {
        return window;
    }

}
