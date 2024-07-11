package loc.ex.symphony.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import loc.ex.symphony.Symphony;
import loc.ex.symphony.listview.Note;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Objects;

public class NoteWindow {

    private final Stage window = new Stage();

    public NoteWindow(String title, Note note) throws IOException, URISyntaxException {
        FXMLLoader fxmlLoader = new FXMLLoader(Symphony.class.getResource("windows/note-design.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 300, 300);

        ((NoteController)fxmlLoader.getController()).note = note.open();
        ((NoteController)fxmlLoader.getController()).initTextAreaHandler();

        window.setOnCloseRequest(request -> {
            note.close();
        });

        InputStream urlStream = Symphony.class.getResourceAsStream("buttons/to-note.png");
        if (urlStream == null) {
            throw new RuntimeException("Resource not found: buttons/to-note.png");
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
